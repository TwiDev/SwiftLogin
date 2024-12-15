/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.common.database;

import ch.twidev.swiftlogin.common.SwiftLogger;

public abstract class Driver<C> {

    private final DriverConfig driverConfig;

    private final DriverType driverType;
    public final SwiftLogger logs;

    public C connection = null;

    public Driver(DriverConfig driverConfig, DriverType driverType, String name) {
        this.driverConfig = driverConfig;
        this.driverType = driverType;

        this.logs = new SwiftLogger(name);
    }

    protected void setConnection(C connection) {
        this.connection = connection;
    }

    public C getConnection() {
        return connection;
    }

    public DriverType getDriverType() {
        return driverType;
    }

    public SwiftLogger getLog() {
        return logs;
    }

    public DriverConfig getDriverConfig() {
        return driverConfig;
    }

    public abstract void initConnection();

    public abstract void closeConnection();

    public abstract boolean isConnected();

}
