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
import com.google.common.reflect.TypeToken;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Logger;

@SuppressWarnings("unchecked")
public abstract class SQLTableTemplate<E> {

    private static final Logger log = new SwiftLogger("TableTemplate");

    private final String tableName, primaryKey;

    private final Class<E> primaryType;

    private E primaryValue;

    private boolean exists = false;

    private SQLConnection connection = null;

    public SQLTableTemplate(String tableName, String primaryKey) {
        this.tableName = tableName;
        this.primaryKey = primaryKey;

        this.primaryType = (Class<E>) new TypeToken<E>(getClass()){}.getRawType();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Column {

        String columnName();

        ColumnType columnType();

        boolean isNull() default true;

    }

    public enum ColumnType {
        VARCHAR,
        TEXT,
        INT,
        BIGINT,
        FLOAT,
        BOOLEAN;

        @Override
        public String toString() {
            if(this == VARCHAR){
                return "VARCHAR(255)";
            }

            return super.toString();
        }
    }

    public void populate(ResultSet resultSet) {
        final HashMap<String, Field> fields = new HashMap<>();

        for(Field declaredField : this.getClass().getDeclaredFields()) {
            SQLTableTemplate.Column annotation = declaredField.getAnnotation(SQLTableTemplate.Column.class);

            if (annotation != null) {
                declaredField.setAccessible(true);
                fields.put(annotation.columnName(), declaredField);
            }
        }

        try {
            ResultSetMetaData meta = resultSet.getMetaData();

            for (int i = 1; i <= meta.getColumnCount(); i++) {

                String columnName = meta.getColumnName(i);
                Object columnValue = resultSet.getObject(i);

                if(fields.containsKey(columnName)) {
                    Field field = fields.get(columnName);

                    if(columnValue == null) {
                        continue;
                    }

                    if(columnName.equals(primaryKey)) {
                        if(primaryType.equals(UUID.class)) {
                            primaryValue = (E) UUID.fromString(columnValue.toString());
                        }else{
                            primaryValue = (E) columnValue;
                        }
                    }

                    if(field.getType().equals(UUID.class)) {
                        field.set(this, UUID.fromString(columnValue.toString()));
                        continue;
                    }

                    field.set(this, columnValue);
                }
            }

            this.exists = true;

        } catch (SQLException | IllegalAccessException e) {
            log.severe("Cannot populate " + this.tableName + " SQL Table");

            throw new RuntimeException(e);
        }
    }


    /**
     * Update a value in sql row and template
     *
     * @param columnName Name of the column to update
     * @param value New value
     * @param <T> Value type
     */
    public <T> void set(String columnName, T value) {
        if (this.isExists() && columnName.equals(this.primaryKey)) {
            return;
        }

        for(Field declaredField : this.getClass().getDeclaredFields()) {
            SQLTableTemplate.Column annotation = declaredField.getAnnotation(SQLTableTemplate.Column.class);

            if (annotation != null) {
                if(annotation.columnName().equals(columnName)) {
                    declaredField.setAccessible(true);
                    try {
                        declaredField.set(this, value);
                    } catch (IllegalAccessException e) {
                        log.severe("Cannot change value of " + columnName + " in " + this.tableName + " SQL Table");

                        throw new RuntimeException(e);
                    }

                    Object rawValue = value == null ? null : (value.getClass().equals(UUID.class) ? value.toString() : value);
                    Object rawPrimaryValue = primaryValue == null ? null : (primaryValue.getClass().equals(UUID.class) ? primaryValue.toString() : primaryValue);

                    if (rawValue == null) {
                        connection.asyncExecute("UPDATE " + this.tableName
                                + " SET " + columnName + " = NULL WHERE " + this.primaryKey + " = ?", rawPrimaryValue);
                    } else {
                        connection.asyncExecute("UPDATE " + this.tableName
                                + " SET " + columnName + " = ? WHERE " + this.primaryKey + " = ?", rawValue, rawPrimaryValue);
                    }
                }
            }
        }
    }

    public void delete() {
        connection.asyncExecute("DELETE FROM " + this.getTableName()
                + " WHERE " + this.getPrimaryKey() + " = ?", primaryValue);
    }

    public boolean isExists() {
        return exists;
    }

    public void setExists(boolean exists) {
        this.exists = exists;
    }

    public String getTableName() {
        return tableName;
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public E getPrimaryValue() {
        return primaryValue;
    }

    public void setPrimaryValue(E primaryValue) {
        this.primaryValue = primaryValue;
    }

    public SQLConnection getConnection() {
        return connection;
    }

    public void setConnection(SQLConnection connection) {
        this.connection = connection;
    }
}
