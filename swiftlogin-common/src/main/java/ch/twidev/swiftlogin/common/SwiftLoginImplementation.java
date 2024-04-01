/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.common;

import ch.twidev.swiftlogin.api.SwiftLogin;
import ch.twidev.swiftlogin.api.authorization.AuthorizationProvider;
import ch.twidev.swiftlogin.api.crypto.CryptoProvider;
import ch.twidev.swiftlogin.api.crypto.CryptoType;
import ch.twidev.swiftlogin.api.servers.SwiftServerManager;
import ch.twidev.swiftlogin.api.utils.Nullable;
import ch.twidev.swiftlogin.common.command.CommandProvider;
import ch.twidev.swiftlogin.common.configuration.ConfigurationHandler;
import ch.twidev.swiftlogin.common.crypto.provider.BCryptProvider;
import ch.twidev.swiftlogin.common.crypto.provider.MessageDigestProvider;
import ch.twidev.swiftlogin.common.database.DriverConfig;
import ch.twidev.swiftlogin.common.database.redis.RedissonConnection;
import ch.twidev.swiftlogin.common.database.sql.SQLConnection;
import ch.twidev.swiftlogin.common.exception.PluginIssues;
import ch.twidev.swiftlogin.common.hooks.MojangManager;
import ch.twidev.swiftlogin.common.player.ProfileFactory;
import ch.twidev.swiftlogin.common.player.ProfileManager;
import ch.twidev.swiftlogin.common.servers.BackendServerHandler;
import ch.twidev.swiftlogin.common.servers.ServerType;
import ch.twidev.swiftlogin.common.util.RunningTask;

import java.util.HashMap;
import java.util.logging.Logger;

public abstract class SwiftLoginImplementation<P, S> extends SwiftLogin<P> {

    public static final String GIT_VERSION = "DEV";
    public static final String VERSION = "DEV";

    public static final String CHANNEL_MESSAGE = "swiftlogin:status";
    public static final String INIT_CHANNEL_MESSAGE = "swiftlogininit:init";

    private static SwiftLoginImplementation<?, ?> swiftLoginImplementation;

    private final HashMap<CryptoType, CryptoProvider> loadedCryptoProvider = new HashMap<>();
    private final SwiftLoginPlugin<P, S> swiftLoginPlugin;
    private final SQLConnection sqlConnection;
    private final RedissonConnection redissonConnection;

    private final ProfileManager profileManager;

    private final ProfileFactory profileFactory;
    private final MojangManager mojangManager;
    private final BCryptProvider bCryptProvider;
    private final MessageDigestProvider messageDigest512Provider;
    private final MessageDigestProvider messageDigest256Provider;
    private final BackendServerHandler<P, S> backendServerHandler;

    private final SwiftLogger logger;

    private final ServerType serverType;

    private CommandProvider<P> commandProvider;

    private boolean isMultiInstanceSupported = false;

    public SwiftLoginImplementation(SwiftLoginPlugin<P, S> swiftLoginPlugin, ConfigurationHandler configurationHandler, ServerType serverType, SwiftLogger logger, DriverConfig sqlConfig, @Nullable DriverConfig redis) {
        swiftLoginImplementation = this;

        this.swiftLoginPlugin = swiftLoginPlugin;

        this.logger = logger;
        this.serverType = serverType;

        this.sqlConnection = new SQLConnection(sqlConfig);

        if(redis != null) {
            this.redissonConnection = new RedissonConnection(redis);

            if(!redissonConnection.isConnected()) {
                isMultiInstanceSupported = false;

                logger.sendPluginError(PluginIssues.REDISSON_NOT_CONNECTED, "Cannot handle multi instances events, because redisson driver isn't connected. Please check your configuration file!");
            }else{
                isMultiInstanceSupported = true;
            }
        }else{
            this.redissonConnection = null;
        }

        this.profileManager = new ProfileManager(this.sqlConnection);
        this.profileFactory = new ProfileFactory(this.sqlConnection, profileManager);
        this.backendServerHandler = new BackendServerHandler<>(this, swiftLoginPlugin);
        this.mojangManager = new MojangManager(swiftLoginPlugin);
        this.bCryptProvider = new BCryptProvider(this);
        this.messageDigest256Provider = new MessageDigestProvider(CryptoType.SHA_256, this);
        this.messageDigest512Provider = new MessageDigestProvider(CryptoType.SHA_512, this);

        if(!sqlConnection.isConnected()) {
            swiftLoginPlugin.disable();
            return;
        }

        swiftLoginPlugin.onConfigurationLoaded(this, configurationHandler);

        logger.info("SwiftLogin has been successfully activated!");
    }

    public boolean isMultiInstanceSupported() {
        return isMultiInstanceSupported;
    }

    public void initCommands(Class<P> pClass) {
        if(!(swiftLoginPlugin instanceof SwiftProxy)) return;

        SwiftProxy<P, S, ?> swiftProxy = (SwiftProxy<P, S, ?>) this.swiftLoginPlugin;

        this.commandProvider = new CommandProvider<>(pClass, swiftProxy);

    }

    public static SwiftLoginImplementation<?,?> getInstance() {
        return swiftLoginImplementation;
    }

    public void onStop() {
        RunningTask runningTask = this.backendServerHandler.getCacheTask();
        if(runningTask != null) runningTask.stop();
    }

    public RedissonConnection getRedissonConnection() {
        return redissonConnection;
    }

    public MojangManager getMojangManager() {
        return mojangManager;
    }

    public ServerType getServerType() {
        return serverType;
    }

    public ProfileFactory getProfileFactory() {
        return profileFactory;
    }

    public SQLConnection getSQLConnection() {
        return sqlConnection;
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public ProfileManager getProfileManager() {
        return profileManager;
    }

    @Override
    public CryptoProvider getBCryptCryptoProvider() {
        return bCryptProvider;
    }

    @Override
    public CryptoProvider getMessageDigest256Provider() {
        return messageDigest256Provider;
    }

    @Override
    public CryptoProvider getMessageDigest512Provider() {
        return messageDigest512Provider;
    }

    @Override
    public SwiftServerManager getServerManager() {
        return backendServerHandler;
    }

    public CommandProvider<P> getCommandProvider() {
        return commandProvider;
    }

    public SwiftLoginPlugin<P, S> getSwiftLoginPlugin() {
        return swiftLoginPlugin;
    }

    public abstract ServerPlatformHandler<P, S> getServerPlatformHandler();
    public abstract PlatformHandler<P> getPlatformHandler();

    @Override
    public String translateAlternateColorCodes(char altColorChar, String textToTranslate) {
        return swiftLoginPlugin.translateAlternateColorCodes(altColorChar, textToTranslate);
    }

    public abstract AuthorizationProvider<P> getAuthorizationProvider();

    public HashMap<CryptoType, CryptoProvider> getLoadedCryptoProvider() {
        return loadedCryptoProvider;
    }
}
