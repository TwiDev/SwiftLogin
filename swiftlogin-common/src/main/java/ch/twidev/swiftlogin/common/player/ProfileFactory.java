/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.common.player;

import ch.twidev.swiftlogin.common.database.sql.SQLConnection;
import ch.twidev.swiftlogin.common.database.sql.SQLManager;

import java.util.UUID;

public class ProfileFactory {

    private final SQLConnection connection;
    private final ProfileManager profileManager;

    public ProfileFactory(SQLConnection connection, ProfileManager profileManager) {
        this.connection = connection;
        this.profileManager = profileManager;
    }

    public Profile createOnlinePlayer(UUID uuid, UUID premiumId, String name, String firstAddress) {
        ProfileTemplate emptyProfile = new SQLManager<>(connection, ProfileTemplate.class).getOrInsert(uuid);
        emptyProfile.set("premiumId", premiumId);
        emptyProfile.set("recentName", name);
        emptyProfile.set("firstAddress", firstAddress);
        emptyProfile.set("firstSeen", System.currentTimeMillis());

        return new Profile(emptyProfile);
    }

    public Profile createOfflinePlayer(UUID uuid, String name) {
        ProfileTemplate emptyProfile = new SQLManager<>(connection, ProfileTemplate.class).getOrInsert(uuid);
        emptyProfile.set("recentName", name);
        return new Profile(emptyProfile);
    }


}
