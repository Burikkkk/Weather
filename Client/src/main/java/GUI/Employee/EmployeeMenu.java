package GUI.Employee;

import Enums.Months;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import java.util.Calendar;
import Enums.RequestType;
import Enums.ResponseStatus;
import Enums.Roles;
import Models.Entities.*;
import Models.TCP.Request;
import Models.TCP.Response;
import Utilities.ClientSocket;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
import java.lang.reflect.Type;
import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class EmployeeMenu implements Initializable {

    @FXML
    private Button add, analitics, calendarButton, clear, delete, edit, editDB, exit,
            personalAccount, Change, buttonSignUp, firstPage, saveBtn;
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
    private AnchorPane todayWeatherPanel, pane1, panel0, panel1, panel2, panel3, personalPanel;
    @FXML
    private Label labelCurrentDate, labelError, labelUser, labelMessage, textC1, textC2,
            textK1, textK2, textKMH1, textKMH2, textMM1, textMM2, textMS1, textMS2,
            texthPa1, texthPa2, xHumidity1, xHumidity2, xPressure1, xPressure2,
            xRain1, xRain2, xTemperatura1, xTemperatura2, xWeatherName, xWind1, xWind2;
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
    private MenuButton region, regionFilter,monthMenuBtn,yearMenuBtn;
    @FXML
    private RadioButton C, K, hPa, km, m, mmHg;
    @FXML
    private ToggleGroup Pressure, Speed, Temperature;
    @FXML
    private CheckBox sendWeather;
    @FXML
    private GridPane daysGrid;
    @FXML
    private TextArea textArea;
    @FXML
    private Label chosenDateLabel;

    private final Calendar calendar = Calendar.getInstance();
    private boolean monthChosen = false;
    private boolean yearChosen = false;
    private final int BUTTONS_IN_A_ROW = 7;
    private final int LINES = 6;

    private Button[] dayButtons;
    private final CalendarData calendarData = new CalendarData();

    private User currentUser;
    private List<Location> regions;
    private LocalDate today;
    private Day todayWeather;
    private String selectedTown;
    private ObservableList<Day> tableDays;
    private Day rowDay;


    public String validateAndFormatString(String input) {
        labelError.setVisible(true);
        // Убираем лишние пробелы
        input = input.trim();
        // Проверка на длину строки
        if (input.length() <= 3) {
            labelError.setText("Строка должна быть длиннее 3 символов");
            return "error";
        }

        // Проверка на наличие цифр
        if (!input.matches("[a-zA-Zа-яА-ЯёЁ]+")) {
            labelError.setText("Строка должна содержать только буквы");
            return "error"; // Возвращаем "error" для указания ошибки
        }
        // Преобразуем первую букву в верхний регистр, остальные — в нижний
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }

    public double validateAndParseDouble(String input){
        labelError.setVisible(true);
        if (!input.matches("-?\\d+(\\.\\d+)?")) {
            labelError.setText("Число должно содержать только цифры");
            return -1000;
        }
        double value = Double.parseDouble(input);
        if (value < -1000||value>1000) {
            labelError.setText("Слишком маленькое или большое значение");
            return -1000;
        }
        return value;
    }

    public int validateAndParseInt(String input) {
        labelError.setVisible(true);
        // Проверка на наличие букв (ни русских, ни английских)
        if (!input.matches("-?\\d+(\\.\\d+)?")) {
            labelError.setText("Число должно содержать только цифры");
            return -1000; // Возвращаем ошибочный код
        }
        return Integer.parseInt(input.trim()); // Убираем пробелы и пытаемся распарсить
        // Проверка на положительное значение
    }

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
        Request requestModel = new Request();
        requestModel.setRequestType(RequestType.GET_REGION);

        try {

            ClientSocket.getInstance().getOut().println(new Gson().toJson(requestModel));
            ClientSocket.getInstance().getOut().flush();

            String answer = ClientSocket.getInstance().getInStream().readLine();
            Response responseModel = new Gson().fromJson(answer, Response.class);

            if (responseModel.getResponseStatus() == ResponseStatus.OK) {
                // Используем TypeToken для точного указания типа списка
                Type regionListType = new TypeToken<List<Location>>() {
                }.getType();
                regions = new Gson().fromJson(responseModel.getResponseData(), regionListType);


            } else {
                labelError.setText("Ошибка загрузки данных: " + responseModel.getResponseMessage());
                labelError.setVisible(true);
            }
        } catch (IOException e) {
            labelError.setText("Ошибка связи с сервером.");
            labelError.setVisible(true);
        }
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
        }
    }

    public void updateMenuButtons(){

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
        SortedList <Day> sortData=filteredRegions.sorted();
        sortData.comparatorProperty().bind(tableWeather.comparatorProperty());
        tableWeather.setItems(sortData);
    }

    public void initWeatherTable() {

        Request requestModel = new Request();
        requestModel.setRequestType(RequestType.GET_ALL_DAYS);

        try {

            ClientSocket.getInstance().getOut().println(new Gson().toJson(requestModel));
            ClientSocket.getInstance().getOut().flush();

            String answer = ClientSocket.getInstance().getInStream().readLine();
            Response responseModel = new Gson().fromJson(answer, Response.class);

            if (responseModel.getResponseStatus() == ResponseStatus.OK) {
                // Используем TypeToken для точного указания типа списка
                Type weatherListType = new TypeToken<List<Day>>() {
                }.getType();
                List<Day> tempList = new Gson().fromJson(responseModel.getResponseData(), weatherListType);
                tableDays = FXCollections.observableArrayList(tempList);

                tableWeather.setItems(tableDays);

            } else {
                labelError.setText("Ошибка загрузки данных: " + responseModel.getResponseMessage());
                labelError.setVisible(true);
            }
        } catch (IOException e) {
            labelError.setText("Ошибка связи с сервером.");
            labelError.setVisible(true);
        }
    }

    @FXML
    void editDB_Pressed(ActionEvent event) {
        initWeatherTable();
        updateMenuButtons();
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
        if (prepareToEdit()) {
            double t = validateAndParseDouble(textFieldTemperature1.getText());
            int pr = validateAndParseInt(textFieldPressure1.getText());
            int h = validateAndParseInt(textFieldHumidity1.getText());
            double r = validateAndParseDouble(textFieldPrecipitation1.getText());
            double w = validateAndParseDouble(textFieldWind1.getText());
            if(t==-1000||pr==-1000||h==-1000||r==-1000||w==-1000)
                return;
            if(t>60||t<-40||pr<600||pr>850||r>100||r<0||w>50||w<0||h<0||h>100)
            {
                labelError.setVisible(true);
                labelError.setText("Некорректные данные");
                return;
            }
            WeatherParameters dayW = new WeatherParameters(t, pr, h, r, w);

            t = validateAndParseDouble(textFieldTemperature2.getText());
            pr = validateAndParseInt(textFieldPressure2.getText());
            h = validateAndParseInt(textFieldHumidity2.getText());
            r = validateAndParseDouble(textFieldPrecipitation2.getText());
            w = validateAndParseDouble(textFieldWind2.getText());
            if(t==-1000||pr==-1000||h==-1000||r==-1000||w==-1000)
                return;
            if(t>60||t<-40||pr<600||pr>850||r>100||r<0||w>50||w<0||h<0||h>100)
            {
                labelError.setVisible(true);
                labelError.setText("Некорректные данные");
                return;
            }
            WeatherParameters nightW = new WeatherParameters(t, pr, h, r, w);

            String temp =validateAndFormatString(textFieldWeather.getText());
            if(temp.equals("error"))
                return;
            WeatherName insertW = new WeatherName(temp);
            temp =validateAndFormatString(textFieldTown.getText());
            if(temp.equals("error"))
                return;
            String temp1 =validateAndFormatString(textFieldCounty.getText());
            if(temp1.equals("error"))
                return;
            Location insertL = new Location(temp,temp1);

            LocalDate selectedDate = textFieldDate.getValue();
            java.sql.Date sqlDate = java.sql.Date.valueOf(selectedDate);

            Day insertDay = new Day(sqlDate, dayW, nightW, insertW, insertL);

            Request requestModel = new Request();
            requestModel.setRequestMessage(new Gson().toJson(insertDay));
            requestModel.setRequestType(RequestType.ADD_DAY);
            ClientSocket.getInstance().getOut().println(new Gson().toJson(requestModel));
            ClientSocket.getInstance().getOut().flush();
            try {
                String answer = ClientSocket.getInstance().getInStream().readLine();
                Response responseModel = new Gson().fromJson(answer, Response.class);
                if (responseModel.getResponseStatus() == ResponseStatus.OK) {
                    insertDay = new Gson().fromJson(responseModel.getResponseData(), Day.class);
                    labelError.setText(responseModel.getResponseMessage());
                    labelError.setVisible(true);
                    tableDays.add(insertDay);

                    updateMenuButtons();
                } else {
                    labelError.setText(responseModel.getResponseMessage());
                    labelError.setVisible(true);
                }

            } catch (IOException e) {
                labelError.setText("Ошибка связи с сервером.");
                labelError.setVisible(true);
            }
        } else {
            labelMessage.setText("Не все поля заполнены.");
            labelMessage.setVisible(true);

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
        if (prepareToEdit()) {
        double t = validateAndParseDouble(textFieldTemperature1.getText());
        int pr = validateAndParseInt(textFieldPressure1.getText());
        int h = validateAndParseInt(textFieldHumidity1.getText());
        double r = validateAndParseDouble(textFieldPrecipitation1.getText());
        double w = validateAndParseDouble(textFieldWind1.getText());
            if(t==-1000||pr==-1000||h==-1000||r==-1000||w==-1000)
                return;
            if(t>60||t<-40||pr<600||pr>850||r>100||r<0||w>50||w<0||h<0||h>100)
            {
                labelError.setVisible(true);
                labelError.setText("Некорректные данные");
                return;
            }
        WeatherParameters dayW = new WeatherParameters(rowDay.getDayWeather().getId(),t, pr, h, r, w);

        t = validateAndParseDouble(textFieldTemperature2.getText());
        pr = validateAndParseInt(textFieldPressure2.getText());
        h = validateAndParseInt(textFieldHumidity2.getText());
        r = validateAndParseDouble(textFieldPrecipitation2.getText());
        w = validateAndParseDouble(textFieldWind2.getText());
            if(t==-1000||pr==-1000||h==-1000||r==-1000||w==-1000)
                return;
            if(t>60||t<-40||pr<600||pr>850||r>100||r<0||w>50||w<0||h<0||h>100)
            {
                labelError.setVisible(true);
                labelError.setText("Некорректные данные");
                return;
            }

        WeatherParameters nightW = new WeatherParameters(rowDay.getNightWeather().getId(),t, pr, h, r, w);

            String temp =validateAndFormatString(textFieldWeather.getText());
            if(temp.equals("error"))
                return;
            WeatherName insertW = new WeatherName(rowDay.getWeatherName().getId(),temp);
            temp =validateAndFormatString(textFieldTown.getText());
            if(temp.equals("error"))
                return;
            String temp1 =validateAndFormatString(textFieldCounty.getText());
            if(temp1.equals("error"))
                return;

        Location insertL = new Location(rowDay.getLocation().getId(),temp,temp1);


        LocalDate selectedDate = textFieldDate.getValue();
        java.sql.Date sqlDate = java.sql.Date.valueOf(selectedDate);

        Day updatedDay = new Day(rowDay.getId(),sqlDate, dayW, nightW, insertW, insertL);

        Request requestModel = new Request();
        requestModel.setRequestMessage(new Gson().toJson(updatedDay));
        requestModel.setRequestType(RequestType.UPDATE_DAY);
        ClientSocket.getInstance().getOut().println(new Gson().toJson(requestModel));
        ClientSocket.getInstance().getOut().flush();
        try {
            String answer = ClientSocket.getInstance().getInStream().readLine();
            Response responseModel = new Gson().fromJson(answer, Response.class);
            if (responseModel.getResponseStatus() == ResponseStatus.OK) {
                updatedDay = new Gson().fromJson(responseModel.getResponseData(), Day.class);
                labelError.setText(responseModel.getResponseMessage());
                labelError.setVisible(true);
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
                    labelError.setText("Элемент для обновления не найден!");
                    labelError.setVisible(true);
                }

                updateMenuButtons();
            } else {
                labelError.setText(responseModel.getResponseMessage());
                labelError.setVisible(true);
            }
        } catch (IOException e) {
            labelError.setText("Ошибка связи с сервером.");
            labelError.setVisible(true);
        }
        } else {
            labelMessage.setText("Не все поля заполнены.");
            labelMessage.setVisible(true);

        }
    }

    public void deleteDay()    {
        Day deletedDay = rowDay;

        Request requestModel = new Request();
        requestModel.setRequestMessage(new Gson().toJson(deletedDay));
        requestModel.setRequestType(RequestType.DELETE_DAY);
        ClientSocket.getInstance().getOut().println(new Gson().toJson(requestModel));
        ClientSocket.getInstance().getOut().flush();
        try {
            String answer = ClientSocket.getInstance().getInStream().readLine();
            Response responseModel = new Gson().fromJson(answer, Response.class);
            if (responseModel.getResponseStatus() == ResponseStatus.OK) {

                labelError.setText(responseModel.getResponseMessage());
                labelError.setVisible(true);
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
                    labelError.setText("Элемент для удаления не найден!");
                    labelError.setVisible(true);
                }
                rowDay=null;
                updateMenuButtons();
            } else {
                labelError.setText(responseModel.getResponseMessage());
                labelError.setVisible(true);
            }
        } catch (IOException e) {
            labelError.setText("Ошибка связи с сервером.");
            labelError.setVisible(true);
        }
    }


   public void searchDays(){
       FilteredList<Day> filter = new FilteredList<>(tableDays, e->true);
       textFieldSearch.textProperty().addListener((observable, oldValue,newValue)->{
           filter.setPredicate(predicateDay->{
               if(newValue.isEmpty())
                   return true;
               String keySearch=newValue.toLowerCase();
               if(predicateDay.getDate().toString().contains(keySearch))
                   return true;
               else if(predicateDay.getLocation().getTown().toLowerCase().contains(keySearch))
                   return true;
               else if(predicateDay.getLocation().getCountry().toLowerCase().contains(keySearch))
                   return true;
               else if(predicateDay.getWeatherName().getName().toLowerCase().contains(keySearch))
                   return true;


               return false;
           });

       });
       SortedList <Day> sortData=filter.sorted();
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
    void exit_Pressed(ActionEvent event) {
        exit();
    }

    public void exit() {
        Request requestModel = new Request();
        requestModel.setRequestMessage(new Gson().toJson(currentUser));
        requestModel.setRequestType(RequestType.LOGOUT);
        try {
            ClientSocket.getInstance().getOut().println(new Gson().toJson(requestModel));

            ClientSocket.getInstance().getOut().flush();

            String answer = ClientSocket.getInstance().getInStream().readLine();
            Response responseModel = new Gson().fromJson(answer, Response.class);
            if (responseModel.getResponseStatus() == ResponseStatus.OK) {
                labelError.setVisible(false);

                ClientSocket.getInstance().setUser(new Gson().fromJson(responseModel.getResponseData(), User.class));
                ClientSocket.resetConnection();
                Stage stage = (Stage) exit.getScene().getWindow();
                Parent root = FXMLLoader.load(getClass().getResource("/fxml/Login.fxml"));
                Scene newScene = new Scene(root);
                stage.setScene(newScene);
                stage.centerOnScreen();
            } else {
                labelError.setText(responseModel.getResponseMessage());
                labelError.setVisible(true);
            }
        } catch (IOException e) {
            labelError.setText("Ошибка связи с сервером.");
            labelError.setVisible(true);
        }
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
        Request requestModel = new Request();
        requestModel.setRequestMessage(new Gson().toJson(tempDay));
        requestModel.setRequestType(RequestType.TODAY_WEATHER);
        try {
            ClientSocket.getInstance().getOut().println(new Gson().toJson(requestModel));
            ClientSocket.getInstance().getOut().flush();

            String answer = ClientSocket.getInstance().getInStream().readLine();
            Response responseModel = new Gson().fromJson(answer, Response.class);
            if (responseModel.getResponseStatus() == ResponseStatus.OK) {

                todayWeather = new Gson().fromJson(responseModel.getResponseData(), Day.class);
                initWeatherFields();
                todayWeatherPanel.setVisible(true);
                labelError.setVisible(false);
            } else {
                todayWeatherPanel.setVisible(false);
                labelError.setText(responseModel.getResponseMessage());
                labelError.setVisible(true);
            }
        } catch (IOException e) {
            labelError.setText("Ошибка связи с сервером.");
            labelError.setVisible(true);
        }
    }

    public void initWeatherFields() {
        xWeatherName.setText(todayWeather.getWeatherName().getName());
        if (currentUser.getPersonalSettings().getTemperature().equals("K")) {
            double x = todayWeather.getDayWeather().getTemperature() + 273.15;
            xTemperatura1.setText(String.valueOf(x));
            x = todayWeather.getNightWeather().getTemperature() + 273.15;
            xTemperatura2.setText(String.valueOf(x));
            textK1.setVisible(true);
            textK2.setVisible(true);
            textC1.setVisible(false);
            textC2.setVisible(false);
        } else {
            xTemperatura1.setText(todayWeather.getDayWeather().getTemperature().toString());
            xTemperatura2.setText(todayWeather.getNightWeather().getTemperature().toString());
            textC1.setVisible(true);
            textC2.setVisible(true);
            textK1.setVisible(false);
            textK2.setVisible(false);
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

    public void updateUser(User currentUser) {
        if (!passwordfieldPassword.getText().equals(passwordfieldConfirmPassword.getText())) {
            labelMessage.setText("Пароли не совпадают");
            labelMessage.setVisible(true);
            return;
        }
        if (textfieldLogin.getText().isEmpty() ||
                passwordfieldPassword.getText().isEmpty() ||
                passwordfieldConfirmPassword.getText().isEmpty() ||
                textfieldPhone.getText().isEmpty()) {
            labelMessage.setText("Не все поля заполнены.");
            labelMessage.setVisible(true);
            return;
        }
        User user = new User();
        user.setId(currentUser.getId());
        String tempLogin = textfieldLogin.getText();
        user.setRole(currentUser.getRole());
        // Проверка на латинские буквы и цифры
        if (!tempLogin.matches("^[a-zA-Z0-9]+$")) {
            labelMessage.setText("Логин должен содержать только латинские буквы или цифры!");
            labelMessage.setVisible(true);
            return;
        }

        // Проверка длины логина
        if (tempLogin.length() < 3 || tempLogin.length() > 25) {
            labelMessage.setText("Логин должен быть от 3 до 25 символов!");
            labelMessage.setVisible(true);
            return;
        }
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
        String tempPhone = textfieldPhone.getText();
        if (tempPhone.length() != 9 || !tempPhone.matches("\\d+")) {
            labelMessage.setText("Введите номер телефона с кодом!");
            labelMessage.setVisible(true);
            return;
        }
        settings.setPhone(tempPhone);

        String temperature = ((RadioButton) Temperature.getSelectedToggle()).getText();
        if (temperature.equals("K"))
            settings.setTemperature(temperature);
        else
            settings.setTemperature("C");
        String pressure = ((RadioButton) Pressure.getSelectedToggle()).getText();
        String speed = ((RadioButton) Speed.getSelectedToggle()).getText();
        settings.setPressure(pressure);
        settings.setSpeed(speed);
        settings.setNotifications(sendWeather.isSelected());

        user.setPersonalSettings(settings);

        if (!user.equals(currentUser)) {
            Request requestModel = new Request();
            requestModel.setRequestMessage(new Gson().toJson(user));
            requestModel.setRequestType(RequestType.UPDATE_USER);
            ClientSocket.getInstance().getOut().println(new Gson().toJson(requestModel));
            ClientSocket.getInstance().getOut().flush();
            try {
                String answer = ClientSocket.getInstance().getInStream().readLine();
                Response responseModel = new Gson().fromJson(answer, Response.class);
                if (responseModel.getResponseStatus() == ResponseStatus.OK) {
                    ClientSocket.getInstance().setUser(new Gson().fromJson(responseModel.getResponseData(), User.class));
                    labelMessage.setText("Данные изменены!");
                    labelMessage.setVisible(true);
                    currentUser = ClientSocket.getInstance().getUser();

                    //заново проинициализировать поля
                    labelUser.setText(currentUser.getLogin());
                    if (selectedTown != null) {
                        initUserWeather();
                        initWeatherFields();
                    }

                } else {
                    labelMessage.setText(responseModel.getResponseMessage());
                    labelMessage.setVisible(true);
                }
            } catch (IOException e) {
                labelError.setText("Ошибка связи с сервером.");
                labelError.setVisible(true);
            }
        } else {
            labelMessage.setText("Введите новые данные!");
            labelMessage.setVisible(true);
        }
    }

    public void initPersonalAccount(User currentUser) {
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
                if ("K".equals(settings.getTemperature())) {
                    K.setSelected(true);
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
        updateUser(currentUser);
    }

    @FXML
    void personalAccount_Pressed(ActionEvent event) {
        initPersonalAccount(currentUser);
        switchForm(event);
    }

    @FXML
    void calendarButton_Pressed(ActionEvent event) {
        switchForm(event);
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
            showError("Неверный формат месяца: " + btn.getText());
        }
    }

    /**
     * Метод вызывается при нажатии на кнопку "Сохранить"
     */
    @FXML
    void savePressed(ActionEvent event) {
        calendarData.setData(calendar, textArea.getText()); // Сохраняем данные для выбранной даты
    }

    /**
     * Метод вызывается при выборе года
     */
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
            showError("Неверный формат года: " + btn.getText());
        }
    }

    /**
     * Обновляет календарь, если выбраны месяц и год
     */
    private void updateCalendar() {
        if (!monthChosen || !yearChosen) return; // Проверяем, что месяц и год выбраны

        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH); // Определяем количество дней в месяце
        createDaysButtons(daysInMonth); // Создаем кнопки для дней месяца
    }

    /**
     * Создает кнопки для отображения дней в календаре
     * @param daysInMonth количество дней в текущем месяце
     */
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
                Button dayButton = new Button(String.valueOf(day)); // Создаем кнопку с текстом для текущего дня
                dayButton.setPrefSize(daysGrid.getPrefWidth() / BUTTONS_IN_A_ROW, daysGrid.getPrefHeight() / LINES); // Устанавливаем размер кнопки
                dayButton.setOnAction(this::dayPressed); // Устанавливаем обработчик события для кнопки
                daysGrid.add(dayButton, i % BUTTONS_IN_A_ROW, i / BUTTONS_IN_A_ROW); // Добавляем кнопку в сетку
                dayButtons[i] = dayButton; // Сохраняем кнопку в массив
            }
        }
    }

    /**
     * Обрабатывает нажатие на кнопку дня
     * @param event событие нажатия
     */
    private void dayPressed(ActionEvent event) {
        Button dayBtn = (Button) event.getSource(); // Получаем кнопку, вызвавшую событие
        try {
            int day = Integer.parseInt(dayBtn.getText()); // Определяем выбранный день
            calendar.set(Calendar.DAY_OF_MONTH, day); // Устанавливаем день в календаре
            textArea.setText(calendarData.getData(calendar)); // Отображаем данные для выбранной даты

            int dayInMonth = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH) + 1; // Преобразуем месяц из 0-базового формата
            int year = calendar.get(Calendar.YEAR);

            // Обновляем текст метки с выбранной датой
            chosenDateLabel.setText(String.format("Выбранная дата: %d/%d/%d", dayInMonth, month, year));
        } catch (NumberFormatException e) {
            showError("Неверное значение дня.");
        }
    }

    /**
     * Отображает сообщение об ошибке
     * @param message текст сообщения
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR); // Создаем окно с типом "Ошибка"
        alert.setTitle("Ошибка");
        alert.setContentText(message);
        alert.showAndWait(); // Показываем сообщение пользователю
    }

}
