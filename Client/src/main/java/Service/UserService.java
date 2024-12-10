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

public class UserService {
    private User sendUserRequest(User user, RequestType requestType, Label labelMessage) {
        ErrorStrategy errorStrategy = new LabelErrorStrategy(labelMessage);

        Request requestModel = new Request();
        requestModel.setRequestMessage(new Gson().toJson(user));
        requestModel.setRequestType(requestType);

        // Отправка запроса на сервер
        ClientSocket socket = ClientSocket.getInstance();
        socket.getOut().println(new Gson().toJson(requestModel));
        socket.getOut().flush();

        // Получение ответа от сервера
        try {
            String answer = socket.getInStream().readLine();
            Response responseModel = new Gson().fromJson(answer, Response.class);
            if (responseModel.getResponseStatus() == ResponseStatus.OK) {
                errorStrategy.handleError(responseModel.getResponseMessage());
                return new Gson().fromJson(responseModel.getResponseData(), User.class);
            } else {
                errorStrategy.handleError("Ошибка загрузки данных: " + responseModel.getResponseMessage());
            }
        } catch (IOException e) {
            errorStrategy.handleError("Ошибка связи с сервером.");
        }
        return null;
    }


    // Логика для выхода пользователя
    public User logout(User currentUser, Label labelMessage) {
        return sendUserRequest(currentUser, RequestType.LOGOUT, labelMessage);
    }

    // Логика для обновления пользователя
    public User update(User user, Label labelMessage) {
        return sendUserRequest(user, RequestType.UPDATE_USER, labelMessage);
    }

}
