/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.common.command;

import ch.twidev.swiftlogin.api.players.SwiftPlayer;
import ch.twidev.swiftlogin.common.PlatformHandler;
import ch.twidev.swiftlogin.common.SwiftProxy;
import ch.twidev.swiftlogin.common.configuration.schema.MainConfiguration;
import ch.twidev.swiftlogin.common.configuration.schema.TranslationConfiguration;
import ch.twidev.swiftlogin.common.exception.PluginCommandException;
import ch.twidev.swiftlogin.common.player.Profile;
import co.aikar.commands.CommandContexts;
import co.aikar.commands.CommandManager;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.MessageKeys;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;

public class CommandProvider<P> {

    private static final String COMMAND_PACKAGE = "ch.twidev.swiftlogin.common.command.commands";

    public CommandManager<?,?,?,?,?,?> commandManager;

    public CommandProvider(Class<P> pClass, SwiftProxy<P,?,?> swiftLoginPlugin) {
        this.commandManager = swiftLoginPlugin.getCommandProvider();
        if(commandManager == null) return;

        PlatformHandler<P> platformHandler = swiftLoginPlugin.getImplementation().getPlatformHandler();

        CommandContexts<?> commandContexts = commandManager.getCommandContexts();
        commandContexts.registerIssuerAwareContext(Profile.class, commandExecutionContext -> {
            P p = platformHandler.getIssuer(commandExecutionContext.getIssuer());

            if (p == null)
                throw new InvalidCommandArgument(MessageKeys.NOT_ALLOWED_ON_CONSOLE, false);

            Profile player = swiftLoginPlugin.getImplementation().getProfileManager().getCachedLegacyProfileByUUID(
                    platformHandler.getPlayerUUID(p)
            ).orElse(null);

            if (player == null)
                throw new InvalidCommandArgument(MessageKeys.NOT_ALLOWED_ON_CONSOLE, false);

            return player;
        });

        commandContexts.registerIssuerAwareContext(pClass, commandExecutionContext -> {
            P player = platformHandler.getIssuer(commandExecutionContext.getIssuer());

            if (player == null)
                throw new InvalidCommandArgument(MessageKeys.NOT_ALLOWED_ON_CONSOLE, false);

            return player;
        });

        commandContexts.registerIssuerAwareContext(Object.class, commandExecutionContext -> {
            P player = platformHandler.getIssuer(commandExecutionContext.getIssuer());

            if (player == null)
                throw new InvalidCommandArgument(MessageKeys.NOT_ALLOWED_ON_CONSOLE, false);

            return player;
        });

        commandManager.setDefaultExceptionHandler((command, registeredCommand, sender, args, t) -> {
            if(t instanceof PluginCommandException) {
                PluginCommandException exception = (PluginCommandException) t;

                P issuer = platformHandler.getIssuer(sender);

                if(exception.hasReason()) {
                    platformHandler.sendMessage(issuer, exception.getReason());
                }else{
                    platformHandler.sendMessage(issuer, "&cAn unexpected exception occurred while performing command");
                }

                return true;
            } else {
                swiftLoginPlugin.getSwiftLogger().severe("An unexpected exception occurred while performing command, please attach the stacktrace below and report this issue.");
                t.printStackTrace();
                return false;
            }
        }, false);

        Reflections reflection = new Reflections(COMMAND_PACKAGE);
        reflection.getSubTypesOf(CommandBuilder.class)
                .stream()
                .filter(clazz -> clazz.isAnnotationPresent(CommandInfo.class))
                .filter(clazz -> clazz.getAnnotation(CommandInfo.class).isBackend() == swiftLoginPlugin.getServerType().isBackend())
                .map(clazz -> {
                    try {
                        return clazz.getConstructor(SwiftProxy.class).newInstance(swiftLoginPlugin);
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                             NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }
                })
                .filter(CommandBuilder::isEnabled)
                .forEach(commandBuilder -> {
                    commandManager.registerCommand(commandBuilder);
                });

    }

    public static void checkPasswordIsSecure(SwiftPlayer swiftPlayer, String rawPassword) {
        if(!rawPassword.matches(MainConfiguration.getSafePasswordPattern())) {
            throw new PluginCommandException(TranslationConfiguration.ERROR_UNSAFE_PASSWORD);
        }

        if (rawPassword.toLowerCase().contains(swiftPlayer.getRecentName().toLowerCase())) {
            throw new PluginCommandException(TranslationConfiguration.ERROR_UNSAFE_PASSWORD_CONTAINS_PLAYER_NAME);
        }

    }
}

