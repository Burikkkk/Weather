package DAO;

import Models.Entities.Day;
import Models.Entities.WeatherName;
import Models.Entities.WeatherParameters;
import Models.Entities.Location;
import Utilities.ConnectorDB;
import org.junit.*;

import java.sql.*;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class DayDaoTest {

    private static Connection connection;
    private DayDao dayDao;
    private Day day;
    private WeatherParameters dayWeather;
    private WeatherParameters nightWeather;
    private WeatherName weatherName;
    private Location location;
    private WeatherNameDao weatherNameDao;
    private LocationDao locationDao;
    private WeatherParametersDao weatherParametersDao;


    @BeforeClass
    public static void setupClass() throws SQLException {
        connection = ConnectorDB.getConnection();
    }

    @Before
    public void setup() throws SQLException {
        dayDao = new DayDao(connection);

        // Создаем тестовые объекты WeatherName, Location и WeatherParameters
        weatherName = new WeatherName("Солнечно");
        weatherNameDao = new WeatherNameDao(connection);
        weatherNameDao.add(weatherName);

        location = new Location("Брест", "Беларусь");
        locationDao = new LocationDao(connection);
        locationDao.add(location);

        dayWeather = new WeatherParameters(25.5, 760, 80, 0.0, 5.0);
        weatherParametersDao = new WeatherParametersDao(connection);
        weatherParametersDao.add(dayWeather);

        nightWeather = new WeatherParameters(15.0, 755, 90, 0.1, 3.0);
        weatherParametersDao.add(nightWeather);

        // Создаем Day объект
        day = new Day(
                Date.valueOf("2024-12-04"),
                dayWeather,
                nightWeather,
                weatherName,
                location
        );
        dayDao.add(day);
    }

    @Test
    public void testFindById() throws SQLException {
        Optional<Day> result = dayDao.findById(day.getId());
        assertTrue(result.isPresent());
        assertEquals(Date.valueOf("2024-12-04"), result.get().getDate());
        assertEquals(dayWeather.getId(), result.get().getDayWeather().getId());
        assertEquals(nightWeather.getId(), result.get().getNightWeather().getId());
        assertEquals(weatherName.getId(), result.get().getWeatherName().getId());
        assertEquals(location.getId(), result.get().getLocation().getId());
    }

    @Test
    public void testFindAll() throws SQLException {
        List<Day> daysList = dayDao.findAll();
        Day foundDay = daysList.stream()
                .filter(d -> d.getId() == day.getId())
                .findFirst()
                .orElse(null);

        assertNotNull(foundDay);
        assertEquals(day.getDate(), foundDay.getDate());
        assertEquals(day.getDayWeather().getId(), foundDay.getDayWeather().getId());
        assertEquals(day.getNightWeather().getId(), foundDay.getNightWeather().getId());
        assertEquals(day.getWeatherName().getId(), foundDay.getWeatherName().getId());
        assertEquals(day.getLocation().getId(), foundDay.getLocation().getId());
    }

    @Test
    public void testUpdate() throws SQLException {
        day.setDate(Date.valueOf("2024-01-05"));

        // Создаём объекты WeatherParameters и WeatherName (проверка на null)
        WeatherParameters dayWeather = new WeatherParameters(28.0, 760, 75, 0.0, 6.0);
        WeatherParameters nightWeather = new WeatherParameters(18.0, 755, 85, 0.2, 4.0);
        WeatherName weatherName = new WeatherName("Возможны осадки");
        Location location = new Location("Лиозно", "Беларусь");
        weatherNameDao.add(weatherName);
        locationDao.add(location);
        weatherParametersDao.add(dayWeather);
        weatherParametersDao.add(nightWeather);


        // Устанавливаем их в объект Day
        day.setDayWeather(dayWeather);
        day.setNightWeather(nightWeather);
        day.setWeatherName(weatherName);
        day.setLocation(location);

        // Выполняем обновление
        dayDao.update(day);

        // Получаем обновлённый объект Day по ID
        Optional<Day> result = dayDao.findById(day.getId());

        // Проверяем, что объект найден
        assertTrue(result.isPresent());

        assertEquals(Date.valueOf("2024-01-05"), result.get().getDate());
        assertEquals("Возможны осадки", result.get().getWeatherName().getName());
        assertEquals("Лиозно", result.get().getLocation().getTown());
        assertEquals(28.0, result.get().getDayWeather().getTemperature(), 0.01);
        assertEquals(18.0, result.get().getNightWeather().getTemperature(), 0.01);

        weatherNameDao.delete(weatherName);
        locationDao.delete(location);
        weatherParametersDao.delete(dayWeather);
        weatherParametersDao.delete(nightWeather);
    }

    @After
    public void testDelete() throws SQLException {
        dayDao.delete(day);
        weatherNameDao.delete(weatherName);
        locationDao.delete(location);
        weatherParametersDao.delete(dayWeather);
        weatherParametersDao.delete(nightWeather);
        Optional<Day> result = dayDao.findById(day.getId());
        assertFalse(result.isPresent());
    }


    @AfterClass
    public static void tearDownClass() throws SQLException {
        connection.close();
    }
}
