package Controller.User;


import Controller.Calendar.CalendarData;
import Controller.File.AnaliticsData;
import Controller.File.Regions;
import Controller.Validation.ErrorStrategy;
import Controller.Validation.LabelErrorStrategy;
import Controller.Validation.LogErrorStrategy;
import Controller.Validation.Validation;
import Enums.Months;
import Service.DashboardService;
import Service.DayService;
import Service.LocationService;
import Service.UserService;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.io.*;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import Models.Entities.*;
import Utilities.ClientSocket;
import com.google.gson.Gson;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class UserMenu implements Initializable {

    @FXML
    private Button calendarButton, exit,
            personalAccount, Change, buttonSignUp, save, back;
    @FXML
    private AnchorPane todayWeatherPanel, panel0, panel1, personalPanel, todayWeatherPanel2;
    @FXML
    private Label labelCurrentDate, labelError, labelUser, labelMessage, textC1, textC2,
            textF1, textF2, textKMH1, textKMH2, textMM1, textMM2, textMS1, textMS2,
            texthPa1, texthPa2, xHumidity1, xHumidity2, xPressure1, xPressure2,
            xRain1, xRain2, xTemperatura1, xTemperatura2, xWeatherName, xWind1, xWind2,
            textC3, textC4, textF3, textF4, textKMH3, textKMH4, textMM3, textMM4, textMS3, textMS4,
            texthPa3, texthPa4, xHumidity3, xHumidity4, xPressure3, xPressure4,
            xRain3, xRain4, xTemperatura3, xTemperatura4, xWeatherName2, xWind3, xWind4, labelCalendarDate;

    @FXML
    private TextField textfieldLogin, textfieldPhone;
    @FXML
    private PasswordField passwordfieldPassword, passwordfieldConfirmPassword;

    @FXML
    private MenuButton region, regionCalendar, monthMenuBtn, yearMenuBtn;
    @FXML
    private RadioButton C, F, hPa, km, m, mmHg;
    @FXML
    private ToggleGroup Pressure, Speed, Temperature;
    @FXML
    private ToggleButton threeDays, week, monthPeriod;
    @FXML
    private CheckBox sendWeather;
    @FXML
    private GridPane daysGrid;
    @FXML
    private AreaChart<String, Number> temperatureChart;


    private final Calendar calendar = Calendar.getInstance();
    private boolean monthChosen = false, yearChosen = false, regionChosen = false;
    private final int BUTTONS_IN_A_ROW = 7;
    private final int LINES = 6;
    private int currentChartDays = 0;

    private Button[] dayButtons;

    private User currentUser;
    private List<Location> regions;
    private LocalDate today, calendarDate, chartDate;
    private Day todayWeather, calendarWeather, rowDay;
    private String selectedTown, selectedTownCalendar, selectedTownChart;
    private ObservableList<Day> tableDays;

    private LocationService locationService = new LocationService();
    private DayService dayService = new DayService();
    private UserService userService = new UserService();
    private DashboardService dashboardService = new DashboardService();




    @Override
    public void initialize(URL location, ResourceBundle resources) {
        currentUser = ClientSocket.getInstance().getUser();
        labelUser.setText(currentUser.getLogin());
        today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM", new Locale("ru"));
        String formattedDate = today.format(formatter);
        labelCurrentDate.setText(formattedDate);
        panel0.setVisible(true);

        initMenuButtons();
        initWeatherTable();
    }


    public void switchForm(ActionEvent event) {
        if (event.getSource() == calendarButton) {
            labelError.setVisible(false);
            panel1.setVisible(true);
            panel0.setVisible(false);
            personalPanel.setVisible(false);
            calendarButton.getStyleClass().add("button-menu:hover");
        } else if (event.getSource() == personalAccount) {
            if (!personalPanel.isVisible()) {
                //initPersonalAccount();
                personalPanel.setVisible(true); // Делаем видимой
            } else {
                personalPanel.setVisible(false); // Иначе скрываем
                labelMessage.setVisible(false);
            }
        } else if (event.getSource() == back) {
            labelError.setVisible(false);
            panel0.setVisible(true);
            panel1.setVisible(false);
            personalPanel.setVisible(false);
        }
    }

    public void initRegion() {
        regions = locationService.getRegions(labelError);
        if (regions.isEmpty())
            new LogErrorStrategy().handleError("Ошибка получения регионов");
    }

    public void initMenuButtons() {
        initRegion(); // Инициализация регионов

        for (Location temp : regions) {
            String displayText = temp.getTown() + ", " + temp.getCountry();

            // Создаем пункт меню для region
            MenuItem menuItem = new MenuItem(displayText);
            menuItem.getStyleClass().add("menu-item");
            menuItem.setOnAction(event -> handleRegionSelection(displayText, temp.getTown()));
            region.getItems().add(menuItem);

            // Создаем пункт меню для regionCalendar
            MenuItem menuItem2 = new MenuItem(displayText);
            menuItem2.getStyleClass().add("menu-item");
            menuItem2.setOnAction(event -> handleRegionCalendarSelection(displayText, temp.getTown()));
            regionCalendar.getItems().add(menuItem2);

        }
    }

    public void updateMenuButtons() {

        // Получаем список городов из tableDays
        List<String> currentCities = tableDays.stream()
                .map(day -> day.getLocation().getTown()) // Извлекаем названия городов
                .distinct() // Убираем дубликаты
                .collect(Collectors.toList());

        // Получаем список городов из regions
        List<String> regionCities = regions.stream()
                .map(region -> region.getTown()) // Извлекаем названия городов из regions
                .distinct()
                .collect(Collectors.toList());

        // Если списки городов отличаются
        if (!currentCities.equals(regionCities)) {
            regions.clear();
            initRegion(); // Перезагружаем список регионов с сервера
            region.getItems().clear();
            regionCalendar.getItems().clear();
            initMenuButtons(); // Перезагружаем кнопки меню
        }

    }

    private void handleRegionSelection(String displayText, String town) {
        region.setText(displayText);
        selectedTown = town;
        initUserWeather();
    }


    public void handleRegionCalendarSelection(String displayText, String town) {
        regionCalendar.setText(displayText);
        selectedTownCalendar = town;
        regionChosen = true;
        updateCalendar();

    }


    public void initWeatherTable() {
        List<Day> dayList = dayService.getAllDays(labelError);
        if (dayList != null) {
            // Если данные успешно получены, заполняем таблицу
            tableDays = FXCollections.observableArrayList(dayList);
        } else
            new LogErrorStrategy().handleError("Ошибка получения дней");
    }


    @FXML
    void exit_Pressed(ActionEvent event) throws IOException {
        exit();
    }

    public void exit() throws IOException {
        User temp = userService.logout(currentUser, labelError);
        if(temp!=null){
            ClientSocket.getInstance().setUser(temp);
            ClientSocket.resetConnection();
            Stage stage = (Stage) exit.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/Login.fxml"));
            Scene newScene = new Scene(root);
            stage.setScene(newScene);
            stage.centerOnScreen();
        }
        else
            new LogErrorStrategy().handleError("Ошибка выхода");
    }


    @FXML
    void back_Pressed(ActionEvent event) {
        updateMenuButtons();
        switchForm(event);
    }

    public void initUserWeather() {

        Location tempL = new Location();
        Day tempDay = new Day();
        tempL.setTown(selectedTown);
        tempDay.setLocation(tempL);
        tempDay.setDate(java.sql.Date.valueOf(today));
        todayWeather = dayService.getSelectedDayWeather(tempDay,labelError);
        if(today!=null)
        {
            initWeatherFields();
            todayWeatherPanel.setVisible(true);
            labelError.setVisible(false);
        }
        else{
            todayWeatherPanel.setVisible(false);
            new LogErrorStrategy().handleError("Ошибка поиска выбранной погоды");
        }
    }

    public void initWeatherFields() {
        xWeatherName.setText(todayWeather.getWeatherName().getName());
        if (currentUser.getPersonalSettings().getTemperature().equals("F")) {
            double x = todayWeather.getDayWeather().getTemperature() * 9 / 5 + 32;
            xTemperatura1.setText(String.valueOf(x));
            x = todayWeather.getNightWeather().getTemperature() * 9 / 5 + 32;
            xTemperatura2.setText(String.valueOf(x));
            textF1.setVisible(true);
            textF2.setVisible(true);
            textC1.setVisible(false);
            textC2.setVisible(false);
        } else {
            xTemperatura1.setText(todayWeather.getDayWeather().getTemperature().toString());
            xTemperatura2.setText(todayWeather.getNightWeather().getTemperature().toString());
            textC1.setVisible(true);
            textC2.setVisible(true);
            textF1.setVisible(false);
            textF2.setVisible(false);
        }
        if (currentUser.getPersonalSettings().getPressure().equals("гПа")) {
            double x = todayWeather.getDayWeather().getPressure() * 1.33322;
            xPressure1.setText(String.format("%.2f", x));
            x = todayWeather.getNightWeather().getPressure() * 1.33322;
            xPressure2.setText(String.format("%.2f", x));
            texthPa1.setVisible(true);
            texthPa2.setVisible(true);
            textMM1.setVisible(false);
            textMM2.setVisible(false);
        } else {
            xPressure1.setText(todayWeather.getDayWeather().getPressure().toString());
            xPressure2.setText(todayWeather.getNightWeather().getPressure().toString());
            textMM1.setVisible(true);
            textMM2.setVisible(true);
            texthPa1.setVisible(false);
            texthPa2.setVisible(false);
        }
        if (currentUser.getPersonalSettings().getSpeed().equals("км/ч")) {
            double x = todayWeather.getDayWeather().getWindSpeed() * 3.6;
            xWind1.setText(String.valueOf(x));
            x = todayWeather.getNightWeather().getWindSpeed() * 3.6;
            xWind2.setText(String.valueOf(x));
            textKMH1.setVisible(true);
            textKMH2.setVisible(true);
            textMS1.setVisible(false);
            textMS2.setVisible(false);
        } else {
            xWind1.setText(todayWeather.getDayWeather().getWindSpeed().toString());
            xWind2.setText(todayWeather.getNightWeather().getWindSpeed().toString());
            textMS1.setVisible(true);
            textMS2.setVisible(true);
            textKMH1.setVisible(false);
            textKMH2.setVisible(false);
        }
        xHumidity1.setText(todayWeather.getDayWeather().getHumidity().toString());
        xHumidity2.setText(todayWeather.getNightWeather().getHumidity().toString());
        xRain1.setText(todayWeather.getDayWeather().getPrecipitation().toString());
        xRain2.setText(todayWeather.getNightWeather().getPrecipitation().toString());

    }


    //для настроек пользователя

    public void updateUser() {
        ErrorStrategy errorStrategy = new LabelErrorStrategy(labelMessage);
        if (!passwordfieldPassword.getText().equals(passwordfieldConfirmPassword.getText())) {
            errorStrategy.handleError("Пароли не совпадают");
            return;
        }
        if (textfieldLogin.getText().isEmpty() ||
                passwordfieldPassword.getText().isEmpty() ||
                passwordfieldConfirmPassword.getText().isEmpty() ||
                textfieldPhone.getText().isEmpty()) {
            errorStrategy.handleError("Не все поля заполнены.");
            return;
        }
        User user = new User();
        user.setId(currentUser.getId());
        user.setRole(currentUser.getRole());
        // Валидация логина
        Validation loginValidator = new Validation(errorStrategy);
        String tempLogin = loginValidator.validateLogin(textfieldLogin.getText());
        if ("error".equals(tempLogin)) return;
        user.setLogin(tempLogin);

        if (!passwordfieldPassword.getText().equals("11111")) {
            String pass = passwordfieldPassword.getText();

            byte[] temp = user.getHash(pass);
            user.setPassword(temp);
        } else {
            user.setPassword(currentUser.getPassword());
        }
        PersonalSettings settings = new PersonalSettings();
        settings.setId(currentUser.getPersonalSettings().getId());

        // Валидация телефона
        String tempPhone = loginValidator.validatePhoneNumber(textfieldPhone.getText());
        if ("error".equals(tempPhone)) return;
        settings.setPhone(tempPhone);

        String temperature = ((RadioButton) Temperature.getSelectedToggle()).getText();
        if (temperature.equals("°F"))
            settings.setTemperature("F");
        else
            settings.setTemperature("C");
        String pressure = ((RadioButton) Pressure.getSelectedToggle()).getText();
        String speed = ((RadioButton) Speed.getSelectedToggle()).getText();
        settings.setPressure(pressure);
        settings.setSpeed(speed);
        settings.setNotifications(sendWeather.isSelected());

        user.setPersonalSettings(settings);

        if (!user.equals(currentUser)) {
            user=userService.update(user,labelMessage);
            if(user!=null) {
                ClientSocket.getInstance().setUser(user);
                errorStrategy.handleError("Данные изменены!");
                currentUser = ClientSocket.getInstance().getUser();

                //заново проинициализировать поля
                labelUser.setText(currentUser.getLogin());
                if (selectedTown != null) {
                    initWeatherFields();

                }
                if (selectedTownCalendar != null) {
                    initWeatherFields2();
                    updateCalendar();
                    updateChart();
                }
            }
            else
                new LogErrorStrategy().handleError("Ошибка редактирования настроек");
        } else {
            errorStrategy.handleError("Введите новые данные!");
        }
    }

    public void initPersonalAccount() {
        if (currentUser != null) {
            // Инициализируем логин
            textfieldLogin.setText(currentUser.getLogin());
            passwordfieldPassword.setText("11111");
            passwordfieldConfirmPassword.setText("11111");
            // Инициализируем личные настройки пользователя
            PersonalSettings settings = currentUser.getPersonalSettings();
            if (settings != null) {
                // Телефон
                textfieldPhone.setText(settings.getPhone());

                // Температура
                if ("F".equals(settings.getTemperature())) {
                    F.setSelected(true);
                } else {
                    C.setSelected(true);
                }

                // Давление
                if ("гПа".equals(settings.getPressure())) {
                    hPa.setSelected(true);
                } else {
                    mmHg.setSelected(true);
                }

                // Скорость ветра
                if ("км/ч".equals(settings.getSpeed())) {
                    km.setSelected(true);
                } else {
                    m.setSelected(true);
                }
                if (settings.getNotifications())
                    sendWeather.setSelected(true);
                else
                    sendWeather.setSelected(false);
            }
        }
    }

    @FXML
    void change_Pressed(ActionEvent actionEvent) {
        updateUser();
    }

    @FXML
    void personalAccount_Pressed(ActionEvent event) {
        initPersonalAccount();
        switchForm(event);
    }

    @FXML
    void calendarButton_Pressed(ActionEvent event) {
        switchForm(event);
        initChart();
    }


    @FXML
    void monthPressed(ActionEvent event) {
        MenuItem btn = (MenuItem) event.getSource();
        monthMenuBtn.setText(btn.getText()); // Устанавливаем текст выбранного месяца на кнопку
        try {

            String monthName = btn.getText();
            Months selectedMonth = Months.valueOf(monthName);
            calendar.set(Calendar.MONTH, selectedMonth.getMonthNumber() - 1); // Устанавливаем выбранный месяц (учитываем, что в Calendar месяцы начинаются с 0)
            monthChosen = true;
            updateCalendar(); // Обновляем календарь
        } catch (NumberFormatException e) {
            ErrorStrategy errorStrategy = new LabelErrorStrategy(labelError);
            errorStrategy.handleError("Неверный формат месяца: " + btn.getText());
        }
    }


    @FXML
    void yearPressed(ActionEvent event) {
        MenuItem btn = (MenuItem) event.getSource();
        yearMenuBtn.setText(btn.getText()); // Устанавливаем текст выбранного года на кнопку
        try {
            int year = Integer.parseInt(btn.getText());
            calendar.set(Calendar.YEAR, year); // Устанавливаем выбранный год
            yearChosen = true;
            updateCalendar(); // Обновляем календарь
        } catch (NumberFormatException e) {
            ErrorStrategy errorStrategy = new LabelErrorStrategy(labelError);
            errorStrategy.handleError("Неверный формат года: " + btn.getText());
        }
    }

    private void updateCalendar() {
        if (!monthChosen || !yearChosen || !regionChosen) return; // Проверяем, что месяц и год выбраны

        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH); // Определяем количество дней в месяце
        createDaysButtons(daysInMonth); // Создаем кнопки для дней месяца
    }

    private void createDaysButtons(int daysInMonth) {
        calendar.set(Calendar.DAY_OF_MONTH, 1); // Устанавливаем первый день месяца
        calendar.setFirstDayOfWeek(Calendar.MONDAY); // Устанавливаем первый день недели как понедельник
        int firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY; // Определяем день недели первого числа месяца

        if (firstDayOfWeek < 0) {
            firstDayOfWeek += 7; // Если день недели смещен из-за понедельника как первого дня недели
        }

        dayButtons = new Button[daysInMonth + firstDayOfWeek]; // Размер массива включает "пустые" ячейки для смещения
        daysGrid.getChildren().clear(); // Очищаем сетку перед созданием кнопок

        for (int i = 0; i < dayButtons.length; i++) {
            if (i < firstDayOfWeek) {
                // Добавляем пустые ячейки для смещения (дни до первого числа месяца)
                daysGrid.add(new Label(""), i % BUTTONS_IN_A_ROW, i / BUTTONS_IN_A_ROW);
            } else {
                int day = i - firstDayOfWeek + 1; // Рассчитываем текущий день
                //Button dayButton = new Button(String.valueOf(day)); // Создаем кнопку с текстом для текущего дня

                Button dayButton = new Button(); // Создаем кнопку

                // Устанавливаем текст для кнопки
                String buttonText = setButtonCalendarText(day, calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR));
                dayButton.setText(buttonText);

                dayButton.setPrefSize(daysGrid.getPrefWidth() / BUTTONS_IN_A_ROW, daysGrid.getPrefHeight() / LINES); // Устанавливаем размер кнопки
                dayButton.setOnAction(this::dayPressed); // Устанавливаем обработчик события для кнопки
                daysGrid.add(dayButton, i % BUTTONS_IN_A_ROW, i / BUTTONS_IN_A_ROW); // Добавляем кнопку в сетку
                dayButton.getStyleClass().add("grid-pane-item");
                dayButtons[i] = dayButton; // Сохраняем кнопку в массив
            }
        }


    }

    public String setButtonCalendarText(int day, int month, int year) {
        for (Day tableDay : tableDays) { // tableDays — это ваш ObservableList<Day>
            // Преобразуем `Date` в `LocalDate`
            LocalDate localDate = tableDay.getDate().toLocalDate();
            String dayTemp, nightTemp;
            // Сравнение даты и города
            if (localDate.equals(LocalDate.of(year, month, day)) &&
                    tableDay.getLocation().getTown().equals(selectedTownCalendar)) {

                if (currentUser.getPersonalSettings().getTemperature().equals("F")) {
                    double temp = tableDay.getDayWeather().getTemperature() * 9 / 5 + 32;
                    dayTemp = Double.toString(temp);
                    temp = tableDay.getNightWeather().getTemperature() * 9 / 5 + 32;
                    nightTemp = Double.toString(temp);
                } else {
                    dayTemp = tableDay.getDayWeather().getTemperature().toString();
                    nightTemp = tableDay.getNightWeather().getTemperature().toString();
                }

                // Возвращаем текст кнопки
                return day + "\n" + dayTemp + "/" + nightTemp;
            }
        }

        // Если данных нет, возвращаем просто день
        return String.valueOf(day);
    }


    private void dayPressed(ActionEvent event) {
        Button dayBtn = (Button) event.getSource(); // Получаем кнопку, вызвавшую событие
        try {
            // Разделяем текст на строку и получаем первый элемент, который будет числом дня
            String[] buttonText = dayBtn.getText().split("\n");
            int day = Integer.parseInt(buttonText[0].trim()); // Определяем выбранный день

            calendar.set(Calendar.DAY_OF_MONTH, day); // Устанавливаем день в календаре

            int dayInMonth = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH) + 1; // Преобразуем месяц из 0-базового формата
            int year = calendar.get(Calendar.YEAR);
            calendarDate = LocalDate.of(year, month, dayInMonth); // Устанавливаем дату

            initSelectedWeather();
            updateChart();

        } catch (NumberFormatException e) {
            ErrorStrategy errorStrategy = new LabelErrorStrategy(labelError);
            errorStrategy.handleError("Неверное значение дня.");
        }
    }

    public void initSelectedWeather() {
        Location tempL = new Location();
        Day tempDay = new Day();
        tempL.setTown(selectedTownCalendar);
        tempDay.setLocation(tempL);
        tempDay.setDate(java.sql.Date.valueOf(calendarDate));
        calendarWeather=dayService.getSelectedDayWeather(tempDay,labelError);
        if(calendarWeather!=null){
            initWeatherFields2();
            todayWeatherPanel2.setVisible(true);
            labelError.setVisible(false);
        } else {
            todayWeatherPanel2.setVisible(false);
            new LogErrorStrategy().handleError("Ошибка поиска выбранной погоды");
        }

    }

    public void initWeatherFields2() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", new Locale("ru"));
        String formattedDate = calendarDate.format(formatter);
        labelCalendarDate.setText(formattedDate);
        xWeatherName2.setText(calendarWeather.getWeatherName().getName());
        if (currentUser.getPersonalSettings().getTemperature().equals("F")) {
            double x = calendarWeather.getDayWeather().getTemperature() * 9 / 5 + 32;
            xTemperatura3.setText(String.valueOf(x));
            x = calendarWeather.getNightWeather().getTemperature() * 9 / 5 + 32;
            xTemperatura4.setText(String.valueOf(x));
            textF3.setVisible(true);
            textF4.setVisible(true);
            textC3.setVisible(false);
            textC4.setVisible(false);
        } else {
            xTemperatura3.setText(calendarWeather.getDayWeather().getTemperature().toString());
            xTemperatura4.setText(calendarWeather.getNightWeather().getTemperature().toString());
            textC3.setVisible(true);
            textC4.setVisible(true);
            textF3.setVisible(false);
            textF4.setVisible(false);
        }
        if (currentUser.getPersonalSettings().getPressure().equals("гПа")) {
            double x = calendarWeather.getDayWeather().getPressure() * 1.33322;
            xPressure3.setText(String.format("%.2f", x));
            x = calendarWeather.getNightWeather().getPressure() * 1.33322;
            xPressure4.setText(String.format("%.2f", x));
            texthPa3.setVisible(true);
            texthPa4.setVisible(true);
            textMM3.setVisible(false);
            textMM4.setVisible(false);
        } else {
            xPressure3.setText(calendarWeather.getDayWeather().getPressure().toString());
            xPressure4.setText(calendarWeather.getNightWeather().getPressure().toString());
            textMM3.setVisible(true);
            textMM4.setVisible(true);
            texthPa3.setVisible(false);
            texthPa4.setVisible(false);
        }
        if (currentUser.getPersonalSettings().getSpeed().equals("км/ч")) {
            double x = calendarWeather.getDayWeather().getWindSpeed() * 3.6;
            xWind3.setText(String.valueOf(x));
            x = calendarWeather.getNightWeather().getWindSpeed() * 3.6;
            xWind4.setText(String.valueOf(x));
            textKMH3.setVisible(true);
            textKMH4.setVisible(true);
            textMS3.setVisible(false);
            textMS4.setVisible(false);
        } else {
            xWind3.setText(calendarWeather.getDayWeather().getWindSpeed().toString());
            xWind4.setText(calendarWeather.getNightWeather().getWindSpeed().toString());
            textMS3.setVisible(true);
            textMS4.setVisible(true);
            textKMH3.setVisible(false);
            textKMH4.setVisible(false);
        }
        xHumidity3.setText(calendarWeather.getDayWeather().getHumidity().toString());
        xHumidity4.setText(calendarWeather.getNightWeather().getHumidity().toString());
        xRain3.setText(calendarWeather.getDayWeather().getPrecipitation().toString());
        xRain4.setText(calendarWeather.getNightWeather().getPrecipitation().toString());

    }

    @FXML
    void threeDays_Pressed(ActionEvent event) {
        currentChartDays = 2;
        updateChart();
    }

    @FXML
    void week_Pressed(ActionEvent event) {
        currentChartDays = 6;
        updateChart();
    }

    @FXML
    void monthPeriod_Pressed(ActionEvent event) {
        currentChartDays = 30;
        updateChart();
    }


    public void updateChart() {
        ErrorStrategy errorStrategy = new LabelErrorStrategy(labelMessage);
        if (calendarDate != null && selectedTownCalendar != null) {
            // Определяем начальную и конечную дату для графика
            LocalDate chartDateStart = calendarDate;
            LocalDate chartDateEnd = chartDateStart.plusDays(currentChartDays);

            // Фильтрация данных по указанному периоду и городу
            ObservableList<Day> filteredDays = tableDays.filtered(day -> {
                // Преобразуем java.sql.Date в LocalDate
                LocalDate dayDate = day.getDate().toLocalDate(); // преобразуем в LocalDate

                // Фильтруем по диапазону дат и городу
                return !dayDate.isBefore(chartDateStart) &&
                        !dayDate.isAfter(chartDateEnd) &&
                        day.getLocation().getTown().equals(selectedTownCalendar);
            });

            // Проверяем, есть ли данные для графика
            if (filteredDays.isEmpty()) {
                // Если данных нет, выводим ошибку

                errorStrategy.handleError("Нет данных для выбранного периода и города.");
                return;
            }

            // Создаем оси для графика
            CategoryAxis xAxis = new CategoryAxis(); // Используем CategoryAxis для отображения дат
            NumberAxis yAxis = new NumberAxis();
            xAxis.setLabel("Дата");


            // Создаем график
            AreaChart<String, Number> areaChart = new AreaChart<>(xAxis, yAxis);
            areaChart.setTitle("Температура за период");

            // Создаем серии для дневной и ночной температуры
            XYChart.Series<String, Number> daySeries = new XYChart.Series<>();
            daySeries.setName("Дневная температура");

            XYChart.Series<String, Number> nightSeries = new XYChart.Series<>();
            nightSeries.setName("Ночная температура");

            if (currentUser.getPersonalSettings().getTemperature().equals("F")) {
                yAxis.setLabel("Т, °F");

                // Добавляем данные в серии
                for (Day day : filteredDays) {
                    LocalDate date = day.getDate().toLocalDate();
                    double dayTemp = day.getDayWeather().getTemperature() * 9 / 5 + 32;
                    double nightTemp = day.getNightWeather().getTemperature() * 9 / 5 + 32;

                    // Преобразуем дату в строку
                    String dateStr = date.format(DateTimeFormatter.ofPattern("d MMM yyyy"));

                    // Добавляем точки на график
                    daySeries.getData().add(new XYChart.Data<>(dateStr, dayTemp));
                    nightSeries.getData().add(new XYChart.Data<>(dateStr, nightTemp));

                }
            } else {
                yAxis.setLabel("Т, °C");

                // Добавляем данные в серии
                for (Day day : filteredDays) {
                    LocalDate date = day.getDate().toLocalDate();
                    double dayTemp = day.getDayWeather().getTemperature();
                    double nightTemp = day.getNightWeather().getTemperature();

                    // Преобразуем дату в строку
                    String dateStr = date.format(DateTimeFormatter.ofPattern("d MMM yyyy"));

                    // Добавляем точки на график
                    daySeries.getData().add(new XYChart.Data<>(dateStr, dayTemp));
                    nightSeries.getData().add(new XYChart.Data<>(dateStr, nightTemp));

                }
            }

            // Добавляем серии в график
            temperatureChart.getData().clear(); // Убираем старые данные с графика
            temperatureChart.getData().add(daySeries);  // Добавляем новые серии
            temperatureChart.getData().add(nightSeries);

            daySeries.getNode().getStyleClass().add("chart-day-temperature");
            nightSeries.getNode().getStyleClass().add("chart-night-temperature");
        } else {
            errorStrategy.handleError("Сначала выберите город и дату");
            return;
        }
    }


    public void initChart() {
        Dashboard initDashboard =dashboardService.getDashboard(currentUser, labelError);
        if(initDashboard!=null)
        {
            selectedTownCalendar = initDashboard.getStartDate().getLocation().getTown();
            calendarDate = initDashboard.getStartDate().getDate().toLocalDate();
            calendarWeather = initDashboard.getStartDate();
            LocalDate startDate = initDashboard.getStartDate().getDate().toLocalDate();
            LocalDate endDate = initDashboard.getEndDate().getDate().toLocalDate();
            currentChartDays = (int) ChronoUnit.DAYS.between(startDate, endDate);
            initSelectedWeather();
            updateChart();
        }
        else new LogErrorStrategy().handleError("Ошибка загрузки dashboard");
    }

    @FXML
    void savePressed(ActionEvent event) {
        saveDashboard();

    }

    public void saveDashboard()
    {
        ErrorStrategy errorStrategy = new LabelErrorStrategy(labelMessage);
        if (calendarWeather == null || currentChartDays == 0) {
            errorStrategy.handleError("Сначала выберите день и период");

        }
        Dashboard newDashboard = new Dashboard();
        newDashboard.setUser(currentUser);
        newDashboard.setStartDate(calendarWeather);
        LocalDate startLocalDate = calendarWeather.getDate().toLocalDate(); // Преобразование в LocalDate
        LocalDate endLocalDate = startLocalDate.plusDays(currentChartDays); // Добавляем дни
        java.sql.Date endDate = Date.valueOf(endLocalDate);
        Day end = new Day();
        end.setDate(endDate);
        end.setLocation(newDashboard.getStartDate().getLocation());
        newDashboard.setEndDate(end);
        String result = dashboardService.addUserDashboard(newDashboard, labelError);
        if(result.equals("ERROR"))
            new LogErrorStrategy().handleError("Ошибка сохранения dashboard");
    }



}

