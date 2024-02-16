/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.api.players;

import java.util.Optional;
import java.util.UUID;

public interface SwiftPlayerManager {

    Optional<SwiftPlayer> getProfileByUniqueId(UUID uuid);

    Optional<SwiftPlayer> getProfileByPremiumId(UUID uuid);

    Optional<SwiftPlayer> getProfileByName(String uuid);

    Optional<SwiftPlayer> getCachedProfileByUniqueId(UUID uuid);

    Optional<SwiftPlayer> getCachedProfileByPremiumId(UUID uuid);

    Optional<SwiftPlayer> getCachedProfileByName(String uuid);

}
