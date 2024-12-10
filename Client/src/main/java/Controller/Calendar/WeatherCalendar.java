package Controller.Calendar;

import Controller.Validation.ErrorStrategy;
import Controller.Validation.LabelErrorStrategy;
import Models.Entities.Day;
import Models.Entities.User;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.time.LocalDate;
import java.util.Calendar;

public class WeatherCalendar {
    private final int BUTTONS_IN_A_ROW = 7;
    private final int LINES = 6;

    private Calendar calendar;
    private Button[] dayButtons;
    private GridPane daysGrid;
    private ObservableList<Day> tableDays;
    private User currentUser;
    private String selectedTownCalendar;
    private Label labelError;
    private LocalDate selectedDate;

    public LocalDate getSelectedDate(){
        return selectedDate;
    }

    public WeatherCalendar(Calendar calendar, Button[] dayButtons, GridPane daysGrid, ObservableList<Day> tableDays, User currentUser, String selectedTownCalendar, Label labelError) {
        this.calendar = calendar;
        this.dayButtons = dayButtons;
        this.daysGrid = daysGrid;
        this.tableDays = tableDays;
        this.currentUser = currentUser;
        this.selectedTownCalendar = selectedTownCalendar;
        this.labelError = labelError;
    }

    public void updateCalendar() {

        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH); // Определяем количество дней в месяце
        createDaysButtons(daysInMonth); // Создаем кнопки для дней месяца
    }

    private void createDaysButtons(int daysInMonth) {
        calendar.set(Calendar.DAY_OF_MONTH, 1); // Устанавливаем первый день месяца
        calendar.setFirstDayOfWeek(Calendar.MONDAY); // Устанавливаем первый день недели как понедельник
        int firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY; // Определяем день недели первого числа месяца

        if (firstDayOfWeek < 0) {
            firstDayOfWeek += 7; // Если день недели смещен из-за понедельника как первого дня недели
        }

        dayButtons = new Button[daysInMonth + firstDayOfWeek]; // Размер массива включает "пустые" ячейки для смещения
        daysGrid.getChildren().clear(); // Очищаем сетку перед созданием кнопок

        for (int i = 0; i < dayButtons.length; i++) {
            if (i < firstDayOfWeek) {
                // Добавляем пустые ячейки для смещения (дни до первого числа месяца)
                daysGrid.add(new Label(""), i % BUTTONS_IN_A_ROW, i / BUTTONS_IN_A_ROW);
            } else {
                int day = i - firstDayOfWeek + 1; // Рассчитываем текущий день
                //Button dayButton = new Button(String.valueOf(day)); // Создаем кнопку с текстом для текущего дня

                Button dayButton = new Button(); // Создаем кнопку

                // Устанавливаем текст для кнопки
                String buttonText = setButtonCalendarText(day, calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR));
                dayButton.setText(buttonText);

                dayButton.setPrefSize(daysGrid.getPrefWidth() / BUTTONS_IN_A_ROW, daysGrid.getPrefHeight() / LINES); // Устанавливаем размер кнопки
                dayButton.setOnAction(this::dayPressed); // Устанавливаем обработчик события для кнопки
                daysGrid.add(dayButton, i % BUTTONS_IN_A_ROW, i / BUTTONS_IN_A_ROW); // Добавляем кнопку в сетку
                dayButton.getStyleClass().add("grid-pane-item");
                dayButtons[i] = dayButton; // Сохраняем кнопку в массив
            }
        }


    }

    public String setButtonCalendarText(int day, int month, int year) {
        for (Day tableDay : tableDays) { // tableDays — это ваш ObservableList<Day>
            // Преобразуем `Date` в `LocalDate`
            LocalDate localDate = tableDay.getDate().toLocalDate();
            String dayTemp, nightTemp;
            // Сравнение даты и города
            if (localDate.equals(LocalDate.of(year, month, day)) &&
                    tableDay.getLocation().getTown().equals(selectedTownCalendar)) {

                if (currentUser.getPersonalSettings().getTemperature().equals("F")) {
                    double temp = tableDay.getDayWeather().getTemperature() * 9 / 5 + 32;
                    dayTemp = Double.toString(temp);
                    temp = tableDay.getNightWeather().getTemperature() * 9 / 5 + 32;
                    nightTemp = Double.toString(temp);
                } else {
                    dayTemp = tableDay.getDayWeather().getTemperature().toString();
                    nightTemp = tableDay.getNightWeather().getTemperature().toString();
                }

                // Возвращаем текст кнопки
                return day + "\n" + dayTemp + "/" + nightTemp;
            }
        }

        // Если данных нет, возвращаем просто день
        return String.valueOf(day);
    }


    private void dayPressed(ActionEvent event) {
        Button dayBtn = (Button) event.getSource(); // Получаем кнопку, вызвавшую событие
        try {
            // Разделяем текст на строку и получаем первый элемент, который будет числом дня
            String[] buttonText = dayBtn.getText().split("\n");
            int day = Integer.parseInt(buttonText[0].trim()); // Определяем выбранный день

            calendar.set(Calendar.DAY_OF_MONTH, day); // Устанавливаем день в календаре

            int dayInMonth = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH) + 1; // Преобразуем месяц из 0-базового формата
            int year = calendar.get(Calendar.YEAR);
            selectedDate= LocalDate.of(year, month, dayInMonth); // Устанавливаем дату
            System.out.println("Updated selectedDate: " + selectedDate);

        } catch (NumberFormatException e) {
            ErrorStrategy errorStrategy = new LabelErrorStrategy(labelError);
            errorStrategy.handleError("Неверное значение дня.");
        }
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public void setDayButtons(Button[] dayButtons) {
        this.dayButtons = dayButtons;
    }

    public void setDaysGrid(GridPane daysGrid) {
        this.daysGrid = daysGrid;
    }

    public void setTableDays(ObservableList<Day> tableDays) {
        this.tableDays = tableDays;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public void setSelectedTownCalendar(String selectedTownCalendar) {
        this.selectedTownCalendar = selectedTownCalendar;
    }

    public void setLabelError(Label labelError) {
        this.labelError = labelError;
    }

    public void setSelectedDate(LocalDate selectedDate) {
        this.selectedDate = selectedDate;
    }

}
