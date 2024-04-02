/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.api.servers;

import ch.twidev.swiftlogin.api.utils.Unique;

public interface SwiftServer extends Unique {

    String getServerName();

    ServerState getServerState();

    BackendType getServerType();

    boolean isOnline();

    boolean isFull();

    int getSlots();

    int getPlayerCount();

    long getLastPing();

    void unregister();

    String toRawString();

    String toJson();

}
