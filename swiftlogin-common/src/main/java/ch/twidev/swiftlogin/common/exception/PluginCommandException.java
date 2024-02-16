/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.common.exception;

import ch.twidev.swiftlogin.common.configuration.ConfigurationMessage;

public class PluginCommandException extends RuntimeException {

    private final ConfigurationMessage reason;

    public PluginCommandException() {
        this.reason = null;
    }

    public PluginCommandException(ConfigurationMessage reason) {
        this.reason = reason;
    }

    public boolean hasReason() {
        return reason != null;
    }

    public ConfigurationMessage getReason() {
        return reason;
    }
}
