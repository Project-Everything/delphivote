package com.playdelphi;

import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.UUID;
import org.bukkit.entity.Player;

public class PlayerEnvManager {

    private final DelphiVote plugin;
    private final Logger logger;
    private static ConcurrentHashMap<UUID, PlayerEnv> playerEnvMap;

    // Constructor
    public PlayerEnvManager(DelphiVote plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        playerEnvMap = new ConcurrentHashMap<>();
    }

    // Create PlayerEnv for an online player (overrides any offline playerenv that exists)
    public PlayerEnv getPlayerEnv(Player player) {
        return playerEnvMap.compute(player.getUniqueId(), (key, playerEnv) -> new PlayerEnv(player));
    }

    // Get or create PlayerEnv by UUID (best option)
    public PlayerEnv getPlayerEnv(UUID uuid) {
        return playerEnvMap.computeIfAbsent(uuid, key -> new PlayerEnv(uuid));
    }

    // Get or create PlayerEnv by name (only use if uuid is unknown)
    public PlayerEnv getPlayerEnv(String name) {

        UUID uuid = plugin.getUtilsManager().mojangUUIDLookup(name);

        PlayerEnv thisPlayer = playerEnvMap.computeIfAbsent(uuid, key -> new PlayerEnv(uuid));

        if (thisPlayer.player != null) {
                thisPlayer.name = thisPlayer.player.getName(); // get name from player object
        } else {
            thisPlayer.name = name; // fallback to provided name
        }

        return thisPlayer;
    }
    
    // Remove a PlayerEnv by UUID
    public void removePlayerEnv(UUID uuid) {
        playerEnvMap.remove(uuid);
    }

    // Print all PlayerEnvs to console
    public void listActivePlayerEnvs() {
        logger.info("Active PlayerEnv entries:");
        for (UUID uuid : playerEnvMap.keySet()) {
            PlayerEnv playerEnv = playerEnvMap.get(uuid);
            logger.info("UUID: " + uuid + ", PlayerEnv: " + playerEnv.toString() + ", Player: " + playerEnv.name + ", uuid: " + playerEnv.uuid + ", online: " + (playerEnv.player != null));
        }
    }
}