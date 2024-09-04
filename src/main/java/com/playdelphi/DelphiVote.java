package com.playdelphi;

import com.vexsoftware.votifier.model.VotifierEvent;
import java.util.UUID;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.Plugin;

public class DelphiVote extends JavaPlugin implements Listener {
    private FileConfiguration config;
    private ConfigManager configManager;
    private DatabaseManager databaseManager;
    private LanguageManager languageManager;
    private RewardManager rewardManager;
    private VoteManager voteManager;
    private CommandManager commandManager;
    private HeadDatabaseAPI headDatabaseAPI;
    private UtilsManager utilsManager;
    private PlayerEnvManager playerEnvManager;
    private YamlManager yamlManager;

    // Getters
    public ConfigManager getConfigManager() {return configManager;}
    public DatabaseManager getDatabaseManager() {return databaseManager;}
    public LanguageManager getLanguageManager() {return languageManager;}
    public RewardManager getRewardManager() {return rewardManager;}
    public VoteManager getVoteManager() {return voteManager;}
    public CommandManager getCommandManager() {return commandManager;}
    public HeadDatabaseAPI getHeadDatabaseAPI() {return headDatabaseAPI;}
    public UtilsManager getUtilsManager() {return utilsManager;}
    public PlayerEnvManager getPlayerEnvManager() {return playerEnvManager;}
    public YamlManager getYamlManager() {return yamlManager;}

    @Override
    public void onEnable() {

        // getLogger uses Bukkit logger
        getLogger().info("DelphiVote plugin is starting up...");

        // Initialize YamlManager first
        yamlManager = new YamlManager(this);

        // Initialize ConfigManager second (load configs)
        configManager = new ConfigManager(this);
        
        // Initialize Utils
        utilsManager = new UtilsManager(this);
        
        // Initialize PlayerEnvManager
        playerEnvManager = new PlayerEnvManager(this);

        // Initialize DatabaseManager
        databaseManager = new DatabaseManager(this);

        // Initialize LanguageManager
        languageManager = new LanguageManager(this);

        // Initialize HeadDatabaseAPI
        if (Bukkit.getPluginManager().getPlugin("HeadDatabase") != null) {
            headDatabaseAPI = new HeadDatabaseAPI();
            getLogger().info("HeadDatabase detected! Enabling custom head rewards.");
        } else {
            headDatabaseAPI = null;
            getLogger().warning("HeadDatabase plugin not detected. Custom head rewards are disabled.");
        }

        // Initialize RewardManager
        rewardManager = new RewardManager(this);

        // Initialize VoteManager
        voteManager = new VoteManager(this);

        // Register Events
        getServer().getPluginManager().registerEvents(this, this);
        
        // Register CommandManager last
        commandManager = new CommandManager(this);
        getCommand("vote").setExecutor(commandManager);

        // Complete plugin enable
        getLogger().info("DelphiVote plugin has been enabled!");
        startPeriodicTasks();
    }

    @Override
    public void onDisable() {
        getLogger().info("DelphiVote plugin is shutting down...");
        if (databaseManager != null) {
            databaseManager.close();
        }
    }
    
    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {

        // Init PlayerEnv
        PlayerEnv playerEnv = playerEnvManager.getPlayerEnv(event.getPlayer());

        // QA PlayerEnv
        //  if (playerEnv != null) {
        //     getLogger().info("Created PlayerEnv for " + playerEnv.name + " " + playerEnv.uuid);
        // } else {
        //     getLogger().warning("Failed to create PlayerEnv for " + event.getPlayer().getName());
        // }
        
        // Add or update player in the database
        databaseManager.addOrUpdatePlayer(playerEnv);
        
        // Schedule a task to handle offline rewards
        getServer().getScheduler().runTaskLater(this, () -> {
            rewardManager.processPendingOfflineRewards(playerEnv);
        }, 20L); // delay to ensure the player is fully logged in
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        
        // Remove PlayerEnv
        playerEnvManager.removePlayerEnv(event.getPlayer().getUniqueId());
        // getLogger().info("Removed PlayerEnv for " + event.getPlayer().getName());
    }
    
    @EventHandler
    public void onVotifierEvent(VotifierEvent event) {

        // Get or create PlayerEnv with voting player name
        PlayerEnv playerEnv = playerEnvManager.getPlayerEnv(event.getVote().getUsername());

        // send source player, target player, service name, trigger name
        voteManager.handleVote(playerEnv, playerEnv, event.getVote().getServiceName());
    }

    private void startPeriodicTasks() {
        // Expire old rewards daily
        getServer().getScheduler().runTaskTimerAsynchronously(this, rewardManager::expireRewards, 12000L, 728000L); // wait 10 min after startup, run daily (in ticks)
    }
}