package GUI.Admin;

import Enums.RequestType;
import Enums.ResponseStatus;
import Models.Entities.PersonalSettings;
import Models.Entities.User;
import Models.TCP.Request;
import Models.TCP.Response;
import Utilities.ClientSocket;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class Statistic implements Initializable{
    @FXML
    private Label loginStyle;
    @FXML
    private Label amount;

    @FXML
    private Button backButton;

    @FXML
    private TableColumn<User, String> loginColumn;

    @FXML
    private TableColumn<User, String> roleColumn;

    @FXML
    private Label statusMessage;

    @FXML
    private Button update;

    @FXML
    private TableView<User> usersTable;

    @FXML
    void back_Pressed(ActionEvent event) throws IOException {
        Stage stage = (Stage) backButton.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/Admin_menu.fxml"));
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
    }

    @FXML
    void update_Pressed(ActionEvent event) throws IOException {
        // Формируем запрос для получения списка подключенных пользователей
        Request requestModel = new Request();
        requestModel.setRequestType(RequestType.GET_CONNECTED_USERS);


        // Отправляем запрос
        ClientSocket.getInstance().getOut().println(new Gson().toJson(requestModel));
        ClientSocket.getInstance().getOut().flush();

        // Читаем ответ
        String answer = ClientSocket.getInstance().getInStream().readLine();
        Response responseModel = new Gson().fromJson(answer, Response.class);

        if (responseModel.getResponseStatus() == ResponseStatus.OK) {
            // Используем TypeToken для точного указания типа списка
            Type userListType = new TypeToken<List<User>>() {}.getType();
            List<User> users = new Gson().fromJson(responseModel.getResponseData(), userListType);

            // Преобразуем список в ObservableList и обновляем таблицу
            usersTable.setItems(FXCollections.observableArrayList(users));

            usersTable.setRowFactory(tv -> {
                TableRow<User> row = new TableRow<>();
                row.itemProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        // Сравниваем логин текущего пользователя с логином строки
                        if (newValue.getLogin().equals(ClientSocket.getInstance().getUser().getLogin())) {
                            row.setStyle("-fx-background-color: lightblue;"); // Устанавливаем стиль строки
                        } else {
                            row.setStyle(""); // Сбрасываем стиль для остальных строк
                        }
                    }
                });
                return row;
            });

            amount.setText(String.valueOf(users.size()));
            usersTable.refresh();
            statusMessage.setText("Данные успешно обновлены.");
            statusMessage.setTextFill(Color.GREEN);
            statusMessage.setVisible(true);
        } else {
            statusMessage.setText(responseModel.getResponseMessage());
            statusMessage.setTextFill(Color.RED);
            statusMessage.setVisible(true);
        }

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Настройка колонок таблицы
        loginColumn.setCellValueFactory(new PropertyValueFactory<>("login"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));

        // Формируем запрос для получения списка пользователей
        Request requestModel = new Request();
        requestModel.setRequestType(RequestType.GET_CONNECTED_USERS);

        try {
            // Отправляем запрос
            ClientSocket.getInstance().getOut().println(new Gson().toJson(requestModel));
            ClientSocket.getInstance().getOut().flush();

            // Читаем ответ
            String answer = ClientSocket.getInstance().getInStream().readLine();
            Response responseModel = new Gson().fromJson(answer, Response.class);

            if (responseModel.getResponseStatus() == ResponseStatus.OK) {
                // Используем TypeToken для точного указания типа списка
                Type userListType = new TypeToken<List<User>>() {}.getType();
                List<User> users = new Gson().fromJson(responseModel.getResponseData(), userListType);

                // Преобразуем список в ObservableList и добавляем в таблицу
                usersTable.setItems(FXCollections.observableArrayList(users));

                usersTable.setRowFactory(tv -> {
                    TableRow<User> row = new TableRow<>();
                    row.itemProperty().addListener((observable, oldValue, newValue) -> {
                        if (newValue != null) {
                            // Сравниваем логин текущего пользователя с логином строки
                            if (newValue.getLogin().equals(ClientSocket.getInstance().getUser().getLogin())) {
                                row.setStyle("-fx-background-color: lightblue;"); // Устанавливаем стиль строки
                            } else {
                                row.setStyle(""); // Сбрасываем стиль для остальных строк
                            }
                        }
                    });
                    return row;
                });

                amount.setText(String.valueOf(users.size()));
            } else {
                statusMessage.setText("Ошибка загрузки данных: " + responseModel.getResponseMessage());
                statusMessage.setTextFill(Color.RED);
                statusMessage.setVisible(true);
            }
        } catch (IOException e) {
            statusMessage.setText("Ошибка связи с сервером.");
            statusMessage.setTextFill(Color.RED);
            statusMessage.setVisible(true);
        }
    }

}

