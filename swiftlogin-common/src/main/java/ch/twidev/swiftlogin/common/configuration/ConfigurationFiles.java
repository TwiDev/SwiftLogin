/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.common.configuration;

import ch.twidev.swiftlogin.common.SwiftLoginPlugin;
import ch.twidev.swiftlogin.common.configuration.schema.BackendConfiguration;
import ch.twidev.swiftlogin.common.configuration.schema.MainConfiguration;
import ch.twidev.swiftlogin.common.configuration.schema.TranslationConfiguration;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

public enum ConfigurationFiles {

    CONFIG(MainConfiguration.class, "configuration.yml", false),
    LANG(TranslationConfiguration.class,"translation.yml", false),
    BACKEND_CONFIG(BackendConfiguration.class,"backend_configuration.yml", true);

    private final Class<?> clazz;
    private final String fileName;
    private final boolean isBackend;

    ConfigurationFiles(Class<?> clazz, String fileName, boolean isBackend) {
        this.clazz = clazz;
        this.fileName = fileName;
        this.isBackend = isBackend;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public String getFileName() {
        return fileName;
    }

    public boolean isBackend() {
        return isBackend;
    }

    public void generatedExampleConfiguration(SwiftLoginPlugin<?,?> swiftLoginPlugin, Instant instant) throws Exception {
        File file = new File("src/test/resources/"+ new SimpleDateFormat("dd-MM-yyyy_H-mm-ss").format(Date.from(instant)));
        file.mkdir();

        Configuration configuration = ConfigurationReader.loadConfiguration(swiftLoginPlugin, file, this);
    }
}
