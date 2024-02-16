/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.common;

import ch.twidev.swiftlogin.api.players.SwiftPlayer;
import ch.twidev.swiftlogin.common.configuration.ConfigurationMessage;
import ch.twidev.swiftlogin.common.util.Callback;
import ch.twidev.swiftlogin.common.util.RunningTask;
import co.aikar.commands.CommandIssuer;

import java.net.InetSocketAddress;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface PlatformHandler<P> {

    P getPlayerFromUUID(UUID uuid);

    P getIssuer(CommandIssuer commandIssuer);

    InetSocketAddress getPlayerAddress(P player);

    SwiftPlayer getSwiftPlayer(P player);

    UUID getPlayerUUID(P player);

    void sendMessage(P player, String message);

    void sendMessage(P player, ConfigurationMessage message);

    void sendMessage(P player, ConfigurationMessage message, String... args);

    void disconnect(P player, String message);

    CompletableFuture<Void> runAsync(Runnable runnable);

    RunningTask runAsyncTask(Callback<RunningTask> runningTaskCallback, long delay, long period);


}
