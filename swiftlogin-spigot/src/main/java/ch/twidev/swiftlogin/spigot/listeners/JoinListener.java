/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.spigot.listeners;

import ch.twidev.swiftlogin.common.configuration.schema.BackendConfiguration;
import ch.twidev.swiftlogin.spigot.RegistrationState;
import ch.twidev.swiftlogin.spigot.RegistrationUser;
import ch.twidev.swiftlogin.spigot.SwiftLoginSpigot;
import ch.twidev.swiftlogin.spigot.map.CaptchaMap;
import net.minecraft.server.v1_8_R3.PacketPlayOutSetSlot;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;
import java.util.UUID;

public class JoinListener implements Listener {

    public static final String SPAWN_LOCATION_SEPARATOR = "/";

    private final SwiftLoginSpigot spigot;

    public JoinListener(SwiftLoginSpigot spigot) {
        this.spigot = spigot;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onFailLogin(PlayerLoginEvent event) {
        if (event.getResult().equals(PlayerLoginEvent.Result.ALLOWED)) return;

        SwiftLoginSpigot.getPlayerRegistrations().remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        RegistrationState.checkState(null, RegistrationState.AUTHORIZED, player);
        SwiftLoginSpigot.getPlayerRegistrations().remove(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        SwiftLoginSpigot.getPlayerRegistrations().put(uuid, new RegistrationUser(uuid, player.getName()));
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        String[] rawLocation = BackendConfiguration.getSpawnLocation().split(SPAWN_LOCATION_SEPARATOR);
        Location location = null;

        RegistrationUser registrationUser = SwiftLoginSpigot.getPlayerRegistrations().getOrDefault(event.getPlayer().getUniqueId(),null);
        if(registrationUser == null || registrationUser.getRegistrationState() == RegistrationState.AUTHORIZED) {
            return;
        }

        if(rawLocation.length == 6) {
            location = new Location(
                    Bukkit.getWorld(rawLocation[0]),
                    Double.parseDouble(rawLocation[1]),
                    Double.parseDouble(rawLocation[2]),
                    Double.parseDouble(rawLocation[3]),
                    Float.parseFloat(rawLocation[4]),
                    Float.parseFloat(rawLocation[5])
            );
        }else if(rawLocation.length == 4) {
            location = new Location(
                    Bukkit.getWorld(rawLocation[0]),
                    Double.parseDouble(rawLocation[1]),
                    Double.parseDouble(rawLocation[2]),
                    Double.parseDouble(rawLocation[3])
            );
        }else if(rawLocation.length == 3) {
            location = new Location(
                    event.getPlayer().getWorld(),
                    Double.parseDouble(rawLocation[0]),
                    Double.parseDouble(rawLocation[1]),
                    Double.parseDouble(rawLocation[2])
            );
        }

        if(location != null) {
            event.getPlayer().teleport(location);
        }
    }

}
