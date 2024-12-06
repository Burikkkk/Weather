package Utilities;

import Models.Entities.User;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.io.IOException;

public class ClientSocket {
    private static ClientSocket SINGLE_INSTANCE;
    private User user;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    // Конструктор для установления соединения с сервером
    private ClientSocket() {
        connectToServer();
    }

    // Метод для создания нового соединения
    private void connectToServer() {
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
        if (SINGLE_INSTANCE == null) {
            SINGLE_INSTANCE = new ClientSocket();
        }
        return SINGLE_INSTANCE;
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

    // Метод для перезапуска соединения
    public static void resetConnection() {
        if (SINGLE_INSTANCE != null) {
            SINGLE_INSTANCE.close(); // Закрываем старое соединение
        }
        SINGLE_INSTANCE = new ClientSocket(); // Создаем новый экземпляр с новым соединением
    }

    // Получение сокета
    public Socket getSocket() {
        return socket;
    }

    // Получение потока ввода
    public BufferedReader getInStream() {
        return in;
    }

    // Получение потока вывода
    public PrintWriter getOut() {
        return out;
    }

    // Получение пользователя
    public User getUser() {
        return user;
    }

    // Установка пользователя
    public void setUser(User user) {
        this.user = user;
    }
}
