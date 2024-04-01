/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.bungee.listeners;

import ch.twidev.swiftlogin.api.event.events.PlayerJoinLimboServerEvent;
import ch.twidev.swiftlogin.api.event.events.PlayerJoinMainServerEvent;
import ch.twidev.swiftlogin.api.players.SwiftPlayer;
import ch.twidev.swiftlogin.api.servers.BackendType;
import ch.twidev.swiftlogin.api.servers.ServerState;
import ch.twidev.swiftlogin.api.servers.SwiftServer;
import ch.twidev.swiftlogin.bungee.SwiftLoginBungee;
import ch.twidev.swiftlogin.common.ServerPlatformHandler;
import ch.twidev.swiftlogin.common.configuration.schema.MainConfiguration;
import ch.twidev.swiftlogin.common.configuration.schema.TranslationConfiguration;
import ch.twidev.swiftlogin.common.exception.PluginIssues;
import ch.twidev.swiftlogin.common.player.Profile;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class ServersListener implements Listener {

    private final SwiftLoginBungee bungee;

    public ServersListener(SwiftLoginBungee swiftLoginBungee) {
        this.bungee = swiftLoginBungee;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onConnect(ServerConnectedEvent event) {
        ProxiedPlayer player = event.getPlayer();
        SwiftPlayer swiftPlayer = bungee.getImplementation().getProfileManager().getCachedProfileByName(player.getName()).orElse(null);

        if(swiftPlayer == null) {
            player.disconnect(new TextComponent("An error occurred try joining the server again. If the problem persists, please contact the staff (Error code : SwiftLogin-2)"));
            return;
        }

        // Send plugin message

        try {
            bungee.getImplementation().getServerPlatformHandler().sendConnectionToServer(player, swiftPlayer, event.getServer().getInfo());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void requestConnection(ServerConnectEvent event) {
        ProxiedPlayer proxiedPlayer = event.getPlayer();

        Profile swiftPlayer = bungee.getImplementation().getProfileManager().getCachedLegacyProfileByName(proxiedPlayer.getName()).orElse(null);

        if(swiftPlayer == null) {
            proxiedPlayer.disconnect(new TextComponent("An error occurred try joining the server again. If the problem persists, please contact the staff (Error code : SwiftLogin-3)"));
            return;
        }

        ServerPlatformHandler<ProxiedPlayer, ServerInfo> platformHandler = bungee.getImplementation().getServerPlatformHandler();

        if(event.getReason() != ServerConnectEvent.Reason.UNKNOWN && event.getReason() != ServerConnectEvent.Reason.JOIN_PROXY && event.getReason() != ServerConnectEvent.Reason.LOBBY_FALLBACK) {
            if(!swiftPlayer.isLogged() && !MainConfiguration.getLimboServers().contains(event.getTarget().getName())) {
                event.setCancelled(true);
            }
        }else{
            if(swiftPlayer.isLogged()) {
                SwiftServer swiftServer = bungee.getImplementation().getServerManager().getAvailableServers(BackendType.MAIN).orElse(null);

                PlayerJoinMainServerEvent<ProxiedPlayer> serverEvent = new PlayerJoinMainServerEvent<>(swiftPlayer, proxiedPlayer, swiftServer);

                bungee.getImplementation().getEventProvider().callEvent(serverEvent, false);

                if(serverEvent.isCancelled()) {
                    event.setCancelled(true);

                    proxiedPlayer.disconnect(
                            TextComponent.fromLegacy(serverEvent.getCancelledReason())
                    );
                    return;
                }

                swiftServer = serverEvent.getSwiftServer();

                if (swiftServer == null || !platformHandler.isServerExist(swiftServer.getServerName())) {
                    this.cancelServerEvent(event, proxiedPlayer, BackendType.MAIN);

                    return;
                }

                if (MainConfiguration.getLastServerRedirection() && swiftPlayer.hasRecentServer()) {
                    // Last server redirection
                    String lastServerName = swiftPlayer.getRecentServer();
                    ServerInfo lastServer = platformHandler.getServer(lastServerName);

                    if(lastServer != null) {
                        swiftPlayer.getProfileTemplate().set("recentServer", null);
                        event.setTarget(lastServer);
                        return;
                    }
                }

                ServerInfo serverInfo = platformHandler.getServer(swiftServer.getServerName());

                event.setTarget(serverInfo);
            }else{
                SwiftServer swiftServer = bungee.getImplementation().getServerManager().getAvailableServers(BackendType.LIMBO).orElse(null);

                PlayerJoinLimboServerEvent<ProxiedPlayer> serverEvent = new PlayerJoinLimboServerEvent<>(swiftPlayer, proxiedPlayer, swiftServer);

                bungee.getImplementation().getEventProvider().callEvent(serverEvent, false);

                if(serverEvent.isCancelled()) {
                    proxiedPlayer.disconnect(
                            TextComponent.fromLegacy(serverEvent.getCancelledReason())
                    );
                    return;
                }

                swiftServer = serverEvent.getSwiftServer();

                if(swiftServer == null || !platformHandler.isServerExist(swiftServer.getServerName())) {
                    this.cancelServerEvent(event, proxiedPlayer, BackendType.LIMBO);

                    return;
                }

                ServerInfo serverInfo = platformHandler.getServer(swiftServer.getServerName());

                try {
                    event.setTarget(serverInfo);
                } catch (Exception e) {

                    throw new RuntimeException(e);
                }
            }
        }

    }

    public void cancelServerEvent(ServerConnectEvent event, ProxiedPlayer proxiedPlayer, BackendType backendType) {
        bungee.getSwiftLogger().sendRawPluginError(
                PluginIssues.NO_SERVERS_FOUND,
                "Impossible to move the player %s to a server of type %s because\nno available servers were found.",
                proxiedPlayer.getName(), backendType.toString());

        event.setCancelled(true);

        proxiedPlayer.disconnect(new TextComponent(
                "Sorry but no available servers were found"));
    }

    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        Profile swiftPlayer = (Profile) bungee.getImplementation().getProfileManager().getCachedProfileByName(player.getName()).orElse(null);
        if(swiftPlayer != null) {
            swiftPlayer.setLogged(false);
            if(player.getServer() != null && !MainConfiguration.getLimboServers().contains(player.getServer().getInfo().getName())) {
                swiftPlayer.getProfileTemplate().set("recentServer", player.getServer().getInfo().getName());
            }

            //ProfileManager.getCachedProfiles().remove(swiftPlayer.getUniqueId());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onServerKick(ServerKickEvent event) {
        if(MainConfiguration.getLimboDisconnectRedirection()) {
            ProxiedPlayer proxiedPlayer = event.getPlayer();
            SwiftPlayer swiftPlayer = bungee.getImplementation().getProfileManager().getCachedProfileByName(proxiedPlayer.getName()).orElse(null);
            if (swiftPlayer == null) return;

            if (proxiedPlayer.isConnected()) {
                bungee.getImplementation().getServerManager().getAvailableServers(
                        swiftPlayer.isLogged() ? BackendType.MAIN : BackendType.LIMBO
                ).ifPresent(swiftServer -> {
                    ServerPlatformHandler<ProxiedPlayer, ServerInfo> platformHandler = bungee.getImplementation().getServerPlatformHandler();

                    if (event.isCancelled()) return;

                    if (swiftServer.getServerState() == ServerState.ONLINE) {
                        ServerInfo serverInfo = platformHandler.getServer(swiftServer.getServerName());
                        if (serverInfo == null) return;

                        event.setCancelServer(serverInfo);
                        event.setCancelled(true);

                        bungee.getImplementation().getPlatformHandler().sendMessage(proxiedPlayer, TranslationConfiguration.SUCCESS_REDIRECTION,
                                "%reason%", TextComponent.toLegacyText(event.getKickReasonComponent()),
                                "%server%", event.getCancelServer().getName()
                        );

                    } else {
                        event.setCancelled(true);
                        proxiedPlayer.disconnect(new TextComponent("Sorry but no available servers were found"));
                        return;
                    }
                });
            }
        }
    }
}
