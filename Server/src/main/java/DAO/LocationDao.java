package DAO;

import Models.Entities.Location;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LocationDao implements Dao<Location> {

    private final Connection connection;

    public LocationDao(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void add(Location location) throws SQLException {
        String query = "INSERT INTO location (town, country) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)){
            statement.setString(1, location.getTown());
            statement.setString(2, location.getCountry());
            // Выполнение обновления (вставка данных)
            int affectedRows = statement.executeUpdate();

            // Проверяем, были ли затронуты строки (успешная вставка)
            if (affectedRows > 0) {
                // Получаем сгенерированные ключи (например, ID автоинкрементного поля)
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        // Получаем сгенерированный ID и устанавливаем его в объект настроек
                        int generatedId = generatedKeys.getInt(1);
                        location.setId(generatedId); // Устанавливаем ID в объект
                    }
                }
            } else {
                throw new SQLException("Вставка не затронула ни одной строки.");
            }
        }
    }

    @Override
    public void update(Location location) throws SQLException {
        String query = "UPDATE location SET town = ?, country = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, location.getTown());
            statement.setString(2, location.getCountry());
            statement.setInt(3, location.getId());
            statement.executeUpdate();
        }
    }

    @Override
    public void delete(Location location) throws SQLException {
        String query = "DELETE FROM location WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, location.getId());
            statement.executeUpdate();
        }
    }

    @Override
    public Optional<Location> findById(int id) throws SQLException {
        String query = "SELECT * FROM location WHERE id = ?";
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
    public List<Location> findAll() throws SQLException {
        String query = "SELECT * FROM location";
        List<Location> locations = new ArrayList<>();
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                locations.add(resultSetToEntity(resultSet));
            }
        }
        return locations;
    }

    @Override
    public Location resultSetToEntity(ResultSet rs) throws SQLException {
        return new Location(
                rs.getInt("id"),
                rs.getString("town"),
                rs.getString("country")
        );
    }
}

