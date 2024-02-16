/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.common.servers;

import ch.twidev.swiftlogin.api.servers.ServerState;

public class ServerPing {

    public final ServerState serverState;

    private final int players, maxPlayers;

    public ServerPing(ServerState serverState, int players, int maxPlayers) {
        this.serverState = serverState;
        this.players = players;
        this.maxPlayers = maxPlayers;
    }

    public ServerState getServerState() {
        return serverState;
    }

    public int getPlayers() {
        return players;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    @Override
    public String toString() {
        return "ServerPing{" +
                "serverState=" + serverState +
                ", players=" + players +
                ", maxPlayers=" + maxPlayers +
                '}';
    }
}
