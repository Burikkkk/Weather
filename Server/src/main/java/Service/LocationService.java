package Service;

import DAO.LocationDao;
import Models.Entities.Location;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class LocationService implements Service<Location> {

    private final LocationDao locationDao;

    public LocationService(Connection connection) {
        this.locationDao = new LocationDao(connection);  // Создаём объект LocationDao
    }

    @Override
    public Optional<Location> getEntityById(int id) throws SQLException {
        return locationDao.findById(id);  // Используем метод findById из LocationDao
    }

    @Override
    public void createEntity(Location location) throws SQLException {
        locationDao.add(location);  // Используем метод add из LocationDao
    }

    @Override
    public void deleteEntity(Location location) throws SQLException {
        locationDao.delete(location);  // Используем метод delete из LocationDao
    }

    @Override
    public void updateEntity(Location location) throws SQLException {
        locationDao.update(location);  // Используем метод update из LocationDao
    }

    @Override
    public List<Location> getAllEntities() throws SQLException {
        return locationDao.findAll();  // Используем метод findAll из LocationDao
    }
}
