package Service;

import DAO.DashboardDao;
import DAO.Dao;
import DAO.UserDao;
import DAO.DayDao;
import Models.Entities.Dashboard;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class DashboardService implements Service<Dashboard> {

    private final DashboardDao dashboardDao;

    public DashboardService(Connection connection) {
        this.dashboardDao = new DashboardDao(connection);  // Создаём объект DashboardDao
    }

    @Override
    public Optional<Dashboard> getEntityById(int id) throws SQLException {
        return dashboardDao.findById(id);  // Используем метод findById из DashboardDao
    }

    @Override
    public void createEntity(Dashboard dashboard) throws SQLException {
        dashboardDao.add(dashboard);  // Используем метод add из DashboardDao
    }

    @Override
    public void deleteEntity(Dashboard dashboard) throws SQLException {
        dashboardDao.delete(dashboard);  // Используем метод delete из DashboardDao
    }

    @Override
    public void updateEntity(Dashboard dashboard) throws SQLException {
        dashboardDao.update(dashboard);  // Используем метод update из DashboardDao
    }

    @Override
    public List<Dashboard> getAllEntities() throws SQLException {
        return dashboardDao.findAll();  // Используем метод findAll из DashboardDao
    }
}

