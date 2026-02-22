import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class IRCServer {
    private static final int PORT = 6667;
    private static final List<PrintWriter> clients = Collections.synchronizedList(new ArrayList<>());
    private static final Map<PrintWriter, String> clientUsers = Collections.synchronizedMap(new HashMap<>());

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("IRC Server running on port " + PORT);
            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(() -> handleClient(socket)).start();
            }
        } catch (IOException e) {
            System.err.println("服务器启动失败: " + e.getMessage());
        }
    }

    private static void handleClient(Socket socket) {
        PrintWriter out = null;
        try {
            // 使用UTF-8编码解决中文乱码
            BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8)
            );
            
            out = new PrintWriter(
                new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8),
                true
            );
            
            // 添加到客户端列表
            synchronized (clients) {
                clients.add(out);
            }
            
            String input;
            while ((input = in.readLine()) != null) {
                // 解析消息: UUID|用户名|内容
                String[] parts = input.split("\\|", 3);
                if (parts.length == 3) {
                    // 存储用户信息
                    clientUsers.put(out, parts[1] + "|" + parts[0]);
                    
                    // 广播原始数据（不添加格式代码）
                    // 排除发送者避免重复显示
                    broadcast(parts[0] + "|" + parts[1] + "|" + parts[2], out);
                }
            }
        } catch (IOException e) {
            System.out.println("客户端断开连接: " + e.getMessage());
        } finally {
            if (out != null) {
                synchronized (clients) {
                    clients.remove(out);
                    clientUsers.remove(out);
                }
            }
            try {
                socket.close();
            } catch (IOException e) {
                // 忽略关闭错误
            }
        }
    }

    // 修改广播方法，排除发送者
    private static void broadcast(String message, PrintWriter exclude) {
        synchronized (clients) {
            List<PrintWriter> toRemove = new ArrayList<>();
            
            for (PrintWriter client : clients) {
                // 排除消息发送者
                if (client == exclude) continue;
                
                try {
                    client.println(message);
                } catch (Exception e) {
                    // 标记断开连接
                    toRemove.add(client);
                }
            }
            
            // 清理断开连接的客户端
            clients.removeAll(toRemove);
            toRemove.forEach(clientUsers::remove);
        }
    }
}