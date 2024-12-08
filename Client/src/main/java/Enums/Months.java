package Enums;

public enum Months {
    Январь(1),
    Февраль(2),
    Март(3),
    Апрель(4),
    Май(5),
    Июнь(6),
    Июль(7),
    Август(8),
    Сентябрь(9),
    Октябрь(10),
    Ноябрь(11),
    Декабрь(12);

    private final int monthNumber;

    Months(int monthNumber) {
        this.monthNumber = monthNumber;
    }

    public int getMonthNumber() {
        return monthNumber;
    }

    public static Months fromNumber(int number) {
        for (Months month : Months.values()) {
            if (month.getMonthNumber() == number) {
                return month;
            }
        }
        return null;
    }

    public static int fromName(String name) {
        for (Months month : Months.values()) {
            if (month.name().equalsIgnoreCase(name)) {
                return month.getMonthNumber();
            }
        }

        return 0;
    }
}
