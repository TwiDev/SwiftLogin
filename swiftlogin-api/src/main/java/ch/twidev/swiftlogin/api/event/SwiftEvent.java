/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.api.event;

public class SwiftEvent<P> {

    public static final String EVENTS_PACKAGE_NAME = "ch.twidev.swiftlogin.api.event.events";

    public static final String DEFAULT_CANCELLED_REASON = "Your action has been cancelled by an external agent.";

    private final String eventName;

    private final boolean async;

    public SwiftEvent() {
        this.async = false;

        this.eventName = this.getEventName();
    }

    @Deprecated
    public SwiftEvent(boolean isAsync) {
        this.async = isAsync;

        this.eventName = this.getEventName();
    }

    public String getEventName() {
        return this.getClass().getName();
    }

    public boolean isAsynchronous() {
        return async;
    }
}
