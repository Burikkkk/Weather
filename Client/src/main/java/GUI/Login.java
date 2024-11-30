package GUI;

import Enums.RequestType;
import Enums.ResponseStatus;
import Enums.Roles;
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

public class Login {

    @FXML
    private Button buttonForgotPassword;
    @FXML
    private Button buttonRegister;
    @FXML
    private Button buttonLogin;

    @FXML
    private Label labelMessage;

    @FXML
    private PasswordField passwordfieldPassword;

    @FXML
    private TextField textfieldLogin;

    @FXML
    void login_Pressed(ActionEvent event) throws IOException {
        labelMessage.setVisible(false);
        User user = new User();
        if (textfieldLogin.getText().isEmpty() || passwordfieldPassword.getText().isEmpty()) {
            labelMessage.setText("Введите логин и пароль!");
            labelMessage.setVisible(true);
            return;
        }
        user.setLogin(textfieldLogin.getText());
        String pass=passwordfieldPassword.getText();
        user.setPassword(User.getHash(pass));

        Request requestModel = new Request();
        requestModel.setRequestMessage(new Gson().toJson(user));
        requestModel.setRequestType(RequestType.LOGIN);
        ClientSocket.getInstance().getOut().println(new Gson().toJson(requestModel));
        ClientSocket.getInstance().getOut().flush();

        String answer = ClientSocket.getInstance().getInStream().readLine();
        Response responseModel = new Gson().fromJson(answer, Response.class);
        if (responseModel.getResponseStatus() == ResponseStatus.OK) {
            labelMessage.setVisible(false);
            ClientSocket.getInstance().setUser(new Gson().fromJson(responseModel.getResponseData(), User.class));
            Stage stage = (Stage) buttonLogin.getScene().getWindow();
            Parent root = null;
            labelMessage.setText(responseModel.getResponseMessage());
            labelMessage.setVisible(true);
            if (ClientSocket.getInstance().getUser() != null) {
                if (Roles.user.name().equals(ClientSocket.getInstance().getUser().getRole()))
                    root = FXMLLoader.load(getClass().getResource("/User_menu.fxml"));
                if (Roles.admin.name().equals(ClientSocket.getInstance().getUser().getRole()))
                    root = FXMLLoader.load(getClass().getResource("/Admin_menu.fxml"));
                else {
                }

                Scene newScene = new Scene(root);
                stage.setScene(newScene);

            }
        } else {
            labelMessage.setText(responseModel.getResponseMessage());
            labelMessage.setVisible(true);
        }
    }

    @FXML
    void register_Pressed(ActionEvent event) throws IOException {
        Stage stage = (Stage) buttonRegister.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/Register.fxml"));
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
    }

    @FXML
    public void password_Pressed(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) buttonForgotPassword.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/Forgot_password.fxml"));
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
    }
}

