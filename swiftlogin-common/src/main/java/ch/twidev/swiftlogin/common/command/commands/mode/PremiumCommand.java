/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.common.command.commands.mode;

import ch.twidev.swiftlogin.api.players.SwiftPlayer;
import ch.twidev.swiftlogin.common.PlatformHandler;
import ch.twidev.swiftlogin.common.SwiftProxy;
import ch.twidev.swiftlogin.common.command.CommandBuilder;
import ch.twidev.swiftlogin.common.command.CommandInfo;
import ch.twidev.swiftlogin.common.configuration.schema.TranslationConfiguration;
import ch.twidev.swiftlogin.common.exception.PluginCommandException;
import ch.twidev.swiftlogin.common.hooks.MojangManager;
import ch.twidev.swiftlogin.common.player.Profile;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Single;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@CommandInfo(name = "premium")
@CommandAlias("premium")
@CommandPermission("swiftlogin.commands.premium")
public class PremiumCommand<P> extends CommandBuilder<P> {
    public PremiumCommand(SwiftProxy<P, ?, ?> swiftLoginPlugin) {
        super(swiftLoginPlugin);
    }

    @Default
    public CompletableFuture<Void> onCommand(CommandIssuer commandIssuer, Profile swiftPlayer, @Single String rawPassword) {
        PlatformHandler<P> platformHandler = this.getPlayerPlatformHandler();

        return platformHandler.runAsync(() -> {
            P player = this.getPlayer(commandIssuer);

            if (swiftPlayer.isPremium()) {
                throw new PluginCommandException(TranslationConfiguration.ERROR_ALREADY_PREMIUM);
            }

            if(!swiftPlayer.getHashedPassword().match(rawPassword)) {
                throw new PluginCommandException(TranslationConfiguration.ERROR_PASSWORD_NOT_MATCH);
            }

            try {
                swiftLoginPlugin.getImplementation().getMojangManager().request(MojangManager.MOJANG_API, swiftPlayer.getRecentName()).ifPresentOrElse(premiumUuid -> {
                    SwiftPlayer premiumPlayer = swiftLoginPlugin.getImplementation().getProfileManager().getProfileByPremiumId(premiumUuid).orElse(null);

                    if(premiumPlayer != null){
                        throw new PluginCommandException(TranslationConfiguration.ERROR_PREMIUM_ALREADY_EXIST);
                    }

                    swiftPlayer.getProfileTemplate().set("premiumId", premiumUuid);
                    swiftPlayer.getProfileTemplate().set("currentSession", 0L);
                    swiftPlayer.getProfileTemplate().set("hashedPassword", null);

                    platformHandler.sendMessage(player, TranslationConfiguration.SUCCESS_PREMIUM_ENABLED);

                }, () -> {
                    throw new PluginCommandException(TranslationConfiguration.ERROR_NOT_PREMIUM);
                });
            } catch (IOException e) {
                throw new PluginCommandException(TranslationConfiguration.MOJANG_SERVERS_DOWN);
            }
        });
    }
}
