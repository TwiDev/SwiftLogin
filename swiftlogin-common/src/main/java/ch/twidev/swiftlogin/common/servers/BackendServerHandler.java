/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.common.servers;

import ch.twidev.swiftlogin.api.servers.BackendType;
import ch.twidev.swiftlogin.api.servers.SwiftServer;
import ch.twidev.swiftlogin.api.servers.SwiftServerManager;
import ch.twidev.swiftlogin.common.ServerPlatformHandler;
import ch.twidev.swiftlogin.common.SwiftLoginImplementation;
import ch.twidev.swiftlogin.common.SwiftLoginPlugin;
import ch.twidev.swiftlogin.common.SwiftProxy;
import ch.twidev.swiftlogin.common.configuration.schema.MainConfiguration;
import ch.twidev.swiftlogin.common.util.RunningTask;

import java.util.*;
import java.util.stream.Collectors;

public class BackendServerHandler<P, S> implements SwiftServerManager {

    // TODO: ADD REDIS FOR MULTI PROXY
    private static final HashMap<UUID, BackendServer> SERVERS_CACHE = new HashMap<>();

    private static final Random RANDOM;

    static {
        RANDOM = new Random();
    }

    private SwiftProxy<P, S, ?> swiftProxy;

    private ServerPlatformHandler<P, S> platformHandler;

    private RunningTask cacheTask;

    public BackendServerHandler(SwiftLoginImplementation<P, S> swiftLoginImplementation, SwiftLoginPlugin<P, S> swiftLoginPlugin) {
        if(swiftLoginPlugin.getServerType() == ServerType.BACKEND || !(swiftLoginPlugin instanceof SwiftProxy)) return;

        this.swiftProxy = (SwiftProxy<P, S, ?>) swiftLoginPlugin;
        MainConfiguration.getMainServers().forEach(serverName -> {
            UUID uuid = UUID.randomUUID();

            SERVERS_CACHE.put(uuid, new BackendServer(uuid, serverName, BackendType.MAIN));
        });

        MainConfiguration.getLimboServers().forEach(serverName -> {
            UUID uuid = UUID.randomUUID();

            SERVERS_CACHE.put(uuid, new BackendServer(uuid, serverName, BackendType.LIMBO));
        });

        this.platformHandler = swiftLoginImplementation.getServerPlatformHandler();

        // Updating Servers Infos
        this.cacheTask = swiftLoginImplementation.getPlatformHandler().runAsyncTask(runningTask -> {
            long time = System.currentTimeMillis();

            SERVERS_CACHE.values().forEach(backendServer -> {
                platformHandler.pingServer(backendServer.getServerName(), (serverPing, throwable) -> {
                    if(throwable != null) {
                        throw new RuntimeException(throwable);
                    }

                    backendServer.setServerState(serverPing.getServerState());
                    backendServer.setSlots(serverPing.getMaxPlayers());
                    backendServer.setPlayerCount(serverPing.getPlayers());
                    backendServer.setLastPing(time);
                });
            });
        },1000L, 5_000L);
    }

    public RunningTask getCacheTask() {
        return cacheTask;
    }

    @Override
    public Optional<SwiftServer> getServer(UUID uuid) {
        return Optional.ofNullable(SERVERS_CACHE.getOrDefault(uuid, null));
    }

    @Override
    public Optional<SwiftServer> getServer(String name) {
        return SERVERS_CACHE.values().stream()
                .filter(backendServer -> backendServer.getServerName().equals(name))
                .map(SwiftServer.class::cast)
                .findAny();
    }

    @Override
    public List<SwiftServer> getServers() {
        return new ArrayList<>(SERVERS_CACHE.values());
    }

    @Override
    public List<SwiftServer> getServers(BackendType backendType) {
        return SERVERS_CACHE.values().stream()
                .filter(backendServer -> backendServer.getServerType() == backendType)
                .map(SwiftServer.class::cast)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<SwiftServer> getAvailableServers(BackendType backendType) {
        List<SwiftServer> servers = this.getServers(backendType);

        servers.sort(
                new BackendServerComparator(
                        MainConfiguration.getServerStrategy()
                )
        );

        SwiftServer server = servers.stream().findFirst().orElse(null);

        if(server != null && (!server.isOnline() || server.isFull())) {
            server = null;
        }

        return Optional.ofNullable(server);
    }

    @Override
    public boolean isServerExist(UUID uuid) {
        return SERVERS_CACHE.containsKey(uuid);
    }

    @Override
    public boolean isServerExist(String name) {
        return SERVERS_CACHE.values().stream()
                .anyMatch(backendServer -> backendServer.getServerName().equals(name));
    }

    @Override
    public void registerServer(BackendType backendType, String serverName) throws UnsupportedOperationException {
        if(platformHandler == null) {
            throw new UnsupportedOperationException("Cannot register server " + serverName + " because you aren't on a bungeecord/velocity side. Backend side server registration isn't yet available.");
        }

        if(!platformHandler.isServerExist(serverName)) {
            throw new UnsupportedOperationException("Cannot register server " + serverName + " because this server does not exists on this side");
        }

        UUID uuid = UUID.randomUUID();

        SERVERS_CACHE.put(uuid, new BackendServer(uuid, serverName, backendType));
    }

    @Override
    public void unregisterServer(String serverName) {
        SERVERS_CACHE.entrySet().removeIf(entry -> entry.getValue().getServerName().equals(serverName));
    }
}
