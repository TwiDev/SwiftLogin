/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.bungee.listeners;

import ch.twidev.swiftlogin.api.players.SwiftPlayerManager;
import ch.twidev.swiftlogin.bungee.SwiftLoginBungee;
import ch.twidev.swiftlogin.common.configuration.Configuration;
import ch.twidev.swiftlogin.common.configuration.schema.MainConfiguration;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.TabCompleteEvent;
import net.md_5.bungee.api.plugin.Cancellable;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class ActionListener implements Listener {

    private final SwiftPlayerManager swiftPlayerManager;

    private final Configuration config;

    public ActionListener(SwiftLoginBungee swiftLoginBungee) {
        this.swiftPlayerManager = swiftLoginBungee.getImplementation().getProfileManager();
        this.config = swiftLoginBungee.getMainConfiguration();
    }

    public void checkCancel(Cancellable cancellable, ProxiedPlayer proxiedPlayer) {
        if(cancellable.isCancelled()) return;

        swiftPlayerManager.getCachedProfileByName(proxiedPlayer.getName()).ifPresentOrElse(swiftPlayer -> {
            if(!swiftPlayer.isLogged()) {
                cancellable.setCancelled(true);
            }
        }, () -> {
            cancellable.setCancelled(true);
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(ChatEvent event) {
        ProxiedPlayer player = (ProxiedPlayer) event.getSender();

        if(!event.isCommand()) {
            this.checkCancel(event, player);
            return;
        }

        if(!MainConfiguration.getAuthorizedCommands().contains(event.getMessage().split(" ")[0].replaceAll("/",""))) {
            this.checkCancel(event, player);
            return;
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onTabComplete(TabCompleteEvent event) {
        ProxiedPlayer player = (ProxiedPlayer) event.getSender();

        this.checkCancel(event, player);

    }
}
