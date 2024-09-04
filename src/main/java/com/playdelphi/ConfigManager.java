package com.playdelphi;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.HashMap;
import java.util.logging.Logger;
import java.util.Map;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.InvalidConfigurationException;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.representer.Representer;
import org.yaml.snakeyaml.Yaml;

public class ConfigManager {
    private final DelphiVote plugin;
    private Logger logger;
    private File datafolder;
    private Map<String, YamlConfiguration> configs;
    private YamlManager yamlManager;
    
    // Constructor
    public ConfigManager(DelphiVote plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.datafolder = plugin.getDataFolder();
        this.yamlManager = plugin.getYamlManager();
        this.configs = new HashMap<>();
        loadConfigs();
    }

    // Load all tracked config files into the configs map
    public void loadConfigs() {
        loadConfig("config", "config.yml");
        loadConfig("sites", "sites.yml");
        loadConfig("reward_items", "reward_items.yml");
        loadConfig("reward_triggers", "reward_triggers.yml");
        loadConfig("example_reward_items", "examples/reward_items.yml");
        loadConfig("example_reward_triggers", "examples/reward_triggers.yml");

        // Create lang folder if it doesn't exist
        File langFolder = new File(datafolder, "lang");
        if (!langFolder.exists()) {
            langFolder.mkdirs();
        }

        // Create any missing language files
        List<String> resources = getResourceConfig("plugin.yml").getStringList("resources");
        // logger.info("Available lang resources: " + resources.toString());
        for (String resource : resources) {
            if (resource.startsWith("lang/")) {
                File langFile = new File(datafolder, resource);
                if (!langFile.exists()) {
                    plugin.saveResource(resource, false);
                    logger.info("Created language file: " + resource);
                }
            }
        }

        // Load language files
        File[] langFiles = langFolder.listFiles((dir, name) -> name.toLowerCase().startsWith("messages-") && name.toLowerCase().endsWith(".yml"));
        if (langFiles != null) {
            for (File langFile : langFiles) {
                loadConfig("lang_" + langFile.getName(), "lang/" + langFile.getName());
            }
        }
    }

    // Load local config files, compare to defaults and merge if needed, add to configs map
    private void loadConfig(String configName, String resourceName) {
        File configFile = new File(datafolder, resourceName);
        // logger.info("Initializing " + configFile.getPath());

        // Check if the config file exists, if not, copy the default config from the jar
        if (!configFile.exists()) {
            plugin.saveResource(resourceName, false);
            // logger.info("Created " + resourceName + " from resource");
        }

        // Compare and merge the local config file with the default config from the jar
        try {
            InputStream resourceStream = plugin.getResource(resourceName);
            if (resourceStream == null) {
                logger.warning("Resource not found: " + resourceName);
                return;
            }
            yamlManager.processYaml(resourceStream, configFile.getPath(), configFile.getPath());
        } catch (IOException e) {
            logger.severe("Error processing " + resourceName + ": " + e.getMessage());
            return;
        }

        // Load the updated config file
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.load(configFile);
            configs.put(configName, config);
            // logger.info("Loaded configuration: " + configName + " from " + resourceName);
        } catch (IOException | InvalidConfigurationException e) {
            if (e.getMessage().contains("not a Map")) {
                logger.warning(resourceName + " is empty, be sure to set up your configs and restart the server!");
                return;
            } else {
                logger.severe("Error loading " + resourceName + ": " + e.getMessage());
                return;
            }
        }
    }

    // Load a file from the configs map with no processing - public for use in commands.
    public YamlConfiguration getConfig(String configName) {
        return configs.get(configName);
    }

    // Load a resource file from the jar (plugin.yml, database.yml, etc.) - public for use in commands.
    public YamlConfiguration getResourceConfig(String resourceName) {
        InputStream stream = plugin.getResource(resourceName);
        return YamlConfiguration.loadConfiguration(new InputStreamReader(stream, StandardCharsets.UTF_8));
    }

    // Get the reward triggers table
    public ConfigurationSection getTriggerTable() {
        YamlConfiguration triggersConfig = configs.get("reward_triggers");
        if (triggersConfig == null) {
            logger.severe("Triggers configuration is null");
            return null;
        }
        return triggersConfig.getRoot();
    }

    // Get the reward items table
    public ConfigurationSection getRewardItemTable() {
        YamlConfiguration rewardsConfig = configs.get("reward_items");
        if (rewardsConfig == null) {
            logger.severe("Rewards configuration is null");
            return null;
        }
        return rewardsConfig.getRoot();
    }
}