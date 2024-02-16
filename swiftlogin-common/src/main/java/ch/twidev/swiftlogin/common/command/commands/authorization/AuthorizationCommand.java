/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.common.command.commands.authorization;

import ch.twidev.swiftlogin.api.players.SwiftPlayer;
import ch.twidev.swiftlogin.common.SwiftProxy;
import ch.twidev.swiftlogin.common.command.CommandBuilder;
import ch.twidev.swiftlogin.common.configuration.schema.TranslationConfiguration;
import ch.twidev.swiftlogin.common.exception.PluginCommandException;

public class AuthorizationCommand<C> extends CommandBuilder<C> {

    public AuthorizationCommand(SwiftProxy<C, ?, ?> swiftLoginPlugin) {
        super(swiftLoginPlugin);
    }

    public void isPlayerAuthorized(SwiftPlayer swiftPlayer) {
        if(swiftPlayer.isLogged()) {
            throw new PluginCommandException(
                    TranslationConfiguration.ERROR_ALREADY_LOGGED
            );
        }
    }
}
