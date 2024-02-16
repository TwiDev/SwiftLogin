/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.common.events;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class EventWrapper<E> implements ParameterizedType {

    private Class<?> wrapped;

    public EventWrapper(Class<E> wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public Type[] getActualTypeArguments() {
        return new Type[]{wrapped};
    }

    @Override
    public Type getRawType() {
        return wrapped;
    }

    @Override
    public Type getOwnerType() {
        return null;
    }
}
