package Models.Entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "weather_parameters")
public class WeatherParameters implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private Double temperature;

    @Column
    private Integer pressure;

    @Column
    private Integer humidity;

    @Column
    private Double precipitation;

    @Column
    private Double windSpeed;

    // Constructors
    public WeatherParameters() {}

    public WeatherParameters(Integer id, Double temperature, Integer pressure, Integer humidity, Double precipitation, Double windSpeed) {
        this.id = id;
        this.temperature = temperature;
        this.pressure = pressure;
        this.humidity = humidity;
        this.precipitation = precipitation;
        this.windSpeed = windSpeed;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Integer getPressure() {
        return pressure;
    }

    public void setPressure(Integer pressure) {
        this.pressure = pressure;
    }

    public Integer getHumidity() {
        return humidity;
    }

    public void setHumidity(Integer humidity) {
        this.humidity = humidity;
    }

    public Double getPrecipitation() {
        return precipitation;
    }

    public void setPrecipitation(Double precipitation) {
        this.precipitation = precipitation;
    }

    public Double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(Double windSpeed) {
        this.windSpeed = windSpeed;
    }
}

