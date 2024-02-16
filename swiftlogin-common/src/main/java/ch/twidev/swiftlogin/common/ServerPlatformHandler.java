/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.common;

import ch.twidev.swiftlogin.api.players.SwiftPlayer;
import ch.twidev.swiftlogin.api.servers.BackendType;
import ch.twidev.swiftlogin.api.servers.SwiftServer;
import ch.twidev.swiftlogin.common.servers.ServerPing;
import ch.twidev.swiftlogin.common.util.Callback;
import ch.twidev.swiftlogin.common.util.RunningTask;
import ch.twidev.swiftlogin.common.util.ThrowableCallback;

import java.util.concurrent.CompletableFuture;

public interface ServerPlatformHandler<P, S> {

    void sendPlayerTo(P player, BackendType backendType);

    boolean sendPlayerTo(P player, SwiftServer swiftServer);

    boolean sendPlayerTo(P player, String serverName);

    boolean isServerExist(String serverName);

    void pingServer(String serverName, ThrowableCallback<ServerPing> callback);

    S getServer(String serverName);

    void connectToServer(P player, S server);

    void sendConnectionToServer(P player, SwiftPlayer swiftPlayer, S server) throws Exception;
    void sendInitialisationServerPacket(SwiftServer swiftServer) throws Exception;

}
