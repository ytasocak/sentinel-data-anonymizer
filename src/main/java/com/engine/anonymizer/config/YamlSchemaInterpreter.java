package com.engine.anonymizer.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * YamlSchemaInterpreter reads basic YAML configurations.
 * It ignores comments and empty lines, extracting simple key-value pairs.
 * This avoids needing large third-party YAML parser libraries.
 */
public class YamlSchemaInterpreter {

    public static Map<String, String> interpret(String filePath) {
        Map<String, String> mappings = new HashMap<>();

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath), StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Remove extra whitespace around the line
                line = line.trim();

                // Skip comments and blank lines
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                // Split at the first colon to get key and value
                if (line.contains(":")) {
                    String[] parts = line.split(":", 2);
                    String key = parts[0].trim();
                    String value = parts[1].trim();

                    // Strip optional single or double quotes around the value
                    if (value.startsWith("\"") && value.endsWith("\"") && value.length() >= 2) {
                        value = value.substring(1, value.length() - 1);
                    } else if (value.startsWith("'") && value.endsWith("'") && value.length() >= 2) {
                        value = value.substring(1, value.length() - 1);
                    }

                    // Store the mapping
                    mappings.put(key, value);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading the schema configuration file: " + e.getMessage());
        }

        return mappings;
    }
}
