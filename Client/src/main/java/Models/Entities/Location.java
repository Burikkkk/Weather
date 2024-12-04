package Models.Entities;

import java.io.Serializable;


public class Location implements Serializable {

    private Integer id;
    private String town;
    private String country;


    public Location() {}

    public Location(Integer id, String town, String country) {
        this.id = id;
        this.town = town;
        this.country = country;
    }
    public Location(String town, String country) {
        this.town = town;
        this.country = country;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
