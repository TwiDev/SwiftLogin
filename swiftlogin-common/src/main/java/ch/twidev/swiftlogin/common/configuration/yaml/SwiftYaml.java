/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.common.configuration.yaml;

import ch.twidev.swiftlogin.common.configuration.ConfigurationKey;
import ch.twidev.swiftlogin.common.configuration.node.ConfigurationNode;
import ch.twidev.swiftlogin.common.configuration.node.ConfigurationValue;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SwiftYaml {

    public static ConfigurationNode read(File file) throws IOException{
        try (InputStream input = new FileInputStream(file)) {
            InputStreamReader reader = new InputStreamReader(input, StandardCharsets.UTF_8);
            Yaml yaml = new Yaml();

            Map<String, Object> objectMap = yaml.load(reader);
            Map<String, ConfigurationValue> configurationValueMap = objectMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> new ConfigurationValue(entry.getValue(), null)));

            input.close();
            return new ConfigurationNode(configurationValueMap);
        }
    }

    public static ConfigurationNode saveConfiguration(String header, File outputFile, List<ConfigurationKey<?>> configurationKeys) throws IOException{
        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setPrettyFlow(true);
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        dumperOptions.setAllowReadOnlyProperties(true);
        dumperOptions.setAllowUnicode(true);

        try {
            new FileOutputStream(outputFile).close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        
        Yaml yaml = new Yaml(dumperOptions);

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile, true), StandardCharsets.UTF_8))) {
            if (header != null) {
                for (String line : header.split("\n")) {
                    writer.write("# " + line + "\n");
                }

                writer.write("\n");
            }

            Map<String, ConfigurationValue> nodes = new HashMap<>();

            for (ConfigurationKey<?> configurationKey : configurationKeys) {
                ConfigurationValue configurationNode = new ConfigurationValue(configurationKey);

                if (configurationNode.hasComment()) {
                    writer.write("\n");
                    String comment = configurationNode.getComment();
                    for (String line : comment.split("\n")) {
                        writer.write("# " + line + "\n");
                    }
                }

                nodes.put(configurationKey.getKey(), configurationNode);

                writer.write(yaml.dump(
                        Collections.singletonMap(configurationKey.getKey(), configurationNode.getValue()))
                );
            }

            writer.close();

            return new ConfigurationNode(nodes);
        }
    }

}
