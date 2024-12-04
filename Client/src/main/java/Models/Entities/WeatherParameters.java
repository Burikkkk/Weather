package Models.Entities;

import java.io.Serializable;


public class WeatherParameters implements Serializable {

    private Integer id;
    private Double temperature;
    private Integer pressure;
    private Integer humidity;
    private Double precipitation;
    private Double windSpeed;


    public WeatherParameters() {}

    public WeatherParameters(Integer id, Double temperature, Integer pressure, Integer humidity, Double precipitation, Double windSpeed) {
        this.id = id;
        this.temperature = temperature;
        this.pressure = pressure;
        this.humidity = humidity;
        this.precipitation = precipitation;
        this.windSpeed = windSpeed;
    }
    public WeatherParameters(Double temperature, Integer pressure, Integer humidity, Double precipitation, Double windSpeed) {
        this.temperature = temperature;
        this.pressure = pressure;
        this.humidity = humidity;
        this.precipitation = precipitation;
        this.windSpeed = windSpeed;
    }

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

