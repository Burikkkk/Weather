package GUI.User;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.IOException;

public class UserMenu {

    @FXML
    private Button calendar;

    @FXML
    private Label currentDate;

    @FXML
    private TextArea parameters;

    @FXML
    private Button personalAccount;

    @FXML
    void calendar_Pressed(ActionEvent event) throws IOException {
        Stage stage = (Stage) calendar.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/Calendar.fxml"));
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
    }

    @FXML
    void personalAccount_Pressed(ActionEvent event) throws IOException {
        Stage stage = (Stage) personalAccount.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/Personal_account.fxml"));
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
    }

}
