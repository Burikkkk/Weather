package Controller.Entrance;

import Controller.Validation.ErrorStrategy;
import Controller.Validation.LabelErrorStrategy;
import Controller.Validation.LogErrorStrategy;
import Controller.Validation.Validation;
import Enums.ResponseStatus;
import Models.Entities.User;
import Models.TCP.Response;
import Service.EntranceService;
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
    private Button buttonSignUp, buttonBack;
    @FXML
    private ToggleGroup Speed, Pressure, Temperature;
    @FXML
    private Label labelMessage;
    @FXML
    private PasswordField passwordfieldConfirmPassword, passwordfieldPassword;
    @FXML
    private TextField textfieldLogin, textfieldPhone;


    @FXML
    void back_Pressed(ActionEvent event) throws IOException {
        Stage stage = (Stage) buttonBack.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/Login.fxml"));
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
        stage.centerOnScreen();
    }


    @FXML
    void signup_Pressed(ActionEvent event) throws IOException {

        // Установка стратегии обработки ошибок
        ErrorStrategy errorStrategy = new LabelErrorStrategy(labelMessage);

        // Проверка совпадения паролей
        if (!passwordfieldPassword.getText().equals(passwordfieldConfirmPassword.getText())) {
            errorStrategy.handleError("Пароли не совпадают");
            return;
        }

        // Проверка на заполненность всех полей
        if (textfieldLogin.getText().isEmpty() || passwordfieldPassword.getText().isEmpty() || passwordfieldConfirmPassword.getText().isEmpty()) {
            errorStrategy.handleError("Не все поля заполнены.");
            return;
        }

        // Валидация логина
        Validation loginValidator = new Validation(errorStrategy);
        String tempLogin = loginValidator.validateLogin(textfieldLogin.getText());
        if ("error".equals(tempLogin)) return;

        // Создание пользователя
        User user = new User();
        user.setLogin(tempLogin);
        String pass = passwordfieldPassword.getText();
        user.setPassword(user.getHash(pass));

        // Настройки пользователя
        PersonalSettings settings = new PersonalSettings();

        // Валидация телефона
        String tempPhone = loginValidator.validatePhoneNumber(textfieldPhone.getText());
        if ("error".equals(tempPhone)) return;
        settings.setPhone(tempPhone);

        // Настройки температуры, давления и скорости
        String temperature = ((RadioButton) Temperature.getSelectedToggle()).getText();
        settings.setTemperature(temperature.equals("°F") ? "F" : "C");

        settings.setPressure(((RadioButton) Pressure.getSelectedToggle()).getText());
        settings.setSpeed(((RadioButton) Speed.getSelectedToggle()).getText());
        user.setPersonalSettings(settings);

        // Вызов сервиса регистрации
        EntranceService registerService = new EntranceService();
        User registeredUser = registerService.register(user, labelMessage);

        if (registeredUser != null) {
            ClientSocket.getInstance().setUser(registeredUser);
            navigateToLoginScreen();
        } else {
            new LogErrorStrategy().handleError("Ошибка регистрации");
        }

    }

    private void navigateToLoginScreen() throws IOException {
        Stage stage = (Stage) buttonSignUp.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/Login.fxml"));
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
        stage.centerOnScreen();
    }

}

