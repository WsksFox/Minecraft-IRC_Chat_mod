# Minecraft IRC Mod
This is an IRC chat mod and its corresponding server for the Forge version 1.20.1 of Minecraft.
## Feature
- **Bidirectional message bridge**:
    - Inputting .i <message> within the game will send the message to the IRC server.
    - Messages from IRC will be displayed in color in the chat bar.
- **Full UTF-8 support for Chinese characters**:
    - Use UTF-8 encoding when communicating with IRC to avoid issues with unreadable characters.
- **Intelligent connection management**:
    - The player automatically connects to the IRC server when logging in.
    - Automatic retry after connection failure (default: reconnect after 10 seconds).
    - Automatically clean up resources when the connection is disconnected.
- **Color message display**:
    - [IRC] Prefix: Cyan
    - Player UUID: Green
    - Player Name: Yellow
    - Message content: white
- **Thread safe**:
    - Use AtomicBoolean to ensure the correctness of connection status under multithreading, and all UI operations are executed on the main thread of Minecraft.

## How to configure?
**The mod currently uses hard-coded configurations, and you need to modify the constants in the source code to match your IRC server settings.**
### 1. Modify the source code (recommended)
- Open the `Irc.java` file.
- Locate the following constants at the top of the class and modify them according to your environment:
```java
private static final String SERVER_IP = "127.0.0.1";      // IRC Server IP
private static final int SERVER_PORT = 6667;              // IRC Server Port
private static final long COOLDOWN_MS = 5000;             // Send cooling (milliseconds)
private static final int RECONNECT_DELAY = 10000;         // Reconnection delay (milliseconds)
```

- Recompile the mod.

### 2. Custom configuration file (optional)
- If you wish to adjust configurations without modifying the source code, you can extend the functionality yourself, such as adding a configuration file (e.g., `.toml` or `.json`) that is read during module initialization. The current version does not include this feature, but you can implement it yourself as needed.

### Instructions for use
- 1.Ensure that the corresponding version of Minecraft Forge has been installed.
- 2.Place the compiled mod into the `mods` folder.
- 3.Start the game and enter the world or server.
- 4.Type `.i Hello, IRC!` in the chat bar, and the message will be sent to the IRC server.
- 6.When there are new messages in the IRC, they will be automatically displayed within the game.

## How to build?
### Client
```bash
# Clone the project
git clone https://github.com/WsksFox/Minecraft-IRC_Chat_mod.git
cd Minecraft-IRC_Chat_mod

# Build the project
./gradlew build
```

### Server
```bash
cd Server-src
javac -d bin IRCServer.java
jar cfm Server.jar Manifest.txt -C bin/ .
```

## License
This project is open-sourced under the [Apache License 2.0](https://github.com/WsksFox/Minecraft-IRC_Chat_mod/blob/master/LICENSE).