/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.common.command;

import ch.twidev.swiftlogin.common.configuration.ConfigurationMessage;
import ch.twidev.swiftlogin.common.configuration.schema.TranslationConfiguration;

public enum CommandConfiguration {

    PERMISSION_DENIED("acf-core.permission_denied", TranslationConfiguration.COMMAND_ERROR_PERMISSION_DENIED),
    PERMISSION_DENIED_PARAMETER("acf-core.permission_denied_parameter", TranslationConfiguration.COMMAND_ERROR_PERMISSION_DENIED),
    INVALID_SYNTAX("acf-core.invalid_syntax", TranslationConfiguration.COMMAND_ERROR_INVALID_SYNTAX);

    final String commandKey;
    final ConfigurationMessage configurationMessage;

    CommandConfiguration(String commandKey, ConfigurationMessage configurationMessage) {
        this.commandKey = commandKey;
        this.configurationMessage = configurationMessage;
    }

    public String getCommandKey() {
        return commandKey;
    }

    public ConfigurationMessage getConfigurationMessage() {
        return configurationMessage;
    }
}
