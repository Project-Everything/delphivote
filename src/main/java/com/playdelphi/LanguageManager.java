package com.playdelphi;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

public class LanguageManager {
    private final DelphiVote plugin;
	private File datafolder;
	private Logger logger;
    private YamlConfiguration langConfig;
    private ConfigManager configManager;
    private final Map<String, String> messages = new HashMap<>();

    public LanguageManager(DelphiVote plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.datafolder = plugin.getDataFolder();
        this.configManager = plugin.getConfigManager();
        loadLanguageConfigs();
    }

    public void loadLanguageConfigs() {
        File langFolder = new File(datafolder, "lang");
        
        // Get preferred language from config, default to English if not set
        String langPref = configManager.getConfig("config").getString("language", "messages-en.yml");

        langConfig = configManager.getConfig("lang_" + langPref);
        // logger.info("Using language file: " + langPref);

        for (String key : langConfig.getKeys(false)) {
            String message = langConfig.getString(key);
            if (message != null) {
                messages.put(key, ChatColor.translateAlternateColorCodes('&', message));
            }
        }
        // logger.info("Loaded language file: " + file.getName());
    }

    private void createLanguageFile(String fileName) {
        File file = new File(datafolder + File.separator + fileName);
        if (!file.exists()) {
            plugin.saveResource(fileName, false);
            // logger.info("Created language file: " + fileName);
        }
    }

    public String getMessage(String key) {
        // logger.info("Getting message for key: " + key);
        return messages.getOrDefault(key, "Message not found: " + key);
    }

    public String getMessage(String key, Map<String, String> placeholders) {
        String message = messages.getOrDefault(key, key);
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            message = message.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public String formatMessage(String messageKey, String playerName, int voteCount) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("player", playerName);
        placeholders.put("votes", String.valueOf(voteCount));
        return getMessage(messageKey, placeholders);
    }
}