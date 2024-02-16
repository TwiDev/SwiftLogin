/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.common.configuration;

import ch.twidev.swiftlogin.common.SwiftLoginPlugin;
import ch.twidev.swiftlogin.common.configuration.helpers.ListHelper;
import ch.twidev.swiftlogin.common.configuration.node.ConfigurationNode;
import ch.twidev.swiftlogin.common.exception.PluginIssues;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

public class Configuration {

    public static final String UNKNOWN_TRANSLATION_PLACEHOLDER = "Unknown Translation";

    public static final ConfigurationMessage UNKNOWN_TRANSLATION_KEY;

    static {
        UNKNOWN_TRANSLATION_KEY = new ConfigurationMessage(
                "unknownTranslation",
                UNKNOWN_TRANSLATION_PLACEHOLDER
        );
    }

    private HashMap<String, ConfigurationKey<?>> keys = new HashMap<>();

    private final ConfigurationFiles configurationFiles;

    private boolean isPopulate = false;

    public Configuration(ConfigurationFiles configurationFiles) {
        this.configurationFiles = configurationFiles;
    }

    public HashMap<String, ConfigurationKey<?>> getKeys() {
        return keys;
    }

    public ConfigurationFiles getConfigurationFiles() {
        return configurationFiles;
    }

    public boolean isPopulate() {
        return isPopulate;
    }

    public void setPopulate(boolean populate) {
        isPopulate = populate;
    }

    public void setKeys(HashMap<String, ConfigurationKey<?>> keys) {
        this.keys = keys;
    }

    public static Configuration populate(SwiftLoginPlugin<?,?> swiftLoginPlugin, ConfigurationFiles configurationFiles, ConfigurationNode configurationNode) {
        Configuration configuration = new Configuration(configurationFiles);

        try {
            for (Field declaredField : configurationFiles.getClazz().getDeclaredFields()) {
                declaredField.setAccessible(true);

                if (declaredField.getType().equals(ConfigurationKey.class) || declaredField.getType().equals(ConfigurationMessage.class)) {
                    ConfigurationKey<?> configurationKey = (ConfigurationKey<?>) declaredField.get(null);
                    if(!configurationNode.getData().containsKey(configurationKey.getKey()) && !declaredField.isAnnotationPresent(OptionalKey.class)) {
                        swiftLoginPlugin.getSwiftLogger().sendRawPluginError(Level.WARNING, PluginIssues.CONFIGURATION_MISSING, PluginIssues.CONFIGURATION_MISSING.getMessage(), configurationKey.getKey(), configurationFiles.getFileName());

                        configuration.getKeys().put(configurationKey.getKey(), configurationKey);

                        continue;

                    }


                    Object data;

                    if(configurationNode.getData().containsKey(configurationKey.getKey())) {
                        data = configurationNode.getData().get(configurationKey.getKey()).getValue();
                    }else{
                        data = null;
                    }

                    if (configurationKey.getType().isEnum()) {
                        for (Object enumConstant : configurationKey.getType().getEnumConstants()) {
                            if (enumConstant.toString().equals(data.toString())) {
                                configurationKey.setObjectValue(enumConstant);
                                break;
                            }
                        }
                    } else if (configurationKey.getType().getSuperclass().equals(ListHelper.class)) {
                        ListHelper<?> listHelper = (ListHelper<?>) configurationKey.getDefaultValue().run();
                        ArrayList<?> list = (ArrayList<?>) data;

                        configurationKey.setObjectValue(ListHelper.fromType(listHelper.getType(), list));
                    } else {
                        configurationKey.setObjectValue(data);
                    }

                    configuration.getKeys().put(configurationKey.getKey(), configurationKey);
                }
            }

            configuration.setPopulate(true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return configuration;
    }

    public String getString(String key) {
        if(!keys.containsKey(key)) return "";

        return keys.get(key).getValue().toString();
    }

    public Integer getInt(String key) {
        if(!keys.containsKey(key)) return 0;

        try {
            return Integer.parseInt(
                    keys.get(key).getValue().toString()
            );
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public ConfigurationMessage getTranslationKey(String key) {
        if(!keys.containsKey(key)) return UNKNOWN_TRANSLATION_KEY;

        ConfigurationKey<?> configurationKey = keys.get(key);
        if(configurationKey instanceof ConfigurationMessage) {
            return (ConfigurationMessage) configurationKey;
        }else{
            return UNKNOWN_TRANSLATION_KEY;
        }
    }

}
