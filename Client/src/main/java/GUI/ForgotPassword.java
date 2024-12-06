package GUI;

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
    private Button buttonSave;
    @FXML
    private Button buttonBack;

    @FXML
    private Button buttonFindUser;

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
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/Login.fxml"));
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
        stage.centerOnScreen();
    }

    @FXML
    void findUser_Pressed(ActionEvent event) throws IOException {

        user = new User();
        if (textfieldLogin.getText().isEmpty() || textfieldPhone.getText().isEmpty()) {
            labelMessage.setText("Введите логин и телефон!");
            labelMessage.setVisible(true);
            return;
        }
        user.setLogin(textfieldLogin.getText());

        PersonalSettings settings = new PersonalSettings();
        settings.setPhone(textfieldPhone.getText());

        user.setPersonalSettings(settings);

        Request requestModel = new Request();
        requestModel.setRequestMessage(new Gson().toJson(user));
        requestModel.setRequestType(RequestType.FORGOT_PASSWORD);
        ClientSocket.getInstance().getOut().println(new Gson().toJson(requestModel));
        ClientSocket.getInstance().getOut().flush();

        String answer = ClientSocket.getInstance().getInStream().readLine();
        Response responseModel = new Gson().fromJson(answer, Response.class);
        if (responseModel.getResponseStatus() == ResponseStatus.OK) {
            labelMessage.setVisible(false);
            ClientSocket.getInstance().setUser(new Gson().fromJson(responseModel.getResponseData(), User.class));


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



        } else {
            labelMessage.setText(responseModel.getResponseMessage());
            labelMessage.setVisible(true);
        }
    }

    @FXML
    void savePassword_Pressed(ActionEvent actionEvent) throws IOException {
        if (!passwordfieldPassword.getText().equals(passwordfieldConfirmPassword.getText())) {
            labelMessage.setText("Пароли не совпадают");
            labelMessage.setVisible(true);
            return;
        }
        if(passwordfieldPassword.getText().isEmpty() || passwordfieldConfirmPassword.getText().isEmpty()){
            labelMessage.setText("Не все поля заполнены.");
            labelMessage.setVisible(true);
            return;
        }
        String pass=passwordfieldPassword.getText();
        byte[] temp=user.getHash(pass);
        user.setPassword(temp);
        Request requestModel = new Request();
        requestModel.setRequestMessage(new Gson().toJson(user));
        requestModel.setRequestType(RequestType.UPDATE_PASSWORD);
        ClientSocket.getInstance().getOut().println(new Gson().toJson(requestModel));
        ClientSocket.getInstance().getOut().flush();

        String answer = ClientSocket.getInstance().getInStream().readLine();
        Response responseModel = new Gson().fromJson(answer, Response.class);
        if (responseModel.getResponseStatus() == ResponseStatus.OK) {
            labelMessage.setVisible(false);
            ClientSocket.getInstance().setUser(new Gson().fromJson(responseModel.getResponseData(), User.class));
            Stage stage = (Stage) buttonSave.getScene().getWindow();
            Parent root;
            root = FXMLLoader.load(getClass().getResource("/fxml/Login.fxml"));
            Scene newScene = new Scene(root);
            stage.setScene(newScene);
            stage.centerOnScreen();
        } else {
            labelMessage.setText(responseModel.getResponseMessage());
            labelMessage.setVisible(true);
        }


    }


}