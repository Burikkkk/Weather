package Utilities;

import Enums.RequestType;
import Enums.ResponseStatus;
import Models.TCP.Request;
import Models.TCP.Response;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.Socket;
import java.sql.SQLException;
import java.util.*;

import com.google.gson.reflect.TypeToken;
import Models.Entities.User;
import Models.Entities.PersonalSettings;
import Service.UserService;
import Service.PersonalSettingsService;

public class ClientThread implements Runnable {
    private static final List<User> loginUsers = Collections.synchronizedList(new ArrayList<>());
    private String currentLogin = null;
    private final Socket clientSocket;
    private final Gson gson = new Gson();
    private BufferedReader in;
    private PrintWriter out;

    // Сервисы для работы с данными
    private final UserService userService = new UserService();
    private final PersonalSettingsService personalSettingsService = new PersonalSettingsService();

    public ClientThread(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new PrintWriter(clientSocket.getOutputStream(), true);
    }

    @Override
    public void run() {
        try {
            System.out.println("Клиент " + clientSocket.getInetAddress() + ":" + clientSocket.getPort() + " подключен.");
            while (clientSocket.isConnected()) {
                String message = in.readLine();
                if (message == null) { // Клиент разорвал соединение
                    break;
                }
                Request request = gson.fromJson(message, Request.class);
                if (request != null && request.getRequestType() == RequestType.LOGIN) {
                    User user = gson.fromJson(request.getRequestMessage(), User.class);
                    currentLogin = user.getLogin(); // Сохраняем логин подключившегося
                }

                Response response = handleRequest(message);
                out.println(gson.toJson(response)); // Отправка ответа клиенту
            }
        } catch (IOException e) {
            System.err.println("Ошибка связи с клиентом: " + e.getMessage());
        } finally {
            if (currentLogin != null) {
                synchronized (loginUsers) {
                    loginUsers.removeIf(user -> user.getLogin().equalsIgnoreCase(currentLogin));
                }
            }
            closeResources();
        }
    }

    private Response handleRequest(String message) {
        try {
            Request request = gson.fromJson(message, Request.class);

            if (request == null || request.getRequestType() == null) {
                return new Response(ResponseStatus.ERROR, "Некорректный запрос", "");
            }

            switch (request.getRequestType()) {
                case REGISTER:
                    return handleRegister(request);
                case LOGIN:
                    return handleLogin(request);
                case FORGOT_PASSWORD:
                    return handleForgotPassword(request);
                case UPDATE_PASSWORD:
                    return handleUpdatePassword(request);
                case UPDATE_USER:
                    return handleUpdateUser(request);
                case GET_ALL_USERS:
                    return handleGetAllUsers();
                case UPDATE_ALL_USERS:
                    return handleUpdateAllUsers(request);
                case GET_CONNECTED_USERS:
                    return handleGetConnectedUsers();
                default:
                    return new Response(ResponseStatus.ERROR, "Неизвестный тип запроса", "");
            }
        } catch (Exception e) {
            System.err.println("Ошибка обработки запроса: " + e.getMessage());
            return new Response(ResponseStatus.ERROR, "Ошибка обработки запроса", "");
        }
    }

    private Response handleRegister(Request request) throws SQLException{
        User user = gson.fromJson(request.getRequestMessage(), User.class);

        if (personalSettingsService.getAllEntities().stream().noneMatch(x -> x.getPhone().equalsIgnoreCase(user.getPersonalSettings().getPhone()))) {
        // Проверка, существует ли уже пользователь с таким логином
            if (userService.getAllEntities().stream().noneMatch(x -> x.getLogin().equalsIgnoreCase(user.getLogin()))) {
                // Сохранение PersonalSettings пользователя
                PersonalSettings personalSettings = user.getPersonalSettings();
                if (personalSettings != null) {
                    personalSettingsService.createEntity(personalSettings);
                }

                // Сохранение пользователя
                userService.createEntity(user);

                return new Response(ResponseStatus.OK, "Регистрация завершена", gson.toJson(user));
            } else {
                return new Response(ResponseStatus.ERROR, "Такой пользователь уже существует", "");
            }
        }else
            return new Response(ResponseStatus.ERROR, "Пользователь с таким телефоном уже существует", "");
    }

    private Response handleLogin(Request request) throws SQLException {
        User requestUser = gson.fromJson(request.getRequestMessage(), User.class);

        User existingUser = userService.getAllEntities().stream()
                .filter(x -> x.getLogin().equalsIgnoreCase(requestUser.getLogin())) // Сравниваем логины
                .findFirst()
                .orElse(null);

        if (existingUser != null) {
            synchronized (loginUsers) {
                if (!loginUsers.contains(existingUser)) {
                    loginUsers.add(existingUser); // Добавляем в список подключенных пользователей
                }
            }
        }
        if (existingUser != null && Arrays.equals(existingUser.getPassword(), requestUser.getPassword())) {
            return new Response(ResponseStatus.OK, "Авторизация успешна", gson.toJson(existingUser));
        } else {
            return new Response(ResponseStatus.ERROR, "Неверный логин или пароль", "");
        }
    }

    private Response handleForgotPassword(Request request) throws SQLException {
        // Преобразуем сообщение запроса в объект User
        User requestUser = gson.fromJson(request.getRequestMessage(), User.class);

        // Пытаемся найти пользователя по логину и телефону
        User existingUser = userService.getAllEntities().stream()
                .filter(x -> x.getLogin().equalsIgnoreCase(requestUser.getLogin()) &&
                        x.getPersonalSettings() != null &&
                        x.getPersonalSettings().getPhone().equals(requestUser.getPersonalSettings().getPhone()))
                .findFirst()
                .orElse(null);

        // Если пользователь найден
        if (existingUser != null) {
            // Возвращаем успешный ответ с данными пользователя (например, его id или имя)
            return new Response(ResponseStatus.OK, "Пользователь найден", gson.toJson(existingUser));
        } else {
            // Если пользователь не найден, возвращаем ошибку
            return new Response(ResponseStatus.ERROR, "Пользователь с таким логином и телефоном не найден", "");
        }
    }


