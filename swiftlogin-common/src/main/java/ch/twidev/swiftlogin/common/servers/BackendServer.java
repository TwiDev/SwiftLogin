/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.common.servers;

import ch.twidev.swiftlogin.api.servers.BackendType;
import ch.twidev.swiftlogin.api.servers.ServerState;
import ch.twidev.swiftlogin.api.servers.SwiftServer;
import ch.twidev.swiftlogin.common.SwiftLoginImplementation;

import java.util.UUID;

public class BackendServer implements SwiftServer {

    private final UUID uuid;
    private final String serverName;
    private final BackendType backendType;

    private ServerState serverState;
    private int slots, players;

    private long lastPing = -1L;

    public BackendServer(UUID uuid, String serverName, BackendType backendType) {
        this.uuid = uuid;
        this.serverName = serverName;
        this.backendType = backendType;

        this.serverState = ServerState.PINGING;
        this.slots = -1;
        this.players = 0;
    }

    @Override
    public UUID getUniqueId() {
        return uuid;
    }

    @Override
    public String getServerName() {
        return serverName;
    }

    @Override
    public ServerState getServerState() {
        return serverState;
    }

    @Override
    public BackendType getServerType() {
        return backendType;
    }

    @Override
    public boolean isOnline() {
        return serverState == ServerState.ONLINE;
    }

    @Override
    public boolean isFull() {
        return players >= slots;
    }

    @Override
    public int getSlots() {
        return slots;
    }

    @Override
    public int getPlayerCount() {
        return players;
    }

    @Override
    public long getLastPing() {
        return lastPing;
    }

    @Override
    public void unregister() {
        SwiftLoginImplementation.getInstance().getServerManager().unregisterServer(serverName);
    }

    @Override
    public String toRawString() {
        return this.toString();
    }

    @Override
    public String toString() {
        return "BackendServer{" +
                "uuid=" + uuid +
                ", serverName='" + serverName + '\'' +
                ", backendType=" + backendType +
                ", serverState=" + serverState +
                ", slots=" + slots +
                ", players=" + players +
                ", lastPing=" + lastPing +
                '}';
    }

    public void setServerState(ServerState serverState) {
        this.serverState = serverState;
    }

    public void setSlots(int slots) {
        this.slots = slots;
    }

    public void setPlayerCount(int players) {
        this.players = players;
    }

    public void setLastPing(long lastPing) {
        this.lastPing = lastPing;
    }
}
