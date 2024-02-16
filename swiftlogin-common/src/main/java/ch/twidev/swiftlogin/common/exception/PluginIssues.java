/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.common.exception;

public enum PluginIssues {

    TOKEN_NOT_MATCH(3, "The tokens in the configuration files do not match. Player connections to the server cannot be verified and accepted"),
    CONFIGURATION_MISSING(2, "The configuration key %s is missing in the configuration file %s.\nThe default value was applied for this configuration."),
    CRYPTO_ALGORITHM_NOT_FOUND(20, "Impossible to find the %s encryption algorithm that you entered in the plugin configuration, this encryption is not supported by Swiftlogin."),
    CRYPTO_ALGORITHM_NOT_SUPPORTED(21, "Impossible to load the %s encryption algorithm provider that you entered in the plugin configuration, this encryption is not supported by Swiftlogin because of a problem on your installation."),
    NO_SERVERS_FOUND(9),
    CANNOT_CREATE_CONFIG(19),
    DATABASE_ACCESS_DENIED(2, "Access denied to the SQL database, impossible to connect to the database\nplease check the access provided in the configuration file"),
    REDISSON_NOT_CONNECTED(51),
    NONE(-1);

    final int code;

    String message = null;

    PluginIssues(int code) {
        this.code = code;
    }

    PluginIssues(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
