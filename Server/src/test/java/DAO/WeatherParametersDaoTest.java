package DAO;

import Models.Entities.WeatherParameters;
import Utilities.ConnectorDB;
import org.junit.*;

import java.sql.*;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class WeatherParametersDaoTest {

    private static Connection connection;
    private WeatherParametersDao weatherParametersDao;
    private WeatherParameters weatherParameters;
    private WeatherParameters weatherParameters1;

    @BeforeClass
    public static void setupClass() throws SQLException {
        connection = ConnectorDB.getConnection();
    }

    @Before
    public void setup() throws SQLException {
        weatherParametersDao = new WeatherParametersDao(connection);
        weatherParameters = new WeatherParameters(25.5, 760, 80, 20.0, 5.5);
        weatherParametersDao.add(weatherParameters);
    }

    @Test
    public void testFindById() throws SQLException {
        Optional<WeatherParameters> result = weatherParametersDao.findById(weatherParameters.getId());
        assertTrue(result.isPresent());
        assertEquals(25.5, result.get().getTemperature(), 0.01);
        assertEquals(760.0, result.get().getPressure(), 0.01);
        assertEquals(80.0, result.get().getHumidity(), 0.01);
        assertEquals(20.0, result.get().getPrecipitation(), 0.01);
        assertEquals(5.5, result.get().getWindSpeed(), 0.01);
    }

    @Test
    public void testFindAll() throws SQLException {
        weatherParameters1 = new WeatherParameters(18.3, 770, 75, 0.0, 3.0);
        weatherParametersDao.add(weatherParameters1);

        List<WeatherParameters> parametersList = weatherParametersDao.findAll();

        // Поиск объектов в списке по их ID
        WeatherParameters foundWeatherParameters = parametersList.stream()
                .filter(w -> w.getId() == weatherParameters.getId())
                .findFirst()
                .orElse(null);

        WeatherParameters foundWeatherParameters1 = parametersList.stream()
                .filter(w -> w.getId() == weatherParameters1.getId())
                .findFirst()
                .orElse(null);

        // Проверка, что объекты найдены и их данные корректны
        assertNotNull(foundWeatherParameters);
        assertEquals(weatherParameters.getTemperature(), foundWeatherParameters.getTemperature(), 0.01);
        assertEquals(weatherParameters.getPressure(), foundWeatherParameters.getPressure());
        assertEquals(weatherParameters.getHumidity(), foundWeatherParameters.getHumidity());
        assertEquals(weatherParameters.getPrecipitation(), foundWeatherParameters.getPrecipitation(), 0.01);
        assertEquals(weatherParameters.getWindSpeed(), foundWeatherParameters.getWindSpeed(), 0.01);

        assertNotNull(foundWeatherParameters1);
        assertEquals(weatherParameters1.getTemperature(), foundWeatherParameters1.getTemperature(), 0.01);
        assertEquals(weatherParameters1.getPressure(), foundWeatherParameters1.getPressure());
        assertEquals(weatherParameters1.getHumidity(), foundWeatherParameters1.getHumidity());
        assertEquals(weatherParameters1.getPrecipitation(), foundWeatherParameters1.getPrecipitation(), 0.01);
        assertEquals(weatherParameters1.getWindSpeed(), foundWeatherParameters1.getWindSpeed(), 0.01);

        // Удаление созданного объекта
        weatherParametersDao.delete(weatherParameters1);
    }

    @Test
    public void testUpdate() throws SQLException {
        weatherParameters.setTemperature(30.0);
        weatherParameters.setPressure(700);
        weatherParameters.setHumidity(70);
        weatherParameters.setPrecipitation(0.5);
        weatherParameters.setWindSpeed(6.5);
        weatherParametersDao.update(weatherParameters);

        Optional<WeatherParameters> result = weatherParametersDao.findById(weatherParameters.getId());
        assertTrue(result.isPresent());
        assertEquals(30.0, result.get().getTemperature(), 0.01);
        assertEquals(700.0, result.get().getPressure(), 0.01);
        assertEquals(70.0, result.get().getHumidity(), 0.01);
        assertEquals(0.5, result.get().getPrecipitation(), 0.01);
        assertEquals(6.5, result.get().getWindSpeed(), 0.01);
    }

    @After
    public void testDelete() throws SQLException {
        weatherParametersDao.delete(weatherParameters);
        Optional<WeatherParameters> result = weatherParametersDao.findById(weatherParameters.getId());
        assertFalse(result.isPresent());
    }

    @AfterClass
    public static void tearDownClass() throws SQLException {
        connection.close();
    }
}
