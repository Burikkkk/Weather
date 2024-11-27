package Models.Entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Scanner;

@Entity
@Table(name = "user")
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String login;

    @Column
    private String password;

    @Column
    private String role = "user";

    @OneToOne(optional = false)
    @JoinColumn(name = "settings_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "FK_user_personal_settings"))
    private PersonalSettings personalSettings;


    public User(){}

    public User(int id, String login, String password, String role, PersonalSettings personalSettings) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.role = role;
        this.personalSettings = personalSettings;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public PersonalSettings getPersonalSettings() {
        return personalSettings;
    }

    public void setPersonalSettings(PersonalSettings personalSettings) {
        this.personalSettings = personalSettings;
    }

}
