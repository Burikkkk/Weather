package DAO;

import Models.Entities.Dashboard;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DashboardDao implements Dao<Dashboard> {

    private final Connection connection;

    public DashboardDao(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void add(Dashboard dashboard) throws SQLException {
        String query = "INSERT INTO dashboard (start_date_id, end_date_id, user_id) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, dashboard.getStartDate().getId());
            statement.setInt(2, dashboard.getEndDate().getId());
            statement.setInt(3, dashboard.getUser().getId());
            statement.executeUpdate();
        }
    }

    @Override
    public void update(Dashboard dashboard) throws SQLException {
        String query = "UPDATE dashboard SET start_date_id = ?, end_date_id = ?, user_id = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, dashboard.getStartDate().getId());
            statement.setInt(2, dashboard.getEndDate().getId());
            statement.setInt(3, dashboard.getUser().getId());
            statement.setInt(4, dashboard.getId());
            statement.executeUpdate();
        }
    }

    @Override
    public void delete(Dashboard dashboard) throws SQLException {
        String query = "DELETE FROM dashboard WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, dashboard.getId());
            statement.executeUpdate();
        }
    }

    @Override
    public Optional<Dashboard> findById(int id) throws SQLException {
        String query = "SELECT * FROM dashboard WHERE id = ?";
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
    public List<Dashboard> findAll() throws SQLException {
        String query = "SELECT * FROM dashboard";
        List<Dashboard> dashboards = new ArrayList<>();
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                dashboards.add(resultSetToEntity(resultSet));
            }
        }
        return dashboards;
    }

    @Override
    public Dashboard resultSetToEntity(ResultSet rs) throws SQLException {
        DayDao dayDao = new DayDao(connection);
        UserDao userDao = new UserDao();

        return new Dashboard(
                rs.getInt("id"),
                dayDao.findById(rs.getInt("start_date_id")).orElse(null),
                dayDao.findById(rs.getInt("end_date_id")).orElse(null),
                userDao.findById(rs.getInt("user_id")).orElse(null)
        );
    }
}
