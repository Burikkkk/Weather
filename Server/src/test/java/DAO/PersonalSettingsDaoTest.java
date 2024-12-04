package DAO;

import Models.Entities.PersonalSettings;
import Utilities.ConnectorDB;
import org.junit.*;

import java.sql.*;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class PersonalSettingsDaoTest {

    private static Connection connection;
    private PersonalSettingsDao personalSettingsDao;
    private PersonalSettings settings;
    private PersonalSettings settings1;

    @BeforeClass
    public static void setupClass() throws SQLException {
        connection = ConnectorDB.getConnection();
    }

    @Before
    public void setup() throws SQLException {
        personalSettingsDao = new PersonalSettingsDao();
        settings = new PersonalSettings("000000000", true, "C", "мм рт. ст.", "м/с");
        personalSettingsDao.add(settings);
    }

    @Test
    public void testFindById() throws SQLException {
        Optional<PersonalSettings> result = personalSettingsDao.findById(settings.getId());
        assertTrue(result.isPresent());
        assertEquals("000000000", result.get().getPhone());
    }

    @Test
    public void testFindAll() throws SQLException {
        settings1 = new PersonalSettings("111111111", true, "C", "мм рт. ст.", "км/ч");
        personalSettingsDao.add(settings1);

        List<PersonalSettings> settingsList = personalSettingsDao.findAll();

        // Поиск объектов в списке по их ID
        PersonalSettings foundSettings = settingsList.stream()
                .filter(s -> s.getId() == settings.getId())
                .findFirst()
                .orElse(null);

        PersonalSettings foundSettings1 = settingsList.stream()
                .filter(s -> s.getId() == settings1.getId())
                .findFirst()
                .orElse(null);

        // Проверка, что объекты найдены и их данные корректны
        assertNotNull(foundSettings);
        assertEquals(settings.getPhone(), foundSettings.getPhone());

        assertNotNull(foundSettings1);
        assertEquals(settings1.getPhone(), foundSettings1.getPhone());

        // Удаление созданного объекта
        personalSettingsDao.delete(settings1);
    }


    @Test
    public void testUpdate() throws SQLException {

        settings.setPhone("111111111");
        settings.setTemperature("K");
        personalSettingsDao.update(settings);
        Optional<PersonalSettings> result = personalSettingsDao.findById(settings.getId());
        assertTrue(result.isPresent());
        assertEquals("111111111", result.get().getPhone());
        assertEquals("K", result.get().getTemperature());
        assertTrue(result.get().getNotifications());
        assertEquals("мм рт. ст.", result.get().getPressure());
        assertEquals("м/с", result.get().getSpeed());
    }

    @After
    public void testDelete() throws SQLException {
        personalSettingsDao.delete(settings);
        Optional<PersonalSettings> result = personalSettingsDao.findById(settings.getId());
        assertFalse(result.isPresent());
    }


    @AfterClass
    public static void tearDownClass() throws SQLException {
        connection.close();
    }
}

