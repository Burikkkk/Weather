package Models.Entities;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class User implements Serializable {

    private int id;
    private String login;
    private byte[] password;
    private String role = "user";
    private PersonalSettings personalSettings;


    public User(){}

    public User(int id, String login, byte[] password, String role, PersonalSettings personalSettings) {
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

    public byte[] getPassword() {
        return password;
    }

    public void setPassword(byte[] password) {
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



    public static byte[] getHash(String password) {
        MessageDigest digest = null;
        byte[] hash = null;
        try {
            digest = MessageDigest.getInstance("MD5");
            digest.reset();
            hash = digest.digest(password.getBytes("UTF-8"));

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return hash;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id &&
                (login != null ? login.equals(user.login) : user.login == null) &&
                (role != null ? role.equals(user.role) : user.role == null) &&
                (password != null ? java.util.Arrays.equals(password, user.password) : user.password == null) &&
                (personalSettings != null ? personalSettings.equals(user.personalSettings) : user.personalSettings == null);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (login != null ? login.hashCode() : 0);
        result = 31 * result + (role != null ? role.hashCode() : 0);
        result = 31 * result + java.util.Arrays.hashCode(password); // Для массива используется Arrays.hashCode
        result = 31 * result + (personalSettings != null ? personalSettings.hashCode() : 0);
        return result;
    }


}
