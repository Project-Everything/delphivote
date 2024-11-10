package com.playdelphi;

import java.io.*;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;
import org.snakeyaml.engine.v2.api.Dump;
import org.snakeyaml.engine.v2.api.DumpSettings;
import org.snakeyaml.engine.v2.api.Load;
import org.snakeyaml.engine.v2.api.LoadSettings;
import org.snakeyaml.engine.v2.common.FlowStyle;
import org.snakeyaml.engine.v2.common.ScalarStyle;

/**
 * YamlManager is a utility class for processing YAML configurations.
 * 
 * This class provides functionality to:
 * - Load YAML files
 * - Merge configurations from different sources
 * - Extract and preserve comment blocks (always at the top of the file)
 * - Add timestamps to configurations
 * - Save processed YAML data
 * 
 * It uses the SnakeYAML library for YAML parsing and dumping operations.
 */

public class YamlManager {
    private final DelphiVote plugin;
    private final Logger logger;
    private final Load yamlLoader;
    private final Dump yamlDumper;

    public YamlManager(DelphiVote plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        LoadSettings loadSettings = LoadSettings.builder().build();
        DumpSettings dumpSettings = DumpSettings.builder()
            .setDefaultFlowStyle(FlowStyle.BLOCK)
            .setIndent(2)
            .setDefaultScalarStyle(ScalarStyle.PLAIN)
            .build();
        this.yamlLoader = new Load(loadSettings);
        this.yamlDumper = new Dump(dumpSettings);
    }

    // Process YAML from a default resource stream, local file path, and output local file path
    public void processYaml(InputStream resourceStream, String localFilePath, String outputFilePath) throws IOException {
        byte[] resourceContent = resourceStream.readAllBytes();
        String commentBlock = extractCommentBlock(new ByteArrayInputStream(resourceContent));
        Map<String, Object> resourceData = loadYamlStream(new ByteArrayInputStream(resourceContent));
        Map<String, Object> localData = loadYamlFile(localFilePath);
        Map<String, Object> mergedData = mergeConfigurations(resourceData, localData);
        saveYaml(mergedData, outputFilePath, commentBlock);
    }
    
    // Extract comment block
    private String extractCommentBlock(InputStream resourceStream) throws IOException {
        StringBuilder commentBlock = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resourceStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().startsWith("#")) {
                    commentBlock.append(line).append("\n");
                } else if (!line.trim().isEmpty()) {
                    break;
                }
            }
        }
        // logger.info("extracted comment block from resource");
        return commentBlock.toString();
    }

    // Load YAML from a file path
    public Map<String, Object> loadYamlFile(String filePath) throws IOException {
        try (FileInputStream inputStream = new FileInputStream(filePath)) {
            return loadYamlStream(inputStream);
        }
    }

    // Load YAML from an InputStream
    public Map<String, Object> loadYamlStream(InputStream inputStream) {
        Object loaded = yamlLoader.loadFromInputStream(inputStream);
        @SuppressWarnings("unchecked")
        Map<String, Object> result = (Map<String, Object>) loaded;
        // logger.info("loaded yaml from input stream");
        return result;
    }

    // Merge configuration files to preserve local changes
    private Map<String, Object> mergeConfigurations(Map<String, Object> resourceConfig, Map<String, Object> localConfig) {
        Map<String, Object> mergedConfig = new LinkedHashMap<>();

        // if resource version is empty, use local
        if (resourceConfig == null) {
            // logger.warning("resourceConfig is null, using localConfig");
            return localConfig;
        }
        
        for (Map.Entry<String, Object> entry : resourceConfig.entrySet()) {
            String key = entry.getKey();
            Object resourceValue = entry.getValue();
            Object localValue = localConfig.get(key);

            if (localValue != null) {
                if (resourceValue instanceof Map && localValue instanceof Map) {
                    // Recursively merge nested maps
                    mergedConfig.put(key, mergeConfigurations((Map<String, Object>) resourceValue, (Map<String, Object>) localValue));
                } else {
                    // Use local value if it exists
                    mergedConfig.put(key, localValue);
                }
            } else {
                // Add new key from resource config
                mergedConfig.put(key, resourceValue);
            }
        }
        return mergedConfig;
    }

    // Write final Yaml file to local
    public void saveYaml(Map<String, Object> data, String filePath, String commentBlock) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(commentBlock);
            
            // Convert the map to YAML preserving formatting
            StringBuilder yamlContent = new StringBuilder();
            writeMapToYaml(data, yamlContent, 0);
            
            writer.write(yamlContent.toString());
        }
    }

    private void writeMapToYaml(Map<String, Object> map, StringBuilder builder, int indent) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            // Add indentation
            builder.append(" ".repeat(indent));
            
            // Write key
            builder.append(entry.getKey()).append(": ");
            
            Object value = entry.getValue();
            if (value instanceof Map) {
                // Handle nested maps
                builder.append("\n");
                @SuppressWarnings("unchecked")
                Map<String, Object> nestedMap = (Map<String, Object>) value;
                writeMapToYaml(nestedMap, builder, indent + 2);
            } else if (value instanceof Collection<?>) {
                // Handle lists/collections
                builder.append("\n");
                for (Object item : (Collection<?>) value) {
                    builder.append(" ".repeat(indent + 2)).append("- ");
                    if (item instanceof Map) {
                        // Handle maps within lists - using indent+2 instead of indent+4
                        @SuppressWarnings("unchecked")
                        Map<String, Object> mapItem = (Map<String, Object>) item;
                        writeMapToYaml(mapItem, builder, indent + 2);
                    } else {
                        // Handle simple list items
                        String itemStr = formatValue(item != null ? item.toString() : "");
                        builder.append(itemStr).append("\n");
                    }
                }
            } else {
                // Handle string values
                String stringValue = formatValue(value != null ? value.toString() : "");
                builder.append(stringValue).append("\n");
            }
        }
    }

    private String formatValue(String value) {
        // Escape any actual newlines in the string
        value = value.replace("\n", "\\n");
        
        // Preserve existing quotes and formatting
        if (value.contains("\"") || value.contains("\\n") || 
            value.contains("&") || value.startsWith(" ") || 
            value.endsWith(" ")) {
            // Keep existing quotes if they exist
            if (!value.startsWith("\"")) {
                value = "\"" + value + "\"";
            }
        }
        
        return value;
    }
}