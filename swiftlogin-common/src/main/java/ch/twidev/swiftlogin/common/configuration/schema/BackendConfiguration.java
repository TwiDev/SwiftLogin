/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.common.configuration.schema;

import ch.twidev.swiftlogin.common.configuration.ConfigurationHeader;
import ch.twidev.swiftlogin.common.configuration.ConfigurationKey;
import ch.twidev.swiftlogin.common.configuration.ConfigurationSide;
import ch.twidev.swiftlogin.common.configuration.OptionalKey;
import ch.twidev.swiftlogin.common.configuration.helpers.StringList;
import ch.twidev.swiftlogin.common.util.SecureTokenGenerator;

@ConfigurationSide(isBackend = true)
@ConfigurationHeader(description = "\n" +
        "  _________       .__  _____  __    .____                 .__        \n" +
        " /   _____/_  _  _|__|/ ____\\/  |_  |    |    ____   ____ |__| ____  \n" +
        " \\_____  \\\\ \\/ \\/ /  \\   __\\\\   __\\ |    |   /  _ \\ / ___\\|  |/    \\ \n" +
        " /        \\\\     /|  ||  |   |  |   |    |__(  <_> ) /_/  >  |   |  \\\n" +
        "/_______  / \\/\\_/ |__||__|   |__|   |_______ \\____/\\___  /|__|___|  /\n" +
        "        \\/                                  \\/    /_____/         \\/ \n" +
        "\nConfiguration version: {version}\n"
)
public class BackendConfiguration{

    public static final ConfigurationKey<String> TOKEN = new ConfigurationKey<>(
            String.class,
            "accessToken",
            SecureTokenGenerator::nextToken,
            "The access token allows you to verify the connection of your players and avoid connections to backend servers" +
                    "\nwithout going through the bungeecord and bypassing security" +
                    "\nThe default token is randomly generated but if you want to modify it, choose a long and random token for the security of your server" +
                    "\nImportant : The token must be the same on each server where SwiftLogin is installed, so choose a token and copy it to the other configurations"
    );

    public static final ConfigurationKey<String> MYSQL_HOST = new ConfigurationKey<>(
            String.class,
            "mysqlHost",
            "localhost",
            "This section is used for database configuration, swiftlogin currently supports only MySQL/MariaDB databases." +
                    "\nErrors during plugin activation when the plugin connects to the database are always caused by misconfiguration or connection issues with your database.\n" +
                    "\nIf you have a problem reporting it not to the plugin author, please see this wiki page to resolve your issue" +
                    "\nhttps://github.com/TwiDev/swiftlogin/wiki#database"
    );

    public static final ConfigurationKey<Integer> MYSQL_PORT = new ConfigurationKey<>(
            Integer.class,
            "mysqlPort",
            3306
    );

    public static final ConfigurationKey<String> MYSQL_DATABASE = new ConfigurationKey<>(
            String.class,
            "mysqlDatabase",
            "swiftlogin"
    );

    public static final ConfigurationKey<String> MYSQL_USER = new ConfigurationKey<>(
            String.class,
            "mysqlUser",
            "root"
    );

    public static final ConfigurationKey<String> MYSQL_PASSWORD = new ConfigurationKey<>(
            String.class,
            "mysqlPassword",
            ""
    );

    public static final ConfigurationKey<StringList> MYSQL_PROPERTIES = new ConfigurationKey<>(
            StringList.class,
            "mysqlProperties",
            new StringList().append("useSSL=true").append("verifyServerCertificate=true")
    );

    public static final ConfigurationKey<Integer> POOL_SIZE = new ConfigurationKey<>(
            Integer.class,
            "poolSize",
            10,
            "These settings are used to configure the storage pool" +
                    "\nPlease don't change them if you don't know what you are doing."
    );

    public static final ConfigurationKey<Integer> POOL_IDLE = new ConfigurationKey<>(
            Integer.class,
            "poolIdle",
            10
    );

    public static final ConfigurationKey<Integer> POOL_TIMEOUT = new ConfigurationKey<>(
            Integer.class,
            "poolTimeout",
            5000
    );

    public static final ConfigurationKey<Integer> POOL_LIFETIME = new ConfigurationKey<>(
            Integer.class,
            "poolLifetime",
            1800000
    );

