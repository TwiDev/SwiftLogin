/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.common.configuration.helpers;

import ch.twidev.swiftlogin.common.exception.PluginConfigurationException;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ListHelper<T> extends ArrayList<T> {

    private static final HashMap<Class<?>, Class<? extends ListHelper<?>>> helpers = new HashMap<>(){{
        put(String.class, StringList.class);
    }};

    private final Class<T> type;

    public ListHelper(List<T> list, Class<T> type) {
        super(list);

        this.type = type;
    }

    public ListHelper(Class<T> type) {
        this.type = type;
    }

    public Class<T> getType() {
        return type;
    }

    public static <T> ListHelper<T> fromType(Class<?> t, ArrayList<?> arrayList) {
        if(!helpers.containsKey(t)) {
            throw new RuntimeException(new PluginConfigurationException("Cannot parse a list of " + t + " because list helpers does not exist"));
        }

        try {
            return (ListHelper<T>) helpers.get(t).getConstructor(List.class).newInstance(arrayList);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static HashMap<Class<?>, Class<? extends ListHelper<?>>> getHelpers() {
        return helpers;
    }
}
