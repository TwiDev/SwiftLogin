/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.velocity;

import ch.twidev.swiftlogin.api.SwiftLogin;
import ch.twidev.swiftlogin.api.crypto.CryptoProvider;
import ch.twidev.swiftlogin.api.exception.ServerSideNotSupportedException;
import ch.twidev.swiftlogin.common.SwiftLogger;
import ch.twidev.swiftlogin.common.SwiftLoginImplementation;
import ch.twidev.swiftlogin.common.SwiftProxy;
import ch.twidev.swiftlogin.common.configuration.Configuration;
import ch.twidev.swiftlogin.common.configuration.helpers.TranslationHandler;
import ch.twidev.swiftlogin.common.exception.PluginConfigurationException;
import ch.twidev.swiftlogin.common.servers.ServerType;
import co.aikar.commands.CommandManager;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;

import java.io.File;
import java.io.InputStream;
import java.util.logging.Logger;

@Plugin(
        id = "swiftlogin",
        name = "SwiftLogin",
        version = "1.0.0"
)
public class SwiftLoginVelocity implements SwiftProxy {

    private static SwiftLoginVelocity instance;

    private final ProxyServer proxyServer;
    private final Logger logger;

    private SwiftLoginImplementation swiftLoginImplementation;

    @Inject
    public SwiftLoginVelocity(ProxyServer server, Logger logger) {
        instance = this;

        this.proxyServer = server;
        this.logger = logger;

        this.getSwiftLogger().info("SwiftLogin is enabling...");
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        /*this.swiftLoginImplementation = new SwiftLoginImplementation(this, ServerSide.VELOCITY) {
            @Override
            public AuthorizationProvider getAuthorizationProvider() {
                return null;
            }
        };*/
    }

    public SwiftLoginImplementation getSwiftLoginImplementation() {
        return swiftLoginImplementation;
    }

    public ProxyServer getProxyServer() {
        return proxyServer;
    }

    public SwiftLogger getSwiftLogger() {
        return null;
    }

    @Override
    public ServerType getServerType() {
        return null;
    }

    public SwiftLoginImplementation getImplementation() {
        return swiftLoginImplementation;
    }


    @Override
    public InputStream getResourceFile(String filename) {
        return null;
    }

    @Override
    public File getDataFolder() {
        return null;
    }

    @Override
    public String translateAlternateColorCodes(char altColorChar, String textToTranslate) {
        return null;
    }

    @Override
    public void disable() {

    }

    @Override
    public void reloadConfiguration() throws PluginConfigurationException {

    }

    public static SwiftLoginVelocity getInstance() {
        return instance;
    }

    @Override
    public Configuration getMainConfiguration() {
        return null;
    }

    @Override
    public Configuration getTranslationConfiguration() {
        return null;
    }

    @Override
    public CommandManager<?, ?, ?, ?, ?, ?> getCommandProvider() {
        return null;
    }

    @Override
    public TranslationHandler<?,?> getTranslationHandler() {
        return null;
    }

    @Override
    public CryptoProvider getDefaultCryptoProvider() {
        return null;
    }
}
