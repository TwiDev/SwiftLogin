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
import ch.twidev.swiftlogin.api.authorization.AuthenticatedReason;
import ch.twidev.swiftlogin.api.event.SwiftEvent;
import ch.twidev.swiftlogin.api.event.SwiftPlayerEvent;
import ch.twidev.swiftlogin.api.event.SwiftReasonableCancellableEvent;
import ch.twidev.swiftlogin.api.players.SwiftPlayer;
import ch.twidev.swiftlogin.api.utils.Include;

@SwiftInitializer
public class PlayerAuthenticatedEvent<P> extends SwiftPlayerEvent<P> implements SwiftReasonableCancellableEvent {

    @Include
    private AuthenticatedReason authenticatedReason;

    @Include
    private String cancelledReason = SwiftEvent.DEFAULT_CANCELLED_REASON;

    @Include
    private boolean isCancelled = false;

    public PlayerAuthenticatedEvent(Class<P> player) {
        super(player);
    }

    public PlayerAuthenticatedEvent(SwiftPlayer swiftPlayer, P player, AuthenticatedReason authenticatedReason) {
        super(swiftPlayer, player);
        this.authenticatedReason = authenticatedReason;
    }

    public AuthenticatedReason getAuthenticatedReason() {
        return authenticatedReason;
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
        this.cancelledReason = cancelledReason;
    }

    @Override
    public String toString() {
        return "PlayerAuthenticatedEvent{" +
                "authenticatedReason=" + authenticatedReason +
                ", cancelledReason='" + cancelledReason + '\'' +
                ", isCancelled=" + isCancelled +
                ", player=" + getPlayer().toString() +
                ", swiftPlayer=" + getSwiftPlayer() +
                '}';
    }
}
