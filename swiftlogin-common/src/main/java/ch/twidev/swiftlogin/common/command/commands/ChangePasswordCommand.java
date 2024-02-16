/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.common.command.commands;

import ch.twidev.swiftlogin.common.PlatformHandler;
import ch.twidev.swiftlogin.common.SwiftProxy;
import ch.twidev.swiftlogin.common.command.CommandBuilder;
import ch.twidev.swiftlogin.common.command.CommandInfo;
import ch.twidev.swiftlogin.common.command.CommandProvider;
import ch.twidev.swiftlogin.common.configuration.schema.MainConfiguration;
import ch.twidev.swiftlogin.common.configuration.schema.TranslationConfiguration;
import ch.twidev.swiftlogin.common.exception.PluginCommandException;
import ch.twidev.swiftlogin.common.player.Profile;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;

import java.util.concurrent.CompletableFuture;

@CommandInfo(name = "changepassword")
@CommandAlias("changepassword")
@CommandPermission("swiftlogin.commands.changepassword")
public class ChangePasswordCommand<P> extends CommandBuilder<P> {
    public ChangePasswordCommand(SwiftProxy<P, ?, ?> swiftLoginPlugin) {
        super(swiftLoginPlugin);
    }

    @Default
    public CompletableFuture<Void> onExecute(CommandIssuer commandIssuer, Profile swiftPlayer, @Single String rawChangedPassword, @Optional String rawConfirmationPassword) {
        PlatformHandler<P> serverPlatformHandler = this.getPlayerPlatformHandler();

        return serverPlatformHandler.runAsync(() -> {
            P player = this.getPlayer(commandIssuer);

            if(swiftPlayer.isPremium()) {
                throw new PluginCommandException(TranslationConfiguration.ERROR_CHANGE_PASSWORD_PREMIUM_USER);
            }

            if(!swiftPlayer.isRegistered()) {
                throw new PluginCommandException(TranslationConfiguration.ERROR_CHANGE_PASSWORD_UNREGISTERED_USER);
            }

            if(MainConfiguration.getConfirmPassword()) {
                if(rawConfirmationPassword == null || rawConfirmationPassword.isEmpty()) {
                    throw new PluginCommandException(TranslationConfiguration.ERROR_CHANGE_PASSWORD_MISSING_CONFIRMATION_PASSWORD);
                }

                if(!swiftPlayer.getHashedPassword().match(rawConfirmationPassword)) {
                    throw new PluginCommandException(TranslationConfiguration.ERROR_PASSWORD_NOT_MATCH);
                }
            }

            CommandProvider.checkPasswordIsSecure(swiftPlayer, rawChangedPassword);

            swiftPlayer.getProfileTemplate().set("hashedPassword", swiftLoginPlugin.getImplementation().getBCryptCryptoProvider().createHashedPassword(rawChangedPassword).toPasswordString());

            this.getPlayerPlatformHandler().sendMessage(player, TranslationConfiguration.SUCCESS_CHANGED_PASSWORD);
        });
    }
}
