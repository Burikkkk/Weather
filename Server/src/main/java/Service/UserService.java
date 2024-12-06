package Service;

import DAO.PersonalSettingsDao;
import DAO.UserDao;
import Models.Entities.PersonalSettings;
import Models.Entities.User;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class UserService implements Service<User>{

    private final UserDao userDao;


    public UserService() throws SQLException {
        this.userDao = new UserDao();
    }

    @Override
    public void createEntity(User user) throws SQLException {

        userDao.add(user);
    }

    @Override
    public Optional<User> getEntityById(int id) throws SQLException {
        return userDao.findById(id);
    }

    //поиск по логину
    public Optional<User> getUserByLogin(String login) throws SQLException {
        return userDao.findByLogin(login);
    }

    @Override
    public void updateEntity(User user) throws SQLException {
        userDao.update(user);
    }

    @Override
    public void deleteEntity(User user) throws SQLException {
        userDao.delete(user);
    }

    @Override
    public List<User> getAllEntities() throws SQLException{
        return userDao.findAll();
    }
}

