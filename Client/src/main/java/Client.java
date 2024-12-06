import Utilities.ClientSocket;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Client extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        ClientSocket.getInstance().getSocket();
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/Login.fxml"));
        primaryStage.setTitle("Добро пожаловать!");
        primaryStage.setScene(new Scene(root, 314, 358));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}



