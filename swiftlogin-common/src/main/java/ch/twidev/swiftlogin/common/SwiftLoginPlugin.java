/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.common;

import ch.twidev.swiftlogin.api.SwiftLoginProvider;
import ch.twidev.swiftlogin.common.configuration.Configuration;
import ch.twidev.swiftlogin.common.configuration.ConfigurationFiles;
import ch.twidev.swiftlogin.common.configuration.ConfigurationHandler;
import ch.twidev.swiftlogin.common.configuration.ConfigurationReader;
import ch.twidev.swiftlogin.common.exception.PluginConfigurationException;
import ch.twidev.swiftlogin.common.servers.ServerType;

import java.io.File;
import java.io.InputStream;

public interface SwiftLoginPlugin<P, S> extends SwiftLoginProvider<P> {

    SwiftLogger getSwiftLogger();

    ServerType getServerType();

    InputStream getResourceFile(String filename);

    File getDataFolder();

    String translateAlternateColorCodes(char altColorChar, String textToTranslate);

    void disable();

    void reloadConfiguration() throws PluginConfigurationException;

    default ConfigurationHandler initConfiguration() {
        ServerType serverSide = this.getServerType();

        final ConfigurationHandler configs = new ConfigurationHandler();

        for (ConfigurationFiles value : ConfigurationFiles.values()) {
            if(value.isBackend() != serverSide.isBackend()) continue;

            try {
                Configuration configuration = ConfigurationReader.loadConfiguration(this, this.getDataFolder(), value);

                if(configuration == null || !configuration.isPopulate()) {
                    throw new PluginConfigurationException("Cannot register " + value.getFileName() + " because the configuration is null or isn't populate. Skipping...");
                }

                configs.put(value, configuration);

            } catch (Exception e) {
                this.getSwiftLogger().severe("An error occurred while reading file configuration.");

                e.printStackTrace();
            }
        }

        if(this.getImplementation() != null)
            this.onConfigurationLoaded((SwiftLoginImplementation<P, S>) this.getImplementation(), configs);

        this.getSwiftLogger().info("File configuration read successfully!");

        return configs;
    }

    default void onConfigurationLoaded(SwiftLoginImplementation<P,S> swiftLoginImplementation, ConfigurationHandler configurationHandler) {}

}
