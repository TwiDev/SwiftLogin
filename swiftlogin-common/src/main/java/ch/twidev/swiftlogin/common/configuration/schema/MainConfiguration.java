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
import ch.twidev.swiftlogin.common.configuration.helpers.StringList;
import ch.twidev.swiftlogin.common.servers.ServerComparatorStrategy;
import ch.twidev.swiftlogin.common.util.SecureTokenGenerator;

@ConfigurationSide(isBackend = false)
@ConfigurationHeader(description = "\n" +
        "  _________       .__  _____  __    .____                 .__        \n" +
        " /   _____/_  _  _|__|/ ____\\/  |_  |    |    ____   ____ |__| ____  \n" +
        " \\_____  \\\\ \\/ \\/ /  \\   __\\\\   __\\ |    |   /  _ \\ / ___\\|  |/    \\ \n" +
        " /        \\\\     /|  ||  |   |  |   |    |__(  <_> ) /_/  >  |   |  \\\n" +
        "/_______  / \\/\\_/ |__||__|   |__|   |_______ \\____/\\___  /|__|___|  /\n" +
        "        \\/                                  \\/    /_____/         \\/ \n")
public class MainConfiguration {


    public static final ConfigurationKey<String> token = new ConfigurationKey<>(
            String.class,
            "accessToken",
            SecureTokenGenerator::nextToken,
            "The access token allows you to verify the connection of your players and avoid connections to backend servers" +
                    "\nwithout going through the bungeecord and bypassing security" +
                    "\nThe default token is randomly generated but if you want to modify it, choose a long and random token for the security of your server" +
                    "\nImportant : The token must be the same on each server where SwiftLogin is installed, so choose a token and copy it to the other configurations"
    );

    public static final ConfigurationKey<StringList> hostnames = new ConfigurationKey<>(
            StringList.class,
            "authorizedHostnames",
            new StringList(),
            "This field specifies a list of authorized hostnames from which players are allowed to connect. " +
                    "\nExample: [\"example.com:25565\", \"subdomain.example.net\"]." +
                    "\nIf you want to disable this option please set value to \"[]\"."
    );

    public static final ConfigurationKey<String> mysqlHost = new ConfigurationKey<>(
            String.class,
            "mysqlHost",
            "localhost",
            "This section is used for database configuration, swiftlogin currently supports only MySQL/MariaDB databases." +
                    "\nErrors during plugin activation when the plugin connects to the database are always caused by misconfiguration or connection issues with your database.\n" +
                    "\nIf you have a problem reporting it not to the plugin author, please see this wiki page to resolve your issue" +
                    "\nhttps://github.com/TwiDev/swiftlogin/wiki#database"
    );

    public static final ConfigurationKey<Integer> mysqlPort = new ConfigurationKey<>(
            Integer.class,
            "mysqlPort",
            3306
    );

    public static final ConfigurationKey<String> mysqlDatabase = new ConfigurationKey<>(
            String.class,
            "mysqlDatabase",
            "swiftlogin"
    );

    public static final ConfigurationKey<String> mysqlUser = new ConfigurationKey<>(
            String.class,
            "mysqlUser",
            "root"
    );

    public static final ConfigurationKey<String> mysqlPassword = new ConfigurationKey<>(
            String.class,
            "mysqlPassword",
            ""
    );

    public static final ConfigurationKey<StringList> mysqlProperties = new ConfigurationKey<>(
            StringList.class,
            "mysqlProperties",
            new StringList().append("useSSL=true").append("verifyServerCertificate=true")
    );

    public static final ConfigurationKey<Integer> poolSize = new ConfigurationKey<>(
            Integer.class,
            "poolSize",
            10,
            "These settings are used to configure the storage pool" +
                    "\nPlease don't change them if you don't know what you are doing."
    );

    public static final ConfigurationKey<Integer> poolIdle = new ConfigurationKey<>(
            Integer.class,
            "poolIdle",
            10
    );

