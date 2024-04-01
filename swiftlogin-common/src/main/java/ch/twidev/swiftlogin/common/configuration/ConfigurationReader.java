/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.common.configuration;

import ch.twidev.swiftlogin.common.SwiftLoginImplementation;
import ch.twidev.swiftlogin.common.SwiftLoginPlugin;
import ch.twidev.swiftlogin.common.configuration.node.ConfigurationNode;
import ch.twidev.swiftlogin.common.configuration.yaml.SwiftYaml;
import ch.twidev.swiftlogin.common.exception.PluginConfigurationException;
import ch.twidev.swiftlogin.common.exception.PluginIssues;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ConfigurationReader {

    public ConfigurationReader() {
    }

    private static ConfigurationNode save(File dataFolder, ConfigurationFiles configurationFiles) {
        if (dataFolder == null || configurationFiles == null) return null;

        try {
            File outFile = new File(dataFolder, configurationFiles.getFileName());

            List<ConfigurationKey<?>> configurationKeys = new ArrayList<>();
            for (Field declaredField : configurationFiles.getClazz().getDeclaredFields()) {
                declaredField.setAccessible(true);

                if (declaredField.getType().equals(ConfigurationKey.class) || declaredField.getType().equals(ConfigurationMessage.class)) {
                    ConfigurationKey<?> configurationKey = (ConfigurationKey<?>) declaredField.get(null);

                    configurationKeys.add(configurationKey);
                }
            }

            String description = "";
            if(configurationFiles.getClazz().isAnnotationPresent(ConfigurationHeader.class)){
                description = configurationFiles.getClazz().getAnnotation(ConfigurationHeader.class).description().replace("{version}", SwiftLoginImplementation.VERSION);
            }

            return SwiftYaml.saveConfiguration(
                    description,
                    outFile,
                    configurationKeys
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Configuration loadConfiguration(SwiftLoginPlugin<?,?> swiftLoginPlugin, File dataFolder, ConfigurationFiles configurationFiles) throws Exception {
        if (configurationFiles == null || configurationFiles.getClazz() == null) {
            throw new IllegalArgumentException("ResourcePath cannot be null or empty");
        }
        final String name = configurationFiles.getFileName();

        if (!dataFolder.exists() && !dataFolder.mkdir()) {
            swiftLoginPlugin.getSwiftLogger().severe("Failed to make directory");
            throw new PluginConfigurationException("Cannot access through the plugin configuration directory");
        }

        File outFile = new File(dataFolder, name);

        if (!outFile.exists()) {
            swiftLoginPlugin.getSwiftLogger().info("The resourcePath was not found for "+ configurationFiles.getFileName() + " configuration, creation in progress...");
            boolean wasCreated = outFile.createNewFile();
            if(wasCreated && outFile.exists()) {
                if(configurationFiles != ConfigurationFiles.LANG)
                    swiftLoginPlugin.getSwiftLogger().sendRawPluginInfo("A new configuration file %s has been created\nplease change the default values so that the plugin can activate.", configurationFiles.getFileName());

                return Configuration.populate(swiftLoginPlugin, configurationFiles,
                        save(dataFolder, configurationFiles));
            }else{
                swiftLoginPlugin.getSwiftLogger().sendPluginError(PluginIssues.CANNOT_CREATE_CONFIG,"Unable to copy file.");
                throw new PluginConfigurationException("Unable to copy file");
            }
        }else{
            return Configuration.populate(swiftLoginPlugin, configurationFiles,
                    SwiftYaml.read(outFile));
        }
    }
}
