/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.common.events;

import ch.twidev.swiftlogin.api.SwiftInitializer;
import ch.twidev.swiftlogin.api.event.SwiftEvent;
import ch.twidev.swiftlogin.api.event.SwiftEventHandler;
import ch.twidev.swiftlogin.api.event.SwiftEventProvider;
import ch.twidev.swiftlogin.api.event.SwiftListener;
import ch.twidev.swiftlogin.api.players.SwiftPlayer;
import ch.twidev.swiftlogin.api.servers.SwiftServer;
import ch.twidev.swiftlogin.api.utils.Include;
import ch.twidev.swiftlogin.common.SwiftLoginImplementation;
import ch.twidev.swiftlogin.common.database.redis.RedissonConnection;
import ch.twidev.swiftlogin.common.events.adapter.UniqueIdAdapter;
import ch.twidev.swiftlogin.common.player.Profile;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.reflections.Reflections;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public abstract class AbstractEventsProvider<P> implements SwiftEventProvider<P> {

    protected static final String EVENT_BRIDGE_TOPIC = "swiftlogin:events";

    private final HashMap<Class<? extends SwiftListener>, List<EventMethod<P>>> listeners = new HashMap<>();

    private final HashMap<String, Class<? extends SwiftEvent>> registeredEvents = new HashMap<>();

    private RedissonConnection redissonConnection;

    protected final Gson eventsGson;

    public AbstractEventsProvider(Class<P> clazz) {
        GsonBuilder gsonBuilder = new GsonBuilder();

        gsonBuilder.registerTypeHierarchyAdapter(SwiftPlayer.class, new UniqueIdAdapter<SwiftPlayer>() {
            @Override
            public SwiftPlayer adapt(String fieldValue) {
                return SwiftLoginImplementation.getInstance().getProfileManager().getProfileByUniqueId(
                        UUID.fromString(fieldValue)
                ).orElse(null);
            }
        });

        gsonBuilder.registerTypeHierarchyAdapter(Profile.class, new UniqueIdAdapter<Profile>() {
            @Override
            public Profile adapt(String fieldValue) {
                return (Profile) SwiftLoginImplementation.getInstance().getProfileManager().getProfileByUniqueId(
                        UUID.fromString(fieldValue)
                ).orElse(null);
            }
        });
        gsonBuilder.registerTypeHierarchyAdapter(SwiftServer.class, new UniqueIdAdapter<SwiftServer>() {
            @Override
            public SwiftServer adapt(String fieldValue) {
                return SwiftLoginImplementation.getInstance().getServerManager().fromJson(fieldValue);
            }
        });

        gsonBuilder.registerTypeHierarchyAdapter(clazz, new TypeAdapter<P>() {
            @Override
            public void write(JsonWriter jsonWriter, P o) throws IOException {
                jsonWriter.value(
                        getUniqueIdentifier(o)
                );
            }

            @Override
            public P read(JsonReader jsonReader) throws IOException {
                return getFromUniqueIdentifier(jsonReader.nextString());
            }
        });

        ExclusionStrategy exclusionStrategy = new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes fieldAttributes) {
                return fieldAttributes.getAnnotations().stream().noneMatch(annotation -> annotation.toString().contains(Include.class.getName()));
            }

            @Override
            public boolean shouldSkipClass(Class<?> aClass) {
                return false;
            }
        };

        gsonBuilder.addSerializationExclusionStrategy(exclusionStrategy);
        gsonBuilder.addDeserializationExclusionStrategy(exclusionStrategy);

        this.eventsGson = gsonBuilder.create();

        // Load events class name for decoder

        new Reflections(SwiftEvent.EVENTS_PACKAGE_NAME)
                .getTypesAnnotatedWith(SwiftInitializer.class)
                .forEach(aClass -> registeredEvents.put(aClass.getName(), (Class<? extends SwiftEvent>) aClass));
    }

    @Override
    public void registerListener(SwiftListener swiftListener) {
        List<EventMethod<P>> eventInitializers = Arrays.stream(swiftListener.getClass().getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(SwiftEventHandler.class))
                .filter(method -> method.getParameterCount() == 1)
                .filter(method -> method.getParameters()[0].getType().isAnnotationPresent(SwiftInitializer.class))
                .map(method -> {
                    Class<? extends SwiftEvent<P>> event = (Class<? extends SwiftEvent<P>>) method.getParameters()[0].getType();

                    return new EventMethod<>(swiftListener, method, event, method.getAnnotation(SwiftEventHandler.class).priority());
                })
                .collect(Collectors.toList());

        listeners.put(swiftListener.getClass(), eventInitializers);
    }

    @Override
    @Deprecated
    public void unregisterListener(SwiftListener swiftListener) {
        listeners.remove(swiftListener.getClass());
    }

    public void setRedissonConnection(RedissonConnection redissonConnection) {
        this.redissonConnection = redissonConnection;
    }

    @Override
    public <E extends SwiftEvent<P>> void callEvent(E event, boolean broadcast) {
        if(redissonConnection != null && broadcast) {
            // Send event across instances

            redissonConnection.getConnection().getTopic(EVENT_BRIDGE_TOPIC).publish(
                    eventsGson.toJson(event)
            );
        }

        listeners.values()
                .stream()
                .flatMap(List::stream)
                .filter(e -> e.getClassEvent().equals(event.getClass()))
                .forEach(eventMethod -> {

                    try {
                        eventMethod.getMethod().invoke(eventMethod.getSwiftListener(), event);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                });


    }

    protected RedissonConnection getRedissonConnection() {
        return redissonConnection;
    }

    public abstract String getUniqueIdentifier(P p);

    public abstract P getFromUniqueIdentifier(String s);

    public HashMap<String, Class<? extends SwiftEvent>> getRegisteredEvents() {
        return registeredEvents;
    }

    public Gson getEventsGson() {
        return eventsGson;
    }
}
