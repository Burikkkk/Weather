package Controller.Entrance;

import Controller.Validation.ErrorStrategy;
import Controller.Validation.LabelErrorStrategy;
import Controller.Validation.LogErrorStrategy;
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

import java.io.IOException;

public class Login {

    @FXML
    private Button buttonForgotPassword,buttonRegister,buttonLogin;
    @FXML
    private Label labelMessage;
    @FXML
    private PasswordField passwordfieldPassword;
    @FXML
    private TextField textfieldLogin;

    @FXML
    void login_Pressed(ActionEvent event) throws IOException {
        // Установка стратегии обработки ошибок
        ErrorStrategy errorStrategy = new LabelErrorStrategy(labelMessage);

        // Валидация ввода
        if (textfieldLogin.getText().isEmpty() || passwordfieldPassword.getText().isEmpty()) {
            errorStrategy.handleError("Введите логин и пароль!");
            return;
        }
        // Создание пользователя
        User user = new User();
        user.setLogin(textfieldLogin.getText());
        user.setPassword(User.getHash(passwordfieldPassword.getText()));

        // Вызов сервиса авторизации
        EntranceService loginService = new EntranceService();
        User loggedInUser = loginService.login(user, labelMessage);

        if (loggedInUser != null) {
            ClientSocket.getInstance().setUser(loggedInUser);
            navigateToRoleScreen(ClientSocket.getInstance().getUser());
        } else {
            new LogErrorStrategy().handleError("Ошибка авторизации");
        }


    }

    private void navigateToRoleScreen(User user) throws IOException {
        Stage stage = (Stage) buttonLogin.getScene().getWindow();
        Parent root = null;

        if (user != null) {
            switch (user.getRole()) {
                case "user":
                    root = FXMLLoader.load(getClass().getResource("/fxml/User_menu.fxml"));
                    break;
                case "admin":
                    root = FXMLLoader.load(getClass().getResource("/fxml/Admin_menu.fxml"));
                    break;
                case "employee":
                    root = FXMLLoader.load(getClass().getResource("/fxml/Employee_menu.fxml"));
                    break;
            }
        }

        if (root != null) {
            Scene newScene = new Scene(root);
            stage.setScene(newScene);
            stage.centerOnScreen();
        }
    }


    @FXML
    void register_Pressed(ActionEvent event) throws IOException {
        Stage stage = (Stage) buttonRegister.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/Register.fxml"));
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
        stage.centerOnScreen();
    }

    @FXML
    public void password_Pressed(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) buttonForgotPassword.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/Forgot_password.fxml"));
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
        stage.centerOnScreen();
    }
}

