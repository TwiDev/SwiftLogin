/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.bungee;

import ch.twidev.swiftlogin.api.authorization.AuthorizationProvider;
import ch.twidev.swiftlogin.api.crypto.CryptoProvider;
import ch.twidev.swiftlogin.api.event.SwiftEventProvider;
import ch.twidev.swiftlogin.bungee.authorization.BungeeAuthorizationProvider;
import ch.twidev.swiftlogin.bungee.listeners.ActionListener;
import ch.twidev.swiftlogin.bungee.listeners.LoginListener;
import ch.twidev.swiftlogin.bungee.listeners.ServersListener;
import ch.twidev.swiftlogin.common.*;
import ch.twidev.swiftlogin.common.configuration.Configuration;
import ch.twidev.swiftlogin.common.configuration.ConfigurationFiles;
import ch.twidev.swiftlogin.common.configuration.ConfigurationHandler;
import ch.twidev.swiftlogin.common.configuration.helpers.TranslationHandler;
import ch.twidev.swiftlogin.common.configuration.schema.BackendConfiguration;
import ch.twidev.swiftlogin.common.configuration.schema.MainConfiguration;
import ch.twidev.swiftlogin.common.database.DriverConfig;
import ch.twidev.swiftlogin.common.database.DriverType;
import ch.twidev.swiftlogin.common.database.redis.RedissonConnection;
import ch.twidev.swiftlogin.common.events.AbstractEventsProvider;
import ch.twidev.swiftlogin.common.exception.PluginConfigurationException;
import ch.twidev.swiftlogin.common.servers.ServerType;
import co.aikar.commands.BungeeCommandManager;
import co.aikar.commands.CommandManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;

import java.io.InputStream;
import java.util.UUID;

public class SwiftLoginBungee extends Plugin implements SwiftProxy<ProxiedPlayer, ServerInfo, BaseComponent> {

    private static SwiftLoginBungee instance;
    private SwiftLogger swiftLogger;
    private SwiftLoginImplementation<ProxiedPlayer, ServerInfo> swiftLoginImplementation;
    private AbstractEventsProvider<ProxiedPlayer> eventsProvider;
    private Configuration mainConfiguration;
    private BungeeTranslationHandler translationConfiguration;
    private BungeeAuthorizationProvider bungeeAuthorizationProvider;
    private BungeecordPlatformHandler bungeecordPlatformHandler;
    private BungeecordServerPlatformHandler bungeecordServerPlatformHandler;
    private CryptoProvider cryptoProvider;
    private CommandManager<?,?,?,?,?,?> commandManager;

