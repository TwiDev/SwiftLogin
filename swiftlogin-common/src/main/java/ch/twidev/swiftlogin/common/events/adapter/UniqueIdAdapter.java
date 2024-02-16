/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.common.events.adapter;

import ch.twidev.swiftlogin.api.utils.Unique;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public abstract class UniqueIdAdapter<E extends Unique> extends TypeAdapter<E> {

    @Override
    public void write(JsonWriter jsonWriter, E unique) throws IOException {
        jsonWriter.value(unique.getUniqueId().toString());
    }

    @Override
    public E read(JsonReader jsonReader) throws IOException {
        return this.adapt(jsonReader.nextString());
    }

    public abstract E adapt(String fieldValue);
}
