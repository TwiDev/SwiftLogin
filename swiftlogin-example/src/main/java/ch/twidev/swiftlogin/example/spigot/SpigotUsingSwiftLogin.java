/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.example.spigot;

import ch.twidev.swiftlogin.api.SwiftLogin;
import ch.twidev.swiftlogin.api.SwiftLoginProvider;
import ch.twidev.swiftlogin.api.event.SwiftEventHandler;
import ch.twidev.swiftlogin.api.event.SwiftEventProvider;
import ch.twidev.swiftlogin.api.event.SwiftListener;
import ch.twidev.swiftlogin.api.event.events.PlayerAuthenticatedEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class SpigotUsingSwiftLogin extends JavaPlugin implements SwiftListener {

    private SwiftLogin<Player> swiftLogin;

    @Override
    @SuppressWarnings("unchecked")
    public void onEnable() {
        SwiftLoginProvider<Player> swiftLoginProvider = (SwiftLoginProvider<Player>) Bukkit.getPluginManager().getPlugin("SwiftLogin");

        this.swiftLogin = swiftLoginProvider.getImplementation();
        SwiftEventProvider<Player> swiftEventProvider = swiftLogin.getEventProvider();

        if(swiftEventProvider == null) return;

        swiftLogin.getEventProvider().registerListener(this);
    }

    public SwiftLogin<Player> getSwiftLogin() {
        return swiftLogin;
    }

    @SwiftEventHandler
    public void onAuthenticated(PlayerAuthenticatedEvent<Player> playerPlayerAuthenticatedEvent) {
        System.out.println(playerPlayerAuthenticatedEvent.toString());
        System.out.println(Bukkit.getPlayer(playerPlayerAuthenticatedEvent.getSwiftPlayer().getUniqueId()));
        if(playerPlayerAuthenticatedEvent.getPlayer() != null) {
            Bukkit.broadcastMessage(
                    String.format("%s is on the server and authenticated as %s",
                            playerPlayerAuthenticatedEvent.getPlayer().getName(),
                            playerPlayerAuthenticatedEvent.getAuthenticatedReason().toString())
            );
        }

        swiftLogin.getLogger().info(
                playerPlayerAuthenticatedEvent.getSwiftPlayer().toRawString()
        );
    }
}
