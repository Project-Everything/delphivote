package com.playdelphi;

import java.util.logging.Logger;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlayerEnv {

    private Logger logger;
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
}