/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.common.database.sql;

import ch.twidev.swiftlogin.common.SwiftLogger;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.UUID;
import java.util.logging.Logger;

public class SQLManager<V, E extends SQLTableTemplate<V>> {

    private final Logger logs;

    private final SQLConnection connection;

    private E template;

    /**
     * Construct sql manager by driver
     *
     * @param driver SQL Driver
     * @param template SQL Columns Template class
     */
    public SQLManager(SQLConnection driver, Class<E> template) {
        this.connection = driver;

        // Init logger
        this.logs = new SwiftLogger("SQL<" + template.getSimpleName() + ">");

        try {

            // Create template instance
            this.template = template.newInstance();
            this.template.setConnection(connection);

            //logs.info("SQL Template loaded successfully !");
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void createTableTemplate(SQLConnection connection, Class<? extends SQLTableTemplate<?>> classTemplate) {
        try {
            SQLTableTemplate<?> template = classTemplate.newInstance();

            StringBuilder finalQuery = new StringBuilder(
                    String.format("CREATE TABLE IF NOT EXISTS %s (", template.getTableName())
            );

            // INSERT INTO {table_name} ( {column_name} {column_type} (? HAS {default_value) , ...)
            boolean isInitialize = false;

            for (Field declaredField : template.getClass().getDeclaredFields()) {
                if(declaredField.isAnnotationPresent(SQLTableTemplate.Column.class)) {
                    SQLTableTemplate.Column column = declaredField.getAnnotation(SQLTableTemplate.Column.class);

                    boolean isPrimary = column.columnName().equals(template.getPrimaryKey());
                    finalQuery.append(isInitialize ? ", " : "")
                            .append(column.columnName())
                            .append(" ")
                            .append(column.columnType().toString())
                            .append(" ")
                            .append(column.isNull() ? "NULL" : "NOT NULL")
                            .append(isPrimary ? " PRIMARY KEY" : "");

                    isInitialize = true;
                }
            }

            finalQuery.append(")");

            connection.asyncExecute(finalQuery.toString());

        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Get the SQL template with the filled values from primary key
     * or insert if the row does not exist
     *
     * @param primaryKey primary key value
     * @return Filled template
     */
    public E getOrInsert(V primaryKey) {
        this.get(primaryKey);

        if(!template.isExists()) {
            this.insert(primaryKey);
        }

        return template;
    }

    public E insert(V v) {
        StringBuilder set = new StringBuilder();
        template.setPrimaryValue(v);

        if(v != null) {
            if(v instanceof UUID) {
                set.append("`").append(template.getPrimaryKey()).append("` = '").append(v).append("'").append(", ");
            }else if(v instanceof String) {
                set.append("`").append(template.getPrimaryKey()).append("` = '").append(v).append("'").append(", ");
            }else{
                set.append("`").append(template.getPrimaryKey()).append("` = ").append(v).append(", ");
            }
        }

        for(Field declaredField : this.getClass().getDeclaredFields()) {
            SQLTableTemplate.Column annotation = declaredField.getAnnotation(SQLTableTemplate.Column.class);

            if (annotation != null) {
                declaredField.setAccessible(true);
                try {
                    Object value = declaredField.get(this);

                    if(value == null) {
                        set.append("`").append(annotation.columnName()).append("` = NULL").append(", ");
                    }else{
                        if(value instanceof UUID) {
                            set.append("`").append(annotation.columnName()).append("` = '").append(value).append("'").append(", ");
                        }else if(value instanceof String) {
                            set.append("`").append(annotation.columnName()).append("` = '").append(value).append("'").append(", ");
                        }else{
                            set.append("`").append(annotation.columnName()).append("` = ").append(value).append(", ");
                        }
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        set.deleteCharAt(set.length() - 2);
        String query = "INSERT INTO " + template.getTableName()
                + " SET " + set.toString();

        //logs.info(query);
        connection.execute(query);

        return get(v);
    }

    public E get(V primaryValue) {
        Object rawPrimary = (primaryValue.getClass().equals(UUID.class)) ? primaryValue.toString() : primaryValue;

        connection.query("SELECT * FROM " + template.getTableName()
                + " WHERE " + template.getPrimaryKey() + " = ?", response -> {

            try {
                if(response.next()) {
                    template.populate(response);
                }
            }catch (SQLException exception) {
                logs.severe("Cannot fetch " + template.getTableName() + " SQL Table for primary value " + primaryValue.toString());

                exception.printStackTrace();
            }
        }, rawPrimary);

        return template;
    }

    public E get(String query, Object... args) {
        connection.query("SELECT * FROM " + template.getTableName()
                + " " + query, response -> {
            try {
                if(response.next()) {
                    template.populate(response);
                }
            }catch (SQLException exception) {
                logs.severe("Cannot fetch " + template.getTableName() + " SQL Table for primary value " + Arrays.toString(args));

                exception.printStackTrace();
            }
        }, args);

        return template;
    }

    public void delete(E primaryValue) {
        if(template.isExists()) return;

        connection.asyncExecute("DELETE FROM " + template.getTableName()
                + " WHERE " + template.getPrimaryKey() + " = ?", primaryValue);
    }

    public E getTemplate() {
        return template;
    }

    public SQLConnection getConnection() {
        return connection;
    }

    public Logger getLogs() {
        return logs;
    }
}
