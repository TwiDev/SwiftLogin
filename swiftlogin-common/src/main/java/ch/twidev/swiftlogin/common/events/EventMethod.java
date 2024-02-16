/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.common.events;

import ch.twidev.swiftlogin.api.event.SwiftEvent;
import ch.twidev.swiftlogin.api.event.SwiftListener;

import java.lang.reflect.Method;

public class EventMethod<P> {

    private final SwiftListener swiftListener;
    private final Method method;
    private final Class<? extends SwiftEvent<P>> classEvent;

    private final int priority;

    public EventMethod(SwiftListener swiftListener, Method method, Class<? extends SwiftEvent<P>> classEvent, int priority) {
        this.swiftListener = swiftListener;
        this.method = method;
        this.classEvent = classEvent;
        this.priority = priority;
    }

    public SwiftListener getSwiftListener() {
        return swiftListener;
    }

    public Method getMethod() {
        return method;
    }

    public Class<? extends SwiftEvent<P>> getClassEvent() {
        return classEvent;
    }

    public int getPriority() {
        return priority;
    }

    @Override
    public String toString() {
        return "EventMethod{" +
                "method=" + method +
                ", classEvent=" + classEvent +
                ", priority=" + priority +
                '}';
    }
}
