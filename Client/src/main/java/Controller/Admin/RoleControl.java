package Controller.Admin;

import Enums.RequestType;
import Enums.ResponseStatus;
import Models.Entities.User;
import Models.TCP.Request;
import Models.TCP.Response;
import Utilities.ClientSocket;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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

public class RoleControl implements Initializable {

    private ComboBox<String> roleComboBox;

    @FXML
    private ComboBox comboBox;

    @FXML
    private Button backButton;

    @FXML
    private Button changeRole;

    @FXML
    private TableColumn<User, String> loginColumn;

    @FXML
    private TableColumn<User, String> notificationsColumn;

    @FXML
    private TableColumn<User, String> phoneColumn;

    @FXML
    private TableColumn<User, String> pressureColumn;

    @FXML
    private TableColumn<User, String> roleColumn;

    @FXML
    private TableColumn<User, String> speedColumn;

    @FXML
    private Label statusMessage;

    @FXML
    private TableColumn<User, String> temperatureColumn;

    @FXML
    private TableView<User> usersTable;

    @FXML
    void back_Pressed(ActionEvent event) throws IOException {
        Stage stage = (Stage) backButton.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/Admin_menu.fxml"));
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
    }

    @FXML
    void changeRole_Pressed(ActionEvent event) throws IOException {

        Type userListType = new TypeToken<List<User>>() {}.getType();
        List<User> users = usersTable.getItems();

        // Формируем запрос
        Request requestModel = new Request();
        requestModel.setRequestMessage(new Gson().toJson(users));
        requestModel.setRequestType(RequestType.UPDATE_ALL_USERS);


        // Отправляем запрос на сервер
        ClientSocket.getInstance().getOut().println(new Gson().toJson(requestModel));
        ClientSocket.getInstance().getOut().flush();

        // Читаем ответ
        String answer = ClientSocket.getInstance().getInStream().readLine();
        Response responseModel = new Gson().fromJson(answer, Response.class);

        if (responseModel.getResponseStatus() == ResponseStatus.OK) {

            statusMessage.setText("Роль пользователя успешно обновлена.");
            statusMessage.setTextFill(Color.GREEN);
            statusMessage.setVisible(true);

            // Обновляем данные таблицы
            usersTable.refresh();
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

        // Колонка для телефона (данные вложены в PersonalSettings)
        phoneColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getPersonalSettings() != null ?
                        cellData.getValue().getPersonalSettings().getPhone() : ""));

        // Колонка для уведомлений (булевое значение)
        notificationsColumn.setCellValueFactory(cellData -> {
            boolean notifications = cellData.getValue().getPersonalSettings().getNotifications();
            return new SimpleStringProperty(notifications ? "вкл" : "выкл");
        });

        // Колонка для температуры (строка из PersonalSettings)
        temperatureColumn.setCellValueFactory(cellData ->{
                String temp = cellData.getValue().getPersonalSettings().getTemperature();
                return new SimpleStringProperty(temp.equals("C") ? "°C" : "K");
        });

        // Колонка для давления (строка из PersonalSettings)
        pressureColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getPersonalSettings() != null ?
                        cellData.getValue().getPersonalSettings().getPressure() : ""));

        // Колонка для скорости (строка из PersonalSettings)
        speedColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getPersonalSettings() != null ?
                        cellData.getValue().getPersonalSettings().getSpeed() : ""));



        roleColumn.setCellFactory(new Callback<TableColumn<User, String>, TableCell<User, String>>() {
            @Override
            public TableCell<User, String> call(TableColumn<User, String> param) {
                return new TableCell<User, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);

                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            // Создаём ComboBox для выбора роли
                            ComboBox<String> roleComboBox = new ComboBox<>();
                            roleComboBox.getItems().addAll("user", "admin", "employee");

                            // Устанавливаем текущее значение роли в ComboBox
                            roleComboBox.setValue(item);

                            roleComboBox.setOnAction(event -> {
                                String selectedRole = roleComboBox.getValue();

                                usersTable.getItems().get(getIndex()).setRole(selectedRole);
                                getTableView().refresh();
                            });

                            roleComboBox.setStyle(comboBox.getStyle());
                            // Настроим ComboBox, чтобы растягивался по ширине столбца
                            roleComboBox.setMaxWidth(Double.MAX_VALUE);  // Максимальная ширина
                            HBox.setHgrow(roleComboBox, Priority.ALWAYS);  // Растягиваем по ширине ячейки

                            // Устанавливаем ComboBox в ячейку
                            setGraphic(roleComboBox);
                            setText(null); // Убираем текстовое представление
                        }
                    }
                };
            }
        });


        // Формируем запрос для получения списка пользователей
        Request requestModel = new Request();
        requestModel.setRequestType(RequestType.GET_ALL_USERS);

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
