/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.common.connection;

import ch.twidev.swiftlogin.common.SwiftLoginImplementation;
import ch.twidev.swiftlogin.common.SwiftProxy;
import ch.twidev.swiftlogin.common.configuration.schema.MainConfiguration;
import ch.twidev.swiftlogin.common.hooks.MojangManager;
import ch.twidev.swiftlogin.common.player.Profile;
import ch.twidev.swiftlogin.common.player.ProfileFactory;
import ch.twidev.swiftlogin.common.player.ProfileManager;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.UUID;
import java.util.regex.Pattern;

public abstract class ConnectionListener<P, S, C> {

    public static final Pattern NAME_PATTERN = Pattern.compile("[a-zA-Z0-9_]{2,16}");
    public static final long SESSION_LIVING_TIME_COEFFICIENT = 60*1000L;

    private final SwiftProxy<P, S, ?> plugin;

    protected ConnectionListener(SwiftProxy<P, S, ?> plugin) {
        this.plugin = plugin;

        if(plugin.getServerType().isBackend()) {
            throw new RuntimeException("Cannot initialize a connection listener for backend server side");
        }
    }

    public ConnectionResult checkConnection(C pendingConnection, String username, InetSocketAddress virtualhost, InetAddress inetAddress) {
        if(username.length() > 16 || !NAME_PATTERN.matcher(username).matches()) {
            return new ConnectionResult(ConnectionState.DENIED, "INVALID_NAME");
        }

        SwiftLoginImplementation<P, S> swiftLoginImplementation = plugin.getImplementation();

        /*
         * Checking player logging status
         */

        P player = this.getPlayer(username);
        if (this.isOnline(player)) {
            return new ConnectionResult(ConnectionState.DENIED, "ALREADY_ONLINE");
        }

        /*
         * Checking player authorized hostname
         */

        String address = virtualhost.getHostName().toLowerCase() + ":" + virtualhost.getPort();

        if (!MainConfiguration.getHostnames().contains(address) && !MainConfiguration.getHostnames().isEmpty()) {
            return new ConnectionResult(ConnectionState.DENIED, "INVALID_HOSTNAME");
        }

        /*
         * Checking player current session
         */

        ProfileManager swiftPlayerManager = swiftLoginImplementation.getProfileManager();

        int livingTime = this.plugin.getMainConfiguration().getInt("sessionTime");
        Profile swiftPlayer = (Profile) swiftPlayerManager.getProfileByName(username).orElse(null);
        if(swiftPlayer != null && !swiftPlayer.isPremium()) {

            if(!swiftPlayer.getRecentName().equals(username)) {
                return new ConnectionResult(ConnectionState.DENIED, "INVALID_NAME");
            }

            if(livingTime <= 0 || System.currentTimeMillis() - swiftPlayer.getRecentSeen() < livingTime*SESSION_LIVING_TIME_COEFFICIENT
                    && swiftPlayer.getLastAddress().equals((inetAddress.getHostAddress()))) {

                this.loginOffline(pendingConnection, swiftPlayer.getUniqueId());
                return new ConnectionResult(ConnectionState.OFFLINE, "LOGIN_OFFLINE");
            }else{
                // Expired Session

                swiftPlayer.getProfileTemplate().set("currentSession", 0L);
                this.loginOffline(pendingConnection, swiftPlayer.getUniqueId());
                return new ConnectionResult(ConnectionState.OFFLINE, "LOGIN_OFFLINE");
            }
        }

        /*
         * Checking player mojang UUID
         */

        UUID premiumUUID = null;

        try {
            premiumUUID = swiftLoginImplementation.getMojangManager().request(MojangManager.MOJANG_API, username).orElse(null);
        } catch (IOException e) {
            e.printStackTrace();
            return new ConnectionResult(ConnectionState.DENIED, "SERVERS_DOWN");
        }

        Profile swiftPlayerByUuid = null;
        if(premiumUUID != null) {
            swiftPlayerByUuid = (Profile) swiftPlayerManager.getProfileByPremiumId(premiumUUID).orElse(null);
        }

        if(swiftPlayerByUuid != null) {
            if(!username.equals(swiftPlayerByUuid.getRecentName())) {
                String recentName = swiftPlayerByUuid.getRecentName();
                if (recentName != null) {
                    P recentPlayer = this.getPlayer(recentName);
                    if (this.isOnline(recentPlayer)) {
                        return new ConnectionResult(ConnectionState.DENIED, "ALREADY_ONLINE");
                    }
                }

                swiftPlayerByUuid.getProfileTemplate().set("recentName", username);
                ProfileManager.getCachedProfiles().put(
                        swiftPlayerByUuid.getUniqueId(), swiftPlayerByUuid
                );
                this.loginOnline(pendingConnection, swiftPlayerByUuid.getUniqueId());
                return new ConnectionResult(ConnectionState.ONLINE, "LOGIN_ONLINE");
            }else{
                this.loginOnline(pendingConnection, swiftPlayerByUuid.getUniqueId());
                return new ConnectionResult(ConnectionState.ONLINE, "LOGIN_ONLINE");
            }
        }

        UUID profileUUID = UUID.randomUUID();
        ProfileFactory profileFactory = plugin.getImplementation().getProfileFactory();
        if (premiumUUID != null) {
            profileFactory.createOnlinePlayer(profileUUID, premiumUUID, username, address);
            this.registerOnline(pendingConnection, profileUUID, premiumUUID, username, inetAddress.getHostAddress());
            return new ConnectionResult(ConnectionState.ONLINE, "REGISTERED_ONLINE");
        }else{
            profileFactory.createOfflinePlayer(profileUUID, username);
            this.registerOffline(pendingConnection, profileUUID, username);
            return new ConnectionResult(ConnectionState.ONLINE, "REGISTER_OFFLINE");
        }
    }

    public abstract boolean isOnline(P player);

    public abstract P getPlayer(String name);

    public abstract void registerOnline(C pendingConnection, UUID profileId, UUID premiumId, String name, String address);

    public abstract void registerOffline(C pendingConnection, UUID profileId, String name);

    public abstract void loginOnline(C pendingConnection, UUID uuid);

    public abstract void loginOffline(C pendingConnection, UUID uuid);

}
