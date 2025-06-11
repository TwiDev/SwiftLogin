/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.common.hooks;

import ch.twidev.swiftlogin.common.SwiftLoginPlugin;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Optional;
import java.util.UUID;

public class MojangManager {

    //https://api.mojang.com/users/profiles/minecraft/%s
    public static final String MOJANG_API = "https://api.minecraftservices.com/minecraft/profile/lookup/name/%s";

    private static final int READ_TIMEOUT = 8000;
    private static final int WRITE_TIMEOUT = 8000;

    private final SwiftLoginPlugin plugin;

    public MojangManager(SwiftLoginPlugin plugin) {
        this.plugin = plugin;
    }

    public Optional<UUID> getRealUUID(String var1) {
        try {
            return this.request(MOJANG_API, var1);
        } catch (IOException exception) {
            plugin.getSwiftLogger().warning("Could not fetch a premium id!");
            throw new RuntimeException(exception);
        }
    }

    public Optional<UUID> request(String raw, String params) throws IOException{
        URL url = new URL(String.format(raw, params));
        HttpsURLConnection conn = (HttpsURLConnection)url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("GET");
        conn.setReadTimeout(READ_TIMEOUT);
        conn.setConnectTimeout(WRITE_TIMEOUT);

        int code = conn.getResponseCode();
        switch(code) {
            case 200:
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();

                String s;
                while((s = bufferedReader.readLine()) != null) {
                    stringBuilder.append(s);
                }

                return this.parseBody(stringBuilder.toString());
            case 204:
            case 404:
                return Optional.empty();
            default:
                throw new IOException(String.valueOf(code));
        }
    }

    private Optional<UUID> parseBody(String var1) {
        JsonObject var2 = new Gson().fromJson(var1, JsonObject.class);
        JsonElement var3 = var2.get("id");
        return var3.isJsonNull() ? Optional.empty() : Optional.of(parseUniqueId(var3.getAsString()));
    }

    public static UUID parseUniqueId(String var0) {
        return UUID.fromString(var0.replaceAll("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"));
    }

}
