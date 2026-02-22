<<<<<<< HEAD
# Minecraft IRC Mod
Features
Real-Time Cross-Client Chat: Enables bidirectional communication between Minecraft players via a custom IRC server.
UTF-8 Encoding Support: Fixes Chinese character garbling issues for seamless multilingual chat.
Color-Coded Messages: Distinct color styling for IRC tags, UUIDs, usernames, and message content for improved readability.
Automatic Reconnection: Automatically retries connecting to the IRC server (10-second delay) if the connection is lost.
Anti-Spam Cooldown: 5-second message cooldown to prevent excessive spam in the IRC chat.
Connection Lifecycle Management: Handles player login/logout events to manage IRC connections gracefully.
Background Message Handling: Uses daemon threads for asynchronous message reception without blocking the game thread.
Clean Disconnection: Properly closes connections when the player logs out of Minecraft.
Building Instructions
Client (Minecraft Forge Mod)
Build the client mod using the standard Minecraft Forge Gradle workflow in IntelliJ IDEA:
Prerequisites
IntelliJ IDEA (2020.3+ recommended)
JDK 17 (required for modern Minecraft Forge versions)
Gradle (compatible with your Forge version; included in the project by default)
Build Steps
Clone this repository to your local machine.
Open IntelliJ IDEA and import the project (select the root directory of the repository).
Wait for IntelliJ to sync the Gradle project and download all dependencies.
Build the mod JAR:
Open the Gradle tool window (View > Tool Windows > Gradle).
Navigate to Tasks > build > build.
Run the build task. The compiled JAR will be generated in build/libs/.
Server (Standalone Java Application)
The IRC server is a single Java file located in the Server-src directory and requires manual compilation/execution:
Prerequisites
JDK 8+ (compatible with the server code)
Terminal/command line access
Compilation & Execution
Navigate to the Server-src directory:
cd ./Server-src

Compile the IRCServer.java file with UTF-8 encoding:

javac -encoding UTF-8 IRCServer.java

Run the compiled server:

java IRCServer

The server will start on port 6667 (default) and listen for incoming client connections.
Usage
1. Start the IRC Server
Run the compiled IRC server first (follow the server build steps above).
The default server address is 127.0.0.1:6667 (modify SERVER_IP/SERVER_PORT in Irc.java (client) or PORT in IRCServer.java (server) to use a different address/port).
2. Install & Run the Client Mod
Place the compiled client mod JAR into your Minecraft Forge mod folder (typically .minecraft/mods).
Launch Minecraft with the corresponding Forge version.
The mod will auto-connect to the IRC server 10 seconds after game initialization (or when the player logs in).
3. Send IRC Messages
In Minecraft chat, prefix your message with .i (e.g., .i Hello from Minecraft!) to send it to the IRC server.
Incoming messages from other clients will appear in the game chat with color coding.
A 5-second cooldown applies to message sending (you’ll see a warning if you send messages too quickly).
Notes
Ensure the IRC server is running before launching the Minecraft client to avoid connection errors.
The mod uses the Minecraft Forge event bus to handle chat, player login/logout, and tick events.
Connection status and errors are displayed as system messages in the Minecraft chat.
License
This project is unlicensed (modify as needed for your use case, e.g., MIT, GPL).# Minecraft IRC ModA Minecraft Forge mod that implements real-time IRC (Internet Relay Chat) communication between Minecraft clients and a custom standalone IRC server, enabling cross-client chat with robust connection management and encoding support.
Features
Real-Time Cross-Client Chat: Enables bidirectional communication between Minecraft players via a custom IRC server.
UTF-8 Encoding Support: Fixes Chinese character garbling issues for seamless multilingual chat.
Color-Coded Messages: Distinct color styling for IRC tags, UUIDs, usernames, and message content for improved readability.
Automatic Reconnection: Automatically retries connecting to the IRC server (10-second delay) if the connection is lost.
Anti-Spam Cooldown: 5-second message cooldown to prevent excessive spam in the IRC chat.
Connection Lifecycle Management: Handles player login/logout events to manage IRC connections gracefully.
Background Message Handling: Uses daemon threads for asynchronous message reception without blocking the game thread.
Clean Disconnection: Properly closes connections when the player logs out of Minecraft.
Building Instructions
Client (Minecraft Forge Mod)
Build the client mod using the standard Minecraft Forge Gradle workflow in IntelliJ IDEA:
Prerequisites
IntelliJ IDEA (2020.3+ recommended)
JDK 17 (required for modern Minecraft Forge versions)
Gradle (compatible with your Forge version; included in the project by default)
Build Steps
Clone this repository to your local machine.
Open IntelliJ IDEA and import the project (select the root directory of the repository).
Wait for IntelliJ to sync the Gradle project and download all dependencies.
Build the mod JAR:
Open the Gradle tool window (View > Tool Windows > Gradle).
Navigate to Tasks > build > build.
Run the build task. The compiled JAR will be generated in build/libs/.
Server (Standalone Java Application)
The IRC server is a single Java file located in the Server-src directory and requires manual compilation/execution:
Prerequisites
JDK 8+ (compatible with the server code)
Terminal/command line access
Compilation & Execution
Navigate to the Server-src directory:

cd /path/to/your/project/Server-src
Compile the IRCServer.java file with UTF-8 encoding:

javac -encoding UTF-8 IRCServer.java

Run the compiled server:

java IRCServer

The server will start on port 6667 (default) and listen for incoming client connections.
Usage
1. Start the IRC Server
Run the compiled IRC server first (follow the server build steps above).
The default server address is 127.0.0.1:6667 (modify SERVER_IP/SERVER_PORT in Irc.java (client) or PORT in IRCServer.java (server) to use a different address/port).
2. Install & Run the Client Mod
Place the compiled client mod JAR into your Minecraft Forge mod folder (typically .minecraft/mods).
Launch Minecraft with the corresponding Forge version.
The mod will auto-connect to the IRC server 10 seconds after game initialization (or when the player logs in).
3. Send IRC Messages
In Minecraft chat, prefix your message with .i (e.g., .i Hello from Minecraft!) to send it to the IRC server.
Incoming messages from other clients will appear in the game chat with color coding.
A 5-second cooldown applies to message sending (you’ll see a warning if you send messages too quickly).
Notes
Ensure the IRC server is running before launching the Minecraft client to avoid connection errors.
The mod uses the Minecraft Forge event bus to handle chat, player login/logout, and tick events.
Connection status and errors are displayed as system messages in the Minecraft chat.
=======
# Minecraft-IRC_Chat_mod
An IRC chat mod for Minecraft Forge 1.20.1
>>>>>>> a7d9ce8ef20fa0d0fc1b7d993ea5bbf2a4a9cbee
