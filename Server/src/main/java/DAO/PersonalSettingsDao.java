package DAO;

import Models.Entities.PersonalSettings;
import Utilities.ConnectorDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PersonalSettingsDao implements Dao<PersonalSettings>{

    private static Connection connection;

    public PersonalSettingsDao() throws SQLException {
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
    public void add(PersonalSettings settings) throws SQLException {
        // SQL запрос для вставки данных
        String sql = "INSERT INTO personal_settings (phone, notifications, temperature, pressure, speed) VALUES (?, ?, ?, ?, ?)";

        // Используем PreparedStatement с флагом RETURN_GENERATED_KEYS для получения сгенерированного ключа
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            // Установка параметров запроса
            statement.setString(1, settings.getPhone());
            statement.setBoolean(2, settings.getNotifications());
            statement.setString(3, settings.getTemperature());
            statement.setString(4, settings.getPressure());
            statement.setString(5, settings.getSpeed());

            // Выполнение обновления (вставка данных)
            int affectedRows = statement.executeUpdate();

            // Проверяем, были ли затронуты строки (успешная вставка)
            if (affectedRows > 0) {
                // Получаем сгенерированные ключи (например, ID автоинкрементного поля)
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        // Получаем сгенерированный ID и устанавливаем его в объект настроек
                        int generatedId = generatedKeys.getInt(1);
                        settings.setId(generatedId); // Устанавливаем ID в объект настроек
                    }
                }
            } else {
                throw new SQLException("Вставка не затронула ни одной строки.");
            }
        }
    }



    @Override
    public Optional<PersonalSettings> findById(int id) throws SQLException {
        String sql = "SELECT * FROM personal_settings WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    PersonalSettings settings = resultSetToEntity(resultSet);
                    return Optional.of(settings);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<PersonalSettings> findAll() throws SQLException {
        List<PersonalSettings> settingsList = new ArrayList<>();
        String sql = "SELECT * FROM personal_settings";
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                PersonalSettings settings = resultSetToEntity(resultSet);
                settingsList.add(settings);
            }
        }
        return settingsList;
    }

    @Override
    public void update(PersonalSettings settings) throws SQLException {
        String sql = "UPDATE personal_settings SET phone = ?, notifications = ?, temperature = ?, pressure = ?, speed = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, settings.getPhone());
            statement.setBoolean(2, settings.getNotifications());
            statement.setString(3, settings.getTemperature());
            statement.setString(4, settings.getPressure());
            statement.setString(5, settings.getSpeed());
            statement.setInt(6, settings.getId());
            statement.executeUpdate();
        }
    }

    @Override
    public void delete(PersonalSettings settings) throws SQLException {
        String sql = "DELETE FROM personal_settings WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, settings.getId());
            statement.executeUpdate();
        }
    }



    @Override
    public PersonalSettings resultSetToEntity(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String phone = resultSet.getString("phone");
        boolean notifications = resultSet.getBoolean("notifications");
        String temperature = resultSet.getString("temperature");
        String pressure = resultSet.getString("pressure");
        String speed = resultSet.getString("speed");

        PersonalSettings settings = new PersonalSettings();
        settings.setId(id);
        settings.setPhone(phone);
        settings.setNotifications(notifications);
        settings.setTemperature(temperature);
        settings.setPressure(pressure);
        settings.setSpeed(speed);
        return settings;
    }
}

