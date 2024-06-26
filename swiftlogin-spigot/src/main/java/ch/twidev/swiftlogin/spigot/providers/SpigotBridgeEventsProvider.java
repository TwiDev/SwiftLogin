/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.spigot.providers;

import ch.twidev.swiftlogin.api.event.SwiftEvent;
import ch.twidev.swiftlogin.common.configuration.schema.BackendConfiguration;
import ch.twidev.swiftlogin.common.database.redis.RedissonConnection;
import ch.twidev.swiftlogin.common.events.AbstractEventsProvider;
import ch.twidev.swiftlogin.common.exception.PluginIssues;
import ch.twidev.swiftlogin.spigot.SwiftLoginSpigot;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.UUID;

public class SpigotBridgeEventsProvider extends AbstractEventsProvider<Player> {

    private final SwiftLoginSpigot spigot;

    private boolean isInitialized = false;

    public SpigotBridgeEventsProvider(SwiftLoginSpigot swiftLoginSpigot){
        super(Player.class);

        this.spigot = swiftLoginSpigot;
    }

    public void initialize(RedissonConnection redissonConnection) throws UnsupportedOperationException {
        this.setRedissonConnection(redissonConnection);

        if(BackendConfiguration.isMultiInstanceSupported() && (redissonConnection == null || !redissonConnection.isConnected())) {
            spigot.getSwiftLogger().sendWarningError(PluginIssues.REDISSON_NOT_CONNECTED,
                    "Cannot handle multi instances events, because redisson driver isn't connected. Please check your configuration file!");

            throw new UnsupportedOperationException();
        }else if(redissonConnection == null) {
            return;
        }

        redissonConnection.getConnection().getTopic(EVENT_BRIDGE_TOPIC).addListener(String.class, (charSequence, s) -> {
            try {
                JsonObject jsonObject = new JsonParser().parse(s).getAsJsonObject();
                String eventName = jsonObject.get("eventName").getAsString();

                if(this.getRegisteredEvents().containsKey(eventName)) {
                    Class<? extends SwiftEvent> events = this.getRegisteredEvents().get(eventName);

                    SwiftEvent<Player> swiftEvent = eventsGson.fromJson(s, (Type) events.getConstructor(Class.class).newInstance(Player.class));

                    this.callEvent(swiftEvent, false);
                }
            } catch (JsonParseException | NoSuchMethodException | InstantiationException | IllegalAccessException |
                     InvocationTargetException ignore) {}
        });

        this.isInitialized = true;
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    @Override
    public String getUniqueIdentifier(Player p) {
        return spigot.getImplementation().getPlatformHandler().getPlayerUUID(p).toString();
    }

    @Override
    public Player getFromUniqueIdentifier(String p) {
        return spigot.getImplementation().getPlatformHandler().getPlayerFromUUID(
                UUID.fromString(p)
        );
    }
}
