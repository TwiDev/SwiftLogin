/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.common.configuration.helpers;

import ch.twidev.swiftlogin.common.configuration.ConfigurationMessage;

public interface TranslationHandler<P, C> {

    C formatComponentArgs(ConfigurationMessage key, String playerName, String... args);
    C formatComponent(ConfigurationMessage key, String playerName);
    C formatSimpleComponent(ConfigurationMessage key, String... args);

    void sendMessage(ConfigurationMessage configurationKey, P player);

    void sendMessage(ConfigurationMessage configurationKey, P player, String... args);
}
