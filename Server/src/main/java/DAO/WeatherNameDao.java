package DAO;

import Models.Entities.WeatherName;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WeatherNameDao implements Dao<WeatherName> {

    private final Connection connection;

    public WeatherNameDao(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void add(WeatherName weatherName) throws SQLException {
        String query = "INSERT INTO weather_name (name) VALUES (?)";
        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)){
            statement.setString(1, weatherName.getName());
            // Выполнение обновления (вставка данных)
            int affectedRows = statement.executeUpdate();

            // Проверяем, были ли затронуты строки (успешная вставка)
            if (affectedRows > 0) {
                // Получаем сгенерированные ключи (например, ID автоинкрементного поля)
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        // Получаем сгенерированный ID и устанавливаем его в объект настроек
                        int generatedId = generatedKeys.getInt(1);
                        weatherName.setId(generatedId); // Устанавливаем ID в объект
                    }
                }
            } else {
                throw new SQLException("Вставка не затронула ни одной строки.");
            }
        }
    }

    @Override
    public void update(WeatherName weatherName) throws SQLException {
        String query = "UPDATE weather_name SET name = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, weatherName.getName());
            statement.setInt(2, weatherName.getId());
            statement.executeUpdate();
        }
    }

    @Override
    public void delete(WeatherName weatherName) throws SQLException {
        String query = "DELETE FROM weather_name WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, weatherName.getId());
            statement.executeUpdate();
        }
    }

    @Override
    public Optional<WeatherName> findById(int id) throws SQLException {
        String query = "SELECT * FROM weather_name WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(resultSetToEntity(resultSet));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<WeatherName> findAll() throws SQLException {
        String query = "SELECT * FROM weather_name";
        List<WeatherName> weatherNames = new ArrayList<>();
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                weatherNames.add(resultSetToEntity(resultSet));
            }
        }
        return weatherNames;
    }

    @Override
    public WeatherName resultSetToEntity(ResultSet rs) throws SQLException {
        return new WeatherName(
                rs.getInt("id"),
                rs.getString("name")
        );
    }
}
