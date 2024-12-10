package Controller.Validation;

public class Validation {
    private ErrorStrategy errorStrategy;

    public Validation(ErrorStrategy errorStrategy) {
        this.errorStrategy = errorStrategy;
    }


    public String validateLogin(String login) {
        if (!login.matches("^[a-zA-Z0-9]+$")) {
            errorStrategy.handleError("Логин должен содержать только латинские буквы или цифры!");
            return "error";
        }
        if (login.length() < 3 || login.length() > 25) {
            errorStrategy.handleError("Логин должен быть от 3 до 25 символов!");
            return "error";
        }
        return login;
    }

    public String validatePhoneNumber(String phoneNumber) {
        if (phoneNumber.length() != 9 || !phoneNumber.matches("\\d+")) {
            errorStrategy.handleError("Введите номер телефона с кодом!");
            return "error";
        }
        return phoneNumber;
    }

    public String validateAndFormatString(String input) {
        // Убираем лишние пробелы
        input = input.trim();
        // Проверка на длину строки
        if (input.length() <= 3) {
            errorStrategy.handleError("Строка должна быть длиннее 3 символов");
            return "error";
        }

        // Проверка на наличие только допустимых символов: буквы, пробелы и запятые
        if (!input.matches("[a-zA-Zа-яА-ЯёЁ\\s,]+")) {
            errorStrategy.handleError("Строка должна содержать только буквы, пробелы или запятые");
            return "error"; // Возвращаем "error" для указания ошибки
        }
        // Преобразуем первую букву в верхний регистр, остальные — в нижний
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }

    public double validateAndParseDouble(String input){
        if (!input.matches("-?\\d+(\\.\\d+)?")) {
            errorStrategy.handleError("Число должно содержать только цифры");
            return -1000;
        }
        double value = Double.parseDouble(input);
        if (value < -1000||value>1000) {
            errorStrategy.handleError("Слишком маленькое или большое значение");
            return -1000;
        }
        return value;
    }

    public int validateAndParseInt(String input) {
        // Проверка на наличие букв (ни русских, ни английских)
        if (!input.matches("-?\\d+(\\.\\d+)?")) {
            errorStrategy.handleError("Число должно содержать только цифры");
            return -1000; // Возвращаем ошибочный код
        }
        return Integer.parseInt(input.trim()); // Убираем пробелы и пытаемся распарсить
        // Проверка на положительное значение
    }

}

