package Utilities;

import com.google.gson.Gson;
import Enums.RequestType;
import Models.TCP.Request;
import Models.TCP.Response;

import java.io.IOException;

public class GetService<T> {
    final Class<T> ClassType;

    public GetService(Class<T> classType) {
        this.ClassType = classType;
    }

    // Метод для получения списка сущностей
    public String GetEntities(RequestType requestType) {
        Request request = new Request();
        request.setRequestMessage("");
        request.setRequestType(requestType);

        // Отправляем запрос на сервер
        sendRequest(request);

        // Получаем ответ от сервера
        String answer = receiveResponse();

        if (answer != null) {
            // Десериализуем ответ
            Response response = new Gson().fromJson(answer, Response.class);
            return response.getResponseData();
        }
        return null;
    }

    // Метод для получения одной сущности
    public T GetEntity(RequestType requestType, T entity) throws IOException {
        Request request = new Request();
        request.setRequestMessage(new Gson().toJson(entity));
        request.setRequestType(requestType);

        // Отправляем запрос на сервер
        sendRequest(request);

        // Получаем ответ от сервера
        String answer = receiveResponse();

        if (answer != null) {
            Response response = new Gson().fromJson(answer, Response.class);
            // Десериализуем данные в объект типа T
            return new Gson().fromJson(response.getResponseData(), ClassType);
        }
        return null;
    }

    // Метод для отправки запроса
    private void sendRequest(Request request) {
        try {
            String jsonRequest = new Gson().toJson(request);
            ClientSocket.getInstance().getOut().println(jsonRequest);
            ClientSocket.getInstance().getOut().flush(); // Обязательно вызываем flush, чтобы данные были отправлены
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Ошибка при отправке запроса.");
        }
    }

    // Метод для получения ответа от сервера
    private String receiveResponse() {
        String answer = null;
        try {
            // Чтение ответа от сервера
            answer = ClientSocket.getInstance().getInStream().readLine();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Ошибка при получении ответа от сервера.");
        }
        return answer;
    }
}
