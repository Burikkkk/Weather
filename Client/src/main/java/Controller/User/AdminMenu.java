package Controller.User;

import Models.Entities.User;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.layout.*;

import java.net.URL;
import java.util.ResourceBundle;
import Controller.Calendar.CalendarData;
import Controller.File.AnaliticsData;
import Controller.File.Regions;
import Controller.Validation.ErrorStrategy;
import Controller.Validation.LabelErrorStrategy;
import Controller.Validation.LogErrorStrategy;
import Controller.Validation.Validation;
import Enums.Months;
import Service.DashboardService;
import Service.DayService;
import Service.LocationService;
import Service.UserService;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;

import java.io.*;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import Models.Entities.*;
import Utilities.ClientSocket;
import com.google.gson.Gson;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


public class AdminMenu implements Initializable {

    @FXML
    private TableColumn<User, String> loginColumn1 ,roleColumn1,
            loginColumn, notificationsColumn, phoneColumn,temperatureColumn, pressureColumn, speedColumn;

    @FXML
    private TableView<User> statisticTable, rolesTable;

    @FXML
    private Button update, Changec, hangeRole, exit, loginStatisticBtn, personalAccount, roleControlBtn;

    @FXML
    private Label labelMessage, labelError, labelUser, amount;

    @FXML
    private PasswordField passwordfieldConfirmPassword, passwordfieldPassword;

    @FXML
    private TextField textfieldLogin, textfieldPhone, searchRolesTextfield, searchStatisticTextfield;

    @FXML
    private AnchorPane personalPanel, rolesPanel, statisticPanel;

    private User currentUser;
    private ObservableList<User> tableConnectedUsers;
    private ObservableList<User> tableUsersRoles;
    private UserService userService = new UserService();




    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Получаем текущего пользователя
        currentUser = ClientSocket.getInstance().getUser();
        labelUser.setText(currentUser.getLogin());
        initPersonalAccount();
        statisticTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        rolesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        loginColumn1.setCellValueFactory(new PropertyValueFactory<>("login"));
        roleColumn1.setCellValueFactory(new PropertyValueFactory<>("role"));

        rolesTable.setEditable(true); // Таблица должна быть редактируемой
        loginColumn.setEditable(false);
        phoneColumn.setEditable(false);
        notificationsColumn.setEditable(false);
        temperatureColumn.setEditable(false);
        pressureColumn.setEditable(false);
        speedColumn.setEditable(false);

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
            return new SimpleStringProperty(temp.equals("C") ? "°C" : "°F");
        });

        // Колонка для давления (строка из PersonalSettings)
        pressureColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getPersonalSettings() != null ?
                        cellData.getValue().getPersonalSettings().getPressure() : ""));

        // Колонка для скорости (строка из PersonalSettings)
        speedColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getPersonalSettings() != null ?
                        cellData.getValue().getPersonalSettings().getSpeed() : ""));

        TableColumn<User, String> roleColumn = new TableColumn<>("Роль");
        roleColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getRole())
        );
        roleColumn.setCellFactory(ComboBoxTableCell.forTableColumn("user", "employee", "admin"));

        roleColumn.setOnEditCommit(event -> {
            User user = event.getRowValue();
            String newRole = event.getNewValue();
            user.setRole(newRole);

        });
        rolesTable.getColumns().add(roleColumn);
        initStatisticTable();
    }

