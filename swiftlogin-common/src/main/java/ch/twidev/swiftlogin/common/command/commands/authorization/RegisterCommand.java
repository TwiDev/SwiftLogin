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
import ch.twidev.swiftlogin.common.command.CommandProvider;
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
        name = "register"
)
@CommandAlias("register")
public class RegisterCommand<P> extends AuthorizationCommand<P> {

    public RegisterCommand(SwiftProxy<P, ?, ?> swiftLoginPlugin) {
        super(swiftLoginPlugin);
    }

    @Default
    public CompletableFuture<Void> onRegister(CommandIssuer commandIssuer, Profile swiftPlayer, @Single String rawPassword, @Optional String passwordConfirmation, @Optional String captcha) {
        PlatformHandler<P> platformHandler = this.getPlayerPlatformHandler();

        return platformHandler.runAsync(() -> {
            P player = this.getPlayer(commandIssuer);

            if (swiftPlayer.isPremium()) {
                throw new PluginCommandException(TranslationConfiguration.ERROR_REGISTER_PREMIUM);
            }

            if (swiftPlayer.isRegistered()) {
                throw new PluginCommandException(TranslationConfiguration.ERROR_ALREADY_REGISTERED);
            }

            this.isPlayerAuthorized(swiftPlayer);

            String code = getFactor(rawPassword, passwordConfirmation, captcha);

            if(MainConfiguration.getVerifyCaptchaCode()) {
                if(code == null || code.isEmpty()) {
                    throw new PluginCommandException(TranslationConfiguration.CAPTCHA_NOT_MATCH);
                }

                if(!code.equals(swiftPlayer.getCachedCaptcha())) {
                    throw new PluginCommandException(TranslationConfiguration.CAPTCHA_NOT_MATCH);
                }
            }

            CommandProvider.checkPasswordIsSecure(swiftPlayer, rawPassword);

            long t = System.currentTimeMillis();
            InetSocketAddress inetSocketAddress = platformHandler.getPlayerAddress(player);

            swiftPlayer.getProfileTemplate().set("hashedPassword", swiftLoginPlugin.getDefaultCryptoProvider().createHashedPassword(rawPassword).toPasswordString());
            swiftPlayer.getProfileTemplate().set("recentSeen", t);
            swiftPlayer.getProfileTemplate().set("recentAddress", inetSocketAddress.getAddress().getHostAddress());
            swiftPlayer.getProfileTemplate().set("firstSeen", t);
            swiftPlayer.getProfileTemplate().set("firstAddress", inetSocketAddress.getAddress().getHostAddress());
            swiftPlayer.getProfileTemplate().set("currentSession", t + (MainConfiguration.getSessionTime() * 60 * 1000L));

            swiftPlayer.setLogged(true);

            swiftLoginPlugin.getImplementation().getAuthorizationProvider().authorizeValidSession(ConnectionType.SUCCESS_REGISTER, swiftPlayer, player);

            PlayerAuthenticatedEvent<P> authenticatedEvent = new PlayerAuthenticatedEvent<>(
                    swiftPlayer,
                    player,
                    AuthenticatedReason.REGISTER
            );

            swiftLoginPlugin.getImplementation().getEventProvider().callEvent(authenticatedEvent);

            if(authenticatedEvent.isCancelled()) {
                swiftLoginPlugin.getImplementation().getPlatformHandler().disconnect(player, authenticatedEvent.getCancelledReason());

                return;
            }

            swiftLoginPlugin.getImplementation().getServerPlatformHandler().sendPlayerTo(player, BackendType.MAIN);

        });
    }

    private static String getFactor(String rawPassword, String passwordConfirmation, String captcha) {
        if(MainConfiguration.getConfirmPassword()) {
            if(passwordConfirmation == null) {
                throw new PluginCommandException(TranslationConfiguration.ERROR_REGISTER_MISSING_CONFIRMATION_PASSWORD);
            }

            if(!passwordConfirmation.equals(rawPassword)) {
                throw new PluginCommandException(TranslationConfiguration.ERROR_REGISTER_INVALID_CONFIRMATION_PASSWORD);
            }

            return captcha;
        }else{
            return passwordConfirmation;
        }
    }
}
