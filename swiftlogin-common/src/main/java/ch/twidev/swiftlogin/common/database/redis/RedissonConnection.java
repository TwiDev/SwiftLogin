/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.common.database.redis;

import ch.twidev.swiftlogin.common.database.Driver;
import ch.twidev.swiftlogin.common.database.DriverConfig;
import ch.twidev.swiftlogin.common.database.DriverType;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;

public class RedissonConnection extends Driver<RedissonClient> {

    public RedissonConnection(DriverConfig driverConfig) {
        super(driverConfig, DriverType.REDISSON, "RedissonConnection");

        this.initConnection();
    }

    @Override
    public void initConnection() {
        DriverConfig driverConfig = this.getDriverConfig();
        Config config = new Config();
        config.setCodec(new JsonJacksonCodec());
        config.setThreads(8);
        config.setNettyThreads(8);

        config.useSingleServer()
                .setAddress("redis://" + driverConfig.getString(DriverConfig.EnvVar.REDIS_HOST) + ":" + driverConfig.getString(DriverConfig.EnvVar.REDIS_PORT))
                .setDatabase(0)
                .setPassword(driverConfig.getString(DriverConfig.EnvVar.REDIS_PASSWORD));


        RedissonClient conn = Redisson.create(config);

        this.setConnection(conn);
    }
    
    @Override
    public void closeConnection() {
        connection.shutdown();
    }

    @Override
    public boolean isConnected() {
        return !connection.isShutdown();
    }
}
