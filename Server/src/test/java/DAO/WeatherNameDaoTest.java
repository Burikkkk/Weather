package DAO;

import Models.Entities.WeatherName;
import Utilities.ConnectorDB;
import org.junit.*;

import java.sql.*;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class WeatherNameDaoTest {

    private static Connection connection;
    private WeatherNameDao weatherNameDao;
    private WeatherName weatherName;
    private WeatherName weatherName1;

    @BeforeClass
    public static void setupClass() throws SQLException {
        connection = ConnectorDB.getConnection();
    }

    @Before
    public void setup() throws SQLException {
        weatherNameDao = new WeatherNameDao(connection);
        weatherName = new WeatherName("Солнечно");
        weatherNameDao.add(weatherName);
    }

    @Test
    public void testFindById() throws SQLException {
        Optional<WeatherName> result = weatherNameDao.findById(weatherName.getId());
        assertTrue(result.isPresent());
        assertEquals("Солнечно", result.get().getName());
    }

    @Test
    public void testFindAll() throws SQLException {
        weatherName1 = new WeatherName("Пасмурно");
        weatherNameDao.add(weatherName1);

        List<WeatherName> weatherNamesList = weatherNameDao.findAll();

        // Поиск объектов в списке по их ID
        WeatherName foundWeatherName = weatherNamesList.stream()
                .filter(w -> w.getId() == weatherName.getId())
                .findFirst()
                .orElse(null);

        WeatherName foundWeatherName1 = weatherNamesList.stream()
                .filter(w -> w.getId() == weatherName1.getId())
                .findFirst()
                .orElse(null);

        // Проверка, что объекты найдены и их данные корректны
        assertNotNull(foundWeatherName);
        assertEquals(weatherName.getName(), foundWeatherName.getName());

        assertNotNull(foundWeatherName1);
        assertEquals(weatherName1.getName(), foundWeatherName1.getName());

        // Удаление созданного объекта
        weatherNameDao.delete(weatherName1);
    }

    @Test
    public void testUpdate() throws SQLException {
        weatherName.setName("Пасмурно");
        weatherNameDao.update(weatherName);

        Optional<WeatherName> result = weatherNameDao.findById(weatherName.getId());
        assertTrue(result.isPresent());
        assertEquals("Пасмурно", result.get().getName());
    }

    @After
    public void testDelete() throws SQLException {
        weatherNameDao.delete(weatherName);
        Optional<WeatherName> result = weatherNameDao.findById(weatherName.getId());
        assertFalse(result.isPresent());
    }

    @AfterClass
    public static void tearDownClass() throws SQLException {
        connection.close();
    }
}
