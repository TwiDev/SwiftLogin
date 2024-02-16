/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.common.command.commands.authorization;

import ch.twidev.swiftlogin.api.authorization.AuthenticatedReason;
import ch.twidev.swiftlogin.api.authorization.ConnectionType;
import ch.twidev.swiftlogin.api.event.events.PlayerAuthenticatedEvent;
import ch.twidev.swiftlogin.api.servers.BackendType;
import ch.twidev.swiftlogin.common.PlatformHandler;
import ch.twidev.swiftlogin.common.SwiftProxy;
import ch.twidev.swiftlogin.common.command.CommandInfo;
import ch.twidev.swiftlogin.common.configuration.schema.MainConfiguration;
import ch.twidev.swiftlogin.common.configuration.schema.TranslationConfiguration;
import ch.twidev.swiftlogin.common.exception.PluginCommandException;
import ch.twidev.swiftlogin.common.player.Profile;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Single;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

@CommandInfo(
        name = "login"
)
@CommandAlias("login")
public class LoginCommand<P> extends AuthorizationCommand<P> {


    public LoginCommand(SwiftProxy<P, ?, ?> swiftLoginPlugin) {
        super(swiftLoginPlugin);
    }

    @Default
    public CompletableFuture<Void> onLogin(CommandIssuer commandIssuer, Profile swiftPlayer, @Single String rawPassword, @Optional String factor) {
        PlatformHandler<P> platformHandler = this.getPlayerPlatformHandler();

        return platformHandler.runAsync(() -> {
            P player = this.getPlayer(commandIssuer);


            if (swiftPlayer.isPremium()) {
                throw new PluginCommandException(TranslationConfiguration.ERROR_ALREADY_LOGGED);
            }

            this.isPlayerAuthorized(swiftPlayer);

            if (!swiftPlayer.isRegistered()) {
                throw new PluginCommandException(TranslationConfiguration.ERROR_NOT_REGISTERED);
            }

            if(MainConfiguration.getVerifyCaptchaCode()) {
                if(factor == null || factor.isEmpty()) {
                    throw new PluginCommandException(TranslationConfiguration.CAPTCHA_NOT_MATCH);
                }

                if(!factor.equals(swiftPlayer.getCachedCaptcha())) {
                    throw new PluginCommandException(TranslationConfiguration.CAPTCHA_NOT_MATCH);
                }
            }

            if(swiftPlayer.getHashedPassword().match(rawPassword)) {
                swiftPlayer.setLogged(true);

                long t = System.currentTimeMillis();
                InetSocketAddress inetSocketAddress = platformHandler.getPlayerAddress(player);

                swiftPlayer.getProfileTemplate().set("recentSeen", System.currentTimeMillis());
                swiftPlayer.getProfileTemplate().set("recentAddress", inetSocketAddress.getAddress().getHostAddress());
                swiftPlayer.getProfileTemplate().set("currentSession", t + (MainConfiguration.getSessionTime()*60*1000L));

                if(swiftPlayer.getFirstSeen() == 0L || swiftPlayer.getFirstAddress() == null) {
                    swiftPlayer.getProfileTemplate().set("firstSeen", t);
                    swiftPlayer.getProfileTemplate().set("firstAddress", inetSocketAddress.getAddress().getHostAddress());
                }

                swiftLoginPlugin.getImplementation().getAuthorizationProvider().authorizeValidSession(ConnectionType.SUCCESS_LOGIN, swiftPlayer, player);

                PlayerAuthenticatedEvent<P> authenticatedEvent = new PlayerAuthenticatedEvent<>(
                        swiftPlayer,
                        player,
                        AuthenticatedReason.LOGIN
                );

                swiftLoginPlugin.getImplementation().getEventProvider().callEvent(authenticatedEvent);

                if(authenticatedEvent.isCancelled()) {
                    swiftLoginPlugin.getImplementation().getPlatformHandler().disconnect(player, authenticatedEvent.getCancelledReason());

                    return;
                }
                swiftLoginPlugin.getImplementation().getServerPlatformHandler().sendPlayerTo(player, BackendType.MAIN);
            }else {
                throw new PluginCommandException(TranslationConfiguration.ERROR_PASSWORD_NOT_MATCH);
            }
        });
    }

}
