package GUI.Employee;

import Enums.RequestType;
import Enums.ResponseStatus;
import Enums.Roles;
import Models.Entities.Day;
import Models.Entities.Location;
import Models.Entities.PersonalSettings;
import Models.Entities.User;
import Models.TCP.Request;
import Models.TCP.Response;
import Utilities.ClientSocket;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
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
    private MenuButton country;

    @FXML
    private MenuButton countryFilter;

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
    private TextField textFieldDate;

    @FXML
    private TextField textFieldHumidity;

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
    private MenuButton town;

    @FXML
    private MenuButton townFilter;

    private User currentUser;
    private List<Location> regions;



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        currentUser = ClientSocket.getInstance().getUser();
        labelUser.setText(currentUser.getLogin());
        LocalDate today = LocalDate.now();
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
                new SimpleObjectProperty<>(cellData.getValue().getDayWeather().getPrecipitation()!= null ?
                        cellData.getValue().getDayWeather().getPrecipitation() : 0));
        columnWind1.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getDayWeather().getWindSpeed()!= null ?
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
                new SimpleObjectProperty<>(cellData.getValue().getNightWeather().getPrecipitation()!= null ?
                        cellData.getValue().getNightWeather().getPrecipitation() : 0));
        columnWind2.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getNightWeather().getWindSpeed()!= null ?
                        cellData.getValue().getNightWeather().getWindSpeed() : 0));

        columnWeatherName.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getWeatherName().getName()!= null ?
                        cellData.getValue().getWeatherName().getName() : ""));
        columnTown.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getLocation().getTown()!= null ?
                        cellData.getValue().getLocation().getTown() : ""));
        columnCountry.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getLocation().getCountry()!= null ?
                        cellData.getValue().getLocation().getCountry() : ""));


        initMenuButtons();
    }

    public void initWeatherTable()
    {

        Request requestModel = new Request();
        requestModel.setRequestType(RequestType.GET_ALL_DAYS);

        try {

            ClientSocket.getInstance().getOut().println(new Gson().toJson(requestModel));
            ClientSocket.getInstance().getOut().flush();

            String answer = ClientSocket.getInstance().getInStream().readLine();
            Response responseModel = new Gson().fromJson(answer, Response.class);

            if (responseModel.getResponseStatus() == ResponseStatus.OK) {
                // Используем TypeToken для точного указания типа списка
                Type weatherListType = new TypeToken<List<Day>>() {}.getType();
                List<Day> tableDays = new Gson().fromJson(responseModel.getResponseData(), weatherListType);

                // Преобразуем список в ObservableList и добавляем в таблицу
                tableWeather.setItems(FXCollections.observableArrayList(tableDays));

            } else {
                labelError.setText("Ошибка загрузки данных: " + responseModel.getResponseMessage());
                labelError.setVisible(true);
            }
        } catch (IOException e) {
            labelError.setText("Ошибка связи с сервером.");
            labelError.setVisible(true);
        }
    }

    public void initRegion()
    {
        Request requestModel = new Request();
        requestModel.setRequestType(RequestType.GET_REGION);

        try {

            ClientSocket.getInstance().getOut().println(new Gson().toJson(requestModel));
            ClientSocket.getInstance().getOut().flush();

            String answer = ClientSocket.getInstance().getInStream().readLine();
            Response responseModel = new Gson().fromJson(answer, Response.class);

            if (responseModel.getResponseStatus() == ResponseStatus.OK) {
                // Используем TypeToken для точного указания типа списка
                Type regionListType = new TypeToken<List<Location>>() {}.getType();
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

    public void initMenuButtons()
    {
        initRegion();
        for (Location temp : regions) {
                // Добавляем города
                addMenuItem(town, temp.getTown());
                addMenuItem(townFilter, temp.getTown());

                // Добавляем страны
                addMenuItem(country, temp.getCountry());
                addMenuItem(countryFilter, temp.getCountry());
        }
    }

    private void addMenuItem(MenuButton menuButton, String itemText) {
        if (itemText != null && !itemText.isEmpty()) {
            MenuItem menuItem = new MenuItem(itemText);
            menuButton.getItems().add(menuItem);
        }
    }

    @FXML
    void editDB_Pressed(ActionEvent event) {

        initWeatherTable();
        switchForm(event);
    }

    public void switchForm(ActionEvent event) {
        if(event.getSource() == calendar) {
            panel1.setVisible(true);
            panel2.setVisible(false);
            panel3.setVisible(false);
            panel0.setVisible(false);
            personalPanel.setVisible(false);
            calendar.getStyleClass().add("button-menu:hover");
        }
        else if(event.getSource() == editDB) {
            panel1.setVisible(false);
            panel2.setVisible(true);
            panel3.setVisible(false);
            panel0.setVisible(false);
            personalPanel.setVisible(false);
            editDB.getStyleClass().add("button-menu:hover");
        }
        else if(event.getSource() == analitics) {
            panel1.setVisible(false);
            panel2.setVisible(false);
            panel3.setVisible(true);
            panel0.setVisible(false);
            personalPanel.setVisible(false);
            analitics.getStyleClass().add("button-menu:hover");
        }
        else if(event.getSource() == personalAccount) {
            if (!personalPanel.isVisible()) {
                personalPanel.setVisible(true); // Делаем видимой
            } else {
                personalPanel.setVisible(false); // Иначе скрываем
            }

        }
        else if(event.getSource() == firstPage) {
            panel0.setVisible(true);
            panel1.setVisible(false);
            panel2.setVisible(false);
            panel3.setVisible(false);
            personalPanel.setVisible(false);

        }
    }

    @FXML
    private Button firstPage;

    @FXML
    void calendar_Pressed(ActionEvent event) throws IOException {
        switchForm(event);
    }

    @FXML
    void analitics_Pressed(ActionEvent event) {
        switchForm(event);
    }


    @FXML
    void countryFilter_Pressed(ActionEvent event) {

    }

    @FXML
    void country_Pressed(ActionEvent event) {

    }

    @FXML
    void townFilter_Pressed(ActionEvent event) {

    }

    @FXML
    void town_Pressed(ActionEvent event) {

    }

    @FXML
    void delete_Pressed(ActionEvent event) {

    }
    @FXML
    void clear_Pressed(ActionEvent event) {

    }



    @FXML
    void add_Pressed(ActionEvent event) {

    }
    @FXML
    void edit_Pressed(ActionEvent event) {

    }

    @FXML
    void exit_Pressed(ActionEvent event) throws IOException {

        Request requestModel = new Request();
        requestModel.setRequestMessage(new Gson().toJson(currentUser));
        requestModel.setRequestType(RequestType.LOGOUT);
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
    }

    @FXML
    void personalAccount_Pressed(ActionEvent event){
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
            }
        }
        switchForm(event);
    }

    @FXML
    void firstPage_Pressed(ActionEvent event) {

        switchForm(event);
    }




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
    private Button buttonBack;

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
    private Button phoneSend;

    @FXML
    private TextField textfieldLogin;

    @FXML
    private TextField textfieldPhone;

    @FXML
    private Button phoneUnsend;



    @FXML
    void phoneSend_Pressed(ActionEvent event) throws IOException {
        PersonalSettings settings = currentUser.getPersonalSettings();

        if(currentUser.getPersonalSettings().getNotifications()){
            labelMessage.setText("Рассылка уже подключена");
            labelMessage.setVisible(true);}
        else {
            settings.setNotifications(true);
            currentUser.setPersonalSettings(settings);
            if (currentUser.getPersonalSettings().getPhone() != null) {
                Request requestModel = new Request();
                requestModel.setRequestMessage(new Gson().toJson(currentUser));
                requestModel.setRequestType(RequestType.UPDATE_USER);
                ClientSocket.getInstance().getOut().println(new Gson().toJson(requestModel));
                ClientSocket.getInstance().getOut().flush();

                String answer = ClientSocket.getInstance().getInStream().readLine();
                Response responseModel = new Gson().fromJson(answer, Response.class);
                if (responseModel.getResponseStatus() == ResponseStatus.OK) {
                    ClientSocket.getInstance().setUser(new Gson().fromJson(responseModel.getResponseData(), User.class));
                    labelMessage.setText("Рассылка подключена");
                    labelMessage.setVisible(true);
                } else {
                    labelMessage.setText(responseModel.getResponseMessage());
                    labelMessage.setVisible(true);
                }
            } else {
                labelMessage.setText("Номер телефона не указан");
                labelMessage.setVisible(true);
            }
        }
    }

    @FXML
    void phoneUnsend_Pressed(ActionEvent event) throws IOException {
        PersonalSettings settings = currentUser.getPersonalSettings();

        if(currentUser.getPersonalSettings().getNotifications()==false){
            labelMessage.setText("Рассылка не подключена");
            labelMessage.setVisible(true);}
        else {
            settings.setNotifications(false);
            currentUser.setPersonalSettings(settings);
            if (currentUser.getPersonalSettings().getPhone() != null) {
                Request requestModel = new Request();
                requestModel.setRequestMessage(new Gson().toJson(currentUser));
                requestModel.setRequestType(RequestType.UPDATE_USER);
                ClientSocket.getInstance().getOut().println(new Gson().toJson(requestModel));
                ClientSocket.getInstance().getOut().flush();

                String answer = ClientSocket.getInstance().getInStream().readLine();
                Response responseModel = new Gson().fromJson(answer, Response.class);
                if (responseModel.getResponseStatus() == ResponseStatus.OK) {
                    ClientSocket.getInstance().setUser(new Gson().fromJson(responseModel.getResponseData(), User.class));
                    labelMessage.setText("Рассылка отключена");
                    labelMessage.setVisible(true);
                } else {
                    labelMessage.setText(responseModel.getResponseMessage());
                    labelMessage.setVisible(true);
                }
            } else {
                labelMessage.setText("Номер телефона не указан");
                labelMessage.setVisible(true);
            }
        }
    }

    @FXML
    void change_Pressed(ActionEvent actionEvent) throws IOException {
        if (!passwordfieldPassword.getText().equals(passwordfieldConfirmPassword.getText())) {
            labelMessage.setText("Пароли не совпадают");
            labelMessage.setVisible(true);
            return;
        }
        if (textfieldLogin.equals("") || passwordfieldPassword.equals("") || passwordfieldConfirmPassword.equals("")||textfieldPhone.equals("")) {
            labelMessage.setText("Не все поля заполнены.");
            labelMessage.setVisible(true);
            return;
        }
        User user = new User();
        user.setId(currentUser.getId());
        String tempLogin = textfieldLogin.getText();

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
        if(!passwordfieldPassword.getText().equals("11111")) {
            String pass = passwordfieldPassword.getText();

            byte[] temp = user.getHash(pass);
            user.setPassword(temp);
        }
        else
        {
            user.setPassword(currentUser.getPassword());
        }
        PersonalSettings settings = new PersonalSettings();
        settings.setId(currentUser.getPersonalSettings().getId());
        String tempPhone= textfieldPhone.getText();
        if (tempPhone.length() != 9 || !tempPhone.matches("\\d+"))
        {
            labelMessage.setText("Введите номер телефона с кодом!");
            labelMessage.setVisible(true);
            return;
        }
        settings.setPhone(tempPhone);

        String temperature = ((RadioButton) Temperature.getSelectedToggle()).getText();
        if(temperature.equals("K"))
            settings.setTemperature(temperature);
        else
            settings.setTemperature("C");
        String pressure = ((RadioButton) Pressure.getSelectedToggle()).getText();
        String speed = ((RadioButton) Speed.getSelectedToggle()).getText();
        settings.setPressure(pressure);
        settings.setSpeed(speed);
        user.setPersonalSettings(settings);

        if(!user.equals(currentUser)) {
            Request requestModel = new Request();
            requestModel.setRequestMessage(new Gson().toJson(user));
            requestModel.setRequestType(RequestType.UPDATE_USER);
            ClientSocket.getInstance().getOut().println(new Gson().toJson(requestModel));
            ClientSocket.getInstance().getOut().flush();

            String answer = ClientSocket.getInstance().getInStream().readLine();
            Response responseModel = new Gson().fromJson(answer, Response.class);
            if (responseModel.getResponseStatus() == ResponseStatus.OK) {
                ClientSocket.getInstance().setUser(new Gson().fromJson(responseModel.getResponseData(), User.class));
                labelMessage.setText("Данные изменены!");
                labelMessage.setVisible(true);
            } else {
                labelMessage.setText(responseModel.getResponseMessage());
                labelMessage.setVisible(true);
            }
        }
        else
        {
            labelMessage.setText("Введите новые данные!");
            labelMessage.setVisible(true);
        }
    }



}