    public static final ConfigurationKey<Integer> poolTimeout = new ConfigurationKey<>(
            Integer.class,
            "poolTimeout",
            5000
    );

    public static final ConfigurationKey<Integer> poolLifetime = new ConfigurationKey<>(
            Integer.class,
            "poolLifetime",
            1800000
    );

    public static final ConfigurationKey<StringList> mainServers = new ConfigurationKey<>(
            StringList.class,
            "mainServers",
            new StringList().append("main"),
            "The main servers are on which authorized players join the server, a server is chosen according to the mitigation configuration." +
                    "\nBy default the number of players between the servers are balanced automatically" +
                    "\nSo you can configure multiple main servers, example: [main-1, main-2, main-3], ..."
    );

    public static final ConfigurationKey<StringList> limboServers = new ConfigurationKey<>(
            StringList.class,
            "limboServers",
            new StringList().append("limbo"),
            "The limbo servers are on which unauthorized players join the server, a server is chosen according to the mitigation configuration." +
                    "\nBy default the number of players between the servers are balanced automatically" +
                    "\nSo you can configure multiple limbo servers, example: [limbo-1, limbo-2, limbo-3], ..."
    );

    public static final ConfigurationKey<ServerComparatorStrategy> serverStrategy = new ConfigurationKey<>(
            ServerComparatorStrategy.class,
            "serverStrategy",
            ServerComparatorStrategy.MITIGATION,
            "Configures how players are distributed across main and limbo servers" +
                    "\nby default the number of players between the servers are balanced automatically" +
                    "\nit is not recommended to change this option, other mitigation configurations are very poorly supported"
    );

    public static final ConfigurationKey<Boolean> wrongPasswordDisconnection = new ConfigurationKey<>(
            Boolean.class,
            "wrongPasswordDisconnection",
            false,
            "Should the plugin disconnect players who enter a wrong password during login?"
    );

    public static final ConfigurationKey<Boolean> limboDisconnectRedirection = new ConfigurationKey<>(
            Boolean.class,
            "limboDisconnectRedirection",
            true
    );

    public static final ConfigurationKey<Boolean> lastServerRedirection = new ConfigurationKey<>(
            Boolean.class,
            "lastServerRedirection",
            false,
            "Should the plugin redirect a player to their last server where they were before they left the server."
    );

    public static final ConfigurationKey<Boolean> verifyCaptchaCode = new ConfigurationKey<>(
            Boolean.class,
            "verifyCaptchaCode",
            true,
            "Should players enter a valid captcha before registering this helps prevent bots from authorizing themselves and entering the main servers." +
                    "\nIn the configuration of the backend servers you can modify the slot in which the player receives the captcha"
    );

    public static final ConfigurationKey<Boolean> confirmPassword = new ConfigurationKey<>(
            Boolean.class,
            "confirmPassword",
            false,
            "Should players require to confirm a password in the register, change password and create password commands." +
                    "\nIf you modify this option don't forget to change the authorization request messages in the translation file."
    );

    public static final ConfigurationKey<Integer> maximumProfilesPerAddress = new ConfigurationKey<>(
            Integer.class,
            "maximumProfilesPerAddress",
            2,
            "Sets the maximum amount of accounts that can be registered from the same IP address." +
                    "\nIf you want to disable this option set the value to 0"
    );

    public static final ConfigurationKey<Integer> maximumAuthorisationTime = new ConfigurationKey<>(
            Integer.class,
            "maximumAuthorisationTime",
            90,
            "Sets the maximum authorization time on limbo servers for unauthorized players (in seconds)"
    );

    public static final ConfigurationKey<Integer> sessionTime = new ConfigurationKey<>(
            Integer.class,
            "sessionTime",
            10,
            "Sets the maximum authorization time (in minutes) for cracked players after logging in" +
                    "\nCracked players will not need to authenticate during this session time." +
                    "\nIf you want to disable this option set the value to 0."
    );

