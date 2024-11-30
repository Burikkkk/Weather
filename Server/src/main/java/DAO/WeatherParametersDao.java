package DAO;

import Models.Entities.WeatherParameters;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WeatherParametersDao implements Dao<WeatherParameters> {

    private final Connection connection;

    public WeatherParametersDao(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void add(WeatherParameters weatherParameters) throws SQLException {
        String query = "INSERT INTO weather_parameters (temperature, pressure, humidity, precipitation, wind_speed) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setDouble(1, weatherParameters.getTemperature());
            statement.setInt(2, weatherParameters.getPressure());
            statement.setInt(3, weatherParameters.getHumidity());
            statement.setObject(4, weatherParameters.getPrecipitation(), Types.DOUBLE);
            statement.setDouble(5, weatherParameters.getWindSpeed());
            statement.executeUpdate();
        }
    }

    @Override
    public void update(WeatherParameters weatherParameters) throws SQLException {
        String query = "UPDATE weather_parameters SET temperature = ?, pressure = ?, humidity = ?, precipitation = ?, wind_speed = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setDouble(1, weatherParameters.getTemperature());
            statement.setInt(2, weatherParameters.getPressure());
            statement.setInt(3, weatherParameters.getHumidity());
            statement.setObject(4, weatherParameters.getPrecipitation(), Types.DOUBLE);
            statement.setDouble(5, weatherParameters.getWindSpeed());
            statement.setInt(6, weatherParameters.getId());
            statement.executeUpdate();
        }
    }

    @Override
    public void delete(WeatherParameters weatherParameters) throws SQLException {
        String query = "DELETE FROM weather_parameters WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, weatherParameters.getId());
            statement.executeUpdate();
        }
    }

    @Override
    public Optional<WeatherParameters> findById(int id) throws SQLException {
        String query = "SELECT * FROM weather_parameters WHERE id = ?";
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
    public List<WeatherParameters> findAll() throws SQLException {
        String query = "SELECT * FROM weather_parameters";
        List<WeatherParameters> parameters = new ArrayList<>();
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                parameters.add(resultSetToEntity(resultSet));
            }
        }
        return parameters;
    }

    @Override
    public WeatherParameters resultSetToEntity(ResultSet rs) throws SQLException {
        return new WeatherParameters(
                rs.getInt("id"),
                rs.getDouble("temperature"),
                rs.getInt("pressure"),
                rs.getInt("humidity"),
                rs.getDouble("precipitation"),
                rs.getDouble("wind_speed")
        );
    }
}
