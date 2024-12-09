package Utilities;

import Enums.RequestType;
import Enums.ResponseStatus;
import Models.Entities.*;
import Models.TCP.Request;
import Models.TCP.Response;
import Service.*;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

import com.google.gson.reflect.TypeToken;

public class ClientThread implements Runnable {

    private static final List<Integer> loginUsersId = Collections.synchronizedList(new ArrayList<>());
    private int currentId = 0;
    private final Socket clientSocket;
    private final Gson gson = new Gson();
    private BufferedReader in;
    private PrintWriter out;

    // Сервисы для работы с данными
    private final Connection connection = ConnectorDB.getConnection();
    private final UserService userService = new UserService();
    private final PersonalSettingsService personalSettingsService = new PersonalSettingsService();
    private final DayService dayService = new DayService(connection);
    private final LocationService locationService = new LocationService(connection);
    private final WeatherNameService weatherNameService = new WeatherNameService(connection);
    private final WeatherParametersService weatherParametersService = new WeatherParametersService(connection);
    private final DashboardService dashboardService = new DashboardService(connection);

    public ClientThread(Socket clientSocket) throws IOException, SQLException {
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
                Request request = gson.fromJson(message, Request.class);//
                Response response = handleRequest(message);
                out.println(gson.toJson(response)); // Отправка ответа клиенту
                if (request != null && request.getRequestType() == RequestType.LOGOUT) {
                    closeResources();
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка связи с клиентом: " + e.getMessage());
        } finally {
            if (currentId != 0) {
                synchronized (loginUsersId) {
                    loginUsersId.removeIf(id -> id == currentId);
                }
            }
            if (clientSocket != null && !clientSocket.isClosed())
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
                case UPDATE_USER:
                    return handleUpdateUser(request);
                case GET_ALL_USERS:
                    return handleGetAllUsers();
                case UPDATE_ALL_USERS:
                    return handleUpdateAllUsers(request);
                case GET_CONNECTED_USERS:
                    return handleGetConnectedUsers();
                case GET_REGIONS:
                    return handleGetRegions();
                case GET_ALL_DAYS:
                    return handleGetAllDays();
                case LOGOUT:
                    return handleLogout(request);
                case TODAY_WEATHER:
                    return handleGetTodayWeather(request);
                case ADD_DAY:
                    return handleAddDay(request);
                case UPDATE_DAY:
                    return handleUpdateDay(request);
                case DELETE_DAY:
                    return handleDeleteDay(request);
                case GET_DASHBOARD:
                    return handleGetDashboard(request);
                case ADD_USER_DASHBOARD:
                    return handleAddUserDashboard(request);
                default:
                    return new Response(ResponseStatus.ERROR, "Неизвестный тип запроса", "");
            }
        } catch (Exception e) {
            System.err.println("Ошибка обработки запроса: " + e.getMessage());
            return new Response(ResponseStatus.ERROR, "Ошибка обработки запроса", "");
        }
    }

    private Response handleRegister(Request request) throws SQLException {
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
        } else
            return new Response(ResponseStatus.ERROR, "Пользователь с таким телефоном уже существует", "");
    }

    private Response handleLogin(Request request) throws SQLException {
        User requestUser = gson.fromJson(request.getRequestMessage(), User.class);

        User existingUser = userService.getAllEntities().stream()
                .filter(x -> x.getLogin().equalsIgnoreCase(requestUser.getLogin())) // Сравниваем логины
                .findFirst()
                .orElse(null);


        if (existingUser != null) {
            currentId = existingUser.getId();
            synchronized (loginUsersId) {
                if (!loginUsersId.contains(existingUser.getId())) {
                    loginUsersId.add(existingUser.getId());
                }
            }
        }
        if (existingUser != null && Arrays.equals(existingUser.getPassword(), requestUser.getPassword())) {
            return new Response(ResponseStatus.OK, "Авторизация успешна", gson.toJson(existingUser));
        } else {
            return new Response(ResponseStatus.ERROR, "Неверный логин или пароль", "");
        }
    }

    private Response handleLogout(Request request) throws SQLException {
        // Десериализация пользователя из запроса
        User requestUser = gson.fromJson(request.getRequestMessage(), User.class);

        if (requestUser == null) {
            return new Response(ResponseStatus.ERROR, "Пользователь не найден", "");
        }

        synchronized (loginUsersId) {
            // Проверяем, существует ли пользователь в списке
            if (loginUsersId.contains(requestUser.getId())) {
                loginUsersId.removeIf(id -> id == currentId);


                return new Response(ResponseStatus.OK, "Выход успешен", gson.toJson(requestUser));
            } else {
                return new Response(ResponseStatus.ERROR, "Пользователь не найден в системе", "");
            }
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

    private Response handleUpdateUser(Request request) throws SQLException {
        // Десериализация объекта пользователя из запроса
        User updatedUser = gson.fromJson(request.getRequestMessage(), User.class);


        // Получаем текущего пользователя из базы данных
        Optional<User> existingUser = userService.getEntityById(updatedUser.getId());
        if (existingUser == null) {
            return new Response(ResponseStatus.ERROR, "Пользователь не найден", "");
        }
        Optional<PersonalSettings> existingSettings = personalSettingsService.getEntityById(updatedUser.getPersonalSettings().getId());
        if (existingSettings == null) {
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
        Type userListType = new TypeToken<List<User>>() {
        }.getType();
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
        if (loginUsersId.isEmpty()) {
            return new Response(ResponseStatus.ERROR, "Пользователи не подключены", "");
        }

        List<User> loginUsers = new ArrayList<>();

        for (Integer userId : loginUsersId) {
            // Ищем пользователя по ID
            User user = userService.getAllEntities().stream()
                    .filter(u -> u.getId() == userId) // Сравниваем ID
                    .findFirst()
                    .orElse(null); // Если не нашли пользователя, возвращаем null

            // Если пользователь найден, добавляем его в список loginUsers
            if (user != null) {
                loginUsers.add(user);
            }
        }


        // Возвращаем успешный ответ с данными пользователей
        return new Response(ResponseStatus.OK, "Пользователи подключены", gson.toJson(loginUsers));
    }

    private Response handleGetAllDays() throws SQLException {

        List<Day> outputDays = dayService.getAllEntities();

        // Проверяем, что список пользователей не пустой
        if (outputDays.isEmpty()) {
            return new Response(ResponseStatus.ERROR, "Данные не найдены", "");
        }

        // Возвращаем успешный ответ с данными пользователей
        return new Response(ResponseStatus.OK, "Данные загружены", gson.toJson(outputDays));


    }

    private Response handleGetRegions() throws SQLException {
        List<Day> outputDays = dayService.getAllEntities();
        List<Location> regions = new ArrayList<>();
        Set<String> uniqueTowns = new HashSet<>();

        for (Day day : outputDays) {
            Location location = day.getLocation();
            if (location != null && uniqueTowns.add(location.getTown())) {
                regions.add(location); // Добавляем только уникальные города
            }
        }


        // Проверяем, что список пользователей не пустой
        if (regions.isEmpty()) {
            return new Response(ResponseStatus.ERROR, "Данные не найдены", "");
        }

        // Возвращаем успешный ответ с данными пользователей
        return new Response(ResponseStatus.OK, "Данные загружены", gson.toJson(regions));
    }

    private Response handleGetTodayWeather(Request request) throws SQLException {
        Day tempDay = gson.fromJson(request.getRequestMessage(), Day.class);

        Day existingDay = dayService.getAllEntities().stream()
                .filter(x -> x.getDate().equals(tempDay.getDate()) && x.getLocation().getTown().equals(tempDay.getLocation().getTown()))
                .findFirst()
                .orElse(null);

        if (existingDay != null) {
            return new Response(ResponseStatus.OK, "Погода найдена", gson.toJson(existingDay));
        } else {
            return new Response(ResponseStatus.ERROR, "Погода не найдена", "");
        }
    }

    private Response handleAddDay(Request request) throws SQLException {
        Day insertDay = gson.fromJson(request.getRequestMessage(), Day.class); //без id

        if (dayService.getAllEntities().stream().noneMatch
                (x -> x.getDate().equals(insertDay.getDate()) && x.getLocation().getTown().equals(insertDay.getLocation().getTown()))) {

            Location loc = new Location(insertDay.getLocation().getTown(), insertDay.getLocation().getCountry());
            // Проверяем, есть ли такой город в списке
            Optional<Location> existingLocation = locationService.getAllEntities().stream()
                    .filter(x -> x.getTown().equals(loc.getTown()))
                    .findFirst();

            if (existingLocation.isPresent()) {
                // Если найден, устанавливаем его ID
                loc.setId(existingLocation.get().getId());
            } else {
                // Если не найден, создаем новый объект
                locationService.createEntity(loc);
            }

            WeatherName weatherN = new WeatherName(insertDay.getWeatherName().getName());
            // Проверяем, есть ли такой город в списке
            Optional<WeatherName> existingW = weatherNameService.getAllEntities().stream()
                    .filter(x -> x.getName().equals(weatherN.getName()))
                    .findFirst();

            if (existingW.isPresent()) {
                // Если найден, устанавливаем его ID
                weatherN.setId(existingW.get().getId());
            } else {
                // Если не найден, создаем новый объект
                weatherNameService.createEntity(weatherN);
            }
            WeatherParameters dayP = new WeatherParameters(insertDay.getDayWeather().getTemperature(),
                    insertDay.getDayWeather().getPressure(),
                    insertDay.getDayWeather().getHumidity(),
                    insertDay.getDayWeather().getPrecipitation(),
                    insertDay.getDayWeather().getWindSpeed());
            WeatherParameters nightP = new WeatherParameters(insertDay.getNightWeather().getTemperature(),
                    insertDay.getNightWeather().getPressure(),
                    insertDay.getNightWeather().getHumidity(),
                    insertDay.getNightWeather().getPrecipitation(),
                    insertDay.getNightWeather().getWindSpeed());

            weatherParametersService.createEntity(dayP);
            weatherParametersService.createEntity(nightP);


            Day finalDay = new Day(insertDay.getDate(), dayP, nightP, weatherN, loc);

            dayService.createEntity(finalDay);
            return new Response(ResponseStatus.OK, "Добавление данных прошло успешно", gson.toJson(finalDay));

        } else
            return new Response(ResponseStatus.ERROR, "На этот день погода уже установлена", "");
    }

    private Response handleUpdateDay(Request request) throws SQLException {
        Day updatedDay = gson.fromJson(request.getRequestMessage(), Day.class);

        // Проверяем существование дня в базе данных
        Optional<Day> existingDayOpt = dayService.getEntityById(updatedDay.getId());
        if (!existingDayOpt.isPresent()) {
            return new Response(ResponseStatus.ERROR, "День не найден", "");
        }

        Day existingDay = existingDayOpt.get();

        // 1. Проверка и обновление Location
        Location updatedLocation = updatedDay.getLocation();
        Optional<Location> existingLocationOpt = locationService.getAllEntities().stream()
                .filter(loc -> loc.getTown().equals(updatedLocation.getTown()) &&
                        loc.getCountry().equals(updatedLocation.getCountry()))
                .findFirst();
        if (existingLocationOpt.isPresent()) {
            updatedLocation.setId(existingLocationOpt.get().getId());
        } else {
            locationService.createEntity(updatedLocation);
        }
        updatedDay.setLocation(updatedLocation);

        // 2. Проверка и обновление WeatherName
        WeatherName updatedWeatherName = updatedDay.getWeatherName();
        Optional<WeatherName> existingWeatherNameOpt = weatherNameService.getAllEntities().stream()
                .filter(w -> w.getName().equals(updatedWeatherName.getName()))
                .findFirst();
        if (existingWeatherNameOpt.isPresent()) {
            updatedWeatherName.setId(existingWeatherNameOpt.get().getId());
        } else {
            weatherNameService.createEntity(updatedWeatherName);
        }
        updatedDay.setWeatherName(updatedWeatherName);

        // 3. Обновление погодных параметров без изменений
        weatherParametersService.updateEntity(updatedDay.getDayWeather());
        weatherParametersService.updateEntity(updatedDay.getNightWeather());

        // 4. Проверка уникальности даты и города
        boolean isDuplicate = dayService.getAllEntities().stream()
                .anyMatch(day -> day.getDate().equals(updatedDay.getDate()) &&
                        Objects.equals(day.getLocation().getTown(), updatedDay.getLocation().getTown()) &&
                        day.getId() != updatedDay.getId()); // Исключаем текущую запись
        if (isDuplicate) {
            return new Response(ResponseStatus.ERROR, "Запись с такой датой и городом уже существует", "");
        }

        // Обновление записи дня
        dayService.updateEntity(updatedDay);

        // Возврат успешного ответа
        return new Response(ResponseStatus.OK, "Данные успешно обновлены", gson.toJson(updatedDay));
    }

    private Response handleDeleteDay(Request request) throws SQLException {
        Day deletedDay = gson.fromJson(request.getRequestMessage(), Day.class);
        // Проверяем, существует ли запись
        Optional<Day> existingDay = dayService.getEntityById(deletedDay.getId());
        if (!existingDay.isPresent()) {
            return new Response(ResponseStatus.ERROR, "День не найден", "");
        }
        dayService.deleteEntity(deletedDay);
        weatherParametersService.deleteEntity(deletedDay.getDayWeather());
        weatherParametersService.deleteEntity(deletedDay.getNightWeather());
        return new Response(ResponseStatus.OK, "Данные успешно удалены", "");
    }

    private Response handleGetDashboard(Request request) throws SQLException {
        User user = gson.fromJson(request.getRequestMessage(), User.class);

        Dashboard existingDashboard = dashboardService.getAllEntities().stream()
                .filter(x -> x.getUser().getId() == user.getId())
                .max(Comparator.comparing(Dashboard::getId))
                .orElse(null);

        if (existingDashboard != null) {
            return new Response(ResponseStatus.OK, "Данные найдены", gson.toJson(existingDashboard));
        } else {
            return new Response(ResponseStatus.ERROR, "Данные не найдены", null);
        }
    }

    private Response handleAddUserDashboard(Request request) throws SQLException {
        Dashboard current = gson.fromJson(request.getRequestMessage(), Dashboard.class);
        if (current == null) {
            return new Response(ResponseStatus.ERROR, "Данные не получены", "");
        }
        Dashboard existingDashboard = dashboardService.getAllEntities().stream()
                .filter(x -> x.getUser().getId() == current.getUser().getId() &&
                        x.getStartDate().getDate().equals(current.getStartDate().getDate()) &&
                        x.getStartDate().getLocation().equals(current.getStartDate().getLocation()) &&
                        x.getEndDate().getDate().equals(current.getEndDate().getDate()))

                .findAny()
                .orElse(null);

        if (existingDashboard == null) {

            Day endDay= new Day();
            endDay.setDate(current.getEndDate().getDate());
            endDay.setLocation(current.getEndDate().getLocation());
            Optional<Day> matchingDay = dayService.getAllEntities().stream()
                    .filter(day -> day.getDate().equals(endDay.getDate())
                            && day.getLocation().getTown().equals(endDay.getLocation().getTown()))
                    .findFirst();

            if (matchingDay.isPresent()) {

                endDay.setId(matchingDay.get().getId());
                endDay.setDayWeather(matchingDay.get().getDayWeather());
                endDay.setNightWeather(matchingDay.get().getNightWeather());
                endDay.setWeatherName(matchingDay.get().getWeatherName());
            } else{
                return new Response(ResponseStatus.ERROR, "Ошибка сохранения", "");
            }


            current.setEndDate(endDay);
            dashboardService.createEntity(current);
            return new Response(ResponseStatus.OK, "Данные сохранены", "");
        } else
            return new Response(ResponseStatus.ERROR, "Данные уже сохранены", "");

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
