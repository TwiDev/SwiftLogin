/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.common.command.commands.session;

import ch.twidev.swiftlogin.common.PlatformHandler;
import ch.twidev.swiftlogin.common.SwiftProxy;
import ch.twidev.swiftlogin.common.command.CommandBuilder;
import ch.twidev.swiftlogin.common.command.CommandInfo;
import ch.twidev.swiftlogin.common.configuration.schema.TranslationConfiguration;
import ch.twidev.swiftlogin.common.exception.PluginCommandException;
import ch.twidev.swiftlogin.common.player.Profile;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Single;

import java.util.concurrent.CompletableFuture;

@CommandInfo(name = "destroysession")
@CommandAlias("destroysession")
@CommandPermission("swiftlogin.commands.destroysession")
public class DestroySessionCommand<P> extends CommandBuilder<P> {

    public DestroySessionCommand(SwiftProxy<P, ?, ?> swiftLoginPlugin) {
        super(swiftLoginPlugin);
    }

    @Default
    public CompletableFuture<Void> onExecute(CommandIssuer commandIssuer, Profile author, @Single String targetName) {
        PlatformHandler<P> platformHandler = this.getPlayerPlatformHandler();

        return platformHandler.runAsync(() -> {
            Profile target = swiftLoginPlugin.getImplementation().getProfileManager().getCachedLegacyProfileByName(targetName).orElse(null);
            P player = this.getPlayer(commandIssuer);

            if(target == null) {
                throw new PluginCommandException(TranslationConfiguration.ERROR_USER_NOT_EXISTS);
            }

            if(target.isPremium()) {
                throw new PluginCommandException(TranslationConfiguration.ERROR_DESTROY_PREMIUM_SESSION);
            }

            if(!target.isRegistered()) {
                throw new PluginCommandException(TranslationConfiguration.ERROR_DESTROY_UNREGISTERED_SESSION);
            }

            if(!target.isLogged()) {
                throw new PluginCommandException(TranslationConfiguration.ERROR_DESTROY_NOT_LOGGED_SESSION);
            }

            if(!target.hasCurrentSession()) {
                throw new PluginCommandException(TranslationConfiguration.ERROR_DESTROY_INVALID_SESSION);
            }

            target.getProfileTemplate().set("currentSession", 0L);

            platformHandler.sendMessage(player, TranslationConfiguration.SUCCESS_SESSION_DESTROYED);
        });
    }
}
