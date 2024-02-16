/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.common.configuration;

import ch.twidev.swiftlogin.common.exception.PluginConfigurationException;

import java.util.HashMap;

public class ConfigurationHandler extends HashMap<ConfigurationFiles, Configuration> {

    public <T extends Configuration> T get(ConfigurationFiles configurationFiles) throws PluginConfigurationException {
        if(!this.containsKey(configurationFiles)) {
            throw new PluginConfigurationException(
                    String.format("cannot found file configuration %s in configuration handler", configurationFiles.getFileName())
            );
        }

        return (T) super.get(configurationFiles);
    }

}
