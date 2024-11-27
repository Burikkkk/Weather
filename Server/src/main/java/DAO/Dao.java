package DAO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface Dao<T> {

    void add(T obj) throws SQLException;

    void update(T obj) throws SQLException;

    void delete(T obj) throws SQLException;

    Optional<T> findById(int id) throws SQLException;

    List<T> findAll() throws SQLException;     //получение всех User

    T resultSetToEntity(ResultSet rs) throws SQLException;     //запись resultSet в сущность
}
