package com.playdelphi;

import java.util.logging.Logger;
import java.util.Map;

public class VoteManager {
    private final DelphiVote plugin;
    private final Logger logger;
    private final LanguageManager languageManager;
    private final DatabaseManager databaseManager;
    private final RewardManager rewardManager;

    // Constructor
    public VoteManager(DelphiVote plugin) {
        this.plugin = plugin;
        this.databaseManager = plugin.getDatabaseManager();
        this.languageManager = plugin.getLanguageManager();
        this.logger = plugin.getLogger();
        this.rewardManager = plugin.getRewardManager();
    }

    public void handleVote(PlayerEnv playerEnv, PlayerEnv tgt_playerEnv, String serviceName) {

        // if UUID invalid, stop here
        if (tgt_playerEnv.uuid == null && tgt_playerEnv.player != null) {
            playerEnv.player.sendMessage("Unable to resolve UUID for " + tgt_playerEnv.name + ". Vote not recorded.");
            return;
        }

        // 2. update database with vote
        try {
            databaseManager.addVote(playerEnv, tgt_playerEnv, serviceName);

            // Message to initiating player
            if (playerEnv.uuid != tgt_playerEnv.uuid && playerEnv.player != null) {
                playerEnv.sendMessage(languageManager.getMessage("vote_success", Map.of("player", tgt_playerEnv.name, "service", serviceName)));
            }

            // Message to target player
            if (tgt_playerEnv.player != null) {
                tgt_playerEnv.sendMessage(languageManager.getMessage("vote_success_player", Map.of("service", serviceName)));
            }

            // Broadcast to server
            if (!serviceName.equals("Admin")) {
                plugin.getServer().broadcastMessage(languageManager.getMessage("vote_success_broadcast", Map.of("player", tgt_playerEnv.name, "service", serviceName)));
            }

        } catch (Exception e) {
            logger.severe("Error adding vote: " + e.getMessage());
            if (playerEnv.player != null) {
                playerEnv.sendMessage(languageManager.getMessage("vote_fail", Map.of("player", tgt_playerEnv.name, "service", serviceName)));
            }
        }

        // 3. process reward triggers
        rewardManager.handleTriggers(playerEnv, tgt_playerEnv, serviceName);
    }
}

