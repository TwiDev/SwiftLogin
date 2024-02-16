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
import ch.twidev.swiftlogin.api.players.SwiftPlayer;
import ch.twidev.swiftlogin.api.players.SwiftPlayerManager;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class ProfileManager implements SwiftPlayerManager {

    private static final HashMap<UUID, Profile> cachedProfiles = new HashMap<>();

    private final SQLConnection connection;

    public ProfileManager(SQLConnection connection) {
        this.connection = connection;
    }

    @Override
    public Optional<SwiftPlayer> getCachedProfileByUniqueId(UUID uuid) {
        SwiftPlayer swiftPlayer = cachedProfiles.values().stream().filter(profile -> profile.getUniqueId().equals(uuid)).findAny().orElse(null);
        return Optional.ofNullable(swiftPlayer);
    }

    @Override
    public Optional<SwiftPlayer> getCachedProfileByPremiumId(UUID uuid) {
        SwiftPlayer swiftPlayer = cachedProfiles.values().stream().filter(Profile::isPremium).filter(profile -> profile.getPremiumId().equals(uuid)).findAny().orElse(null);
        return Optional.ofNullable(swiftPlayer);
    }

    @Override
    public Optional<SwiftPlayer> getCachedProfileByName(String name) {
        SwiftPlayer swiftPlayer = cachedProfiles.values().stream().filter(Profile::hasRecentName).filter(profile -> profile.getRecentName().equals(name)).findAny().orElse(null);
        return Optional.ofNullable(swiftPlayer);
    }

    @Override
    public Optional<SwiftPlayer> getProfileByUniqueId(UUID uuid) {
        return Optional.ofNullable(cachedProfiles.values().stream().filter(profile -> profile.getUniqueId().equals(uuid)).findAny().orElseGet(() -> {
            ProfileTemplate profileTemplate = new SQLManager<>(connection, ProfileTemplate.class).get(uuid);
            if(profileTemplate != null && profileTemplate.isExists()) {
                return new Profile(profileTemplate);
            }

            return null;
        }));
    }

    @Override
    public Optional<SwiftPlayer> getProfileByPremiumId(UUID uuid) {
        return Optional.ofNullable(cachedProfiles.values().stream().filter(Profile::isPremium).filter(profile -> profile.getPremiumId().equals(uuid)).findAny().orElseGet(() -> {
            ProfileTemplate profileTemplate = new SQLManager<>(connection, ProfileTemplate.class).get("WHERE premiumId=? LIMIT 1", uuid.toString());
            if(profileTemplate != null && profileTemplate.isExists()) {
                return new Profile(profileTemplate);
            }

            return null;
        }));
    }

    @Override
    public Optional<SwiftPlayer> getProfileByName(String name) {
        return Optional.ofNullable(cachedProfiles.values().stream().filter(Profile::hasRecentName).filter(profile -> profile.getRecentName().equals(name)).findAny().orElseGet(() -> {
            ProfileTemplate profileTemplate = new SQLManager<>(connection, ProfileTemplate.class).get("WHERE recentName=? LIMIT 1", name);
            if(profileTemplate != null && profileTemplate.isExists()) {
                return new Profile(profileTemplate);
            }

            return null;
        }));
    }

    public Optional<Profile> getCachedLegacyProfileByUUID(UUID uuid) {
        return Optional.ofNullable(cachedProfiles.getOrDefault(uuid, null));
    }

    public Optional<Profile> getCachedLegacyProfileByName(String name) {
        Profile swiftPlayer = cachedProfiles.values().stream().filter(Profile::hasRecentName).filter(profile -> profile.getRecentName().equals(name)).findAny().orElse(null);
        return Optional.ofNullable(swiftPlayer);
    }

    public static HashMap<UUID, Profile> getCachedProfiles() {
        return cachedProfiles;
    }
}
