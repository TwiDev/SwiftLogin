/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.common.connection;

public class ConnectionResult {

    private final ConnectionState connectionState;

    private final String message;

    public ConnectionResult(ConnectionState connectionState, String message) {
        this.connectionState = connectionState;
        this.message = message;
    }

    public ConnectionState getConnectionState() {
        return connectionState;
    }

    public String getMessage() {
        return message;
    }
}
