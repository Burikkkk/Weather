package GUI.Admin;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminMenu {

    @FXML
    private Button personalAccount;

    @FXML
    private Button statistic;

    @FXML
    private Button usersControl;

    @FXML
    void personalAccount_Pressed(ActionEvent event) throws IOException {
        Stage stage = (Stage) personalAccount.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/Personal_account_admin.fxml"));
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
    }

    @FXML
    void statistic_Pressed(ActionEvent event) throws IOException {
        Stage stage = (Stage) statistic.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/Admin_statistic.fxml"));
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
    }

    @FXML
    void usersControl_Pressed(ActionEvent event) throws IOException {
        Stage stage = (Stage) usersControl.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/Role_control.fxml"));
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
    }

}
