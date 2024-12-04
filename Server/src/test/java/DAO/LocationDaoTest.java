package DAO;

import Models.Entities.Location;
import Utilities.ConnectorDB;
import org.junit.*;

import java.sql.*;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class LocationDaoTest {

    private static Connection connection;
    private LocationDao locationDao;
    private Location location;
    private Location location1;

    @BeforeClass
    public static void setupClass() throws SQLException {
        connection = ConnectorDB.getConnection();
    }

    @Before
    public void setup() throws SQLException {
        locationDao = new LocationDao(connection);
        location = new Location("Гродно", "Беларусь");
        locationDao.add(location);
    }

    @Test
    public void testFindById() throws SQLException {
        Optional<Location> result = locationDao.findById(location.getId());
        assertTrue(result.isPresent());
        assertEquals("Гродно", result.get().getTown());
        assertEquals("Беларусь", result.get().getCountry());
    }

    @Test
    public void testFindAll() throws SQLException {
        location1 = new Location("Брест", "Беларусь");
        locationDao.add(location1);

        List<Location> locationList = locationDao.findAll();

        // Поиск объектов в списке по их ID
        Location foundLocation = locationList.stream()
                .filter(l -> l.getId() == location.getId())
                .findFirst()
                .orElse(null);

        Location foundLocation1 = locationList.stream()
                .filter(l -> l.getId() == location1.getId())
                .findFirst()
                .orElse(null);

        // Проверка, что объекты найдены и их данные корректны
        assertNotNull(foundLocation);
        assertEquals(location.getTown(), foundLocation.getTown());
        assertEquals(location.getCountry(), foundLocation.getCountry());

        assertNotNull(foundLocation1);
        assertEquals(location1.getTown(), foundLocation1.getTown());
        assertEquals(location1.getCountry(), foundLocation1.getCountry());

        // Удаление созданного объекта
        locationDao.delete(location1);
    }

    @Test
    public void testUpdate() throws SQLException {
        location.setTown("Брест");
        location.setCountry("Беларусь");
        locationDao.update(location);

        Optional<Location> result = locationDao.findById(location.getId());
        assertTrue(result.isPresent());
        assertEquals("Брест", result.get().getTown());
        assertEquals("Беларусь", result.get().getCountry());
    }

    @After
    public void testDelete() throws SQLException {
        locationDao.delete(location);
        Optional<Location> result = locationDao.findById(location.getId());
        assertFalse(result.isPresent());
    }

    @AfterClass
    public static void tearDownClass() throws SQLException {
        connection.close();
    }
}

