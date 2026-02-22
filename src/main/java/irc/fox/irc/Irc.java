package irc.fox.irc;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

@Mod("irc")
public class Irc {
    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 6667;
    private static final long COOLDOWN_MS = 5000;
    private static final int RECONNECT_DELAY = 10000;

    private static Socket socket;
    private static PrintWriter out;
    private static BufferedReader in;
    private static Thread receiverThread;
    private static long lastMessageTime = 0;
    private static final AtomicBoolean isConnected = new AtomicBoolean(false);
    private static final AtomicBoolean connectionInProgress = new AtomicBoolean(false);

    public Irc() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        new Thread(() -> {
            try {
                Thread.sleep(10000);
                connectToServer();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    @SubscribeEvent
    public void onPlayerLogin(ClientPlayerNetworkEvent.LoggingIn event) {
        if (!isConnected.get() && !connectionInProgress.get()) {
            connectToServer();
        }
    }

    private void connectToServer() {
        if (connectionInProgress.get()) return;

        connectionInProgress.set(true);
        try {
            closeConnection();

            socket = new Socket(SERVER_IP, SERVER_PORT);

            out = new PrintWriter(
                    new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8),
                    true
            );

            in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8)
            );

            receiverThread = new Thread(() -> {
                try {
                    String message;
                    while ((message = in.readLine()) != null) {
                        handleServerMessage(message);
                    }
                } catch (IOException e) {
                    System.err.println("IRC Reception error: " + e.getMessage());
                } finally {
                    closeConnection();
                    isConnected.set(false);
                    connectionInProgress.set(false);
                }
            });

            receiverThread.setDaemon(true);
            receiverThread.start();
            isConnected.set(true);

            notifyPlayer(Component.literal("§aSuccessfully connected to the IRC server!"));
        } catch (IOException e) {
            System.err.println("Unable to connect to the IRC server: " + e.getMessage());
            notifyConnectionFailure();
            isConnected.set(false);
        } finally {
            connectionInProgress.set(false);
        }
    }

    private void handleServerMessage(String rawMessage) {
        Minecraft.getInstance().execute(() -> {
            Player player = Minecraft.getInstance().player;
            if (player == null) return;

            String[] parts = rawMessage.split("\\|", 3);
            if (parts.length != 3) return;

            String uuid = parts[0];
            String username = parts[1];
            String content = parts[2];

            MutableComponent messageComponent = Component.literal("[IRC]")
                    .withStyle(Style.EMPTY.withColor(TextColor.parseColor("#00FFFF"))) // 青色
                    .append(Component.literal("{" + uuid + "}")
                            .withStyle(Style.EMPTY.withColor(TextColor.parseColor("#00FF00")))) // 绿色
                    .append(Component.literal("<" + username + ">: ")
                            .withStyle(Style.EMPTY.withColor(TextColor.parseColor("#FFFF00")))) // 黄色
                    .append(Component.literal(content)
                            .withStyle(Style.EMPTY.withColor(TextColor.parseColor("#FFFFFF")))); // 白色

            player.sendSystemMessage(messageComponent);
        });
    }

    private void closeConnection() {
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
        }
        out = null;
        in = null;
        socket = null;
    }

    private void notifyConnectionFailure() {
        notifyPlayer(Component.literal("§cUnable to connect to the IRC server!"));
        notifyPlayer(Component.literal("§eAn attempt to reconnect will be made in " + (RECONNECT_DELAY / 1000) + " seconds..."));

        new Thread(() -> {
            try {
                Thread.sleep(RECONNECT_DELAY);
                connectToServer();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    private void notifyPlayer(Component message) {
        Minecraft.getInstance().execute(() -> {
            Player player = Minecraft.getInstance().player;
            if (player != null) {
                player.sendSystemMessage(message);
            }
        });
    }

    @SubscribeEvent
    public void onClientChat(ClientChatEvent event) {
        String message = event.getMessage();
        if (message.startsWith(".i ")) {
            event.setCanceled(true);
            Player player = Minecraft.getInstance().player;
            if (player == null) return;

            if (!isConnected.get()) {
                notifyPlayer(Component.literal("§cNot connected to the IRC server! Attempting to connect..."));
                connectToServer();
                return;
            }

            long currentTime = System.currentTimeMillis();
            if (currentTime - lastMessageTime < COOLDOWN_MS) {
                notifyPlayer(Component.literal("§cYou speak too fast.！"));
                return;
            }
            lastMessageTime = currentTime;

            String content = message.substring(3);
            UUID uuid = player.getUUID();
            String username = player.getName().getString();

            MutableComponent localMessage = Component.literal("[IRC]")
                    .withStyle(Style.EMPTY.withColor(TextColor.parseColor("#00FFFF")))
                    .append(Component.literal("{" + uuid + "}")
                            .withStyle(Style.EMPTY.withColor(TextColor.parseColor("#00FF00"))))
                    .append(Component.literal("<" + username + ">: ")
                            .withStyle(Style.EMPTY.withColor(TextColor.parseColor("#FFFF00"))))
                    .append(Component.literal(content)
                            .withStyle(Style.EMPTY.withColor(TextColor.parseColor("#FFFFFF"))));

            player.sendSystemMessage(localMessage);

            if (out != null) {
                out.println(uuid + "|" + username + "|" + content);
            } else {
                notifyPlayer(Component.literal("§cIRCConnection has been disconnected, trying to reconnect..."));
                connectToServer();
            }
        }
    }

    @SubscribeEvent
    public void onClientDisconnect(ClientPlayerNetworkEvent.LoggingOut event) {
        closeConnection();
        isConnected.set(false);
    }
}