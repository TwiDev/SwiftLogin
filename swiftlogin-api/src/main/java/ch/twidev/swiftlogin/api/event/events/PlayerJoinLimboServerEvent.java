/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.api.event.events;

import ch.twidev.swiftlogin.api.SwiftInitializer;
import ch.twidev.swiftlogin.api.event.SwiftEvent;
import ch.twidev.swiftlogin.api.event.SwiftPlayerServerEvent;
import ch.twidev.swiftlogin.api.event.SwiftReasonableCancellableEvent;
import ch.twidev.swiftlogin.api.players.SwiftPlayer;
import ch.twidev.swiftlogin.api.servers.SwiftServer;

@SwiftInitializer
public class PlayerJoinLimboServerEvent<P> extends SwiftPlayerServerEvent<P> implements SwiftReasonableCancellableEvent {

    private String cancelledReason = SwiftEvent.DEFAULT_CANCELLED_REASON;
    private boolean isCancelled = false;

    public PlayerJoinLimboServerEvent(SwiftPlayer swiftPlayer, P player, SwiftServer swiftServer) {
        super(swiftPlayer, player, swiftServer);
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.isCancelled = cancelled;
    }

    @Override
    public String getCancelledReason() {
        return cancelledReason;
    }

    @Override
    public void setCancelledReason(String reason) {
        this.cancelledReason = reason;
    }
}