    public static final ConfigurationKey<Boolean> PREVENT_INTERACTION = new ConfigurationKey<>(
            Boolean.class,
            "preventInteraction",
            true,
            "Should players have limited interactions with the servers when they are not authorized."
    );

    public static final ConfigurationKey<Boolean> PREVENT_MOVEMENT = new ConfigurationKey<>(
            Boolean.class,
            "preventMovement",
            true,
            "Should players have limited movements with the servers when they are not authorized"
    );

    public static final ConfigurationKey<String> SPAWN_LOCATION = new ConfigurationKey<>(
            String.class,
            "spawnLocation",
            "",
            "Sets the location to which unauthorized players are teleported after joining a limbo server." +
                    "\nThe format of the location is world/x/y/z/yaw/pitch." +
                    "\nIf you want to disable it leave empty."
    );

    public static final ConfigurationKey<Integer> CAPTCHA_MAP_SLOT = new ConfigurationKey<>(
            Integer.class,
            "captchaMapSlot",
            4,
            "A slot in the hot-bar where unregistered players get the captcha map." +
                    "\nCaptcha mode must be activated on the main proxy configuration."
    );

    public static final ConfigurationKey<Boolean> multiInstanceSupport = new ConfigurationKey<>(
            Boolean.class,
            "multiInstanceSupport",
            false,
            "By activating this option the SwiftLogin API will be available on this server, by default the backend servers cannot access the API, " +
                    "\nonly if the multiInstanceSupport option is activated on each of the servers on which SwiftLogin plugin is installed" +
                    "\n" +
                    "For more information : https://github.com/TwiDev/swiftlogin/wiki#multinstance" +
                    "\n" +
                    "\nTHIS OPTION REQUIRES CONFIGURING THE REDIS SECTION BELOW"
    );

    @OptionalKey
    public static final ConfigurationKey<String> redisHost = new ConfigurationKey<>(
            String.class,
            "redisHost",
            "127.0.0.1",
            "This section is used to configure the redis database used to transfer SwiftLogin data across multiple instances such as multiple proxies or backend servers" +
                    "\nErrors during plugin activation when the plugin connects to the database are always caused by misconfiguration or connection issues with " +
                    "\nyour database. If you have a problem reporting it not to the plugin author, please see this wiki page to resolve your issue" +
                    "\nhttps://github.com/TwiDev/swiftlogin/wiki#database-redis" +
                    "\n" +
                    "\nYou DO NOT NEED to configure this part to use SwiftLogin normally, this option is completely optional."
    );

    @OptionalKey
    public static final ConfigurationKey<Integer> redisPort = new ConfigurationKey<>(
            Integer.class,
            "redisPort",
            6379
    );

    @OptionalKey
    public static final ConfigurationKey<String> redisPassword = new ConfigurationKey<>(
            String.class,
            "redisPassword",
            ""
    );

    public static String getRedisHost() {
        return redisHost.getValue();
    }

    public static int getRedisPort() {
        return redisPort.getValue();
    }

    public static String getRedisPassword() {
        return redisPassword.getValue();
    }

    public static String getToken() {
        return TOKEN.getValue();
    }

    public static String getMysqlHost() {
        return MYSQL_HOST.getValue();
    }

    public static Integer getMysqlPort() {
        return MYSQL_PORT.getValue();
    }

    public static String getMysqlDatabase() {
        return MYSQL_DATABASE.getValue();
    }

    public static String getMysqlUser() {
        return MYSQL_USER.getValue();
    }

    public static String getMysqlPassword() {
        return MYSQL_PASSWORD.getValue();
    }

    public static StringList getMysqlProperties() {
        return MYSQL_PROPERTIES.getValue();
    }

    public static Integer getPoolSize() {
        return POOL_SIZE.getValue();
    }

    public static Integer getPoolIdle() {
        return POOL_IDLE.getValue();
    }

    public static Integer getPoolTimeout() {
        return POOL_TIMEOUT.getValue();
    }

    public static Integer getPoolLifetime() {
        return POOL_LIFETIME.getValue();
    }

    public static boolean getPreventInteraction() {
        return PREVENT_INTERACTION.getValue();
    }

    public static boolean getPreventMovement() {
        return PREVENT_MOVEMENT.getValue();
    }

    public static String getSpawnLocation() {
        return SPAWN_LOCATION.getValue();
    }

    public static boolean isMultiInstanceSupported() {
        return multiInstanceSupport.getValue();
    }


}
