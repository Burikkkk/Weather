import Utilities.ClientThread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private static final int PORT_NUMBER = 5555;
    private static ServerSocket serverSocket;

    private static ClientThread clientHandler;
    private static Thread thread;
    private static List<Socket> currentSockets = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        serverSocket = new ServerSocket(PORT_NUMBER);
        while (true) {
            // Проходим по списку через обычный цикл for с индексами
            for (int i = 0; i < currentSockets.size(); i++) {
                Socket socket = currentSockets.get(i);
                if (socket.isClosed()) {
                    // Если сокет закрыт, удаляем его из списка
                    currentSockets.remove(i);
                    i--; // Сдвигаем индекс на одну позицию назад, чтобы не пропустить следующий элемент
                    continue;
                }

            }

            // Ожидаем нового клиента
            Socket socket = serverSocket.accept();
            currentSockets.add(socket);
            clientHandler = new ClientThread(socket);
            thread = new Thread(clientHandler);
            thread.start(); // Запускаем новый поток для клиента

            System.out.flush();
        }
    }
}
