package GUI.Employee;

import Enums.RequestType;
import Enums.ResponseStatus;
import Enums.Roles;
import Models.Entities.*;
import Models.TCP.Request;
import Models.TCP.Response;
import Utilities.ClientSocket;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

public class EmployeeMenu implements Initializable {

    @FXML
    private Button add;

    @FXML
    private Button analitics;

    @FXML
    private Button calendar;

    @FXML
    private Button clear;

    @FXML
    private TableColumn<Day, String> columnCountry;

    @FXML
    private TableColumn<Day, Date> columnDate;


    @FXML
    private TableColumn<Day, Integer> columnHumidity1;

    @FXML
    private TableColumn<Day, Integer> columnHumidity2;

    @FXML
    private TableColumn<Day, Double> columnPrecipitation1;

    @FXML
    private TableColumn<Day, Double> columnPrecipitation2;

    @FXML
    private TableColumn<Day, Integer> columnPressure1;

    @FXML
    private TableColumn<Day, Integer> columnPressure2;

    @FXML
    private TableColumn<Day, Double> columnTemperature1;

    @FXML
    private TableColumn<Day, Double> columnTemperature2;

    @FXML
    private TableColumn<Day, String> columnTown;

    @FXML
    private TableColumn<Day, String> columnWeatherName;

    @FXML
    private TableColumn<Day, Double> columnWind1;

    @FXML
    private TableColumn<Day, Double> columnWind2;


    @FXML
    private AnchorPane todayWeatherPanel;

    @FXML
    private Button delete;

    @FXML
    private Button edit;

    @FXML
    private Button editDB;

    @FXML
    private Button exit;

    @FXML
    private Label labelCurrentDate;

    @FXML
    private Label labelError;

    @FXML
    private Label labelUser;

    @FXML
    private AnchorPane pane1;

    @FXML
    private AnchorPane panel1;

    @FXML
    private AnchorPane panel2;

    @FXML
    private AnchorPane panel3;

    @FXML
    private AnchorPane panel0;

    @FXML
    private AnchorPane personalPanel;

    @FXML
    private Button personalAccount;

    @FXML
    private TableView<Day> tableWeather;

    @FXML
    private TextField textFieldCounty;

    @FXML
    private DatePicker textFieldDate;

    @FXML
    private TextField textFieldHumidity2;

    @FXML
    private TextField textFieldHumidity1;

    @FXML
    private TextField textFieldPrecipitation1;

    @FXML
    private TextField textFieldPrecipitation2;

    @FXML
    private TextField textFieldPressure1;

    @FXML
    private TextField textFieldPressure2;

    @FXML
    private TextField textFieldSearch;

    @FXML
    private TextField textFieldTemperature1;

    @FXML
    private TextField textFieldTemperature2;

    @FXML
    private TextField textFieldTown;

    @FXML
    private TextField textFieldWeather;

    @FXML
    private TextField textFieldWind1;

    @FXML
    private TextField textFieldWind2;

    @FXML
    private Label textC1;

    @FXML
    private Label textC2;


    @FXML
    private Label textK1;

    @FXML
    private Label textK2;

    @FXML
    private Label textKMH1;

    @FXML
    private Label textKMH2;

    @FXML
    private Label textMM1;

    @FXML
    private Label textMM2;

    @FXML
    private Label textMS1;

    @FXML
    private Label textMS2;
    @FXML
    private Label texthPa1;

    @FXML
    private Label texthPa2;

    @FXML
    private Label xHumidity1;

    @FXML
    private Label xHumidity2;

    @FXML
    private Label xPressure1;

    @FXML
    private Label xPressure2;

    @FXML
    private Label xRain1;

    @FXML
    private Label xRain2;

    @FXML
    private Label xTemperatura1;

    @FXML
    private Label xTemperatura2;

    @FXML
    private Label xWeatherName;

    @FXML
    private Label xWind1;

    @FXML
    private Label xWind2;
    @FXML
    private MenuButton region;

    @FXML
    private MenuButton regionFilter;


    @FXML
    private Button Change;
    @FXML
    private RadioButton C;

    @FXML
    private RadioButton K;

    @FXML
    private ToggleGroup Pressure;

    @FXML
    private ToggleGroup Speed;

    @FXML
    private ToggleGroup Temperature;


    @FXML
    private Button buttonSignUp;

    @FXML
    private RadioButton hPa;

    @FXML
    private RadioButton km;

