/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.api.servers;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SwiftServerManager {


    Optional<SwiftServer> getServer(UUID uuid);

    Optional<SwiftServer> getServer(String name);

    List<SwiftServer> getServers();
    List<SwiftServer> getServers(BackendType backendType);

    Optional<SwiftServer> getAvailableServers(BackendType backendType);

    boolean isServerExist(UUID uuid);
    boolean isServerExist(String name);

    /**
     * Work only on Bungeecord/Velocity side
     *
     * @param serverName Registered server name on proxy
     */
    void registerServer(BackendType backendType, String serverName) throws UnsupportedOperationException;

    void unregisterServer(String serverName);

}
