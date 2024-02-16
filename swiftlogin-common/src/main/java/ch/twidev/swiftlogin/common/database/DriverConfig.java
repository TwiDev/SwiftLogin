/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.common.database;

import java.util.HashMap;

public class DriverConfig {

    private DriverType driverType;

    private HashMap<EnvVar, Object> configVars = new HashMap<>();

    /**
     *
     *  Driver Configuration with credentials and database info (MySQL)
     *
     * @param type DriverType (MYSQL, ...)
     * @param host Host name (127.0.0.1)
     * @param database Database name
     * @param port Port number
     * @param user Username host
     * @param password Password access
     */
    public DriverConfig(DriverType type, String host, String database, int port, String user, String password) {
        this.driverType = type;

        configVars.put(EnvVar.SQL_HOST, host);
        configVars.put(EnvVar.SQL_DATABASE, database);
        configVars.put(EnvVar.SQL_PORT, port);
        configVars.put(EnvVar.SQL_USERNAME, user);
        configVars.put(EnvVar.SQL_PASSWORD, password);

    }

    public DriverConfig(DriverType driverType, HashMap<EnvVar, Object> configVars) {
        this.driverType = driverType;
        this.configVars = configVars;
    }

    public DriverConfig(DriverType driverType) {
        this.driverType = driverType;
    }

    public DriverConfig() {
    }

    public Object getObject(EnvVar var) {
        return configVars.get(var);
    }

    public String getString(EnvVar var) {
        return configVars.get(var).toString();
    }

    public void setVar(EnvVar var, Object v) {
        configVars.put(var, v);
    }

    public int getInt(EnvVar var) {
        try {
            return Integer.parseInt(configVars.get(var).toString());
        }catch (NumberFormatException e) {
            return -1;
        }
    }

    public enum EnvVar {
        SQL_HOST(DriverType.MYSQL),
        SQL_PORT(DriverType.MYSQL),
        SQL_DATABASE(DriverType.MYSQL),
        SQL_USERNAME(DriverType.MYSQL),
        SQL_PASSWORD(DriverType.MYSQL),
        REDIS_HOST(DriverType.REDISSON),
        REDIS_PORT(DriverType.REDISSON),
        REDIS_PASSWORD(DriverType.REDISSON);

        final DriverType type;

        EnvVar(DriverType type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return super.toString().replaceAll("_","-").toLowerCase();
        }
    }

}
