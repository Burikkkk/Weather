package Models.Entities;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;

@Entity
@Table(name = "day")
public class Day implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private Date date;

    @ManyToOne
    @JoinColumn(name = "day_weather", referencedColumnName = "id", nullable = true)
    private WeatherParameters dayWeather;

    @ManyToOne
    @JoinColumn(name = "night_weather", referencedColumnName = "id", nullable = true)
    private WeatherParameters nightWeather;

    @ManyToOne
    @JoinColumn(name = "weather_id", referencedColumnName = "id", nullable = true)
    private WeatherName weatherName;

    @ManyToOne
    @JoinColumn(name = "location_id", referencedColumnName = "id", nullable = true)
    private Location location;

    public Day() {}

    public Day(Integer id, Date date, WeatherParameters dayWeather, WeatherParameters nightWeather, WeatherName weatherName, Location location) {
        this.id = id;
        this.date = date;
        this.dayWeather = dayWeather;
        this.nightWeather = nightWeather;
        this.weatherName = weatherName;
        this.location = location;
    }
    public Day(Date date, WeatherParameters dayWeather, WeatherParameters nightWeather, WeatherName weatherName, Location location) {
        this.date = date;
        this.dayWeather = dayWeather;
        this.nightWeather = nightWeather;
        this.weatherName = weatherName;
        this.location = location;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public WeatherParameters getDayWeather() {
        return dayWeather;
    }

    public void setDayWeather(WeatherParameters dayWeather) {
        this.dayWeather = dayWeather;
    }

    public WeatherParameters getNightWeather() {
        return nightWeather;
    }

    public void setNightWeather(WeatherParameters nightWeather) {
        this.nightWeather = nightWeather;
    }

    public WeatherName getWeatherName() {
        return weatherName;
    }

    public void setWeatherName(WeatherName weatherName) {
        this.weatherName = weatherName;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}

