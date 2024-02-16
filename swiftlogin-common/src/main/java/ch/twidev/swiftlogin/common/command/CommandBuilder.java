/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.common.command;

import ch.twidev.swiftlogin.common.PlatformHandler;
import ch.twidev.swiftlogin.common.ServerPlatformHandler;
import ch.twidev.swiftlogin.common.SwiftProxy;
import ch.twidev.swiftlogin.common.configuration.Configuration;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;

public class CommandBuilder<C> extends BaseCommand {

    public final Configuration translation;

    public final SwiftProxy<C,?,?> swiftLoginPlugin;

    public final PlatformHandler<C> platformHandler;
    public final ServerPlatformHandler<C, ?> serverPlatformHandler;

    public CommandBuilder(SwiftProxy<C,?,?> swiftLoginPlugin) {
        this.swiftLoginPlugin = swiftLoginPlugin;

        this.platformHandler = swiftLoginPlugin.getImplementation().getPlatformHandler();
        this.serverPlatformHandler = swiftLoginPlugin.getImplementation().getServerPlatformHandler();
        this.translation = swiftLoginPlugin.getTranslationConfiguration();
    }

    public C getPlayer(CommandIssuer commandIssuer) {
        return getSwiftLoginPlugin().getImplementation().getPlatformHandler().getIssuer(commandIssuer);
    }

    public boolean isEnabled() {
        return true;
    }

    public SwiftProxy<C, ?, ?> getSwiftLoginPlugin() {
        return swiftLoginPlugin;
    }

    public ServerPlatformHandler<C, ?> getServerPlatformHandler() {
        return serverPlatformHandler;
    }

    public PlatformHandler<C> getPlayerPlatformHandler() {
        return platformHandler;
    }
}
