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
            System.err.println("Server startup failed: " + e.getMessage());
        }
    }

    private static void handleClient(Socket socket) {
        PrintWriter out = null;
        try {
            BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8)
            );
            
            out = new PrintWriter(
                new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8),
                true
            );

            synchronized (clients) {
                clients.add(out);
            }
            
            String input;
            while ((input = in.readLine()) != null) {
                String[] parts = input.split("\\|", 3);
                if (parts.length == 3) {
                    clientUsers.put(out, parts[1] + "|" + parts[0]);
                    broadcast(parts[0] + "|" + parts[1] + "|" + parts[2], out);
                }
            }
        } catch (IOException e) {
            System.out.println("Client disconnected: " + e.getMessage());
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
            }
        }
    }

    private static void broadcast(String message, PrintWriter exclude) {
        synchronized (clients) {
            List<PrintWriter> toRemove = new ArrayList<>();
            
            for (PrintWriter client : clients) {

                if (client == exclude) continue;
                
                try {
                    client.println(message);
                } catch (Exception e) {
                    toRemove.add(client);
                }
            }

            clients.removeAll(toRemove);
            toRemove.forEach(clientUsers::remove);
        }
    }
}