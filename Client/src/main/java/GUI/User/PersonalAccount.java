package GUI.User;

import Enums.RequestType;
import Enums.ResponseStatus;
import Models.Entities.PersonalSettings;
import Models.Entities.User;
import Models.TCP.Request;
import Models.TCP.Response;
import Utilities.ClientSocket;
import com.google.gson.Gson;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PersonalAccount implements Initializable {

private User currentUser;
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
    void back_Pressed(ActionEvent event) throws IOException {
        Stage stage = (Stage) buttonBack.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/User_menu.fxml"));
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // Получаем текущего пользователя
        currentUser = ClientSocket.getInstance().getUser();

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
    }


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
