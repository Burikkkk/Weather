import Enums.RequestType;
import Enums.ResponseStatus;
import Models.Entities.User;
import Models.TCP.Request;
import Models.TCP.Response;
import Utilities.ClientSocket;
import Utilities.GetService;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Client extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        ClientSocket.getInstance().getSocket();
        Parent root = FXMLLoader.load(getClass().getResource("/Login.fxml"));
        primaryStage.setTitle("Добро пожаловать!");
        primaryStage.setScene(new Scene(root, 314, 358));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}



