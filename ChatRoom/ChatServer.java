import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class ChatServer {
    private static final int PORT = 1234;
    private static final int MAX_CLIENTS = 100;
    private static List<ClientHandler> clients = Collections.synchronizedList(new ArrayList<>());

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Chat server started on port " + PORT);

        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress().getHostAddress());
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            } catch (IOException e) {
                System.out.println("Error accepting client: " + e.getMessage());
            }
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String nickname;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out.println("please input your nickname :");
                // 获取客户端昵称
                this.nickname = in.readLine();
                System.out.println("Nickname: " + nickname + " has joined the chat.");

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    System.out.println("Received message from " + nickname + ": " + inputLine);
                    broadcast(inputLine, nickname);
                }
            } catch (IOException e) {
                System.out.println("Error in client handler: " + e.getMessage());
            } finally {
                try {
                    in.close();
                    out.close();
                    socket.close();
                    clients.remove(this);
                    System.out.println(nickname + " has left the chat.");
                } catch (IOException e) {
                    System.out.println("Error closing client connection: " + e.getMessage());
                }
            }
        }

        private void broadcast(String message, String nickname) {
            for (ClientHandler client : clients) {
				if (!client.nickname.equals(this.nickname)) {
                	client.out.println(nickname + ": " + message);
				}
            }
        }
    }
}
