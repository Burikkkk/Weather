package Service;

import Controller.Validation.ErrorStrategy;
import Controller.Validation.LabelErrorStrategy;
import Enums.RequestType;
import Enums.ResponseStatus;
import Models.Entities.Dashboard;
import Models.Entities.User;
import Models.TCP.Request;
import Models.TCP.Response;
import Utilities.ClientSocket;
import com.google.gson.Gson;
import javafx.scene.control.Label;

import java.io.IOException;

public class DashboardService {
    public String addUserDashboard(Dashboard newDashboard, Label labelMessage) {
        ErrorStrategy errorStrategy = new LabelErrorStrategy(labelMessage);
        Request requestModel = new Request();
        requestModel.setRequestMessage(new Gson().toJson(newDashboard));
        requestModel.setRequestType(RequestType.ADD_USER_DASHBOARD);
        ClientSocket.getInstance().getOut().println(new Gson().toJson(requestModel));
        ClientSocket.getInstance().getOut().flush();
        try {
            String answer = ClientSocket.getInstance().getInStream().readLine();
            Response responseModel = new Gson().fromJson(answer, Response.class);
            if (responseModel.getResponseStatus() == ResponseStatus.OK) {
                return "OK";
            } else {
                errorStrategy.handleError("Ошибка загрузки данных: " + responseModel.getResponseMessage());
            }
        } catch (IOException e) {
            new LabelErrorStrategy(labelMessage).handleError("Ошибка связи с сервером.");
        }
        return "ERROR";
    }

    public Dashboard getDashboard(User currentUser, Label labelMessage) {
        ErrorStrategy errorStrategy = new LabelErrorStrategy(labelMessage);
        Request requestModel = new Request();
        requestModel.setRequestMessage(new Gson().toJson(currentUser));
        requestModel.setRequestType(RequestType.GET_DASHBOARD);
        ClientSocket.getInstance().getOut().println(new Gson().toJson(requestModel));
        ClientSocket.getInstance().getOut().flush();
        try {
            String answer = ClientSocket.getInstance().getInStream().readLine();
            Response responseModel = new Gson().fromJson(answer, Response.class);
            if (responseModel.getResponseStatus() == ResponseStatus.OK) {
                return new Gson().fromJson(responseModel.getResponseData(), Dashboard.class);

            } else {
                errorStrategy.handleError("Ошибка загрузки данных: " + responseModel.getResponseMessage());
            }
        } catch (IOException e) {
            errorStrategy.handleError("Ошибка связи с сервером.");
        }
        return null;
    }
}
