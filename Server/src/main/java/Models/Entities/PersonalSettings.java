package Models.Entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "personal_settings")
public class PersonalSettings implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String phone;

    @Column
    private boolean notifications = false;

    @Column
    private String temperature = "C";

    @Column
    private String pressure = "мм рт. ст.";

    @Column
    private String speed = "м/с";


    public PersonalSettings(){}

    public PersonalSettings(String phone, boolean notifications, String temperature, String pressure, String speed) {
        this.phone = phone;
        this.notifications = notifications;
        this.temperature = temperature;
        this.pressure = pressure;
        this.speed = speed;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isNotifications() {
        return notifications;
    }

    public void setNotifications(boolean notifications) {
        this.notifications = notifications;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getPressure() {
        return pressure;
    }

    public void setPressure(String pressure) {
        this.pressure = pressure;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }
}