    @FXML
    private Label labelMessage;

    @FXML
    private RadioButton m;

    @FXML
    private RadioButton mmHg;

    @FXML
    private PasswordField passwordfieldConfirmPassword;

    @FXML
    private PasswordField passwordfieldPassword;

    @FXML
    private CheckBox sendWeather;


    @FXML
    private TextField textfieldLogin;

    @FXML
    private TextField textfieldPhone;
    @FXML
    private Button firstPage;


    private User currentUser;
    private List<Location> regions;
    LocalDate today;
    Day todayWeather;
    private Location currentRegion;
    private String selectedTown;
    private ObservableList<Day> tableDays;
    private Day rowDay;

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


        initRegion();
        initMenuButtons();


    }


    public void switchForm(ActionEvent event) {
        if (event.getSource() == calendar) {
            panel1.setVisible(true);
            panel2.setVisible(false);
            panel3.setVisible(false);
            panel0.setVisible(false);
            personalPanel.setVisible(false);
            calendar.getStyleClass().add("button-menu:hover");
        } else if (event.getSource() == editDB) {
            panel1.setVisible(false);
            panel2.setVisible(true);
            panel3.setVisible(false);
            panel0.setVisible(false);
            personalPanel.setVisible(false);
            editDB.getStyleClass().add("button-menu:hover");
        } else if (event.getSource() == analitics) {
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
            menuItem1.setOnAction(event -> handleRegionSelection(displayText, temp.getTown()));
            regionFilter.getItems().add(menuItem1);
        }
    }

    // Метод для обработки выбора региона
    private void handleRegionSelection(String displayText, String town) {
        // Устанавливаем текст кнопки region
        region.setText(displayText);

        // Сохраняем выбранный город в переменной
        selectedTown = town;

        initUserWeather();
        initWeatherFields();
        todayWeatherPanel.setVisible(true);
    }


    //редактирование БД
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
            double t = Double.parseDouble(textFieldTemperature1.getText());
            int pr = Integer.parseInt(textFieldPressure1.getText());
            int h = Integer.parseInt(textFieldHumidity1.getText());
            double r = Double.parseDouble(textFieldPrecipitation1.getText());
            double w = Double.parseDouble(textFieldWind1.getText());
            WeatherParameters dayW = new WeatherParameters(t, pr, h, r, w);

            t = Double.parseDouble(textFieldTemperature2.getText());
            pr = Integer.parseInt(textFieldPressure2.getText());
            h = Integer.parseInt(textFieldHumidity2.getText());
            r = Double.parseDouble(textFieldPrecipitation2.getText());
            w = Double.parseDouble(textFieldWind2.getText());
            WeatherParameters nightW = new WeatherParameters(t, pr, h, r, w);

            WeatherName insertW = new WeatherName(textFieldWeather.getText());
            Location insertL = new Location(textFieldTown.getText(), textFieldCounty.getText());

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
        double t = Double.parseDouble(textFieldTemperature1.getText());
        int pr = Integer.parseInt(textFieldPressure1.getText());
        int h = Integer.parseInt(textFieldHumidity1.getText());
        double r = Double.parseDouble(textFieldPrecipitation1.getText());
        double w = Double.parseDouble(textFieldWind1.getText());
        WeatherParameters dayW = new WeatherParameters(rowDay.getDayWeather().getId(),t, pr, h, r, w);

        t = Double.parseDouble(textFieldTemperature2.getText());
        pr = Integer.parseInt(textFieldPressure2.getText());
        h = Integer.parseInt(textFieldHumidity2.getText());
        r = Double.parseDouble(textFieldPrecipitation2.getText());
        w = Double.parseDouble(textFieldWind2.getText());
        WeatherParameters nightW = new WeatherParameters(rowDay.getNightWeather().getId(),t, pr, h, r, w);

        WeatherName insertW = new WeatherName(rowDay.getWeatherName().getId(),textFieldWeather.getText());
        Location insertL = new Location(rowDay.getLocation().getId(),textFieldTown.getText(), textFieldCounty.getText());

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

    public void deleteDay()
    {
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
    void calendar_Pressed(ActionEvent event) {
        switchForm(event);
    }

    @FXML
    void analitics_Pressed(ActionEvent event) {
        switchForm(event);
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

        switchForm(event);
        initRegion(); //надо ли
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
            } else {
                labelMessage.setText(responseModel.getResponseMessage());
                labelMessage.setVisible(true);
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


}
