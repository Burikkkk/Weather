package Utilities;

import Enums.ResponseStatus;
import Models.TCP.Request;
import Models.TCP.Response;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Optional;

import Models.Entities.User;
import Models.Entities.PersonalSettings;
import Service.UserService;
import Service.PersonalSettingsService;

public class ClientThread implements Runnable {
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

                Response response = handleRequest(message);
                out.println(gson.toJson(response)); // Отправка ответа клиенту
            }
        } catch (IOException e) {
            System.err.println("Ошибка связи с клиентом: " + e.getMessage());
        } finally {
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
    }

    private Response handleLogin(Request request) throws SQLException {
        User requestUser = gson.fromJson(request.getRequestMessage(), User.class);

        // Проверка, существует ли пользователь и правильный ли пароль
        User existingUser = userService.getAllEntities().stream()
                .filter(x -> x.getLogin().equalsIgnoreCase(requestUser.getLogin()) && x.getPassword().equals(requestUser.getPassword()))
                .findFirst()
                .orElse(null);

        if (existingUser != null) {
            return new Response(ResponseStatus.OK, "Авторизация успешна", gson.toJson(existingUser));
        } else {
            return new Response(ResponseStatus.ERROR, "Неверный логин или пароль", "");
        }
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
