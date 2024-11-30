package Models.Entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "weather_name")
public class WeatherName implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String name;

    // Constructors
    public WeatherName() {}

    public WeatherName(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getters and Setters
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
