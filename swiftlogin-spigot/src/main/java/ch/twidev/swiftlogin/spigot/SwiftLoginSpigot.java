/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.spigot;

import ch.twidev.swiftlogin.api.SwiftLogin;
import ch.twidev.swiftlogin.api.authorization.AuthorizationProvider;
import ch.twidev.swiftlogin.api.crypto.CryptoProvider;
import ch.twidev.swiftlogin.api.event.SwiftEventProvider;
import ch.twidev.swiftlogin.api.exception.ServerSideNotSupportedException;
import ch.twidev.swiftlogin.api.servers.ServerSide;
import ch.twidev.swiftlogin.api.utils.Nullable;
import ch.twidev.swiftlogin.common.*;
import ch.twidev.swiftlogin.common.configuration.Configuration;
import ch.twidev.swiftlogin.common.configuration.ConfigurationFiles;
import ch.twidev.swiftlogin.common.configuration.ConfigurationHandler;
import ch.twidev.swiftlogin.common.configuration.schema.BackendConfiguration;
import ch.twidev.swiftlogin.common.database.DriverConfig;
import ch.twidev.swiftlogin.common.database.DriverType;
import ch.twidev.swiftlogin.common.exception.PluginConfigurationException;
import ch.twidev.swiftlogin.common.servers.ServerType;
import ch.twidev.swiftlogin.common.util.Empty;
import ch.twidev.swiftlogin.spigot.handshake.Handshaker;
import ch.twidev.swiftlogin.spigot.handshake.listener.HandshakeListener;
import ch.twidev.swiftlogin.spigot.handshake.protocol.HandshakeAdapter;
import ch.twidev.swiftlogin.spigot.listeners.ActionListener;
import ch.twidev.swiftlogin.spigot.listeners.JoinListener;
import ch.twidev.swiftlogin.spigot.providers.SpigotBridgeEventsProvider;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.UUID;

public class SwiftLoginSpigot extends JavaPlugin implements SwiftLoginPlugin<Player, Empty> {


    private static final HashMap<UUID, RegistrationUser> playerRegistrations = new HashMap<>();

    private static final String DATA_SEPARATOR = "\u0000";
    private static final String PROTOCOL_LIB = "ProtocolLib";

    private static SwiftLoginSpigot instance;

    private SwiftLogger swiftLogger;

    private SwiftLoginImplementation<Player, Empty> swiftLoginImplementation;

    private SpigotBridgeEventsProvider spigotBridgeEventsProvider;

    private SpigotPlatformHandler spigotPlatformHandler;

    private Configuration backendConfiguration;