//    void selectedRole(){
////        // Устанавливаем текущее значение роли в ComboBox
////        roleComboBox.setValue(item);
////
////        roleComboBox.setOnAction(event -> {
////            String selectedRole = roleComboBox.getValue();
////
////            usersTable.getItems().get(getIndex()).setRole(selectedRole);
////            getTableView().refresh();
////        });
////
////        roleComboBox.setStyle(comboBox.getStyle());
////        // Настроим ComboBox, чтобы растягивался по ширине столбца
////        roleComboBox.setMaxWidth(Double.MAX_VALUE);  // Максимальная ширина
////        HBox.setHgrow(roleComboBox, Priority.ALWAYS);  // Растягиваем по ширине ячейки
////
////        // Устанавливаем ComboBox в ячейку
////        setGraphic(roleComboBox);
////        setText(null); // Убираем текстовое представление
//
//        roleColumn.setOnEditCommit(event -> {
//            User user = event.getRowValue();
//            String newRole = event.getNewValue();
//
//            if ("user".equals(newRole)||"admin".equals(newRole)||"employee".equals(newRole)) {
//                user.setRole(newRole);
//
//            }
//        });
//    }



    public void switchForm(ActionEvent event) {
        if (event.getSource() == loginStatisticBtn) {
            labelError.setVisible(false);
            statisticPanel.setVisible(true);
            rolesPanel.setVisible(false);
            personalPanel.setVisible(false);
        } else if (event.getSource() == roleControlBtn) {
            labelError.setVisible(false);
            statisticPanel.setVisible(false);
            rolesPanel.setVisible(true);
            personalPanel.setVisible(false);
        } else if (event.getSource() == personalAccount) {
            if (!personalPanel.isVisible()) {
                //initPersonalAccount();
                labelMessage.setVisible(false);
                personalPanel.setVisible(true); // Делаем видимой
            } else {
                personalPanel.setVisible(false); // Иначе скрываем
                labelMessage.setVisible(false);
            }
        }
    }


    void initStatisticTable(){
        List<User> connected = userService.getConnectedUsers(labelError);

        if (connected != null) {
            // Если данные успешно получены, заполняем таблицу
            tableConnectedUsers = FXCollections.observableArrayList(connected);
            statisticTable.setItems(tableConnectedUsers);
            statisticTable.setRowFactory(tv -> {
                TableRow<User> row = new TableRow<>();
                row.itemProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        // Сравниваем логин текущего пользователя с логином строки
                        if (newValue.getLogin().equals(ClientSocket.getInstance().getUser().getLogin())) {
                            row.getStyleClass().add("table-row"); // Устанавливаем стиль строки
                        } else {
                            row.getStyleClass().remove("table-row");
                        }
                    }
                });
                return row;
            });
            amount.setVisible(true);
            amount.setText(String.valueOf(tableConnectedUsers.size()));
        } else
            new LogErrorStrategy().handleError("Ошибка получения подключенных пользователей");
    }

    void initRolesTable(){
        List<User> roles = userService.getAllUsers(labelError);

        if (roles != null) {
            // Если данные успешно получены, заполняем таблицу
            tableUsersRoles = FXCollections.observableArrayList(roles);
            rolesTable.setItems(tableUsersRoles);
        } else
            new LogErrorStrategy().handleError("Ошибка получения пользователей");
    }


    void initPersonalAccount(){
        if (currentUser != null) {
            // Инициализируем логин
            textfieldLogin.setText(currentUser.getLogin());
            passwordfieldPassword.setText("11111");
            passwordfieldConfirmPassword.setText("11111");
            // Инициализируем личные настройки пользователя
            PersonalSettings settings = currentUser.getPersonalSettings();
            if (settings != null) {
                // Телефон
                textfieldPhone.setText(settings.getPhone());

            }
        }
    }

    @FXML
    void changeRole_Pressed(ActionEvent event) {
        String result=userService.updateAllUsers(tableUsersRoles,labelError);
        ErrorStrategy errorStrategy = new LabelErrorStrategy(labelError);
        if (result.equals("OK")) {
            errorStrategy.handleError("Роли обновлены");
        }
        else
            new LogErrorStrategy().handleError("Ошибка обновления ролей");
    }

    @FXML
    void change_Pressed(ActionEvent event) {
        updateUser();
    }

    public void updateUser() {
        ErrorStrategy errorStrategy = new LabelErrorStrategy(labelMessage);
        if (!passwordfieldPassword.getText().equals(passwordfieldConfirmPassword.getText())) {
            errorStrategy.handleError("Пароли не совпадают");
            return;
        }
        if (textfieldLogin.getText().isEmpty() ||
                passwordfieldPassword.getText().isEmpty() ||
                passwordfieldConfirmPassword.getText().isEmpty() ||
                textfieldPhone.getText().isEmpty()) {
            errorStrategy.handleError("Не все поля заполнены.");
            return;
        }
        User user = new User();
        user.setId(currentUser.getId());
        user.setRole(currentUser.getRole());
        // Валидация логина
        Validation loginValidator = new Validation(errorStrategy);
        String tempLogin = loginValidator.validateLogin(textfieldLogin.getText());
        if ("error".equals(tempLogin)) return;
        user.setLogin(tempLogin);

        if (!passwordfieldPassword.getText().equals("11111")) {
            String pass = passwordfieldPassword.getText();

            byte[] temp = user.getHash(pass);
            user.setPassword(temp);
        } else {
            user.setPassword(currentUser.getPassword());
        }
        PersonalSettings settings = new PersonalSettings();
        settings.setId(currentUser.getPersonalSettings().getId());

        // Валидация телефона
        String tempPhone = loginValidator.validatePhoneNumber(textfieldPhone.getText());
        if ("error".equals(tempPhone)) return;
        settings.setPhone(tempPhone);

        user.setPersonalSettings(settings);

        if (!user.equals(currentUser)) {
            user=userService.update(user,labelMessage);
            if(user!=null) {
                ClientSocket.getInstance().setUser(user);
                errorStrategy.handleError("Данные изменены!");
                currentUser = ClientSocket.getInstance().getUser();

                //заново проинициализировать поля
                labelUser.setText(currentUser.getLogin());
                initRolesTable();
                initStatisticTable();
            }
            else
                new LogErrorStrategy().handleError("Ошибка редактирования настроек");
        } else {
            errorStrategy.handleError("Введите новые данные!");
        }
    }


    @FXML
    void exit_Pressed(ActionEvent event) throws IOException {
        exit();
    }

    public void exit() throws IOException {
        User temp = userService.logout(currentUser, labelError);
        if(temp!=null){
            ClientSocket.getInstance().setUser(temp);
            ClientSocket.resetConnection();
            Stage stage = (Stage) exit.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/Login.fxml"));
            Scene newScene = new Scene(root);
            stage.setScene(newScene);
            stage.centerOnScreen();
        }
        else
            new LogErrorStrategy().handleError("Ошибка выхода");
    }


    @FXML
    void personalAccount_Pressed(ActionEvent event) {
        initPersonalAccount();
        switchForm(event);
    }

    @FXML
    void roleControlBtn_Pressed(ActionEvent event) {
        initRolesTable();
        switchForm(event);

    }

    @FXML
    void loginStatisticBtn_Pressed(ActionEvent event) {
        initStatisticTable();
        switchForm(event);

    }

    @FXML
    void searchRoles() {
        FilteredList<User> filter = new FilteredList<>(tableUsersRoles, e -> true);
        searchRolesTextfield.textProperty().addListener((observable, oldValue, newValue) -> {
            filter.setPredicate(predicate -> {
                if (newValue.isEmpty())
                    return true;
                String keySearch = newValue.toLowerCase();
                if (predicate.getLogin().toLowerCase().contains(keySearch))
                    return true;
                else if (predicate.getRole().toLowerCase().contains(keySearch))
                    return true;
                else if (predicate.getPersonalSettings().getPhone().contains(keySearch))
                    return true;

                return false;
            });
        });
        SortedList<User> sortData = filter.sorted();
        sortData.comparatorProperty().bind(rolesTable.comparatorProperty());
        rolesTable.setItems(sortData);
    }

    @FXML
    void searchStatistic() {
        FilteredList<User> filter = new FilteredList<>(tableConnectedUsers, e -> true);
        searchStatisticTextfield.textProperty().addListener((observable, oldValue, newValue) -> {
            filter.setPredicate(predicate -> {
                if (newValue.isEmpty())
                    return true;
                String keySearch = newValue.toLowerCase();
                if (predicate.getLogin().toLowerCase().contains(keySearch))
                    return true;
                else if (predicate.getRole().toLowerCase().contains(keySearch))
                    return true;

                return false;
            });

        });
        SortedList<User> sortData = filter.sorted();
        sortData.comparatorProperty().bind(statisticTable.comparatorProperty());
        statisticTable.setItems(sortData);
    }


    @FXML
    void update_Pressed(ActionEvent event) {
        initStatisticTable();
    }
}
