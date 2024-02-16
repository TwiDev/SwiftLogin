/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.common.updater;

import ch.twidev.swiftlogin.common.PlatformHandler;
import ch.twidev.swiftlogin.common.ServerPlatformHandler;
import ch.twidev.swiftlogin.common.SwiftLogger;
import ch.twidev.swiftlogin.common.SwiftLoginImplementation;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class UpdateProvider {

    private static final String SPIGOT_ENDPOINT = "https://api.spigotmc.org/legacy/update.php?resource=%s";
    private static final int RESOURCE_ID = 0;
    private static final int MAX_BITS = 8;
    private final int maskedCurrentVersion;

    private final SwiftLogger swiftLogger;

    private final PlatformHandler<?> platformHandler;

    public UpdateProvider(SwiftLoginImplementation<?, ?> swiftLoginImplementation) {
        this.maskedCurrentVersion = maskVersion(SwiftLoginImplementation.VERSION);

        this.platformHandler = swiftLoginImplementation.getPlatformHandler();
        this.swiftLogger = swiftLoginImplementation.getSwiftLoginPlugin().getSwiftLogger();
    }

    public void checkUpdate() {
        this.getVersion(version -> {
            int latestMaskedVersion = maskVersion(version);

            if(latestMaskedVersion > maskedCurrentVersion) {
                // send update message

                swiftLogger.sendPluginInfo(
                        "A new version of the SwiftLogin plugin is available",
                        "Update the plugin now by downloading the latest version",
                        "on the spigot page https://spigotmc.org/resources/" + RESOURCE_ID);
            }else{
                swiftLogger.info("There is not a new update available.");
            }
        });
    }

    private void getVersion(final Consumer<String> consumer) {
        platformHandler.runAsync(() -> {
            try (InputStream inputStream = new URL(String.format(SPIGOT_ENDPOINT, RESOURCE_ID)).openStream(); Scanner scanner = new Scanner(inputStream)) {
                if (scanner.hasNext()) {
                    consumer.accept(scanner.next());
                }
            } catch (IOException exception) {
                swiftLogger.info("Unable to check for updates: " + exception.getMessage());
            }
        });
    }

    public static int maskVersion(String version) {
        final List<Integer> versions = Arrays.stream(version
                        .replaceAll("v", "") // Remove 'v' from 'v0.1.0'
                        .split("\\."))
                .map(Integer::parseInt)
                .collect(Collectors.toList());

        final int major = versions.get(0);
        final int minor = versions.get(1);
        final int patch = versions.get(2);

        return major << MAX_BITS * 2 | minor << MAX_BITS * 1 | patch << MAX_BITS * 0;
    }

}
