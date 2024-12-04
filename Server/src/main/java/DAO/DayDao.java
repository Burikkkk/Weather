package DAO;

import Models.Entities.Day;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DayDao implements Dao<Day> {

    private final Connection connection;

    public DayDao(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void add(Day day) throws SQLException {
        String query = "INSERT INTO day (date, day_weather, night_weather, weather_id, location_id) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)){
            statement.setDate(1, day.getDate());
            statement.setObject(2, day.getDayWeather() != null ? day.getDayWeather().getId() : null, Types.INTEGER);
            statement.setObject(3, day.getNightWeather() != null ? day.getNightWeather().getId() : null, Types.INTEGER);
            statement.setObject(4, day.getWeatherName() != null ? day.getWeatherName().getId() : null, Types.INTEGER);
            statement.setObject(5, day.getLocation() != null ? day.getLocation().getId() : null, Types.INTEGER);
            // Выполнение обновления (вставка данных)
            int affectedRows = statement.executeUpdate();

            // Проверяем, были ли затронуты строки (успешная вставка)
            if (affectedRows > 0) {
                // Получаем сгенерированные ключи (например, ID автоинкрементного поля)
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        // Получаем сгенерированный ID и устанавливаем его в объект настроек
                        int generatedId = generatedKeys.getInt(1);
                        day.setId(generatedId); // Устанавливаем ID в объект
                    }
                }
            } else {
                throw new SQLException("Вставка не затронула ни одной строки.");
            }
        }
    }

    @Override
    public void update(Day day) throws SQLException {
        String query = "UPDATE day SET date = ?, day_weather = ?, night_weather = ?, weather_id = ?, location_id = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setDate(1, day.getDate());
            statement.setObject(2, day.getDayWeather() != null ? day.getDayWeather().getId() : null, Types.INTEGER);
            statement.setObject(3, day.getNightWeather() != null ? day.getNightWeather().getId() : null, Types.INTEGER);
            statement.setObject(4, day.getWeatherName() != null ? day.getWeatherName().getId() : null, Types.INTEGER);
            statement.setObject(5, day.getLocation() != null ? day.getLocation().getId() : null, Types.INTEGER);
            statement.setInt(6, day.getId());
            statement.executeUpdate();
        }
    }

    @Override
    public void delete(Day day) throws SQLException {
        String query = "DELETE FROM day WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, day.getId());
            statement.executeUpdate();
        }
    }

    @Override
    public Optional<Day> findById(int id) throws SQLException {
        String query = "SELECT * FROM day WHERE id = ?";
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
    public List<Day> findAll() throws SQLException {
        String query = "SELECT * FROM day";
        List<Day> days = new ArrayList<>();
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                days.add(resultSetToEntity(resultSet));
            }
        }
        return days;
    }

    @Override
    public Day resultSetToEntity(ResultSet rs) throws SQLException {
        WeatherParametersDao weatherParametersDao = new WeatherParametersDao(connection);
        WeatherNameDao weatherNameDao = new WeatherNameDao(connection);
        LocationDao locationDao = new LocationDao(connection);

        return new Day(
                rs.getInt("id"),
                rs.getDate("date"),
                weatherParametersDao.findById(rs.getInt("day_weather")).orElse(null),
                weatherParametersDao.findById(rs.getInt("night_weather")).orElse(null),
                weatherNameDao.findById(rs.getInt("weather_id")).orElse(null),
                locationDao.findById(rs.getInt("location_id")).orElse(null)
        );
    }
}
