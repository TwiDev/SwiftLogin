/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.bungee;

import ch.twidev.swiftlogin.common.configuration.Configuration;
import ch.twidev.swiftlogin.common.configuration.ConfigurationMessage;
import ch.twidev.swiftlogin.common.configuration.helpers.TranslationHandler;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BungeeTranslationHandler extends Configuration implements TranslationHandler<ProxiedPlayer, BaseComponent>{
    public BungeeTranslationHandler(Configuration translationConfiguration) {
        super(translationConfiguration.getConfigurationFiles());

        this.setKeys(translationConfiguration.getKeys());
    }

    @Override
    public BaseComponent formatComponent(ConfigurationMessage key, String playerName) {
        return TextComponent.fromLegacy(key.getTranslation(playerName));
    }

    @Override
    public BaseComponent formatComponentArgs(ConfigurationMessage key, String playerName, String... args) {
        return TextComponent.fromLegacy(key.getTranslation(playerName, args));
    }

    @Override
    public BaseComponent formatSimpleComponent(ConfigurationMessage key, String... args) {
        return TextComponent.fromLegacy(key.getSimpleTranslation(args));
    }

    @Override
    public void sendMessage(ConfigurationMessage configurationKey, ProxiedPlayer player) {
        player.sendMessage(
                this.formatComponent(configurationKey, player.getName())
        );
    }

    @Override
    public void sendMessage(ConfigurationMessage configurationMessage, ProxiedPlayer player, String... args) {
        player.sendMessage(
                this.formatComponentArgs(configurationMessage, player.getName(), args)
        );
    }
}