    @Override
    public void onEnable() {
        instance = this;

        PluginManager pluginManager = this.getServer().getPluginManager();

        this.swiftLogger = new SwiftLogger(super.getLogger());
        this.swiftLogger.sendEnableMessage();

        try {
            Class.forName("com.destroystokyo.paper.event.player.PlayerHandshakeEvent");

            HandshakeListener.init(this);
        } catch (ClassNotFoundException e) {
            if (pluginManager.isPluginEnabled(PROTOCOL_LIB)) {
                HandshakeAdapter.init(this);
            } else {
                this.getLogger().severe("It looks like your server doesn't support Handshake's packet listener. This is not a plugin error but a bad installation. The server will shut down");
                this.getLogger().severe("To resolve this problem, install the protocol lib plugin available on spigot at https://www.spigotmc.org/resources/1997/.");
                this.getLogger().severe("Or use a version of paperspigot higher than version 1.9.4");
                this.getLogger().severe("This is not an error from the owner of the plugin if you have a question or a problem to resolve ask it in the discussion thread on spigot");
                this.setEnabled(false);
            }
        }

        final ConfigurationHandler configs = this.initConfiguration();

        try {
            this.backendConfiguration = configs.get(ConfigurationFiles.BACKEND_CONFIG);

            File imageFile = new File(
                    this.getDataFolder(), "logo.png"
            );

            if (!imageFile.exists()) {
                try {
                    boolean wasCreated = imageFile.createNewFile();

                    if (wasCreated) {
                        InputStream in = new BufferedInputStream(
                                this.getResourceFile("logo.png"));
                        OutputStream out = new BufferedOutputStream(
                                Files.newOutputStream(imageFile.toPath()));

                        byte[] buffer = new byte[1024];
                        int lengthRead;
                        while ((lengthRead = in.read(buffer)) > 0) {
                            out.write(buffer, 0, lengthRead);
                            out.flush();
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            this.spigotPlatformHandler = new SpigotPlatformHandler(this);

            try {
                this.spigotBridgeEventsProvider = new SpigotBridgeEventsProvider(this, null);
            } catch (UnsupportedOperationException e) {
                this.spigotBridgeEventsProvider = null;
            }

            this.swiftLoginImplementation = new SwiftLoginImplementation<Player, Empty>(this, configs, ServerType.BACKEND, this.getSwiftLogger(),
                    new DriverConfig(
                            DriverType.MYSQL,
                            BackendConfiguration.getMysqlHost(),
                            BackendConfiguration.getMysqlDatabase(),
                            BackendConfiguration.getMysqlPort(),
                            BackendConfiguration.getMysqlUser(),
                            BackendConfiguration.getMysqlPassword())) {

                @Override
                public CryptoProvider getCurrentCryptoProvider() {
                    return null;
                }

                @Override
                public @Nullable SwiftEventProvider<Player> getEventProvider() {
                    return spigotBridgeEventsProvider;
                }

                @Override
                public PlatformHandler<Player> getPlatformHandler() {
                    return spigotPlatformHandler;
                }

                @Override
                public AuthorizationProvider<Player> getAuthorizationProvider() {
                    throw new UnsupportedOperationException();
                }

                @Override
                public ServerPlatformHandler<Player, Empty> getServerPlatformHandler() {
                    throw new UnsupportedOperationException();
                }
            };

            if (!this.isEnabled()) return;

            pluginManager.registerEvents(new ActionListener(this), this);
            pluginManager.registerEvents(new JoinListener(this), this);

            this.getServer().getMessenger().registerIncomingPluginChannel(this, SwiftLoginImplementation.CHANNEL_MESSAGE, new PluginMessageListener() {
                @Override
                public void onPluginMessageReceived(String s, Player handler, byte[] bytes) {

                    DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(bytes));
                    try {
                        UUID playerUuid = UUID.fromString(
                                dataInputStream.readUTF());

                        String playerName = dataInputStream.readUTF();

                        Player player = Bukkit.getPlayer(playerUuid);
                        if (player != null && !player.isOnline())
                            player = null;

                        String token = dataInputStream.readUTF();
                        if (token.equals(BackendConfiguration.getToken())) {
                            if (!SwiftLoginSpigot.getPlayerRegistrations().containsKey(playerUuid)) {
                                getLogger().warning("Cannot read plugin channel message from " + playerName + " an error appeared while decoding the message.");
                                if (player != null)
                                    player.kickPlayer("An error appeared try joining the server again. If the problem persists, please contact the staff");
                                return;
                            }

                            RegistrationUser registrationState = SwiftLoginSpigot.getPlayerRegistrations().get(playerUuid);

                            registrationState.setRegistrationState(
                                    RegistrationState.valueOf(dataInputStream.readUTF())
                            );
                            String c = dataInputStream.readUTF();
                            if (c.equals("unknown")) {
                                registrationState.setCaptcha(null);
                            } else {
                                registrationState.setCaptcha(c);
                            }

                            registrationState.setLogged(player != null);

                            if (registrationState.isLogged())
                                RegistrationState.checkState(registrationState, registrationState.getRegistrationState(), player);

                        } else {
                            getLogger().warning("Impossible to accept the player " + playerName + "'s packet because the tokens do not match. are you sure you put the same tokens in the bungeecord and spigot configuration files?");
                            if (player != null)
                                player.kickPlayer("An error appeared try joining the server again. If the problem persists, please contact the staff");
                        }
                    } catch (IOException | IllegalArgumentException e) {
                        getLogger().warning("Cannot read plugin channel message from a player connection an error appeared while decoding the message. (3)");
                        throw new RuntimeException(e);
                    }
                }
            });
        } catch (PluginConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public Configuration getBackendConfiguration() {
        return backendConfiguration;
    }

    public static HashMap<UUID, RegistrationUser> getPlayerRegistrations() {
        return playerRegistrations;
    }

    public static SwiftLoginSpigot getInstance() {
        return instance;
    }

    public static Handshaker parseHandshake(String s) {
        if(s.isEmpty()) {
            getInstance().getLogger().severe("Cannot parse Handshake packet because the packet is empty");
            return null;
        }

        if(s.length() >= 9000) {
            getInstance().getLogger().severe("Cannot parse Handshake packet because is too large " + s.length() + " maximum chars is 9000.");
            return null;
        }

        try {
            String[] args = s.split(DATA_SEPARATOR);
            if(args.length >= 3) {
                String token = args[0];
                if(token.equals(BackendConfiguration.getToken())) {
                    return new Handshaker(
                            parseUUID(args[2]),args[1], args.length > 3 ? args[3] : "[]");
                }else{
                    getInstance().getLogger().severe("Cannot parse Handshake packet because token is incorrect");
                    return null;
                }
            }else{
                return null;
            }
        } catch (Exception e) {
            getInstance().getLogger().severe("Cannot parse Handshake packet : " + Base64.getEncoder().encodeToString(s.getBytes(StandardCharsets.UTF_8)));
            return null;
        }
    }


    public static UUID parseUUID(String uuid) {
        return uuid != null && !uuid.isEmpty() ? UUID.fromString(uuid.replaceAll("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5")) : null;
    }

    @Override
    public void onDisable() {
        if(this.swiftLoginImplementation != null) {
            this.swiftLoginImplementation.onStop();
        }

        this.getServer().shutdown();
    }

    @Override
    public SwiftLogger getSwiftLogger() {
        return swiftLogger;
    }

    @Override
    public ServerType getServerType() {
        return ServerType.BACKEND;
    }

    @Override
    public SwiftLoginImplementation<Player, Empty> getImplementation() {
        return swiftLoginImplementation;
    }

    @Override
    public InputStream getResourceFile(String filename) {
        return super.getResource(filename);
    }

    @Override
    public String translateAlternateColorCodes(char altColorChar, String textToTranslate) {
        return ChatColor.translateAlternateColorCodes(altColorChar, textToTranslate);
    }

    @Override
    public void disable() {
        this.getSwiftLogger().info("An error occurred during the installation of SwiftLogin, plugin will disable.");

        this.setEnabled(false);
    }

    @Override
    public void reloadConfiguration() throws PluginConfigurationException {

    }
}
