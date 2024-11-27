package Service;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface Service<T> {

    Optional<T> getEntityById(int id)throws SQLException;     //поиск по id

    void createEntity(T entity)throws SQLException ;     //новая запись

    void deleteEntity(T entity)throws SQLException ; //удаление одного

    void updateEntity(T entity)throws SQLException ; //обновление одного

    List<T> getAllEntities()throws SQLException ;  //получение всех

}

