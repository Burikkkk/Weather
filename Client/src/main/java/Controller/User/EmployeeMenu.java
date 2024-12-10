package Controller.User;

import Controller.Calendar.CalendarData;
import Controller.Validation.ErrorStrategy;
import Controller.Validation.LabelErrorStrategy;
import Controller.Validation.LogErrorStrategy;
import Controller.Validation.Validation;
import Enums.Months;
import Service.DashboardService;
import Service.DayService;
import Service.LocationService;
import Service.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import java.io.FileReader;
import java.io.FileWriter;
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
import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class EmployeeMenu implements Initializable {

    @FXML
    private Button add, analitics, calendarButton, clear, delete, edit, editDB, exit,
            personalAccount, Change, buttonSignUp, firstPage, save, saveInFile;
    @FXML
    private TableColumn<Day, String> columnCountry, columnTown, columnWeatherName;
    @FXML
    private TableColumn<Day, Date> columnDate;
    @FXML
    private TableColumn<Day, Integer> columnHumidity1, columnHumidity2, columnPressure1, columnPressure2;
    @FXML
    private TableColumn<Day, Double> columnPrecipitation1, columnPrecipitation2,
            columnTemperature1, columnTemperature2,
            columnWind1, columnWind2;
    @FXML
    private AnchorPane todayWeatherPanel, pane1, panel0, panel1, panel2, panel3, personalPanel, todayWeatherPanel2;
    @FXML
    private Label labelCurrentDate, labelError, labelUser, labelMessage, textC1, textC2,
            textF1, textF2, textKMH1, textKMH2, textMM1, textMM2, textMS1, textMS2,
            texthPa1, texthPa2, xHumidity1, xHumidity2, xPressure1, xPressure2,
            xRain1, xRain2, xTemperatura1, xTemperatura2, xWeatherName, xWind1, xWind2,
            textC3, textC4, textF3, textF4, textKMH3, textKMH4, textMM3, textMM4, textMS3, textMS4,
            texthPa3, texthPa4, xHumidity3, xHumidity4, xPressure3, xPressure4,
            xRain3, xRain4, xTemperatura3, xTemperatura4, xWeatherName2, xWind3, xWind4, labelCalendarDate;
    @FXML
    private TableView<Day> tableWeather;
    @FXML
    private TextField textFieldCounty, textFieldTown, textFieldWeather, textFieldSearch,
            textFieldHumidity1, textFieldHumidity2, textFieldPrecipitation1, textFieldPrecipitation2,
            textFieldPressure1, textFieldPressure2, textFieldTemperature1, textFieldTemperature2,
            textFieldWind1, textFieldWind2, textfieldLogin, textfieldPhone;
    @FXML
    private PasswordField passwordfieldPassword, passwordfieldConfirmPassword;

    @FXML
    private DatePicker textFieldDate;
    @FXML
    private MenuButton region, regionFilter, regionCalendar, monthMenuBtn, yearMenuBtn, monthMenuBtn2, yearMenuBtn2, regionChart;
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
    @FXML
    private TextArea textArea;
    @FXML
    private Label chosenDateLabel, minText, maxText, averageText;
    @FXML
    private LineChart<String, Number> pressureChart, windSpeedChart, tempChart;
    @FXML
    private BarChart<String, Number> statisticChart;

    private final Calendar calendar = Calendar.getInstance();
    private boolean monthChosen = false, yearChosen = false, regionChosen = false,
            monthChosen2 = false, yearChosen2 = false, regionChosen2 = false;
    private final int BUTTONS_IN_A_ROW = 7;
    private final int LINES = 6;
    private int currentChartDays = 0;

    private Button[] dayButtons;
    private final CalendarData calendarData = new CalendarData();

    private User currentUser;
    private List<Location> regions;
    private LocalDate today, calendarDate, chartDate;
    private Day todayWeather, calendarWeather, rowDay;
    private String selectedTown, selectedTownCalendar, selectedTownChart;
    private ObservableList<Day> tableDays;
    private Map<String, Object> weatherData = new HashMap<>();

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

        tableWeather.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        columnDate.setCellValueFactory(new PropertyValueFactory<>("date"));

        columnTemperature1.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getDayWeather().getTemperature() != null ?
                        cellData.getValue().getDayWeather().getTemperature() : 0));
        columnPressure1.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getDayWeather().getPressure() != null ?
                        cellData.getValue().getDayWeather().getPressure() : 0));
        columnHumidity1.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getDayWeather().getHumidity() != null ?
                        cellData.getValue().getDayWeather().getHumidity() : 0));
        columnPrecipitation1.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getDayWeather().getPrecipitation() != null ?
                        cellData.getValue().getDayWeather().getPrecipitation() : 0));
        columnWind1.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getDayWeather().getWindSpeed() != null ?
                        cellData.getValue().getDayWeather().getWindSpeed() : 0));


        columnTemperature2.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getNightWeather().getTemperature() != null ?
                        cellData.getValue().getNightWeather().getTemperature() : 0));
        columnPressure2.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getNightWeather().getPressure() != null ?
                        cellData.getValue().getNightWeather().getPressure() : 0));
        columnHumidity2.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getNightWeather().getHumidity() != null ?
                        cellData.getValue().getNightWeather().getHumidity() : 0));
        columnPrecipitation2.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getNightWeather().getPrecipitation() != null ?
                        cellData.getValue().getNightWeather().getPrecipitation() : 0));
        columnWind2.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getNightWeather().getWindSpeed() != null ?
                        cellData.getValue().getNightWeather().getWindSpeed() : 0));

        columnWeatherName.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getWeatherName().getName() != null ?
                        cellData.getValue().getWeatherName().getName() : ""));
        columnTown.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getLocation().getTown() != null ?
                        cellData.getValue().getLocation().getTown() : ""));
        columnCountry.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getLocation().getCountry() != null ?
                        cellData.getValue().getLocation().getCountry() : ""));


        initMenuButtons();
        initWeatherTable();


    }


    public void switchForm(ActionEvent event) {
        if (event.getSource() == calendarButton) {
            labelError.setVisible(false);
            panel1.setVisible(true);
            panel2.setVisible(false);
            panel3.setVisible(false);
            panel0.setVisible(false);
            personalPanel.setVisible(false);
            calendarButton.getStyleClass().add("button-menu:hover");
        } else if (event.getSource() == editDB) {
            labelError.setVisible(false);
            panel1.setVisible(false);
            panel2.setVisible(true);
            panel3.setVisible(false);
            panel0.setVisible(false);
            personalPanel.setVisible(false);
            editDB.getStyleClass().add("button-menu:hover");
        } else if (event.getSource() == analitics) {
            labelError.setVisible(false);
            panel1.setVisible(false);
            panel2.setVisible(false);
            panel3.setVisible(true);
            panel0.setVisible(false);
            personalPanel.setVisible(false);
            analitics.getStyleClass().add("button-menu:hover");
        } else if (event.getSource() == personalAccount) {
            if (!personalPanel.isVisible()) {
                //initPersonalAccount();
                personalPanel.setVisible(true); // Делаем видимой
            } else {
                personalPanel.setVisible(false); // Иначе скрываем
                labelMessage.setVisible(false);
            }

        } else if (event.getSource() == firstPage) {
            panel0.setVisible(true);
            panel1.setVisible(false);
            panel2.setVisible(false);
            panel3.setVisible(false);
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

            // Создаем пункт меню для regionFilter
            MenuItem menuItem1 = new MenuItem(displayText);
            menuItem1.getStyleClass().add("menu-item");
            menuItem1.setOnAction(event -> handleFilterRegions(displayText, temp.getTown()));
            regionFilter.getItems().add(menuItem1);

            // Создаем пункт меню для regionCalendar
            MenuItem menuItem2 = new MenuItem(displayText);
            menuItem2.getStyleClass().add("menu-item");
            menuItem2.setOnAction(event -> handleRegionCalendarSelection(displayText, temp.getTown()));
            regionCalendar.getItems().add(menuItem2);

            // Создаем пункт меню для regionCalendar
            MenuItem menuItem3 = new MenuItem(displayText);
            menuItem3.getStyleClass().add("menu-item");
            menuItem3.setOnAction(event -> handleRegionChartSelection(displayText, temp.getTown()));
            regionChart.getItems().add(menuItem3);
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
            regionFilter.getItems().clear(); // Очищаем текущее меню
            regionCalendar.getItems().clear();
            regionChart.getItems().clear();
            initMenuButtons(); // Перезагружаем кнопки меню
        }

    }

    // Метод для обработки выбора региона
    private void handleRegionSelection(String displayText, String town) {
        // Устанавливаем текст кнопки region
        region.setText(displayText);

        // Сохраняем выбранный город в переменной
        selectedTown = town;

        initUserWeather();

    }

    public void handleFilterRegions(String displayText, String town) {
        regionFilter.setText(displayText);
        FilteredList<Day> filteredRegions = new FilteredList<>(tableDays, e -> true);
        // Устанавливаем предикат для фильтрации
        filteredRegions.setPredicate(predicateDay -> {
            // Фильтруем по названию города
            return predicateDay.getLocation().getTown().equals(town);
        });
        SortedList<Day> sortData = filteredRegions.sorted();
        sortData.comparatorProperty().bind(tableWeather.comparatorProperty());
        tableWeather.setItems(sortData);
    }

    public void handleRegionCalendarSelection(String displayText, String town) {
        // Устанавливаем текст кнопки region
        regionCalendar.setText(displayText);
        // Сохраняем выбранный город в переменной
        selectedTownCalendar = town;
        regionChosen = true;
        updateCalendar();

    }

    public void handleRegionChartSelection(String displayText, String town) {
        // Устанавливаем текст кнопки region
        regionChart.setText(displayText);
        // Сохраняем выбранный город в переменной
        selectedTownChart = town;
        regionChosen2 = true;
        updateStatisticCharts();

    }

    public void initWeatherTable() {
        List<Day> dayList = dayService.getAllDays(labelError);

        if (dayList != null) {
            // Если данные успешно получены, заполняем таблицу
            tableDays = FXCollections.observableArrayList(dayList);
            tableWeather.setItems(tableDays);
        } else
            new LogErrorStrategy().handleError("Ошибка получения дней");
    }

    @FXML
    void editDB_Pressed(ActionEvent event) {
        switchForm(event);
    }


    @FXML
    void selectTableRow() {
        rowDay = tableWeather.getSelectionModel().getSelectedItem();
        int num = tableWeather.getSelectionModel().getSelectedIndex();
        if ((num - 1) < -1) {
            return;
        }
        textFieldCounty.setText(rowDay.getLocation().getCountry());
        textFieldTown.setText(rowDay.getLocation().getTown());
        textFieldDate.setValue(rowDay.getDate().toLocalDate());
        textFieldTemperature1.setText(rowDay.getDayWeather().getTemperature().toString());
        textFieldPressure1.setText(rowDay.getDayWeather().getPressure().toString());
        textFieldHumidity1.setText(rowDay.getDayWeather().getHumidity().toString());
        textFieldPrecipitation1.setText(rowDay.getDayWeather().getPrecipitation().toString());
        textFieldWind1.setText(rowDay.getDayWeather().getWindSpeed().toString());
        textFieldTemperature2.setText(rowDay.getNightWeather().getTemperature().toString());
        textFieldPressure2.setText(rowDay.getNightWeather().getPressure().toString());
        textFieldHumidity2.setText(rowDay.getNightWeather().getHumidity().toString());
        textFieldPrecipitation2.setText(rowDay.getNightWeather().getPrecipitation().toString());
        textFieldWind2.setText(rowDay.getNightWeather().getWindSpeed().toString());
        textFieldWeather.setText(rowDay.getWeatherName().getName());


    }

    public void insertDay() {
        ErrorStrategy errorStrategy = new LabelErrorStrategy(labelError);
        if (prepareToEdit()) {

            Validation validator = new Validation(errorStrategy);
            double t = validator.validateAndParseDouble(textFieldTemperature1.getText());
            int pr = validator.validateAndParseInt(textFieldPressure1.getText());
            int h = validator.validateAndParseInt(textFieldHumidity1.getText());
            double r = validator.validateAndParseDouble(textFieldPrecipitation1.getText());
            double w = validator.validateAndParseDouble(textFieldWind1.getText());
            if (t == -1000 || pr == -1000 || h == -1000 || r == -1000 || w == -1000)
                return;
            if (t > 60 || t < -40 || pr < 600 || pr > 850 || r > 100 || r < 0 || w > 50 || w < 0 || h < 0 || h > 100) {
                errorStrategy.handleError("Некорректные данные");
                return;
            }
            WeatherParameters dayW = new WeatherParameters(t, pr, h, r, w);

            t = validator.validateAndParseDouble(textFieldTemperature2.getText());
            pr = validator.validateAndParseInt(textFieldPressure2.getText());
            h = validator.validateAndParseInt(textFieldHumidity2.getText());
            r = validator.validateAndParseDouble(textFieldPrecipitation2.getText());
            w = validator.validateAndParseDouble(textFieldWind2.getText());
            if (t == -1000 || pr == -1000 || h == -1000 || r == -1000 || w == -1000)
                return;
            if (t > 60 || t < -40 || pr < 600 || pr > 850 || r > 100 || r < 0 || w > 50 || w < 0 || h < 0 || h > 100) {
                errorStrategy.handleError("Некорректные данные");
                return;
            }
            WeatherParameters nightW = new WeatherParameters(t, pr, h, r, w);

            String temp = validator.validateAndFormatString(textFieldWeather.getText());
            if (temp.equals("error"))
                return;
            WeatherName insertW = new WeatherName(temp);
            temp = validator.validateAndFormatString(textFieldTown.getText());
            if (temp.equals("error"))
                return;
            String temp1 = validator.validateAndFormatString(textFieldCounty.getText());
            if (temp1.equals("error"))
                return;
            Location insertL = new Location(temp, temp1);

            LocalDate selectedDate = textFieldDate.getValue();
            java.sql.Date sqlDate = java.sql.Date.valueOf(selectedDate);

            Day insertDay = new Day(sqlDate, dayW, nightW, insertW, insertL);
            insertDay = dayService.getDay(insertDay, labelError);
            if (insertDay != null) {
                tableDays.add(insertDay);
                updateMenuButtons();
            } else
                new LogErrorStrategy().handleError("Ошибка добавления дня");
        }else {
            errorStrategy.handleError("Не все поля заполнены.");
        }

    }

    public boolean prepareToEdit() {
        return !textFieldCounty.getText().isEmpty() &&
                !textFieldTown.getText().isEmpty() &&
                textFieldDate.getValue() != null &&
                !textFieldTemperature1.getText().isEmpty() &&
                !textFieldPressure1.getText().isEmpty() &&
                !textFieldPrecipitation1.getText().isEmpty() &&
                !textFieldWind1.getText().isEmpty() &&
                !textFieldHumidity1.getText().isEmpty() &&
                !textFieldTemperature2.getText().isEmpty() &&
                !textFieldPressure2.getText().isEmpty() &&
                !textFieldPrecipitation2.getText().isEmpty() &&
                !textFieldWind2.getText().isEmpty() &&
                !textFieldHumidity2.getText().isEmpty() &&
                !textFieldWeather.getText().isEmpty();

    }

    public void updateDay() {
        ErrorStrategy errorStrategy = new LabelErrorStrategy(labelError);
        if (prepareToEdit()) {

            Validation validator = new Validation(errorStrategy);
            double t = validator.validateAndParseDouble(textFieldTemperature1.getText());
            int pr = validator.validateAndParseInt(textFieldPressure1.getText());
            int h = validator.validateAndParseInt(textFieldHumidity1.getText());
            double r = validator.validateAndParseDouble(textFieldPrecipitation1.getText());
            double w = validator.validateAndParseDouble(textFieldWind1.getText());
            if (t == -1000 || pr == -1000 || h == -1000 || r == -1000 || w == -1000)
                return;
            if (t > 60 || t < -40 || pr < 600 || pr > 850 || r > 100 || r < 0 || w > 50 || w < 0 || h < 0 || h > 100) {
                errorStrategy.handleError("Некорректные данные");
                return;
            }
            WeatherParameters dayW = new WeatherParameters(rowDay.getDayWeather().getId(), t, pr, h, r, w);

            t = validator.validateAndParseDouble(textFieldTemperature2.getText());
            pr = validator.validateAndParseInt(textFieldPressure2.getText());
            h = validator.validateAndParseInt(textFieldHumidity2.getText());
            r = validator.validateAndParseDouble(textFieldPrecipitation2.getText());
            w = validator.validateAndParseDouble(textFieldWind2.getText());
            if (t == -1000 || pr == -1000 || h == -1000 || r == -1000 || w == -1000)
                return;
            if (t > 60 || t < -40 || pr < 600 || pr > 850 || r > 100 || r < 0 || w > 50 || w < 0 || h < 0 || h > 100) {
                errorStrategy.handleError("Некорректные данные");
                return;
            }

            WeatherParameters nightW = new WeatherParameters(rowDay.getNightWeather().getId(), t, pr, h, r, w);

            String temp = validator.validateAndFormatString(textFieldWeather.getText());
            if (temp.equals("error"))
                return;
            WeatherName insertW = new WeatherName(rowDay.getWeatherName().getId(), temp);
            temp = validator.validateAndFormatString(textFieldTown.getText());
            if (temp.equals("error"))
                return;
            String temp1 = validator.validateAndFormatString(textFieldCounty.getText());
            if (temp1.equals("error"))
                return;

            Location insertL = new Location(rowDay.getLocation().getId(), temp, temp1);


            LocalDate selectedDate = textFieldDate.getValue();
            java.sql.Date sqlDate = java.sql.Date.valueOf(selectedDate);

            Day updatedDay = new Day(rowDay.getId(), sqlDate, dayW, nightW, insertW, insertL);
            updatedDay = dayService.updateDay(updatedDay, labelError);
            if (updatedDay != null) {
                int index = -1;
                for (int i = 0; i < tableDays.size(); i++) {
                    if (Objects.equals(tableDays.get(i).getId(), updatedDay.getId())) {
                        index = i;
                        break;
                    }
                }

                // Если элемент найден, обновляем его
                if (index != -1) {
                    tableDays.set(index, updatedDay); // Заменяем элемент на обновленный
                } else {
                    errorStrategy.handleError("Элемент для обновления не найден!");
                }

                updateMenuButtons();
            } else
                new LogErrorStrategy().handleError("Ошибка редактирования дня");
        }else {
            errorStrategy.handleError("Не все поля заполнены.");
        }
    }

    public void deleteDay() {
        Day deletedDay = rowDay;
        String result=dayService.deleteDay(deletedDay,labelError);
        ErrorStrategy errorStrategy = new LabelErrorStrategy(labelError);
        if (result.equals("OK")) {
            int index = -1;
            for (int i = 0; i < tableDays.size(); i++) {
                if (Objects.equals(tableDays.get(i).getId(), rowDay.getId())) {
                    index = i;
                    break;
                }
            }
            // Если элемент найден, обновляем его
            if (index != -1) {
                tableDays.remove(index); // Заменяем элемент на обновленный
            } else {
                errorStrategy.handleError("Элемент для удаления не найден!");
            }
            rowDay = null;
            updateMenuButtons();
        }
        else
            new LogErrorStrategy().handleError("Ошибка удаления дня");
    }




    public void searchDays() {
        FilteredList<Day> filter = new FilteredList<>(tableDays, e -> true);
        textFieldSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filter.setPredicate(predicateDay -> {
                if (newValue.isEmpty())
                    return true;
                String keySearch = newValue.toLowerCase();
                if (predicateDay.getDate().toString().contains(keySearch))
                    return true;
                else if (predicateDay.getLocation().getTown().toLowerCase().contains(keySearch))
                    return true;
                else if (predicateDay.getLocation().getCountry().toLowerCase().contains(keySearch))
                    return true;
                else if (predicateDay.getWeatherName().getName().toLowerCase().contains(keySearch))
                    return true;


                return false;
            });

        });
        SortedList<Day> sortData = filter.sorted();
        sortData.comparatorProperty().bind(tableWeather.comparatorProperty());
        tableWeather.setItems(sortData);
    }

    @FXML
    void delete_Pressed(ActionEvent event) {
        deleteDay();
    }

    @FXML
    void clear_Pressed(ActionEvent event) {
        textFieldCounty.clear();
        textFieldTown.clear();
        textFieldDate.setValue(null);
        textFieldTemperature1.clear();
        textFieldPressure1.clear();
        textFieldPrecipitation1.clear();
        textFieldWind1.clear();
        textFieldHumidity1.clear();
        textFieldTemperature2.clear();
        textFieldPressure2.clear();
        textFieldPrecipitation2.clear();
        textFieldWind2.clear();
        textFieldHumidity2.clear();
        textFieldWeather.clear();

    }

    @FXML
    void add_Pressed(ActionEvent event) {
        insertDay();
    }

    @FXML
    void edit_Pressed(ActionEvent event) {
        updateDay();
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
    void firstPage_Pressed(ActionEvent event) {
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
    void analitics_Pressed(ActionEvent event) {
        switchForm(event);
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


    @FXML
    void monthPressed2(ActionEvent event) {
        MenuItem btn = (MenuItem) event.getSource();
        monthMenuBtn2.setText(btn.getText()); // Устанавливаем текст выбранного месяца на кнопку
        try {
            String monthName = btn.getText();
            Months selectedMonth = Months.valueOf(monthName); // Преобразуем название месяца в enum
            calendar.set(Calendar.MONTH, selectedMonth.getMonthNumber() - 1); // Устанавливаем выбранный месяц (с учетом 0 в Calendar)
            monthChosen2 = true;

            // Если год уже выбран, обновляем chartDate
            if (yearChosen2) {
                updateChartDate();
            }

            updateStatisticCharts();
        } catch (IllegalArgumentException e) {
            ErrorStrategy errorStrategy = new LabelErrorStrategy(labelMessage);
            errorStrategy.handleError("Неверный формат месяца: " + btn.getText());

        }
    }

    @FXML
    void yearPressed2(ActionEvent event) {
        ErrorStrategy errorStrategy = new LabelErrorStrategy(labelMessage);
        MenuItem btn = (MenuItem) event.getSource();
        yearMenuBtn2.setText(btn.getText()); // Устанавливаем текст выбранного года на кнопку
        try {
            int year = Integer.parseInt(btn.getText());
            calendar.set(Calendar.YEAR, year); // Устанавливаем выбранный год
            yearChosen2 = true;

            // Если месяц уже выбран, обновляем chartDate
            if (monthChosen2) {
                updateChartDate();
            }

            updateStatisticCharts();
        } catch (NumberFormatException e) {
            errorStrategy.handleError("Неверный формат года: " + btn.getText());
        }
    }

    // Метод для обновления chartDate
    private void updateChartDate() {
        // Устанавливаем первый день выбранного месяца
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // Calendar.MONTH начинается с 0
        chartDate = LocalDate.of(year, month, 1); // Устанавливаем 1-е число месяца
    }


    public void updateStatisticCharts() {
        ErrorStrategy errorStrategy = new LabelErrorStrategy(labelMessage);
        if (!monthChosen2 || !yearChosen2 || !regionChosen2) return; // Проверяем, что все параметры выбраны

        // Фильтруем данные по выбранным месяцу, году и городу
        ObservableList<Day> filteredDays = tableDays.filtered(day -> {
            LocalDate dayDate = day.getDate().toLocalDate(); // Преобразуем дату
            return dayDate.getMonthValue() == chartDate.getMonthValue() && // Совпадение месяца
                    dayDate.getYear() == chartDate.getYear() &&            // Совпадение года
                    day.getLocation().getTown().equals(selectedTownChart); // Совпадение города
        });

        if (filteredDays.isEmpty()) {
            errorStrategy.handleError("Нет данных для выбранного периода и города.");
            return;
        }

        // Очистка графиков перед заполнением
        tempChart.getData().clear();
        pressureChart.getData().clear();
        windSpeedChart.getData().clear();
        statisticChart.getData().clear();

        // Создаем серии для графиков
        XYChart.Series<String, Number> tempDaySeries = new XYChart.Series<>();
        tempDaySeries.setName("Дневная температура");
        XYChart.Series<String, Number> tempNightSeries = new XYChart.Series<>();
        tempNightSeries.setName("Ночная температура");

        XYChart.Series<String, Number> pressureDaySeries = new XYChart.Series<>();
        pressureDaySeries.setName("Дневное давление");
        XYChart.Series<String, Number> pressureNightSeries = new XYChart.Series<>();
        pressureNightSeries.setName("Ночное давление");

        XYChart.Series<String, Number> windDaySeries = new XYChart.Series<>();
        windDaySeries.setName("Дневная скорость ветра");
        XYChart.Series<String, Number> windNightSeries = new XYChart.Series<>();
        windNightSeries.setName("Ночная скорость ветра");

        // Переменные для расчета минимальных, максимальных и средних значений
        double minDayTemp = Double.MAX_VALUE, maxDayTemp = Double.MIN_VALUE, sumDayTemp = 0;
        double minNightTemp = Double.MAX_VALUE, maxNightTemp = Double.MIN_VALUE, sumNightTemp = 0;
        double minDayPressure = Double.MAX_VALUE, maxDayPressure = Double.MIN_VALUE, sumDayPressure = 0;
        double minNightPressure = Double.MAX_VALUE, maxNightPressure = Double.MIN_VALUE, sumNightPressure = 0;
        double minDayWind = Double.MAX_VALUE, maxDayWind = Double.MIN_VALUE, sumDayWind = 0;
        double minNightWind = Double.MAX_VALUE, maxNightWind = Double.MIN_VALUE, sumNightWind = 0;

        int count = filteredDays.size();

        // Считаем количество прогнозов
        Map<String, Long> weatherCount = new HashMap<>();
        // Заполняем данные для графиков
        for (Day day : filteredDays) {
            LocalDate date = day.getDate().toLocalDate();
            String dateStr = date.format(DateTimeFormatter.ofPattern("d MMM"));

            // Получение данных
            double dayTemp = day.getDayWeather().getTemperature();
            double nightTemp = day.getNightWeather().getTemperature();
            double dayPressure = day.getDayWeather().getPressure();
            double nightPressure = day.getNightWeather().getPressure();
            double dayWind = day.getDayWeather().getWindSpeed();
            double nightWind = day.getNightWeather().getWindSpeed();

            // Данные для температур
            tempDaySeries.getData().add(new XYChart.Data<>(dateStr, dayTemp));
            tempNightSeries.getData().add(new XYChart.Data<>(dateStr, nightTemp));

            // Данные для давления
            pressureDaySeries.getData().add(new XYChart.Data<>(dateStr, dayPressure));
            pressureNightSeries.getData().add(new XYChart.Data<>(dateStr, nightPressure));

            // Данные для скорости ветра
            windDaySeries.getData().add(new XYChart.Data<>(dateStr, dayWind));
            windNightSeries.getData().add(new XYChart.Data<>(dateStr, nightWind));

            // Обновление минимальных, максимальных и суммарных значений
            minDayTemp = Math.min(minDayTemp, dayTemp);
            maxDayTemp = Math.max(maxDayTemp, dayTemp);
            sumDayTemp += dayTemp;

            minNightTemp = Math.min(minNightTemp, nightTemp);
            maxNightTemp = Math.max(maxNightTemp, nightTemp);
            sumNightTemp += nightTemp;

            minDayPressure = Math.min(minDayPressure, dayPressure);
            maxDayPressure = Math.max(maxDayPressure, dayPressure);
            sumDayPressure += dayPressure;

            minNightPressure = Math.min(minNightPressure, nightPressure);
            maxNightPressure = Math.max(maxNightPressure, nightPressure);
            sumNightPressure += nightPressure;

            minDayWind = Math.min(minDayWind, dayWind);
            maxDayWind = Math.max(maxDayWind, dayWind);
            sumDayWind += dayWind;

            minNightWind = Math.min(minNightWind, nightWind);
            maxNightWind = Math.max(maxNightWind, nightWind);
            sumNightWind += nightWind;

            // Считаем количество прогнозов
            String weatherName = day.getWeatherName().getName();
            weatherCount.put(weatherName, weatherCount.getOrDefault(weatherName, 0L) + 1);
        }

        // Добавляем серии данных в графики
        tempChart.getData().addAll(tempDaySeries, tempNightSeries);
        pressureChart.getData().addAll(pressureDaySeries, pressureNightSeries);
        windSpeedChart.getData().addAll(windDaySeries, windNightSeries);

        // Заполняем статистический график
        XYChart.Series<String, Number> statisticSeries = new XYChart.Series<>();
        statisticSeries.setName("Количество прогнозов");
        for (Map.Entry<String, Long> entry : weatherCount.entrySet()) {
            statisticSeries.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }
        statisticChart.getData().add(statisticSeries);

        // Вычисление средних значений
        double avgDayTemp = sumDayTemp / count,
                avgNightTemp = sumNightTemp / count,
                avgDayPressure = sumDayPressure / count,
                avgNightPressure = sumNightPressure / count,
                avgDayWind = sumDayWind / count,
                avgNightWind = sumNightWind / count;

        minText.setText(String.format("T,°C: %.2f/%.2f;\n p, мм рт. ст.: %.2f/%.2f;\n v, м/с: %.2f/%.2f",
                minDayTemp, minNightTemp, minDayPressure, minNightPressure, minDayWind, minNightWind));
        maxText.setText(String.format("T,°C: %.2f/%.2f;\n p, мм рт. ст.: %.2f/%.2f;\n v, м/с: %.2f/%.2f",
                maxDayTemp, maxNightTemp, maxDayPressure, maxNightPressure, maxDayWind, maxNightWind));
        averageText.setText(String.format("T,°C: %.2f/%.2f;\n p, мм рт. ст.: %.2f/%.2f;\n v, м/с: %.2f/%.2f",
                avgDayTemp, avgNightTemp, avgDayPressure, avgNightPressure, avgDayWind, avgNightWind));
        if (!weatherData.isEmpty())
            weatherData.clear();
        String period = chartDate.getMonthValue() + "-" + chartDate.getYear();
        weatherData.put("period", period);

        // Используем общий метод для добавления данных
        weatherData.put("temperature", createWeatherDataMap(minDayTemp, minNightTemp, maxDayTemp, maxNightTemp, avgDayTemp, avgNightTemp));
        weatherData.put("pressure", createWeatherDataMap(minDayPressure, minNightPressure, maxDayPressure, maxNightPressure, avgDayPressure, avgNightPressure));
        weatherData.put("wind", createWeatherDataMap(minDayWind, minNightWind, maxDayWind, maxNightWind, avgDayWind, avgNightWind));
    }

    private Map<String, Double> createWeatherDataMap(double minDay, double minNight, double maxDay, double maxNight, double avgDay, double avgNight) {
        Map<String, Double> data = new HashMap<>();
        data.put("minDay", minDay);
        data.put("minNight", minNight);
        data.put("maxDay", maxDay);
        data.put("maxNight", maxNight);
        data.put("avgDay", avgDay);
        data.put("avgNight", avgNight);
        return data;
    }

    private void saveWeatherDataToFile(Map<String, Object> weatherData, String filePath) {
        Gson gson = new Gson();
        Map<String, Object> existingData = null;
        ErrorStrategy errorStrategy = new LabelErrorStrategy(labelMessage);
        try (FileReader reader = new FileReader(filePath)) {
            // Читаем данные из файла и преобразуем в Map
            existingData = gson.fromJson(reader, Map.class);
        } catch (IOException e) {
            // Если файл не существует или произошла ошибка, выводим сообщение и инициализируем пустую карту
           new LogErrorStrategy().handleError("Файл не существует или ошибка при чтении.");
        }

        // Если данных нет (например, файл пустой или не существовал), создаем новую пустую карту
        if (existingData == null) {
            existingData = new HashMap<>();
        }

        // Извлекаем период (месяц-год) из данных
        String period = (String) weatherData.get("period");

        // Перезаписываем данные для выбранного периода
        existingData.put(period, weatherData);

        // Записываем обновленные данные в файл
        try (FileWriter writer = new FileWriter(filePath)) {
            gson.toJson(existingData, writer); // Преобразуем карту в JSON и записываем в файл
            errorStrategy.handleError("Данные сохранены в файл");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    void saveInFile_Pressed(ActionEvent event) {
        String filePath = "D:\\Sasha\\3_kurs\\прогсп\\КУРСАЧ\\weather\\Client\\src\\main\\resources\\month_statistic.json";
        ErrorStrategy errorStrategy = new LabelErrorStrategy(labelMessage);
        if (weatherData != null) {
            saveWeatherDataToFile(weatherData, filePath);
        } else {
            errorStrategy.handleError("Сначала выберите период и регион");
        }
    }

}
