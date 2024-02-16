/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.common.configuration;

import ch.twidev.swiftlogin.common.util.ReturnCallback;
import ch.twidev.swiftlogin.common.SwiftLoginImplementation;

public class ConfigurationKey<T> {

    private final String key;
    private final Class<T> type;
    private final ReturnCallback<T> defaultValue;
    private String comment = null;

    private T value;

    public ConfigurationKey(Class<T> type, String key, T defaultValue) {
        this.type = type;
        this.key = key;
        this.defaultValue = () -> defaultValue;
        this.value = defaultValue;
    }

    public ConfigurationKey(Class<T> type, String key, T defaultValue, String comment) {
        this.type = type;
        this.key = key;
        this.defaultValue = () -> defaultValue;
        this.comment = comment;
        this.value = defaultValue;
    }

    public ConfigurationKey(Class<T> type, String key, ReturnCallback<T> defaultValue) {
        this.type = type;
        this.key = key;
        this.defaultValue = defaultValue;
        this.value = defaultValue.run();
    }

    public ConfigurationKey(Class<T> type, String key, ReturnCallback<T> defaultValue, String comment) {
        this.type = type;
        this.key = key;
        this.defaultValue = defaultValue;
        this.comment = comment;
        this.value = defaultValue.run();
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public void setObjectValue(Object value) {
        this.value = (T) value;
    }

    public Class<T> getType() {
        return type;
    }

    public String getKey() {
        return key;
    }

    public ReturnCallback<T> getDefaultValue() {
        return defaultValue;
    }

    public String getComment() {
        return comment;
    }

    public boolean hasComment() {
        return comment != null;
    }

}
