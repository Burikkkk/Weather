package GUI;

import Enums.RequestType;
import Enums.ResponseStatus;
import Models.Entities.User;
import Models.TCP.Request;
import Models.TCP.Response;
import Utilities.ClientSocket;
import com.google.gson.Gson;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import Models.Entities.PersonalSettings;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import java.io.IOException;

public class Register {


    @FXML
    private Button buttonSignUp;
    @FXML
    private ToggleGroup Speed;
    @FXML
    private ToggleGroup Pressure;
    @FXML
    private ToggleGroup Temperature;

    @FXML
    private Button buttonBack;

    @FXML
    private Label labelMessage;

    @FXML
    private PasswordField passwordfieldConfirmPassword;

    @FXML
    private PasswordField passwordfieldPassword;

    @FXML
    private TextField textfieldLogin;

    @FXML
    private TextField textfieldPhone;

    @FXML
    void back_Pressed(ActionEvent event) throws IOException {
        Stage stage = (Stage) buttonBack.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/Login.fxml"));
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
    }


    @FXML
    void signup_Pressed(ActionEvent event) throws IOException {
        if (!passwordfieldPassword.getText().equals(passwordfieldConfirmPassword.getText())) {
            labelMessage.setText("Пароли не совпадают");
            labelMessage.setVisible(true);
            return;
        }
        if (textfieldLogin.equals("") || passwordfieldPassword.equals("") || passwordfieldConfirmPassword.equals("")) {
            labelMessage.setText("Не все поля заполнены.");
            labelMessage.setVisible(true);
            return;
        }
        User user = new User();
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
        String pass=passwordfieldPassword.getText();

        byte[] temp=user.getHash(pass);
        user.setPassword(temp);

        PersonalSettings settings = new PersonalSettings();

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
        Request requestModel = new Request();
        requestModel.setRequestMessage(new Gson().toJson(user));
        requestModel.setRequestType(RequestType.REGISTER);
        ClientSocket.getInstance().getOut().println(new Gson().toJson(requestModel));
        ClientSocket.getInstance().getOut().flush();

        String answer = ClientSocket.getInstance().getInStream().readLine();
        Response responseModel = new Gson().fromJson(answer, Response.class);
        if (responseModel.getResponseStatus() == ResponseStatus.OK) {
            labelMessage.setVisible(false);
            ClientSocket.getInstance().setUser(new Gson().fromJson(responseModel.getResponseData(), User.class));
            Stage stage = (Stage) buttonSignUp.getScene().getWindow();
            Parent root;
            root = FXMLLoader.load(getClass().getResource("/User_menu.fxml")); //потом поменять
            Scene newScene = new Scene(root);
            stage.setScene(newScene);
        } else {
            labelMessage.setText(responseModel.getResponseMessage());
            labelMessage.setVisible(true);
        }
    }

}

