/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.spigot.handshake.listener;

import ch.twidev.swiftlogin.spigot.SwiftLoginSpigot;
import ch.twidev.swiftlogin.spigot.handshake.Handshaker;
import com.destroystokyo.paper.event.player.PlayerHandshakeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class HandshakeListener implements Listener {

    private final SwiftLoginSpigot spigot;

    public HandshakeListener(SwiftLoginSpigot spigot) {
        this.spigot = spigot;
    }

    public static void init(SwiftLoginSpigot plugin) {
        plugin.getServer().getPluginManager().registerEvents(new HandshakeListener(plugin), plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onHandShake(PlayerHandshakeEvent event) {
        Handshaker handshaker = SwiftLoginSpigot.parseHandshake(event.getOriginalHandshake());

        if(handshaker == null) {
            event.setFailed(true);
            event.setFailMessage("Please join the server with correct address (1)");
            spigot.getLogger().warning("Connection refuse due to null handshaker");
            return;
        }

        event.setUniqueId(handshaker.getProfileUuid());
        event.setSocketAddressHostname(handshaker.getAddress());
        event.setPropertiesJson(handshaker.getProperties());
        event.setServerHostname("");
    }

}
