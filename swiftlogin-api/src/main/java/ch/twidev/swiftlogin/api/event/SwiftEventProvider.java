/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.api.event;

public interface SwiftEventProvider<P> {

    void registerListener(SwiftListener swiftListener);

    void unregisterListener(SwiftListener swiftListener);

    default <E extends SwiftEvent<P>> void callEvent(E event) {
        callEvent(event, true);
    }

    <E extends SwiftEvent<P>> void callEvent(E event, boolean broadcast);
}
