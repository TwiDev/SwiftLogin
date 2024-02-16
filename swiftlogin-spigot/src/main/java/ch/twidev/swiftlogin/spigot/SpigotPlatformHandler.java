/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.spigot;

import ch.twidev.swiftlogin.api.players.SwiftPlayer;
import ch.twidev.swiftlogin.common.PlatformHandler;
import ch.twidev.swiftlogin.common.configuration.ConfigurationMessage;
import ch.twidev.swiftlogin.common.util.Callback;
import ch.twidev.swiftlogin.common.util.RunningTask;
import co.aikar.commands.CommandIssuer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.net.InetSocketAddress;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class SpigotPlatformHandler implements PlatformHandler<Player> {

    private final SwiftLoginSpigot swiftLoginSpigot;

    public SpigotPlatformHandler(SwiftLoginSpigot swiftLoginSpigot) {
        this.swiftLoginSpigot = swiftLoginSpigot;
    }

    @Override
    public Player getPlayerFromUUID(UUID uuid) {
        return Bukkit.getPlayer(uuid);
    }

    @Override
    public Player getIssuer(CommandIssuer commandIssuer) {
        return null;
    }

    @Override
    public InetSocketAddress getPlayerAddress(Player player) {
        return null;
    }

    @Override
    public SwiftPlayer getSwiftPlayer(Player player) {
        return null;
    }

    @Override
    public UUID getPlayerUUID(Player player) {
        return player.getUniqueId();
    }

    @Override
    public void sendMessage(Player player, String message) {
        player.sendMessage(message);
    }

    @Override
    public void sendMessage(Player player, ConfigurationMessage message) {
        player.sendMessage(message.getTranslation(player.getName()));
    }

    @Override
    public void sendMessage(Player player, ConfigurationMessage message, String... args) {
        player.sendMessage(message.getTranslation(player.getName(), args));
    }

    @Override
    public void disconnect(Player player, String message) {
        player.kickPlayer(message);
    }

    @Override
    public CompletableFuture<Void> runAsync(Runnable runnable) {
        return null;
    }

    @Override
    public RunningTask runAsyncTask(Callback<RunningTask> runningTaskCallback, long delay, long period) {
        RunningTask runningTask = new RunningTask() {
            @Override
            public void cancel() {
                Bukkit.getScheduler().cancelTask(this.getTaskId());
            }
        };

         BukkitTask scheduledTask = Bukkit.getScheduler().runTaskTimerAsynchronously(swiftLoginSpigot, new Runnable() {
            @Override
            public void run() {
                runningTaskCallback.run(runningTask);
            }
        }, delay, period);

        runningTask.setTaskId(scheduledTask.getTaskId());
        return runningTask;
    }
}
