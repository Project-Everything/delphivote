package com.playdelphi;

import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import me.clip.placeholderapi.PlaceholderAPI;

public class PlayerEnv {

    public Player player;
    public String name;
    public UUID uuid;
    public boolean online;

    // Constructor
    public PlayerEnv(Player player) {
        this.player = player;
        this.name = player.getName();
        this.uuid = player.getUniqueId();
    }

    // Constructor for offline player using UUID
    public PlayerEnv(UUID uuid) {
        this.player = null;
        this.name = null; // resolve this later
        this.uuid = uuid;
    }    

    // process player messages
    public void sendMessage(String message) {
        if (player != null) {
            // Process PlaceholderAPI placeholders with player context before sending
            if (Bukkit.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                message = PlaceholderAPI.setPlaceholders(player, message);
            }
            player.sendMessage(message);
        }
    }
}