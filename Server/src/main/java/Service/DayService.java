package Service;

import DAO.DayDao;
import Models.Entities.Day;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class DayService implements Service<Day> {

    private final DayDao dayDao;

    public DayService(Connection connection) {
        this.dayDao = new DayDao(connection);  // Создаём объект DayDao
    }

    @Override
    public Optional<Day> getEntityById(int id) throws SQLException {
        return dayDao.findById(id);  // Используем метод findById из DayDao
    }

    @Override
    public void createEntity(Day day) throws SQLException {
        dayDao.add(day);  // Используем метод add из DayDao
    }

    @Override
    public void deleteEntity(Day day) throws SQLException {
        dayDao.delete(day);  // Используем метод delete из DayDao
    }

    @Override
    public void updateEntity(Day day) throws SQLException {
        dayDao.update(day);  // Используем метод update из DayDao
    }

    @Override
    public List<Day> getAllEntities() throws SQLException {
        return dayDao.findAll();  // Используем метод findAll из DayDao
    }
}
