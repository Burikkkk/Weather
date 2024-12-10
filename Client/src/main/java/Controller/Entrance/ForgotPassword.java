package Controller.Entrance;

import Controller.Validation.ErrorStrategy;
import Controller.Validation.LabelErrorStrategy;
import Controller.Validation.LogErrorStrategy;
import Enums.ResponseStatus;
import Models.Entities.PersonalSettings;
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

public class ForgotPassword {

    private User user;

    @FXML
    private Button buttonSave, buttonBack, buttonFindUser;
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
    void findUser_Pressed(ActionEvent event) {

        ErrorStrategy errorStrategy = new LabelErrorStrategy(labelMessage);

        // Валидация ввода
        if (textfieldLogin.getText().isEmpty() || textfieldPhone.getText().isEmpty()) {
            errorStrategy.handleError("Введите логин и телефон!");
            return;
        }

        // Создание пользователя
        User user = new User();
        user.setLogin(textfieldLogin.getText());

        PersonalSettings settings = new PersonalSettings();
        settings.setPhone(textfieldPhone.getText());
        user.setPersonalSettings(settings);

        // Вызов сервиса восстановления пароля
        EntranceService forgotPasswordService = new EntranceService();
        User foundUser = forgotPasswordService.findUser(user, labelMessage);

        if (foundUser != null) {
            ClientSocket.getInstance().setUser(foundUser);
            this.user = foundUser;

            switchToPasswordInput();
        }else {
            new LogErrorStrategy().handleError("Ошибка поиска пользователя");
        }

    }

    @FXML
    void savePassword_Pressed(ActionEvent actionEvent) throws IOException {

        ErrorStrategy errorStrategy = new LabelErrorStrategy(labelMessage);

        // Валидация ввода
        if (!passwordfieldPassword.getText().equals(passwordfieldConfirmPassword.getText())) {
            errorStrategy.handleError("Пароли не совпадают");
            return;
        }

        if (passwordfieldPassword.getText().isEmpty() || passwordfieldConfirmPassword.getText().isEmpty()) {
            errorStrategy.handleError("Не все поля заполнены.");
            return;
        }

        // Обновление пароля
        user.setPassword(User.getHash(passwordfieldPassword.getText()));

        EntranceService updatePasswordService = new EntranceService();
        User updatedUser = updatePasswordService.updateUserPassword(user, labelMessage);

        if (updatedUser != null) {
            ClientSocket.getInstance().setUser(updatedUser);
            navigateToLoginScreen();
        }else {
            new LogErrorStrategy().handleError("Ошибка изменения пароля");
        }

    }


    private void switchToPasswordInput() {
        textfieldLogin.setVisible(false);
        textfieldPhone.setVisible(false);
        textfieldLogin.setDisable(true);
        textfieldPhone.setDisable(true);

        passwordfieldPassword.setDisable(false);
        passwordfieldPassword.setVisible(true);
        passwordfieldConfirmPassword.setDisable(false);
        passwordfieldConfirmPassword.setVisible(true);
        buttonSave.setDisable(false);
        buttonSave.setVisible(true);
    }

    private void navigateToLoginScreen() throws IOException {
        Stage stage = (Stage) buttonSave.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/Login.fxml"));
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
        stage.centerOnScreen();
    }


}