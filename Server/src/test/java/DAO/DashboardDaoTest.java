package DAO;

import Models.Entities.*;
import Utilities.ConnectorDB;
import org.junit.*;

import java.sql.*;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class DashboardDaoTest {

    private static Connection connection;
    private DashboardDao dashboardDao;
    private Dashboard dashboard;
    private WeatherParametersDao weatherParametersDao;
    private WeatherNameDao weatherNameDao;
    private LocationDao locationDao;
    private PersonalSettingsDao personalSettingsDao;
    private DayDao dayDao;
    private UserDao userDao;
    private Day startDay;
    private Day endDay;
    private User user;
    private WeatherParameters dayWeather1;
    private WeatherParameters nightWeather1;
    private WeatherName weatherName1;
    private Location location1;
    private WeatherParameters dayWeather2;
    private WeatherParameters nightWeather2;
    private WeatherName weatherName2;
    private Location location2;
    private PersonalSettings settings;

    @BeforeClass
    public static void setupClass() throws SQLException {
        connection = ConnectorDB.getConnection();
    }

    @Before
    public void setup() throws SQLException {
        // Создаем экземпляр DashboardDao и других DAO
        dashboardDao = new DashboardDao(connection);
        dayDao = new DayDao(connection);
        userDao = new UserDao();


        // Создаем тестовые объекты WeatherName, Location и WeatherParameters
        weatherName1 = new WeatherName("Солнечно");
        weatherNameDao = new WeatherNameDao(connection);
        weatherNameDao.add(weatherName1);

        location1 = new Location("Брест", "Беларусь");
        locationDao = new LocationDao(connection);
        locationDao.add(location1);

        dayWeather1 = new WeatherParameters(25.5, 760, 80, 0.0, 5.0);
        weatherParametersDao = new WeatherParametersDao(connection);
        weatherParametersDao.add(dayWeather1);

        nightWeather1 = new WeatherParameters(15.0, 755, 90, 0.1, 3.0);
        weatherParametersDao.add(nightWeather1);

        // Создаем Day объект
        startDay = new Day(
                Date.valueOf("2024-01-01"),
                dayWeather1,
                nightWeather1,
                weatherName1,
                location1
        );
        dayDao.add(startDay);

        // Создаем тестовые объекты WeatherName, Location и WeatherParameters
        weatherName2 = new WeatherName("Снег");
        weatherNameDao = new WeatherNameDao(connection);
        weatherNameDao.add(weatherName2);

        location2 = new Location("Молодечно", "Беларусь");
        locationDao = new LocationDao(connection);
        locationDao.add(location2);

        dayWeather2 = new WeatherParameters(10.0, 740, 74, 0.0, 2.0);
        weatherParametersDao = new WeatherParametersDao(connection);
        weatherParametersDao.add(dayWeather2);

        nightWeather2 = new WeatherParameters(3.0, 750, 70, 0.7, 1.0);
        weatherParametersDao.add(nightWeather2);

        // Создаем Day объект
        endDay = new Day(
                Date.valueOf("2024-02-02"),
                dayWeather2,
                nightWeather2,
                weatherName2,
                location2
        );
        dayDao.add(endDay);

        personalSettingsDao = new PersonalSettingsDao();
        settings = new PersonalSettings();
        settings.setPhone("000000000");
        personalSettingsDao.add(settings);
        user = new User("user123",user.getHash("123"),"user",settings);
        userDao.add(user);

        // Создаем объект Dashboard
        dashboard = new Dashboard(startDay, endDay, user);
        dashboardDao.add(dashboard);
    }

    @Test
    public void testFindById() throws SQLException {
        // Проверяем, что Dashboard с нужным ID можно найти в базе
        Optional<Dashboard> result = dashboardDao.findById(dashboard.getId());
        assertTrue(result.isPresent());

        // Проверяем, что объект Dashboard содержит правильные значения
        assertEquals(startDay.getId(), result.get().getStartDate().getId());
        assertEquals(endDay.getId(), result.get().getEndDate().getId());
        assertEquals(user.getId(), result.get().getUser().getId());

        // Проверяем, что значения в StartDate и EndDate правильные
        assertEquals("2024-01-01", result.get().getStartDate().getDate().toString());
        assertEquals("2024-02-02", result.get().getEndDate().getDate().toString());
        assertEquals("user123", result.get().getUser().getLogin());
        assertEquals("000000000", result.get().getUser().getPersonalSettings().getPhone());
    }

    @Test
    public void testFindAll() throws SQLException {
        // Получаем все записи в базе данных
        List<Dashboard> dashboardsList = dashboardDao.findAll();

        // Находим наш тестовый объект в списке
        Dashboard foundDashboard = dashboardsList.stream()
                .filter(d -> d.getId() == dashboard.getId())
                .findFirst()
                .orElse(null);

        // Проверяем, что Dashboard найден
        assertNotNull(foundDashboard);

        // Проверяем, что его данные правильные
        assertEquals(dashboard.getStartDate().getId(), foundDashboard.getStartDate().getId());
        assertEquals(dashboard.getEndDate().getId(), foundDashboard.getEndDate().getId());
        assertEquals(dashboard.getUser().getId(), foundDashboard.getUser().getId());

        // Дополнительно проверяем дату начала и окончания
        assertEquals("2024-01-01", foundDashboard.getStartDate().getDate().toString());
        assertEquals("2024-02-02", foundDashboard.getEndDate().getDate().toString());
    }

    @Test
    public void testUpdate() throws SQLException {
        // Обновляем даты начала и конца
        startDay.setDate(Date.valueOf("2024-01-03"));
        endDay.setDate(Date.valueOf("2024-01-04"));
        // Обновляем email пользователя
        user.setLogin("lalala");

        dayDao.update(startDay);
        dayDao.update(endDay);
        userDao.update(user);

        // Устанавливаем новые значения в Dashboard
        dashboard.setStartDate(startDay);
        dashboard.setEndDate(endDay);
        dashboard.setUser(user);

        // Выполняем обновление в базе
        dashboardDao.update(dashboard);

        // Получаем обновленный объект из базы данных
        Optional<Dashboard> result = dashboardDao.findById(dashboard.getId());

        // Проверяем, что объект найден
        assertTrue(result.isPresent());

        // Проверяем, что новые значения были применены
        assertEquals(startDay.getId(), result.get().getStartDate().getId());
        assertEquals(endDay.getId(), result.get().getEndDate().getId());
        assertEquals(user.getId(), result.get().getUser().getId());

        // Проверяем обновленный email пользователя
        assertEquals("lalala", result.get().getUser().getLogin());

        // Проверяем, что новые даты верные
        assertEquals("2024-01-03", result.get().getStartDate().getDate().toString());
        assertEquals("2024-01-04", result.get().getEndDate().getDate().toString());
    }


    @After
    public void testDelete() throws SQLException {
        // Удаляем объект Dashboard
        dashboardDao.delete(dashboard);
        dayDao.delete(startDay);
        dayDao.delete(endDay);
        userDao.delete(user);

        weatherParametersDao.delete(dayWeather1);
        weatherParametersDao.delete(nightWeather1);
        weatherNameDao.delete(weatherName1);
        locationDao.delete(location1);
        weatherParametersDao.delete(dayWeather2);
        weatherParametersDao.delete(nightWeather2);
        weatherNameDao.delete(weatherName2);
        locationDao.delete(location2);
        Optional<Dashboard> result = dashboardDao.findById(dashboard.getId());
        assertFalse(result.isPresent());
    }

    @AfterClass
    public static void tearDownClass() throws SQLException {
        connection.close();
    }
}
