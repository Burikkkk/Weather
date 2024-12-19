package Service;

import Controller.Validation.ErrorStrategy;
import Controller.Validation.LabelErrorStrategy;
import Enums.ResponseStatus;
import Models.Entities.Day;
import Enums.RequestType;
import Models.TCP.Request;
import Models.TCP.Response;
import Utilities.ClientSocket;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.List;
import com.google.gson.reflect.TypeToken;
import javafx.scene.control.Label;
import java.lang.reflect.Type;

public class DayService {
    public List<Day> getAllDays(Label labelMessage) {
        ErrorStrategy errorStrategy = new LabelErrorStrategy(labelMessage);
        Request requestModel = new Request();
        requestModel.setRequestType(RequestType.GET_ALL_DAYS);

        try {
            // Отправка запроса на сервер
            ClientSocket socket = ClientSocket.getInstance();
            socket.getOut().println(new Gson().toJson(requestModel));
            socket.getOut().flush();

            // Получение ответа
            String answer = socket.getInStream().readLine();
            Response responseModel = new Gson().fromJson(answer, Response.class);

            if (responseModel.getResponseStatus() == ResponseStatus.OK) {
                // Используем TypeToken для точного указания типа списка
                Type weatherListType = new TypeToken<List<Day>>() {
                }.getType();
                return new Gson().fromJson(responseModel.getResponseData(), weatherListType);
            } else {
                errorStrategy.handleError(responseModel.getResponseMessage());
            }
        } catch (IOException e) {
            new LabelErrorStrategy(labelMessage).handleError("Ошибка связи с сервером.");
        }
        return null;
    }

    private Day sendDayRequest(Day day, RequestType requestType, Label labelMessage) {
        ErrorStrategy errorStrategy = new LabelErrorStrategy(labelMessage);
        Request requestModel = new Request();
        requestModel.setRequestMessage(new Gson().toJson(day));
        requestModel.setRequestType(requestType);

        // Отправляем запрос
        ClientSocket socket = ClientSocket.getInstance();
        socket.getOut().println(new Gson().toJson(requestModel));
        socket.getOut().flush();

        // Получаем ответ от сервера
        try {
            String answer = socket.getInStream().readLine();
            Response responseModel = new Gson().fromJson(answer, Response.class);
            if (responseModel.getResponseStatus() == ResponseStatus.OK) {
                errorStrategy.handleError(responseModel.getResponseMessage());
                return new Gson().fromJson(responseModel.getResponseData(), Day.class);
            } else {
                errorStrategy.handleError(responseModel.getResponseMessage());
            }
        } catch (IOException e) {
            errorStrategy.handleError("Ошибка связи с сервером.");
        }
        return null;
    }


    public Day getDay(Day insertDay, Label labelMessage) {
        return sendDayRequest(insertDay, RequestType.ADD_DAY, labelMessage);
    }

    public Day updateDay(Day updatedDay, Label labelMessage) {
        return sendDayRequest(updatedDay, RequestType.UPDATE_DAY, labelMessage);
    }

    public Day getSelectedDayWeather(Day updatedDay, Label labelMessage) {
        return sendDayRequest(updatedDay, RequestType.TODAY_WEATHER, labelMessage);
    }


    public String deleteDay(Day deletedDay, Label labelMessage) {
        ErrorStrategy errorStrategy = new LabelErrorStrategy(labelMessage);
        Request requestModel = new Request();
        requestModel.setRequestMessage(new Gson().toJson(deletedDay));
        requestModel.setRequestType(RequestType.DELETE_DAY);
        ClientSocket.getInstance().getOut().println(new Gson().toJson(requestModel));
        ClientSocket.getInstance().getOut().flush();
        try {
            String answer = ClientSocket.getInstance().getInStream().readLine();
            Response responseModel = new Gson().fromJson(answer, Response.class);
            if (responseModel.getResponseStatus() == ResponseStatus.OK) {

                errorStrategy.handleError(responseModel.getResponseMessage());
                return "OK";

            } else {
                errorStrategy.handleError(responseModel.getResponseMessage());
            }
        } catch (IOException e) {
            new LabelErrorStrategy(labelMessage).handleError("Ошибка связи с сервером.");
        }
        return "ERROR";
    }





}
