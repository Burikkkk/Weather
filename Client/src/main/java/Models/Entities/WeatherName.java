package Models.Entities;

import java.io.Serializable;


public class WeatherName implements Serializable {

    private Integer id;
    private String name;


    public WeatherName() {}

    public WeatherName(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
    public WeatherName(String name) {
        this.name = name;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
