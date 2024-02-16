/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.example.bungee;

import ch.twidev.swiftlogin.api.SwiftLogin;
import ch.twidev.swiftlogin.api.SwiftLoginProvider;
import ch.twidev.swiftlogin.api.event.SwiftEventHandler;
import ch.twidev.swiftlogin.api.event.SwiftListener;
import ch.twidev.swiftlogin.api.event.events.PlayerAuthenticatedEvent;
import ch.twidev.swiftlogin.api.event.events.PlayerJoinMainServerEvent;
import ch.twidev.swiftlogin.api.exception.ServerSideNotSupportedException;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeeUsingSwiftLogin extends Plugin implements SwiftListener {

    private SwiftLogin<ProxiedPlayer> swiftLogin;

    @Override
    @SuppressWarnings("unchecked")
    public void onEnable() {
        SwiftLoginProvider<ProxiedPlayer> swiftLoginProvider = (SwiftLoginProvider<ProxiedPlayer>) this.getProxy().getPluginManager().getPlugin("SwiftLogin");

        this.swiftLogin = swiftLoginProvider.getImplementation();

        swiftLogin.getEventProvider().registerListener(this);
    }

    @SwiftEventHandler
    public void onPlayerAuthenticated(PlayerAuthenticatedEvent<ProxiedPlayer> event) {
        ProxiedPlayer player = event.getPlayer();

        this.getLogger().info(
                String.format("%s is now connected with connection type : %s!",
                        player.getName(),
                        event.getAuthenticatedReason().toString())
        );

        this.getLogger().info(
                String.format("Player Info: %s", event.getSwiftPlayer().toRawString())
        );

        //event.setCancelled(true);
    }

    @SwiftEventHandler
    public void onMainJoin(PlayerJoinMainServerEvent<ProxiedPlayer> event) {
        this.getLogger().info(
                String.format("%s is now connected on server : %s",
                        event.getPlayer().getName(),
                        event.getSwiftServer().toRawString()
                )
        );

        /*event.setCancelledReason("Test");
        event.setCancelled(true);*/
    }

    public SwiftLogin<ProxiedPlayer> getSwiftLogin() {
        return swiftLogin;
    }
}
