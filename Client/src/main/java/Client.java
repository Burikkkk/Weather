import Enums.RequestType;
import Enums.ResponseStatus;
import Models.Entities.User;
import Models.TCP.Request;
import Models.TCP.Response;
import Utilities.ClientSocket;
import Utilities.GetService;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class Client {



    public static void main(String[] args) throws IOException {
        ClientSocket.getInstance().getSocket();

        //GetService<User> getService = new GetService<>(User.class);

        // Получаем данные для регистрации
        User user = new User();
        user.inputUser();  // Запрашиваем данные для регистрации пользователя

        Request request = new Request();

        request.setRequestMessage(new Gson().toJson(user));
        request.setRequestType(RequestType.LOGIN);

        ClientSocket.getInstance().getOut().println(new Gson().toJson(request));
        ClientSocket.getInstance().getOut().flush();



        String answer = ClientSocket.getInstance().getInStream().readLine();
        Response response = new Gson().fromJson(answer, Response.class);
        if (response.getResponseStatus() == ResponseStatus.OK) {
            System.out.println("Ответ от сервера: Регистрация прошла успешно!");
        } else {
            System.out.println("Ошибка при регистрации.");
        }


    }
}
