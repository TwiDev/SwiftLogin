/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.api.event;

import ch.twidev.swiftlogin.api.players.SwiftPlayer;
import ch.twidev.swiftlogin.api.servers.SwiftServer;
import ch.twidev.swiftlogin.api.utils.Nullable;

import java.util.Optional;

public class SwiftPlayerServerEvent<P> extends SwiftPlayerEvent<P>{

    private SwiftServer swiftServer;

    public SwiftPlayerServerEvent(SwiftPlayer swiftPlayer, P player, SwiftServer swiftServer) {
        super(swiftPlayer, player);

        this.swiftServer = swiftServer;
    }

    @Nullable
    public SwiftServer getSwiftServer() {
        return swiftServer;
    }

    public void setSwiftServer(SwiftServer swiftServer) {
        this.swiftServer = swiftServer;
    }

    public void setSwiftServer(Optional<SwiftServer> swiftServer) {
        this.swiftServer = swiftServer.orElse(null);
    }
}
