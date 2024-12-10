package Service;

import Controller.Validation.ErrorStrategy;
import Controller.Validation.LabelErrorStrategy;
import Enums.ResponseStatus;
import Models.Entities.Location;
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

public class LocationService {
    public List<Location> getRegions(Label labelMessage) {
        ErrorStrategy errorStrategy = new LabelErrorStrategy(labelMessage);
        Request requestModel = new Request();
        requestModel.setRequestType(RequestType.GET_REGIONS);

        try {
            // Отправка запроса на сервер
            ClientSocket socket = ClientSocket.getInstance();
            socket.getOut().println(new Gson().toJson(requestModel));
            socket.getOut().flush();

            // Получение ответа от сервера
            String answer = socket.getInStream().readLine();
            Response responseModel = new Gson().fromJson(answer, Response.class);

            if (responseModel.getResponseStatus() == ResponseStatus.OK) {
                // Используем TypeToken для точного указания типа списка
                Type regionListType = new TypeToken<List<Location>>() {
                }.getType();
                return new Gson().fromJson(responseModel.getResponseData(), regionListType);
            } else {
                errorStrategy.handleError("Ошибка загрузки данных: " + responseModel.getResponseMessage());
            }
        } catch (IOException e) {
            new LabelErrorStrategy(labelMessage).handleError("Ошибка связи с сервером.");
        }
        return null;
    }
}
