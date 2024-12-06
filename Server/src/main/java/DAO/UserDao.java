package DAO;

import Models.Entities.PersonalSettings;
import Models.Entities.User;
import Utilities.ConnectorDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDao implements Dao<User> {

    private Connection connection = null;

    public UserDao() throws SQLException {
        connection = ConnectorDB.getConnection();
    }


    public void closeConnection() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void add(User user) throws SQLException {
        String sql = "INSERT INTO user (login, password, role, settings_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {  // Указываем флаг для получения сгенерированных ключей{
            statement.setString(1, user.getLogin());
            statement.setBytes(2, user.getPassword());
            statement.setString(3, user.getRole());
            statement.setInt(4, user.getPersonalSettings().getId());

            // Выполнение обновления (вставка данных)
            int affectedRows = statement.executeUpdate();

            // Проверяем, были ли затронуты строки (успешная вставка)
            if (affectedRows > 0) {
                // Получаем сгенерированные ключи (например, ID автоинкрементного поля)
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        // Получаем сгенерированный ID и устанавливаем его в объект настроек
                        int generatedId = generatedKeys.getInt(1);
                        user.setId(generatedId); // Устанавливаем ID в объект
                    }
                }
            } else {
                throw new SQLException("Вставка не затронула ни одной строки.");
            }
        }
    }

    @Override
    public Optional<User> findById(int id) throws SQLException {
        String sql = "SELECT * FROM user WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    User user = resultSetToEntity(resultSet);
                    return Optional.of(user);
                }
            }
        }
        return Optional.empty();
    }


    public Optional<User> findByLogin(String login) throws SQLException {
        String sql = "SELECT * FROM user WHERE login = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, login);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    User user = resultSetToEntity(resultSet);
                    return Optional.of(user);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<User> findAll() throws SQLException {
        List<User> userList = new ArrayList<>();
        String sql = "SELECT * FROM user";
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                User user = resultSetToEntity(resultSet);
                userList.add(user);
            }
        }
        return userList;
    }

    @Override
    public void update(User user) throws SQLException {
        String sql = "UPDATE user SET login = ?, password = ?, role = ?, settings_id = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, user.getLogin());
            statement.setBytes(2, user.getPassword());
            statement.setString(3, user.getRole());
            statement.setInt(4, user.getPersonalSettings().getId());
            statement.setInt(5, user.getId());
            statement.executeUpdate();
        }
    }

    @Override
    public void delete(User user) throws SQLException {
        String sql = "DELETE FROM user WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, user.getId());
            int rowsAffected = statement.executeUpdate(); // Один вызов executeUpdate()

            if (rowsAffected == 0) {
                throw new SQLException("Удаление не затронуло ни одной строки.");
            }
        }
    }

    @Override
    public User resultSetToEntity(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String login = resultSet.getString("login");
        byte[] password = resultSet.getBytes("password");
        String role = resultSet.getString("role");
        int settingsId = resultSet.getInt("settings_id");

        PersonalSettings settings = new PersonalSettingsDao().findById(settingsId).orElse(null);

        User user = new User();
        user.setId(id);
        user.setLogin(login);
        user.setPassword(password);
        user.setRole(role);
        user.setPersonalSettings(settings);
        return user;
    }
}

