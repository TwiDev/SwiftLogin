/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.bungee.listeners;

import ch.twidev.swiftlogin.api.authorization.AuthenticatedReason;
import ch.twidev.swiftlogin.api.authorization.AuthorizationProvider;
import ch.twidev.swiftlogin.api.authorization.ConnectionType;
import ch.twidev.swiftlogin.api.event.events.PlayerAuthenticatedEvent;
import ch.twidev.swiftlogin.bungee.BungeeTranslationHandler;
import ch.twidev.swiftlogin.bungee.SwiftLoginBungee;
import ch.twidev.swiftlogin.common.SwiftLoginImplementation;
import ch.twidev.swiftlogin.common.configuration.schema.MainConfiguration;
import ch.twidev.swiftlogin.common.configuration.schema.TranslationConfiguration;
import ch.twidev.swiftlogin.common.connection.ConnectionListener;
import ch.twidev.swiftlogin.common.connection.ConnectionResult;
import ch.twidev.swiftlogin.common.connection.ConnectionState;
import ch.twidev.swiftlogin.common.player.Profile;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.connection.InitialHandler;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import net.md_5.bungee.protocol.packet.Handshake;

import java.lang.reflect.Field;
import java.util.UUID;

public class LoginListener extends ConnectionListener<ProxiedPlayer, ServerInfo, PendingConnection> implements Listener {

    private final SwiftLoginBungee bungee;
    private final BungeeTranslationHandler bungeeTranslationHandler;

    public LoginListener(SwiftLoginBungee bungee) {
        super(bungee);

        this.bungee = bungee;
        this.bungeeTranslationHandler = bungee.getTranslationConfiguration();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPreLogin(PreLoginEvent event) {
        if(event.isCancelled()) return;
        if(!event.getConnection().isConnected()) return;

        event.registerIntent(bungee);

        PendingConnection pendingConnection = event.getConnection();

        bungee.getProxy().getScheduler().runAsync(bungee, () -> {
            ConnectionResult connectionResult = this.checkConnection(
                    pendingConnection,
                    pendingConnection.getName(),
                    pendingConnection.getVirtualHost(),
                    pendingConnection.getAddress().getAddress());

            if(connectionResult.getConnectionState() != ConnectionState.PENDING) {
                if(connectionResult.getConnectionState() == ConnectionState.DENIED) {
                    event.setCancelReason(
                            new TextComponent(connectionResult.getMessage())
                    );

                    event.setCancelled(true);
                }

                event.completeIntent(bungee);
            }
        });

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLogin(LoginEvent event) {
        PendingConnection pendingConnection = event.getConnection();
        bungee.getImplementation().getProfileManager().getCachedProfileByName(pendingConnection.getName()).ifPresentOrElse(swiftPlayer -> {
            UUID uuid = swiftPlayer.getUniqueId();

            String accessToken = MainConfiguration.getToken();
            InitialHandler initialHandler = (InitialHandler) pendingConnection;
            Handshake handshake = initialHandler.getHandshake();
            Class<?> clazz = initialHandler.getClass();
            try {
                Field field = clazz.getDeclaredField("uniqueId");
                field.setAccessible(true);
                field.set(initialHandler, uuid);
                handshake.setHost(accessToken);
                handshake.setPort(0);

                if(initialHandler.getLoginProfile() != null) {
                    initialHandler.getLoginProfile().setId(uuid.toString());
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }, () -> {
            event.setCancelReason(
                    bungeeTranslationHandler.formatComponent(TranslationConfiguration.ERROR_USER_NOT_EXISTS, pendingConnection.getName())
            );
            event.setCancelled(true);
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPostLogin(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();
        SwiftLoginImplementation<ProxiedPlayer, ServerInfo> swiftLoginImplementation = bungee.getImplementation();
        AuthorizationProvider<ProxiedPlayer> authorizationProvider = swiftLoginImplementation.getAuthorizationProvider();

        swiftLoginImplementation.getProfileManager().getCachedProfileByName(player.getName()).ifPresentOrElse(swiftPlayer -> {
            boolean isAuthorized = swiftPlayer.isPremium();
            Profile profile = (Profile) swiftPlayer;
            if(swiftPlayer.hasCurrentSession()) {
                if(swiftPlayer.getCurrentSession() > System.currentTimeMillis() && player.getAddress().getAddress().getHostAddress().equals(swiftPlayer.getLastAddress())) {
                    isAuthorized = true;
                }else{
                    profile.getProfileTemplate().set("currentSession", 0L);
                }
            }

            if(isAuthorized) {
                swiftPlayer.setLogged(true);

                ConnectionType connectionType = swiftPlayer.isPremium() ? ConnectionType.PREMIUM : ConnectionType.SESSION;
                String address = player.getAddress().getAddress().getHostAddress();
                long seen = System.currentTimeMillis();

                if(swiftPlayer.getFirstAddress() == null)
                    profile.getProfileTemplate().set("firstAddress", address);

                if(swiftPlayer.getFirstSeen() == 0L)
                    profile.getProfileTemplate().set("firstSeen", seen);

                profile.getProfileTemplate().set("recentAddress", address);
                profile.getProfileTemplate().set("recentSeen", seen);

                PlayerAuthenticatedEvent<ProxiedPlayer> authenticatedEvent = new PlayerAuthenticatedEvent<>(
                        swiftPlayer,
                        player,
                        connectionType.equals(ConnectionType.PREMIUM) ? AuthenticatedReason.PREMIUM : AuthenticatedReason.SESSION
                );

                swiftLoginImplementation.getEventProvider().callEvent(authenticatedEvent);

                if(authenticatedEvent.isCancelled()) {
                    player.disconnect(
                            new TextComponent(authenticatedEvent.getCancelledReason())
                    );

                    return;
                }

                authorizationProvider.authorizeValidSession(connectionType, swiftPlayer, player);
            }else{
                if(swiftPlayer.isRegistered()) {
                    authorizationProvider.authorizeValidSession(ConnectionType.LOGIN, swiftPlayer, player);
                    // Login Call
                }else{
                    authorizationProvider.authorizeValidSession(ConnectionType.REGISTER, swiftPlayer, player);
                    // Register Call
                }
            }
        },() -> {
            player.disconnect(
                    bungee.getTranslationConfiguration().formatComponent(TranslationConfiguration.ERROR_USER_NOT_EXISTS, player.getName())
            );
        });
    }

    @Override
    public boolean isOnline(ProxiedPlayer player) {
        return player != null && player.isConnected();
    }

    @Override
    public ProxiedPlayer getPlayer(String name) {
        return bungee.getProxy().getPlayer(name);
    }

    @Override
    public void registerOnline(PendingConnection pendingConnection, UUID profileId, UUID premiumId, String name, String address) {
        this.loginOnline(pendingConnection, profileId);
    }

    @Override
    public void registerOffline(PendingConnection pendingConnection, UUID profileId, String name) {
        this.loginOffline(pendingConnection, profileId);
    }

    @Override
    public void loginOnline(PendingConnection pendingConnection, UUID uuid) {
        pendingConnection.setOnlineMode(true);
    }

    @Override
    public void loginOffline(PendingConnection pendingConnection, UUID uuid) {
        pendingConnection.setOnlineMode(false);
    }
}
