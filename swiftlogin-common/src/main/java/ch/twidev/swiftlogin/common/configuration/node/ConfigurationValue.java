/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.common.configuration.node;

import ch.twidev.swiftlogin.common.configuration.ConfigurationKey;
import ch.twidev.swiftlogin.common.configuration.helpers.ListHelper;
import ch.twidev.swiftlogin.common.configuration.helpers.StringList;

import java.util.ArrayList;
import java.util.Collection;

public class ConfigurationValue {

    private Object value;
    private String comment;

    public ConfigurationValue(ConfigurationKey<?> configurationKey) {
        if(configurationKey.getType().isEnum()) {
            this.value = configurationKey.getValue().toString();
        }else if(configurationKey.getType().isAssignableFrom(ListHelper.class)) {
            ListHelper<?> list = (ListHelper<?>) configurationKey.getValue();
            if(list instanceof StringList) {
                this.value = new ArrayList<>(list);
            }
        }else{
            this.value = configurationKey.getValue();
        }

        this.comment = configurationKey.getComment();
    }

    public ConfigurationValue(Object value, String comment) {
        this.value = value;
        this.comment = comment;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean hasComment() {
        return comment != null;
    }
}
