package DAO;

import Models.Entities.PersonalSettings;
import Models.Entities.User;
import Utilities.ConnectorDB;
import org.junit.*;

import java.sql.*;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class UserDaoTest {

    private static Connection connection;
    private UserDao userDao;
    private PersonalSettingsDao personalSettingsDao;
    private User user;
    private User user1;
    private PersonalSettings settings;
    private PersonalSettings settings1;


    @BeforeClass
    public static void setupClass() throws SQLException {
        connection = ConnectorDB.getConnection();
    }

    @Before
    public void setup() throws SQLException {
        userDao = new UserDao();
        personalSettingsDao = new PersonalSettingsDao();
        settings = new PersonalSettings();
        settings.setPhone("000000000");
        personalSettingsDao.add(settings);
        user = new User("user123",user.getHash("123"),"user",settings);
        userDao.add(user);
    }

    @Test
    public void testFindByLogin() throws SQLException {
        Optional<User> result = userDao.findByLogin(user.getLogin());
        assertTrue(result.isPresent());
        assertEquals("user123", result.get().getLogin());
        assertEquals("user", result.get().getRole());
    }

    @Test
    public void testFindById() throws SQLException {
        Optional<User> result = userDao.findById(user.getId());
        assertTrue(result.isPresent());
        assertEquals("user123", result.get().getLogin());
        assertEquals("user", result.get().getRole());
    }

    @Test
    public void testFindAll() throws SQLException {
        settings1 = new PersonalSettings();
        settings1.setPhone("000000001");
        personalSettingsDao.add(settings1);
        user1 = new User("user12345",user.getHash("password111"),"admin",settings);
        user1.setPersonalSettings(settings1);
        userDao.add(user1);

        List<User> userList = userDao.findAll();

        // Поиск объектов в списке по их ID
        User foundUser = userList.stream()
                .filter(u -> u.getId() == user.getId())
                .findFirst()
                .orElse(null);

        User foundUser1 = userList.stream()
                .filter(u -> u.getId() == user1.getId())
                .findFirst()
                .orElse(null);

        // Проверка, что объекты найдены и их данные корректны
        assertNotNull(foundUser);
        assertEquals(user.getLogin(), foundUser.getLogin());

        assertNotNull(foundUser1);
        assertEquals(user1.getLogin(), foundUser1.getLogin());

        // Удаление созданного объекта
        userDao.delete(user1);
    }

    @Test
    public void testUpdate() throws SQLException {
        user.setLogin("updated_user");
        user.setRole("admin");
        userDao.update(user);

        Optional<User> result = userDao.findById(user.getId());
        assertTrue(result.isPresent());
        assertEquals("updated_user", result.get().getLogin());
        assertEquals("admin", result.get().getRole());
    }

    @After
    public void testDelete() throws SQLException {
        userDao.delete(user);

        Optional<User> result = userDao.findById(user.getId());
        assertFalse(result.isPresent());
    }

    @AfterClass
    public static void tearDownClass() throws SQLException {
        connection.close();
    }
}