    private Response handleUpdatePassword(Request request) throws SQLException {
        // Преобразуем сообщение запроса в объект User
        User requestUser = gson.fromJson(request.getRequestMessage(), User.class);

        // Пытаемся найти пользователя по логину
        User existingUser = userService.getAllEntities().stream()
                .filter(x -> x.getLogin().equalsIgnoreCase(requestUser.getLogin()))
                .findFirst()
                .orElse(null);

        // Если пользователь найден
        if (existingUser != null) {
            // Обновляем пароль
            existingUser.setPassword(requestUser.getPassword());

            // Сохраняем изменения в базе данных
            userService.updateEntity(existingUser);

            // Возвращаем успешный ответ
            return new Response(ResponseStatus.OK, "Пароль успешно изменен", gson.toJson(existingUser));
        } else {
            // Если пользователь не найден, возвращаем ошибку
            return new Response(ResponseStatus.ERROR, "Пользователь с таким логином не найден", "");
        }
    }




    private Response handleUpdateUser(Request request) throws SQLException {
        // Десериализация объекта пользователя из запроса
        User updatedUser = gson.fromJson(request.getRequestMessage(), User.class);


        // Получаем текущего пользователя из базы данных
        Optional<User> existingUser = userService.getEntityById(updatedUser.getId());
        if (existingUser==null) {
            return new Response(ResponseStatus.ERROR, "Пользователь не найден", "");
        }
        Optional<PersonalSettings> existingSettings = personalSettingsService.getEntityById(updatedUser.getPersonalSettings().getId());
        if (existingSettings==null) {
            return new Response(ResponseStatus.ERROR, "Настройки не найдены", "");
        }
        // Проверка на уникальность логина
        boolean isLoginDuplicate = userService.getAllEntities().stream()
                .anyMatch(user -> user.getLogin().equalsIgnoreCase(updatedUser.getLogin())
                        && user.getId() != updatedUser.getId()); // Исключаем текущего пользователя

        if (isLoginDuplicate) {
            return new Response(ResponseStatus.ERROR, "Пользователь с таким логином уже существует", "");
        }
        // Проверка на уникальность телефона
        boolean isPhoneDuplicate = personalSettingsService.getAllEntities().stream()
                .anyMatch(settings ->
                        settings.getPhone() != null
                                && settings.getPhone().equalsIgnoreCase(updatedUser.getPersonalSettings().getPhone())
                                && settings.getId() != updatedUser.getPersonalSettings().getId()); // Исключаем текущего пользователя

        if (isPhoneDuplicate) {
            return new Response(ResponseStatus.ERROR, "Пользователь с таким телефоном уже существует", "");
        }


        personalSettingsService.updateEntity(updatedUser.getPersonalSettings());
        userService.updateEntity(updatedUser);

        return new Response(ResponseStatus.OK, "Данные пользователя успешно обновлены", gson.toJson(updatedUser));


    }


    private Response handleGetAllUsers() throws SQLException {
        // Получаем всех пользователей из базы данных
        List<User> users = userService.getAllEntities();

        // Проверяем, что список пользователей не пустой
        if (users.isEmpty()) {
            return new Response(ResponseStatus.ERROR, "Пользователи не найдены", "");
        }

        // Возвращаем успешный ответ с данными пользователей
        return new Response(ResponseStatus.OK, "Список пользователей загружен", gson.toJson(users));
    }


    private Response handleUpdateAllUsers(Request request) throws SQLException {
        // Преобразуем сообщение запроса в список пользователей
        Type userListType = new TypeToken<List<User>>() {}.getType();
        List<User> updatedUsers = gson.fromJson(request.getRequestMessage(), userListType);

        if (updatedUsers == null || updatedUsers.isEmpty()) {
            return new Response(ResponseStatus.ERROR, "Список пользователей пуст", "");
        }

        // Обновляем роли пользователей
        for (User updatedUser : updatedUsers) {
            // Находим пользователя в базе данных по логину
            User existingUser = userService.getAllEntities().stream()
                    .filter(user -> user.getLogin().equalsIgnoreCase(updatedUser.getLogin()))
                    .findFirst()
                    .orElse(null);


                existingUser.setRole(updatedUser.getRole());
                userService.updateEntity(existingUser); // Сохраняем изменения в базе данных

        }

        // Возвращаем успешный ответ
        return new Response(ResponseStatus.OK, "Роли пользователей успешно обновлены", "");
    }

    private Response handleGetConnectedUsers() throws SQLException {

        // Проверяем, что список пользователей не пустой
        if (loginUsers.isEmpty()) {
            return new Response(ResponseStatus.ERROR, "Пользователи не подключены", "");
        }
        // Возвращаем успешный ответ с данными пользователей
        return new Response(ResponseStatus.OK, "Пользователи подключены", gson.toJson(loginUsers));
    }

    private void closeResources() {
        System.out.println("Клиент " + clientSocket.getInetAddress() + ":" + clientSocket.getPort() + " отключен.");
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (clientSocket != null && !clientSocket.isClosed()) clientSocket.close();
        } catch (IOException e) {
            System.err.println("Ошибка при закрытии ресурсов: " + e.getMessage());
        }
    }
}