    public static final ConfigurationKey<String> passwordHashingType = new ConfigurationKey<>(
            String.class,
            "passwordHashingType",
            "BCRYPT",
            "Sets the default crypto provider, this is hashing algorithm which is used to hash cracked player passwords." +
                    "\nAvailable crypto provider: " +
                    "\n - SHA256 | Not secure, not recommended" +
                    "\n - SHA512 | Not secure, not recommended" +
                    "\n - BCRYPT | New, more secure and recommended"
    );

    public static final ConfigurationKey<String> safePasswordPattern = new ConfigurationKey<>(
            String.class,
            "safePasswordPattern",
            "[\\S]{6,25}",
            "The safe password pattern (in regular expression)."
    );

    public static final ConfigurationKey<StringList> authorizedCommands = new ConfigurationKey<>(
            StringList.class,
            "authorizedCommands",
            new StringList().append("login").append("register").append("l"),
            "Commands that are allowed while the user is not authorized."
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

    public static final ConfigurationKey<Integer> redisPort = new ConfigurationKey<>(
            Integer.class,
            "redisPort",
            6379
    );

    public static final ConfigurationKey<String> redisPassword = new ConfigurationKey<>(
            String.class,
            "redisPassword",
            ""
    );

    public boolean getMultiInstanceSupport() {
        return multiInstanceSupport.getValue();
    }

    public String getRedisHost() {
        return redisHost.getValue();
    }

    public int getRedisPort() {
        return redisPort.getValue();
    }

    public String getRedisPassword() {
        return redisPassword.getValue();
    }

    public static String getToken() {
        return token.getValue();
    }

    public static StringList getHostnames() {
        return hostnames.getValue();
    }

    public static String getMysqlHost() {
        return mysqlHost.getValue();
    }

    public static int getMysqlPort() {
        return mysqlPort.getValue();
    }

    public static String getMysqlDatabase() {
        return mysqlDatabase.getValue();
    }

    public static String getMysqlUser() {
        return mysqlUser.getValue();
    }

    public static String getMysqlPassword() {
        return mysqlPassword.getValue();
    }

    public static StringList getMysqlProperties() {
        return mysqlProperties.getValue();
    }

    public static int getPoolSize() {
        return poolSize.getValue();
    }

    public static int getPoolIdle() {
        return poolIdle.getValue();
    }

    public static int getPoolTimeout() {
        return poolTimeout.getValue();
    }

    public static int getPoolLifetime() {
        return poolLifetime.getValue();
    }

    public static StringList getMainServers() {
        return mainServers.getValue();
    }

    public static StringList getLimboServers() {
        return limboServers.getValue();
    }

    public static boolean getWrongPasswordDisconnection() {
        return wrongPasswordDisconnection.getValue();
    }

    public static boolean getLimboDisconnectRedirection() {
        return limboDisconnectRedirection.getValue();
    }

    public static boolean getLastServerRedirection() {
        return lastServerRedirection.getValue();
    }

    public static boolean getVerifyCaptchaCode() {
        return verifyCaptchaCode.getValue();
    }

    public static boolean getConfirmPassword() {
        return confirmPassword.getValue();
    }

    public static int getMaximumProfilesPerAddress() {
        return maximumProfilesPerAddress.getValue();
    }

    public static int getMaximumAuthorisationTime() {
        return maximumAuthorisationTime.getValue();
    }

    public static int getSessionTime() {
        return sessionTime.getValue();
    }

    public static String getPasswordHashingType() {
        return passwordHashingType.getValue();
    }

    public static String getSafePasswordPattern() {
        return safePasswordPattern.getValue();
    }

    public static StringList getAuthorizedCommands() {
        return authorizedCommands.getValue();
    }

    public static ServerComparatorStrategy getServerStrategy() {
        return serverStrategy.getValue();
    }
}