    @Override
    public void onEnable() {
        instance = this;

        this.getDescription().setVersion(
                SwiftLoginImplementation.VERSION
        );

        this.swiftLogger = new SwiftLogger(super.getLogger());

        this.swiftLogger.sendEnableMessage();

        final ConfigurationHandler configs = this.initConfiguration();

        try {
            this.mainConfiguration = configs.get(ConfigurationFiles.CONFIG);
            this.translationConfiguration = new BungeeTranslationHandler(
                    configs.get(ConfigurationFiles.LANG));

            this.bungeeAuthorizationProvider = new BungeeAuthorizationProvider(this);
            this.bungeecordPlatformHandler = new BungeecordPlatformHandler(this);
            this.bungeecordServerPlatformHandler = new BungeecordServerPlatformHandler(this);
            this.commandManager = new BungeeCommandManager(this);

            RedissonConnection redissonConnection = null;

            if(BackendConfiguration.isMultiInstanceSupported()) {
                DriverConfig redisConfig = new DriverConfig(
                        DriverType.REDISSON,
                        BackendConfiguration.getRedissonHost(),
                        BackendConfiguration.getRedissonPort(),
                        BackendConfiguration.getRedissonPassword()
                );

                redissonConnection = new RedissonConnection(redisConfig);
            }

            this.eventsProvider = new AbstractEventsProvider<>(ProxiedPlayer.class, redissonConnection) {
                @Override
                public String getUniqueIdentifier(ProxiedPlayer player) {
                    return bungeecordPlatformHandler.getPlayerUUID(player).toString();
                }

                @Override
                public ProxiedPlayer getFromUniqueIdentifier(String s) {
                    return bungeecordPlatformHandler.getPlayerFromUUID(UUID.fromString(s));
                }
            };

            PluginManager pluginManager = this.getProxy().getPluginManager();

            this.swiftLoginImplementation = new SwiftLoginImplementation<>(this, configs, ServerType.BUNGEE, this.getSwiftLogger(),
                    new DriverConfig(
                            DriverType.MYSQL,
                            MainConfiguration.getMysqlHost(),
                            MainConfiguration.getMysqlDatabase(),
                            MainConfiguration.getMysqlPort(),
                            MainConfiguration.getMysqlUser(),
                            MainConfiguration.getMysqlPassword()), redissonConnection) {

                @Override
                public CryptoProvider getCurrentCryptoProvider() {
                    return cryptoProvider;
                }

                @Override
                public SwiftEventProvider<ProxiedPlayer> getEventProvider() {
                    return eventsProvider;
                }

                @Override
                public ServerPlatformHandler<ProxiedPlayer, ServerInfo> getServerPlatformHandler() {
                    return bungeecordServerPlatformHandler;
                }

                @Override
                public PlatformHandler<ProxiedPlayer> getPlatformHandler() {
                    return bungeecordPlatformHandler;
                }

                @Override
                public AuthorizationProvider<ProxiedPlayer> getAuthorizationProvider() {
                    return bungeeAuthorizationProvider;
                }
            };

            this.swiftLoginImplementation.initCommands(ProxiedPlayer.class);

            pluginManager.registerListener(this, new LoginListener(this));
            pluginManager.registerListener(this, new ActionListener(this));
            pluginManager.registerListener(this, new ServersListener(this));

            this.getProxy().registerChannel(SwiftLoginImplementation.CHANNEL_MESSAGE);
        } catch (PluginConfigurationException e) {
            this.getSwiftLogger().severe("Configuration file cannot be loaded? Maybe are missing ...");

            throw new RuntimeException(e);
        }

        super.onEnable();
    }

    @Override
    public void reloadConfiguration() throws PluginConfigurationException {
        final ConfigurationHandler configs = this.initConfiguration();

        this.mainConfiguration = configs.get(ConfigurationFiles.CONFIG);
        this.translationConfiguration = new BungeeTranslationHandler(
                configs.get(ConfigurationFiles.LANG));
    }

    @Override
    public void onDisable() {
        if(this.swiftLoginImplementation != null) {
            this.swiftLoginImplementation.onStop();
        }
    }

    @Override
    public void disable() {
        this.getSwiftLogger().info("An error occurred during the installation of SwiftLogin, plugin cannot be enabled, the server will shut down");

        ProxyServer.getInstance().stop();
    }

    @Override
    public void onConfigurationLoaded(SwiftLoginImplementation<ProxiedPlayer,ServerInfo> swiftLoginImplementation, ConfigurationHandler configurationHandler) {
        this.cryptoProvider = loadCryptoProvider(swiftLoginImplementation);
    }

    @Override
    public SwiftLoginImplementation<ProxiedPlayer, ServerInfo> getImplementation() {
        return swiftLoginImplementation;
    }

    @Override
    public TranslationHandler<ProxiedPlayer, BaseComponent> getTranslationHandler() {
        return translationConfiguration;
    }

    @Override
    public CryptoProvider getDefaultCryptoProvider() {
        return cryptoProvider;
    }

    @Override
    public CommandManager<?, ?, ?, ?, ?, ?> getCommandProvider() {
        return commandManager;
    }

    @Override
    public SwiftLogger getSwiftLogger() {
        return swiftLogger;
    }

    @Override
    public ServerType getServerType() {
        return ServerType.BUNGEE;
    }

    @Override
    public Configuration getMainConfiguration() {
        return mainConfiguration;
    }

    @Override
    public BungeeTranslationHandler getTranslationConfiguration() {
        return translationConfiguration;
    }

    @Override
    public InputStream getResourceFile(String filename) {
        return super.getResourceAsStream(filename);
    }

    @Override
    public String translateAlternateColorCodes(char altColorChar, String textToTranslate) {
        return ChatColor.translateAlternateColorCodes(altColorChar, textToTranslate);
    }

    public static SwiftLoginBungee getInstance() {
        return instance;
    }
}
