/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.bungee;

import ch.twidev.swiftlogin.api.players.SwiftPlayer;
import ch.twidev.swiftlogin.api.servers.BackendType;
import ch.twidev.swiftlogin.api.servers.ServerState;
import ch.twidev.swiftlogin.api.servers.SwiftServer;
import ch.twidev.swiftlogin.common.ServerPlatformHandler;
import ch.twidev.swiftlogin.common.SwiftLogger;
import ch.twidev.swiftlogin.common.SwiftLoginImplementation;
import ch.twidev.swiftlogin.common.configuration.schema.MainConfiguration;
import ch.twidev.swiftlogin.common.exception.PluginIssues;
import ch.twidev.swiftlogin.common.player.Profile;
import ch.twidev.swiftlogin.common.servers.ServerPing;
import ch.twidev.swiftlogin.common.util.ThrowableCallback;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.Random;

public class BungeecordServerPlatformHandler implements ServerPlatformHandler<ProxiedPlayer, ServerInfo> {

    private final SwiftLoginBungee swiftLoginBungee;
    private final SwiftLogger swiftLogger;

    public BungeecordServerPlatformHandler(SwiftLoginBungee swiftLoginBungee) {
        this.swiftLoginBungee = swiftLoginBungee;
        this.swiftLogger = swiftLoginBungee.getSwiftLogger();
    }

    @Override
    public void sendPlayerTo(ProxiedPlayer player, BackendType backendType) {
        Optional<SwiftServer> optionalSwiftServer = swiftLoginBungee.getImplementation().getServerManager().getAvailableServers(backendType);
        if(optionalSwiftServer.isEmpty() || !isServerExist(optionalSwiftServer.get().getServerName())) {
            swiftLogger.sendRawPluginError(
                    PluginIssues.NO_SERVERS_FOUND,
                    "Impossible to move the player %s to a server of type %s because no available servers were found",
                    player.getName(), backendType.toString());

            return;
        }

        ServerInfo serverInfo = this.getServer(optionalSwiftServer.get().getServerName());
        connectToServer(player, serverInfo);
    }

    @Override
    public boolean sendPlayerTo(ProxiedPlayer player, SwiftServer swiftServer) {
        return this.sendPlayerTo(player, swiftServer.getServerName());
    }

    @Override
    public boolean sendPlayerTo(ProxiedPlayer player, String serverName) {
        ServerInfo serverInfo = this.getServer(serverName);

        if(isServerExist(serverName)) {
            connectToServer(player, serverInfo);
            return true;
        }else {
            swiftLogger.sendRawPluginError(
                    PluginIssues.NO_SERVERS_FOUND,
                    "Cannot move player %s to %s because the server does not exist on this proxy",
                    player.getName(), serverName);

            return false;
        }
    }

    @Override
    public boolean isServerExist(String serverName) {
        return this.getServer(serverName) != null;
    }

    @Override
    public void pingServer(String serverName, ThrowableCallback<ServerPing> callback) {
        ServerInfo serverInfo = this.getServer(serverName);

        if(isServerExist(serverName)) {
            serverInfo.ping((serverPing, throwable) -> {
                if(throwable != null) {
                    callback.done(
                            new ServerPing(ServerState.PINGING, 0, -1), null
                    );
                }else {
                    int maxPlayers = serverPing.getPlayers().getMax();

                    callback.done(
                            new ServerPing(ServerState.ONLINE, serverPing.getPlayers().getOnline(),  maxPlayers == -1 ? Integer.MAX_VALUE : maxPlayers),null
                    );

                    return;
                }
            });
        }else{
            callback.done(new ServerPing(ServerState.OFFLINE, 0, -1), null);
        }
    }

    @Override
    public void connectToServer(ProxiedPlayer player, ServerInfo server) {
        swiftLoginBungee.getImplementation().getProfileManager().getCachedProfileByName(player.getName()).ifPresentOrElse(swiftPlayer -> {
            try {
                player.connect(server);

                this.sendConnectionToServer(player, swiftPlayer, server);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, () -> {
            swiftLogger.info(String.format("Cannot send %s player to %s server because profile instance is null please check your database configuration", player.getName(), server.getName()));
        });

    }

    @Override
    public void sendConnectionToServer(ProxiedPlayer player, SwiftPlayer swiftPlayer, ServerInfo serverInfo) throws Exception{
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        Profile profile = (Profile) swiftPlayer;
        if(MainConfiguration.getVerifyCaptchaCode()) {
            profile.setCachedCaptcha(getCaptcha());
        }

        try {
            dataOutputStream.writeUTF(swiftPlayer.getUniqueId().toString());
            dataOutputStream.writeUTF(swiftPlayer.getRecentName());
            dataOutputStream.writeUTF(MainConfiguration.getToken());
            String type = swiftPlayer.isLogged() ? "AUTHORIZED" : (swiftPlayer.isRegistered() ? "LOGIN" : "REGISTER");

            dataOutputStream.writeUTF(type);
            if(profile.getCachedCaptcha() != null) {
                dataOutputStream.writeUTF(profile.getCachedCaptcha());
            }else{
                dataOutputStream.writeUTF("unknown");
            }
            dataOutputStream.writeUTF("");
            serverInfo.sendData(SwiftLoginImplementation.CHANNEL_MESSAGE, byteArrayOutputStream.toByteArray());
        } catch (IOException e) {
            player.disconnect(new TextComponent("An error occurred! cannot send your data to the server!"));
            throw e;
        }


    }


    public static String getCaptcha() {
        Random rd = new Random(System.currentTimeMillis());
        StringBuilder strBuilder = new StringBuilder();
        String tmp = new String("123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
        for (int i = 0; i < 5; i++) {
            strBuilder.append(tmp.charAt(rd.nextInt(tmp.length())));
        }
        return strBuilder.toString();
    }

    @Override
    public void sendInitialisationServerPacket(SwiftServer swiftServer) throws Exception {
        ServerInfo serverInfo = this.getServer(swiftServer.getServerName());

        if(serverInfo != null) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
            dataOutputStream.writeUTF(MainConfiguration.getToken());
            dataOutputStream.writeUTF(swiftServer.getServerName());

            //serverInfo.sendData(SwiftLoginImplementation.CHANNEL_MESSAGE, byteArrayOutputStream.toByteArray());
        }
    }

    @Override
    public ServerInfo getServer(String serverName) {
        return ProxyServer.getInstance().getServerInfo(serverName);
    }

}
