import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;

public class ChatClient extends JFrame {
    private JTextField inputField;
    private JTextArea messageArea;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public ChatClient(String serverAddress, int port) {
        setTitle("Chat Client");
        setSize(600, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initializeComponents();
        connectToServer(serverAddress, port);
        setVisible(true);
    }

    private void initializeComponents() {
        inputField = new JTextField(30);
        JButton sendButton = new JButton("Send");
        messageArea = new JTextArea();
        messageArea.setEditable(false);
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                	String message = inputField.getText();
                	out.println(message);
                	inputField.setText("");
                	messageArea.append("Me: " + message + "\n");
            }
        });
		
        JPanel panel = new JPanel();
        panel.add(inputField);
        panel.add(sendButton);
        add(panel, BorderLayout.SOUTH);
        add(new JScrollPane(messageArea), BorderLayout.CENTER);
    }

    private void connectToServer(String serverAddress, int port) {
        try {
            socket = new Socket(serverAddress, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String message;
                        while ((message = in.readLine()) != null) {
							if ("please input your nickname :".equals(message)) {
							   // 弹出输入昵称的对话框
							   String nickname = JOptionPane.showInputDialog(ChatClient.this, message);
							   out.println(nickname);
								} else {
								   messageArea.append(message + "\n");
							}
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ChatClient("127.0.0.1", 1234);
            }
        });
    }
}