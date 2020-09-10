package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Vector;

public class Server {
    private List<ClientHandler> clients;
    private AuthService authService;

    private int PORT = 8189;
    ServerSocket server = null;
    Socket socket = null;

    private String HELP = "Доступные команды:\n" +
            "'/w имя_пользователя сообщение' - Личное сообщение\n" +
            "'/end' - Выход из текущей учетной записи\n" +
            "'/help' - Помощь";
    public Server(){
        clients = new Vector<>();
        authService = new SimpleAuthService();

        try {
            server = new ServerSocket(PORT);
            System.out.println("Сервер запущен");

            while (true) {
                socket = server.accept();
                System.out.println("Клиент подключился");

                new ClientHandler(this, socket);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getHELP() {
        return HELP;
    }

    public AuthService getAuthService() {
        return authService;
    }


    public void broadcastMsg(ClientHandler sender, String msg){
        String message = String.format("%s: %s", sender.getNickname(), msg);
        for (ClientHandler c : clients) {
            c.sendMsg(message);
        }
    }

    public void broadcastServerMsg(String msg){
        for (ClientHandler c : clients) {
            c.sendMsg(msg);
        }
    }

    public void subscribe(ClientHandler clientHandler){
        clients.add(clientHandler);
    }

    public void unsubscribe(ClientHandler clientHandler){
        clients.remove(clientHandler);
    }

    public int sendByNickname(ClientHandler clientHandler, String str) {
        boolean status = false;
        String[] msg = str.split(" ", 3);
        for (ClientHandler client : clients) {
            System.out.println(client.getNickname() + " " + msg[1].toLowerCase());
            if (client.getNickname().equals(msg[1].toLowerCase())) {
                clientHandler.sendMsg(msg[2]  + " -> " + client.getNickname());
                client.sendMsg(clientHandler.getNickname() + " -> " + msg[2] );
                return 0;
            }
        };
        clientHandler.sendMsg("Проверьте правильность ввода!\n" +
                "'/help' для помощи");
        return 0;
    }

}
