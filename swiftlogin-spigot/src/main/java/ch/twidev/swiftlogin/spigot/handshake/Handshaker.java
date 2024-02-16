/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.spigot.handshake;

import java.util.UUID;

public class Handshaker {

    private final UUID profileUuid;
    private final String properties;
    private final String address;

    public Handshaker(UUID profileUuid, String address, String properties) {
        this.profileUuid = profileUuid;
        this.address = address;
        this.properties = properties;
    }

    public UUID getProfileUuid() {
        return profileUuid;
    }

    public String getProperties() {
        return properties;
    }

    public String getAddress() {
        return address;
    }
}
