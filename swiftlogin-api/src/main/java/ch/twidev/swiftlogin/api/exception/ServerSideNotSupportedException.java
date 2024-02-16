/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.api.exception;

import ch.twidev.swiftlogin.api.servers.ServerSide;

public class ServerSideNotSupportedException extends Exception{

    private final ServerSide serverSide;

    public ServerSideNotSupportedException(String name, ServerSide serverSide) {
        super(name);

        this.serverSide = serverSide;
    }

    public ServerSide getServerSide() {
        return serverSide;
    }
}
