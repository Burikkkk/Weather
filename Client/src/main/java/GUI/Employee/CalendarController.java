package GUI.Employee;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.util.Calendar;

public class CalendarController {

	@FXML
	private GridPane daysGrid;

	@FXML
	private MenuButton monthMenuBtn;

	@FXML
	private Button saveBtn;

	@FXML
	private TextArea textArea;

	@FXML
	private Label chosenDateLabel;

	@FXML
	private MenuButton yearMenuBtn;

	private final Calendar calendar = Calendar.getInstance();
	private boolean monthChosen = false;
	private boolean yearChosen = false;
	private final int BUTTONS_IN_A_ROW = 7;
	private final int LINES = 6;

	private Button[] dayButtons;
	private final CalendarData calendarData = new CalendarData();

	/**
	 * Метод вызывается при выборе месяца
	 */
	@FXML
	void monthPressed(ActionEvent event) {
		MenuItem btn = (MenuItem) event.getSource();
		monthMenuBtn.setText(btn.getText()); // Устанавливаем текст выбранного месяца на кнопку
		try {
			int month = Integer.parseInt(btn.getText());
			calendar.set(Calendar.MONTH, month - 1); // Устанавливаем выбранный месяц (учитываем, что в Calendar месяцы начинаются с 0)
			monthChosen = true;
			updateCalendar(); // Обновляем календарь
		} catch (NumberFormatException e) {
			showError("Неверный формат месяца: " + btn.getText());
		}
	}

	/**
	 * Метод вызывается при нажатии на кнопку "Сохранить"
	 */
	@FXML
	void savePressed(ActionEvent event) {
		calendarData.setData(calendar, textArea.getText()); // Сохраняем данные для выбранной даты
	}

	/**
	 * Метод вызывается при выборе года
	 */
	@FXML
	void yearPressed(ActionEvent event) {
		MenuItem btn = (MenuItem) event.getSource();
		yearMenuBtn.setText(btn.getText()); // Устанавливаем текст выбранного года на кнопку
		try {
			int year = Integer.parseInt(btn.getText());
			calendar.set(Calendar.YEAR, year); // Устанавливаем выбранный год
			yearChosen = true;
			updateCalendar(); // Обновляем календарь
		} catch (NumberFormatException e) {
			showError("Неверный формат года: " + btn.getText());
		}
	}

	/**
	 * Обновляет календарь, если выбраны месяц и год
	 */
	private void updateCalendar() {
		if (!monthChosen || !yearChosen) return; // Проверяем, что месяц и год выбраны

		int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH); // Определяем количество дней в месяце
		createDaysButtons(daysInMonth); // Создаем кнопки для дней месяца
	}

	/**
	 * Создает кнопки для отображения дней в календаре
	 * @param daysInMonth количество дней в текущем месяце
	 */
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
				Button dayButton = new Button(String.valueOf(day)); // Создаем кнопку с текстом для текущего дня
				dayButton.setPrefSize(daysGrid.getPrefWidth() / BUTTONS_IN_A_ROW, daysGrid.getPrefHeight() / LINES); // Устанавливаем размер кнопки
				dayButton.setOnAction(this::dayPressed); // Устанавливаем обработчик события для кнопки
				daysGrid.add(dayButton, i % BUTTONS_IN_A_ROW, i / BUTTONS_IN_A_ROW); // Добавляем кнопку в сетку
				dayButtons[i] = dayButton; // Сохраняем кнопку в массив
			}
		}
	}

	/**
	 * Обрабатывает нажатие на кнопку дня
	 * @param event событие нажатия
	 */
	private void dayPressed(ActionEvent event) {
		Button dayBtn = (Button) event.getSource(); // Получаем кнопку, вызвавшую событие
		try {
			int day = Integer.parseInt(dayBtn.getText()); // Определяем выбранный день
			calendar.set(Calendar.DAY_OF_MONTH, day); // Устанавливаем день в календаре
			textArea.setText(calendarData.getData(calendar)); // Отображаем данные для выбранной даты

			int dayInMonth = calendar.get(Calendar.DAY_OF_MONTH);
			int month = calendar.get(Calendar.MONTH) + 1; // Преобразуем месяц из 0-базового формата
			int year = calendar.get(Calendar.YEAR);

			// Обновляем текст метки с выбранной датой
			chosenDateLabel.setText(String.format("Выбранная дата: %d/%d/%d", dayInMonth, month, year));
		} catch (NumberFormatException e) {
			showError("Неверное значение дня.");
		}
	}

	/**
	 * Отображает сообщение об ошибке
	 * @param message текст сообщения
	 */
	private void showError(String message) {
		Alert alert = new Alert(Alert.AlertType.ERROR); // Создаем окно с типом "Ошибка"
		alert.setTitle("Ошибка");
		alert.setContentText(message);
		alert.showAndWait(); // Показываем сообщение пользователю
	}
}
