package Utilities;

import Models.Entities.User;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.io.IOException;

public class ClientSocket {
    private static final ClientSocket SINGLE_INSTANCE = new ClientSocket();
    private User user;
    private static Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    // Конструктор для установления соединения с сервером
    private ClientSocket() {
        try {
            // Подключаемся к серверу по указанному адресу и порту
            socket = new Socket("localhost", 5555);
            // Создаём потоки для ввода и вывода данных
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true); // Автоматически вызывает flush()
        } catch (IOException e) {
            System.err.println("Ошибка при подключении к серверу: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Получение экземпляра синглтона
    public static ClientSocket getInstance() {
        return SINGLE_INSTANCE;
    }

    // Получение сокета
    public Socket getSocket() {
        return socket;
    }

    // Получение потока ввода
    public BufferedReader getInStream() {
        return in;
    }

    // Получение пользователя
    public User getUser() {
        return user;
    }

    // Установка пользователя
    public void setUser(User user) {
        this.user = user;
    }

    // Получение потока вывода
    public PrintWriter getOut() {
        return out;
    }

    // Установка потока вывода
    public void setOut(PrintWriter out) {
        this.out = out;
    }

    // Метод для закрытия всех ресурсов
    public void close() {
        try {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("Ошибка при закрытии ресурсов: " + e.getMessage());
        }
    }
}
