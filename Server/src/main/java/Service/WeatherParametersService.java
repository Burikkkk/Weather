package Service;

import DAO.WeatherParametersDao;
import Models.Entities.WeatherParameters;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class WeatherParametersService implements Service<WeatherParameters> {

    private final WeatherParametersDao weatherParametersDao;

    public WeatherParametersService(Connection connection) {
        this.weatherParametersDao = new WeatherParametersDao(connection);  // Создаём объект WeatherParametersDao
    }

    @Override
    public Optional<WeatherParameters> getEntityById(int id) throws SQLException {
        return weatherParametersDao.findById(id);  // Используем метод findById из WeatherParametersDao
    }

    @Override
    public void createEntity(WeatherParameters weatherParameters) throws SQLException {
        weatherParametersDao.add(weatherParameters);  // Используем метод add из WeatherParametersDao
    }

    @Override
    public void deleteEntity(WeatherParameters weatherParameters) throws SQLException {
        weatherParametersDao.delete(weatherParameters);  // Используем метод delete из WeatherParametersDao
    }

    @Override
    public void updateEntity(WeatherParameters weatherParameters) throws SQLException {
        weatherParametersDao.update(weatherParameters);  // Используем метод update из WeatherParametersDao
    }

    @Override
    public List<WeatherParameters> getAllEntities() throws SQLException {
        return weatherParametersDao.findAll();  // Используем метод findAll из WeatherParametersDao
    }
}
