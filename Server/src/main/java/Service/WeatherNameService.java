package Service;

import DAO.WeatherNameDao;
import Models.Entities.WeatherName;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class WeatherNameService implements Service<WeatherName> {

    private final WeatherNameDao weatherNameDao;

    public WeatherNameService(Connection connection) {
        this.weatherNameDao = new WeatherNameDao(connection);  // Создаём объект WeatherNameDao
    }

    @Override
    public Optional<WeatherName> getEntityById(int id) throws SQLException {
        return weatherNameDao.findById(id);  // Используем метод findById из WeatherNameDao
    }

    @Override
    public void createEntity(WeatherName weatherName) throws SQLException {
        weatherNameDao.add(weatherName);  // Используем метод add из WeatherNameDao
    }

    @Override
    public void deleteEntity(WeatherName weatherName) throws SQLException {
        weatherNameDao.delete(weatherName);  // Используем метод delete из WeatherNameDao
    }

    @Override
    public void updateEntity(WeatherName weatherName) throws SQLException {
        weatherNameDao.update(weatherName);  // Используем метод update из WeatherNameDao
    }

    @Override
    public List<WeatherName> getAllEntities() throws SQLException {
        return weatherNameDao.findAll();  // Используем метод findAll из WeatherNameDao
    }
}

