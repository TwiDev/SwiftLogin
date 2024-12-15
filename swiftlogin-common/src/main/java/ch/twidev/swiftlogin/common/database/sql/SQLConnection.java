/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.common.database.sql;

import ch.twidev.swiftlogin.common.database.Driver;
import ch.twidev.swiftlogin.common.database.DriverConfig;
import ch.twidev.swiftlogin.common.database.DriverType;
import ch.twidev.swiftlogin.common.exception.PluginIssues;
import ch.twidev.swiftlogin.common.player.ProfileTemplate;
import ch.twidev.swiftlogin.common.scheduler.Scheduler;
import ch.twidev.swiftlogin.common.util.Callback;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.HikariPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLConnection extends Driver<HikariDataSource> {

    public SQLConnection(DriverConfig driverConfig) {
        super(driverConfig, DriverType.MYSQL, "SQLConnection");

        this.initConnection();
    }

    @Override
    public void initConnection() {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        DriverConfig driverConfig = this.getDriverConfig();

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + driverConfig.getString(DriverConfig.EnvVar.SQL_HOST) + ":"+ driverConfig.getString(DriverConfig.EnvVar.SQL_PORT) +"/" + driverConfig.getString(DriverConfig.EnvVar.SQL_DATABASE));
        config.setUsername(driverConfig.getString(DriverConfig.EnvVar.SQL_USERNAME));
        config.setPassword(driverConfig.getString(DriverConfig.EnvVar.SQL_PASSWORD));
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setPoolName("SwiftLogin");

        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        // Avoid maxLifeTime disconnection
        config.setMinimumIdle(0);
        config.setConnectionTimeout(5000);
        config.setIdleTimeout(35000);
        config.setMaxLifetime(1800000);

        try {
            HikariDataSource pool = new HikariDataSource(config);

            this.setConnection(pool);

            if(this.isConnected()) {
                this.logs.info("Connected to MySQL with HikariCP!");

                // Creating default SQL tables

                SQLManager.createTableTemplate(this, ProfileTemplate.class);
            }else{
                this.logs.sendRawPluginError(PluginIssues.DATABASE_ACCESS_DENIED);
            }

        } catch (HikariPool.PoolInitializationException exception) {
            this.logs.sendRawPluginError(PluginIssues.DATABASE_ACCESS_DENIED, PluginIssues.DATABASE_ACCESS_DENIED.getMessage(), exception.getMessage());

            exception.fillInStackTrace();
            return;
        }
    }

    /**
     * Check if the driver is connected to the database
     *
     * @return boolean
     */
    @Override
    public boolean isConnected() {
        return this.connection != null && !this.connection.isClosed();
    }

    /**
     * Close and disconnect driver
     */
    @Override
    public void closeConnection() {
        this.connection.close();
        this.connection = null;
    }

    /**
     * Prepare a statement execution to the connection
     *
     * @param conn Driver connection
     * @param query query to prepare
     * @param vars indefinite variable in the query
     * @return Prepare statement
     */
    public PreparedStatement prepareStatement(Connection conn, String query, Object... vars) {
        try {
            PreparedStatement ps = conn.prepareStatement(query);
            int i = 0;
            if (query.contains("?") && vars.length != 0) {
                for (Object obj : vars) {
                    i++;
                    if(obj instanceof String) {
                        ps.setString(i, obj.toString());
                    }else {
                        ps.setObject(i, obj);
                    }
                }
            }
            return ps;

        } catch (SQLException exception) {
            this.logs.severe("MySQL error: " + exception.getMessage());
        }

        return null;
    }

    /**
     * Execute an async callback to the connection
     *
     * @param query to execute
     * @param callback callback to execute
     * @param vars indefinite variable in the query
     */
    public void asyncExecuteCallback(final String query, final Callback<Integer> callback, final Object... vars) {
        Scheduler.runTask(() -> {
            try (Connection conn = this.connection.getConnection()) {
                try (PreparedStatement ps = this.prepareStatement(conn, query, vars)) {
                    assert ps != null;
                    ps.execute();
                    this.closeRessources(null, ps);
                    if (callback != null) {
                        callback.run(-1);
                    }
                } catch (SQLException exception) {
                    this.logs.severe("MySQL error: " + exception.getMessage());
                    exception.printStackTrace();
                }
            } catch (SQLException exception) {
                this.logs.severe("Error when getting pool connection !");
                exception.printStackTrace();
            }
        });
    }

    /**
     * Execute a query without callback to the connection
     *
     * @param query to execute
     * @param vars indefinite variable in the query
     */
    public void asyncExecute(final String query, final Object... vars) {
        this.asyncExecuteCallback(query, null, vars);
    }


    /**
     * Execute a simple query to the connection
     *
     * @param query to execute
     * @param vars indefinite variable in the query
     */
    public void execute(final String query, final Object... vars) {
        try (Connection conn = this.connection.getConnection()) {
            try (PreparedStatement ps = this.prepareStatement(conn, query, vars)) {
                assert ps != null;
                ps.execute();
                this.closeRessources(null, ps);
            } catch (SQLException exception) {
                this.logs.severe("MySQL error: " + exception.getMessage());
                exception.printStackTrace();
            }
        } catch (SQLException exception) {
            this.logs.severe("Error when getting pool connection !");
            exception.printStackTrace();
        }
    }

    public void asyncQuery(final String query, final Callback<ResultSet> callback, final Object... vars) {
        Scheduler.runTask(() -> {
            try (Connection conn = this.connection.getConnection()) {
                try (PreparedStatement ps = this.prepareStatement(conn, query, vars)) {
                    assert ps != null;
                    try (ResultSet rs = ps.executeQuery()) {
                        callback.run(rs);
                        this.closeRessources(rs, ps);
                    }
                } catch (SQLException e) {
                    this.logs.severe("MySQL error: " + e.getMessage());
                    e.printStackTrace();
                }
            } catch (SQLException exception) {
                this.logs.severe("Error when getting pool connection !");
                exception.printStackTrace();
            }
        });
    }


    /**
     * Execute a query to the connection with callback result
     *
     * @param query to execute
     * @param callback after response
     * @param vars indefinite variable in the query
     */
    public void query(final String query, final Callback<ResultSet> callback, final Object... vars) {
        try (Connection conn = this.connection.getConnection()) {
            try (PreparedStatement ps = this.prepareStatement(conn, query, vars)) {
                assert ps != null;
                try (ResultSet rs = ps.executeQuery()) {
                    callback.run(rs);
                    this.closeRessources(rs, ps);
                }
            } catch (SQLException e) {
                this.logs.severe("MySQL error: " + e.getMessage());
                e.printStackTrace();
            }
        } catch (SQLException exception) {
            this.logs.severe("Error when getting pool connection !");
            exception.printStackTrace();
        }
    }


    /**
     * Get main driver connection
     *
     * @return Hikari Pool
     */
    public HikariDataSource getPool() {
        return connection;
    }

    @Deprecated
    private void closeRessources(ResultSet rs, PreparedStatement st) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (st != null) {
            try {
                st.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
