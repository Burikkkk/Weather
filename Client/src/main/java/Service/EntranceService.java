package Service;

import Controller.Validation.ErrorStrategy;
import Controller.Validation.LabelErrorStrategy;
import Enums.RequestType;
import Enums.ResponseStatus;
import Models.Entities.User;
import Models.TCP.Request;
import Models.TCP.Response;
import Utilities.ClientSocket;
import com.google.gson.Gson;
import javafx.scene.control.Label;

import java.io.IOException;

public class EntranceService {

    /**
     * Универсальный метод для отправки запросов на сервер.
     *
     * @param user        объект User, который будет сериализован в запрос
     * @param requestType тип запроса
     * @return объект Response, десериализованный из ответа сервера
     * @throws IOException если произошла ошибка ввода/вывода
     */
    private User sendRequest(User user, RequestType requestType, Label labelMessage) {
        try {
            ErrorStrategy errorStrategy = new LabelErrorStrategy(labelMessage);
            // Формирование запроса
            Request requestModel = new Request();
            requestModel.setRequestMessage(new Gson().toJson(user));
            requestModel.setRequestType(requestType);

            // Отправка запроса на сервер
            ClientSocket socket = ClientSocket.getInstance();
            socket.getOut().println(new Gson().toJson(requestModel));
            socket.getOut().flush();

            // Получение ответа
            String answer = socket.getInStream().readLine();
            Response responseModel = new Gson().fromJson(answer, Response.class);

            // Проверка успешного ответа
            if (responseModel.getResponseStatus() == ResponseStatus.OK) {
                return new Gson().fromJson(responseModel.getResponseData(), User.class);
            } else {
                errorStrategy.handleError(responseModel.getResponseMessage());
            }
        } catch (IOException e) {
            new LabelErrorStrategy(labelMessage).handleError("Ошибка связи с сервером.");
        }
        return null;
    }

    public User register(User user,Label labelMessage) {
        return sendRequest(user, RequestType.REGISTER,labelMessage);
    }

    public User login(User user, Label labelMessage) {
        return sendRequest(user, RequestType.LOGIN,labelMessage);
    }

    public User findUser(User user, Label labelMessage) {
        return sendRequest(user, RequestType.FORGOT_PASSWORD,labelMessage);
    }

    public User updateUserPassword(User user,Label labelMessage) {
        return sendRequest(user, RequestType.UPDATE_USER,labelMessage);
    }

}
