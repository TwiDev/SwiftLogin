/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.common.command.commands;

import ch.twidev.swiftlogin.common.SwiftLoginImplementation;
import ch.twidev.swiftlogin.common.SwiftProxy;
import ch.twidev.swiftlogin.common.command.CommandBuilder;
import ch.twidev.swiftlogin.common.command.CommandInfo;
import ch.twidev.swiftlogin.common.exception.PluginConfigurationException;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;

@CommandInfo(name = "swiftlogin")
@CommandAlias("swiftlogin")
public class PluginCommand<P> extends CommandBuilder<P> {

    public PluginCommand(SwiftProxy<P, ?, ?> swiftLoginPlugin) {
        super(swiftLoginPlugin);
    }

    @Subcommand("about")
    @Default
    public void onExecute(CommandIssuer commandIssuer) {
        commandIssuer.sendMessage("§a§lSwiftLogin §8§l» §7A plugin by §bTwiDev §8" + SwiftLoginImplementation.VERSION);
    }

    @Subcommand("reload")
    @CommandPermission("swiftlogin.reload")
    public void onReload(CommandIssuer commandIssuer) {
        try {
            swiftLoginPlugin.reloadConfiguration();

            commandIssuer.sendMessage("§a§lSwiftLogin §8§l» §aThe configuration has been reloaded");
        } catch (PluginConfigurationException e) {
            commandIssuer.sendMessage("§cAn error appeared while loading the configuration, look at the console");

            throw new RuntimeException(e);
        }
    }
}
