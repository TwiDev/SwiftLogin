/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.api.event;

import ch.twidev.swiftlogin.api.players.SwiftPlayer;
import ch.twidev.swiftlogin.api.utils.Exclude;
import ch.twidev.swiftlogin.api.utils.Include;
import ch.twidev.swiftlogin.api.utils.Nullable;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class SwiftPlayerEvent<P> extends SwiftEvent<P> implements ParameterizedType {

    @Exclude
    protected Class<?> playerClass;

    @Include
    private SwiftPlayer swiftPlayer;

    @Include
    private P player;

    public SwiftPlayerEvent(SwiftPlayer swiftPlayer, P player) {
        this.swiftPlayer = swiftPlayer;
        this.player = player;
    }

    public SwiftPlayerEvent(Class<P> playerClass) {
        this.playerClass = playerClass;
    }

    public SwiftPlayer getSwiftPlayer() {
        return swiftPlayer;
    }

    @Nullable
    public P getPlayer() {
        return player;
    }

    @Override
    public Type[] getActualTypeArguments() {
        return new Type[]{playerClass};
    }

    @Override
    public Type getRawType() {
        return this.getClass();
    }

    @Override
    public Type getOwnerType() {
        return null;
    }
}
