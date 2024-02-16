/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.bungee;

import ch.twidev.swiftlogin.api.players.SwiftPlayer;
import ch.twidev.swiftlogin.common.PlatformHandler;
import ch.twidev.swiftlogin.common.SwiftLogger;
import ch.twidev.swiftlogin.common.configuration.ConfigurationMessage;
import ch.twidev.swiftlogin.common.util.Callback;
import ch.twidev.swiftlogin.common.util.RunningTask;
import co.aikar.commands.BungeeCommandIssuer;
import co.aikar.commands.CommandIssuer;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.net.InetSocketAddress;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class BungeecordPlatformHandler implements PlatformHandler<ProxiedPlayer> {

    private final SwiftLoginBungee swiftLoginBungee;
    private final SwiftLogger swiftLogger;

    public BungeecordPlatformHandler(SwiftLoginBungee swiftLoginBungee) {
        this.swiftLoginBungee = swiftLoginBungee;
        this.swiftLogger = swiftLoginBungee.getSwiftLogger();
    }


    @Override
    public ProxiedPlayer getPlayerFromUUID(UUID uuid) {
        return ProxyServer.getInstance().getPlayer(uuid);
    }

    @Override
    public ProxiedPlayer getIssuer(CommandIssuer commandIssuer) {
        BungeeCommandIssuer bungee = (BungeeCommandIssuer) commandIssuer;

        return bungee.getPlayer();
    }

    @Override
    public InetSocketAddress getPlayerAddress(ProxiedPlayer player) {
        return player.getAddress();
    }

    @Override
    public SwiftPlayer getSwiftPlayer(ProxiedPlayer player) {
        return swiftLoginBungee.getImplementation().getProfileManager().getProfileByUniqueId(player.getUniqueId()).orElse(null);
    }

    @Override
    public UUID getPlayerUUID(ProxiedPlayer player) {
        return player.getUniqueId();
    }

    @Override
    public void sendMessage(ProxiedPlayer player, String message) {
        player.sendMessage(
                TextComponent.fromLegacy(message)
        );
    }

    @Override
    public void sendMessage(ProxiedPlayer player, ConfigurationMessage message) {
        player.sendMessage(TextComponent.fromLegacy(message.getTranslation(player.getName())));
    }

    @Override
    public void sendMessage(ProxiedPlayer player, ConfigurationMessage message, String... args) {
        player.sendMessage(TextComponent.fromLegacy(message.getTranslation(player.getName(), args)));
    }

    @Override
    public void disconnect(ProxiedPlayer player, String message) {
        player.disconnect(
                new TextComponent(message)
        );
    }

    @Override
    public RunningTask runAsyncTask(Callback<RunningTask> runningTaskCallback, long delay, long period) {
        RunningTask runningTask = new RunningTask() {
            @Override
            public void cancel() {
                ProxyServer.getInstance().getScheduler().cancel(this.getTaskId());
            }
        };

        ScheduledTask scheduledTask = ProxyServer.getInstance().getScheduler().schedule(swiftLoginBungee, new Runnable() {
            @Override
            public void run() {
                runningTaskCallback.run(runningTask);
            }
        }, delay, period, TimeUnit.MILLISECONDS);

        runningTask.setTaskId(scheduledTask.getId());

        return null;
    }

    @Override
    public CompletableFuture<Void> runAsync(Runnable runnable) {
        CompletableFuture<Void> completableFuture = new CompletableFuture<>();

        ProxyServer.getInstance().getScheduler().runAsync(swiftLoginBungee, () -> {
            try {
                runnable.run();
                completableFuture.complete(null);
            } catch (Throwable throwable) {
                completableFuture.completeExceptionally(throwable);
            }
        });

        return completableFuture;
    }

}
