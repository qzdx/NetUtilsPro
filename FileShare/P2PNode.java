import java.io.*;
import java.net.*;
import java.util.Scanner;

public class P2PNode {
    private static final int PORT = 8888;
    private static final int BUFFER_SIZE = 8192;

    public static void main(String[] args) {
        // 启动服务器线程
        new Thread(P2PNode::startServer).start();
        
        // 启动客户端功能
        startClient();
    }

    // 服务器功能 - 持续监听文件传输请求
    private static void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("服务器已启动，监听端口: " + PORT);
            
            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     DataInputStream dis = new DataInputStream(clientSocket.getInputStream())) {
                    
                    // 接收文件信息
                    String fileName = dis.readUTF();
                    long fileSize = dis.readLong();
                    
                    // 接收文件内容
                    receiveFile(dis, fileName, fileSize);
                    System.out.println("接收文件成功: " + fileName);
                } catch (IOException e) {
                    System.err.println("文件接收错误: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("服务器启动失败: " + e.getMessage());
        }
    }

    // 客户端功能 - 主动发送文件
    private static void startClient() {
        Scanner scanner = new Scanner(System.in);
        
        while (true) {
            System.out.println("\n输入命令: ");
            System.out.println("1. send <目标IP> <文件路径>");
            System.out.println("2. exit");
            System.out.print("> ");
            
            String command = scanner.nextLine();
            if (command.equalsIgnoreCase("exit")) {
                System.exit(0);
            }
            
            if (command.startsWith("send")) {
                String[] parts = command.split(" ");
                if (parts.length != 3) {
                    System.out.println("无效命令格式!");
                    continue;
                }
                
                String targetIP = parts[1];
                String filePath = parts[2];
                sendFile(targetIP, filePath);
            } else {
                System.out.println("未知命令");
            }
        }
    }

    // 接收文件实现
    private static void receiveFile(DataInputStream dis, String fileName, long fileSize) throws IOException {
        try (FileOutputStream fos = new FileOutputStream("received_" + fileName)) {
            byte[] buffer = new byte[BUFFER_SIZE];
            long remaining = fileSize;
            
            while (remaining > 0) {
                int read = dis.read(buffer, 0, (int) Math.min(buffer.length, remaining));
                if (read == -1) break;
                fos.write(buffer, 0, read);
                remaining -= read;
            }
        }
    }

    // 发送文件实现
    private static void sendFile(String targetIP, String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("文件不存在: " + filePath);
            return;
        }

        try (Socket socket = new Socket(targetIP, PORT);
             DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
             FileInputStream fis = new FileInputStream(file)) {
            
            // 发送文件信息
            dos.writeUTF(file.getName());
            dos.writeLong(file.length());
            
            // 发送文件内容
            byte[] buffer = new byte[BUFFER_SIZE];
            int read;
            while ((read = fis.read(buffer)) != -1) {
                dos.write(buffer, 0, read);
            }
            
            System.out.println("文件发送成功: " + file.getName() + " 到 " + targetIP);
        } catch (IOException e) {
            System.err.println("文件发送失败: " + e.getMessage());
        }
    }
}
