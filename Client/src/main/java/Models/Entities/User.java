package Models.Entities;

import java.io.Serializable;
import java.util.Scanner;

public class User implements Serializable {

    private int id;
    private String login;
    private String password;
    private String role = "user";
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



    public void inputUser() {
        Scanner scanner = new Scanner(System.in);

        // Запрашиваем у пользователя логин
        System.out.print("Введите логин: ");
        String login = scanner.nextLine();
        this.setLogin(login);  // Сохраняем введённый логин

        // Запрашиваем у пользователя пароль
        System.out.print("Введите пароль: ");
        String password = scanner.nextLine();

        // Запрашиваем подтверждение пароля
        String password1;
        do {
            System.out.print("Подтвердите пароль: ");
            password1 = scanner.nextLine();
            if (password.equals(password1)) {
                this.setPassword(password);  // Сохраняем введённый пароль
            } else {
                System.out.println("Неверный пароль, повторите ввод.");
            }
        } while (!password.equals(password1));  // Повторяем запрос до совпадения пароля

        // Создаём объект PersonalSettings и запрашиваем настройки
        PersonalSettings settings = new PersonalSettings();

        // Запрашиваем номер телефона
        System.out.print("Введите номер телефона (или оставьте пустым): ");
        String phone = scanner.nextLine();
        settings.setPhone(phone);  // Сохраняем номер телефона (может быть пустым)

        // Запрашиваем настройку уведомлений
        System.out.print("Включить уведомления? (true/false): ");
        boolean notifications = Boolean.parseBoolean(scanner.nextLine());
        settings.setNotifications(notifications);  // Сохраняем выбор пользователя по уведомлениям

        // Запрашиваем единицу измерения температуры
        System.out.print("Введите единицу измерения температуры (по умолчанию 'C'): ");
        String temperature = scanner.nextLine();
        if (temperature.isEmpty()) {
            temperature = "C";  // Если не введено, ставим значение по умолчанию
        }
        settings.setTemperature(temperature);  // Сохраняем единицу измерения температуры

        // Запрашиваем единицу измерения давления
        System.out.print("Введите единицу измерения давления (по умолчанию 'мм рт. ст.'): ");
        String pressure = scanner.nextLine();
        if (pressure.isEmpty()) {
            pressure = "мм рт. ст.";  // Если не введено, ставим значение по умолчанию
        }
        settings.setPressure(pressure);  // Сохраняем единицу измерения давления

        // Запрашиваем единицу измерения скорости
        System.out.print("Введите единицу измерения скорости (по умолчанию 'м/с'): ");
        String speed = scanner.nextLine();
        if (speed.isEmpty()) {
            speed = "м/с";  // Если не введено, ставим значение по умолчанию
        }
        settings.setSpeed(speed);  // Сохраняем единицу измерения скорости

        // Связываем настройки с пользователем
        this.setPersonalSettings(settings);

        System.out.println("Пользователь успешно создан.");
    }

}
