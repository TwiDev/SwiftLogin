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
import ch.twidev.swiftlogin.common.util.ReturnCallback;

public class ConfigurationMessage extends ConfigurationKey<String> {

    public ConfigurationMessage(String key, String defaultValue) {
        super(String.class, key, defaultValue);
    }

    public ConfigurationMessage(Class<String> type, String key, String defaultValue) {
        super(type, key, defaultValue);
    }

    public ConfigurationMessage(Class<String> type, String key, String defaultValue, String comment) {
        super(type, key, defaultValue, comment);
    }

    public ConfigurationMessage(Class<String> type, String key, ReturnCallback<String> defaultValue) {
        super(type, key, defaultValue);
    }

    public ConfigurationMessage(Class<String> type, String key, ReturnCallback<String> defaultValue, String comment) {
        super(type, key, defaultValue, comment);
    }

    public ConfigurationMessage(String key, String defaultValue, String comment) {
        super(String.class, key, defaultValue, comment);
    }

    public ConfigurationMessage(String key, ReturnCallback<String> defaultValue) {
        super(String.class, key, defaultValue);
    }

    public ConfigurationMessage(String key, ReturnCallback<String> defaultValue, String comment) {
        super(String.class, key, defaultValue, comment);
    }

    public String getSimpleTranslation(String... args) {
        return this.getTranslation(null, args);
    }

    public String getTranslation(String playerName, String... args) {
        if (this.getValue() == null) return Configuration.UNKNOWN_TRANSLATION_PLACEHOLDER;

        String translation = this.getValue();

        if (playerName != null) {
            translation = translation.replaceAll("%name%", playerName);
        }
        translation = SwiftLoginImplementation.getInstance().getSwiftLoginPlugin().translateAlternateColorCodes('&', translation);

        if (args != null) {
            for (int i = 0; i < args.length; i += 2) {
                translation = translation.replace(args[i], args[i + 1]);
            }
        }

        return translation;
    }

}
