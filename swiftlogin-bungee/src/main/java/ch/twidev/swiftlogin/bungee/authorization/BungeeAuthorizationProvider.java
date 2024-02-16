/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.bungee.authorization;

import ch.twidev.swiftlogin.api.authorization.AuthorizationProvider;
import ch.twidev.swiftlogin.api.authorization.ConnectionType;
import ch.twidev.swiftlogin.api.players.SwiftPlayer;
import ch.twidev.swiftlogin.bungee.BungeeTranslationHandler;
import ch.twidev.swiftlogin.bungee.SwiftLoginBungee;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.concurrent.TimeUnit;

public class BungeeAuthorizationProvider implements AuthorizationProvider<ProxiedPlayer> {

    private final SwiftLoginBungee bungee;
    private final BungeeTranslationHandler config;

    public BungeeAuthorizationProvider(SwiftLoginBungee bungee) {
        this.bungee = bungee;
        this.config = bungee.getTranslationConfiguration();
    }

    @Override
    public void authorizeValidSession(ConnectionType connectionType, SwiftPlayer swiftPlayer, ProxiedPlayer proxiedPlayer, String... args) {
        final String prefix = connectionType.getTranslationPrefix();
        ProxyServer proxyServer = bungee.getProxy();

        proxyServer.getScheduler().schedule(bungee, () -> {
            if(!proxiedPlayer.isConnected()) return;

            String playerName = proxiedPlayer.getName();

            BaseComponent message = config.formatComponentArgs(config.getTranslationKey(prefix + "Message"), playerName, args);
            if(message != null) {
                proxiedPlayer.sendMessage(message);
            }

            BaseComponent title = config.formatComponentArgs(config.getTranslationKey(prefix + "Title"), playerName, args);
            BaseComponent subtitle = config.formatComponentArgs(config.getTranslationKey(prefix + "SubTitle"), playerName, args);

            if(title != null && subtitle != null) {
                proxyServer.createTitle()
                        .title(title)
                        .subTitle(subtitle)
                        .fadeIn(config.getInt(prefix + "FadeIn"))
                        .stay(config.getInt(prefix + "Stay"))
                        .fadeOut(config.getInt(prefix + "FadeOut"))
                        .send(proxiedPlayer);
            }
        }, 100, TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean isAuthorized(ProxiedPlayer player) {
        return false;
    }

}
