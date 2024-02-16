/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.common.command.commands.mode;

import ch.twidev.swiftlogin.common.PlatformHandler;
import ch.twidev.swiftlogin.common.ServerPlatformHandler;
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

@CommandInfo(name = "cracked")
@CommandAlias("cracked")
@CommandPermission("swiftlogin.commands.cracked")
public class CrackedCommand<P> extends CommandBuilder<P> {
    public CrackedCommand(SwiftProxy<P, ?, ?> swiftLoginPlugin) {
        super(swiftLoginPlugin);
    }

    @Default
    public CompletableFuture<Void> onExecute(CommandIssuer commandIssuer, Profile swiftPlayer, @Single String rawPassword) {
        PlatformHandler<P> platformHandler = this.getPlayerPlatformHandler();

        return platformHandler.runAsync(() -> {
            P player = this.getPlayer(commandIssuer);

            if (!swiftPlayer.isPremium()) {
                throw new PluginCommandException(TranslationConfiguration.ERROR_ALREADY_CRACKED);
            }

            if(!swiftPlayer.getHashedPassword().match(rawPassword)) {
                throw new PluginCommandException(TranslationConfiguration.ERROR_PASSWORD_NOT_MATCH);
            }

            swiftPlayer.getProfileTemplate().set("premiumId", null);
            platformHandler.sendMessage(player, TranslationConfiguration.SUCCESS_CRACKED_ENABLED);

        });
    }
}
