package com.playdelphi;

import com.playdelphi.exceptions.*;
import java.util.List;
import java.util.logging.Logger;
import java.util.Map;
import java.util.UUID;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;


public class CommandManager implements CommandExecutor {

    private final DelphiVote plugin;
    private Logger logger;
	private ConfigManager configManager;
    private DatabaseManager databaseManager;
    private LanguageManager languageManager;
    private RewardManager rewardManager;
    private UtilsManager utilsManager;
    private VoteManager voteManager;
    private PlayerEnvManager playerEnvManager;

    // Constructor
    public CommandManager(DelphiVote plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
        this.databaseManager = plugin.getDatabaseManager();
        this.languageManager = plugin.getLanguageManager();
        this.logger = plugin.getLogger();
        this.rewardManager = plugin.getRewardManager();
        this.utilsManager = plugin.getUtilsManager();
        this.voteManager = plugin.getVoteManager();
        this.playerEnvManager = plugin.getPlayerEnvManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    
        try {

            if (!(sender instanceof Player)) {
                sender.sendMessage(languageManager.getMessage("player_only_command"));
                return true;
            }
    
            // Get PlayerEnv
            PlayerEnv playerEnv = playerEnvManager.getPlayerEnv((Player) sender);
    
            // Handle command
            if (args.length == 0) {
                return handleVoteList(playerEnv);
            }
    
            switch (args[0]) {
                case "give":
                    if (args.length > 1) {
                        switch (args[1]) {
                            case "vote":
                            return handleGiveVote(playerEnv, args);
                            case "reward":
                            return handleGiveReward(playerEnv, args);
                        }
                    }
                    return false;
                case "help":
                    return handleHelp(playerEnv);
                case "info":
                    return handleInfo(playerEnv);
                case "list":
                    return handleStats(playerEnv, args); // alias for stats
                case "playerenvs":
                    return handleListPlayerEnvs(playerEnv);
                case "reload":
                    return handleReload(playerEnv);
                case "rewards":
                    return handleListRewards(playerEnv);
                case "stats":
                    return handleStats(playerEnv, args);
                case "triggers":
                    return handleListTriggers(playerEnv);
                case "players":
                    return handleListPlayers(playerEnv);
                case "expire":
                    return handleExpireRewards(playerEnv);
                default:
                    playerEnv.sendMessage(languageManager.getMessage("no_command"));
                    return true;
            }

        } catch (PlayerNotFoundException e) {
            sender.sendMessage(languageManager.getMessage("player_not_found", Map.of("player", e.getMessage())));
            return true;

        } catch (Exception e) {
            // all other exceptions
            logger.severe("Catchall exception: " + e.getMessage());
            sender.sendMessage(languageManager.getMessage("plugin_error"));
            e.printStackTrace();
            return true;
        }
    }

    
    // permissions check
    private boolean checkPerm(PlayerEnv playerEnv, String minPerm) {
        return checkPerm(playerEnv, minPerm, false);
    }

    // permissions check with failure alert
    private boolean checkPerm(PlayerEnv playerEnv, String minPerm, boolean failAlert) {
        String fullPerm = "delphivote." + minPerm;

        if (minPerm == "admin") {
            if (playerEnv.player.hasPermission(fullPerm)) {
                return true;
            } else {
                if (failAlert){
                    playerEnv.sendMessage(languageManager.getMessage("no_permission"));
                }
                return false;
            }
        } 
        
        else if (minPerm == "player") {
            if (playerEnv.player.hasPermission(fullPerm) || playerEnv.player.hasPermission("delphivote.admin")) {
                return true;
            } else {
                if (failAlert) {
                    playerEnv.sendMessage(languageManager.getMessage("no_permission"));
                }
                return false;
            }
        } 

        return false;
    }

    // give rewards (admin)
    private boolean handleGiveReward(PlayerEnv playerEnv, String[] args) {

        if (checkPerm(playerEnv, "admin")) {
    
            String tgt_playerName;
            String rewardName;
    
            // Parse arguments
            if (args.length > 3) {
                rewardName = args[2];
                tgt_playerName = args[3];
            } else {
                playerEnv.sendMessage(languageManager.getMessage("admin_trigger_fail"));
                return false;
            }
    
            // Get target PlayerEnv
            PlayerEnv tgt_playerEnv = playerEnvManager.getPlayerEnv(tgt_playerName);
    
            // Process rewards
            rewardManager.handleRewards(playerEnv, tgt_playerEnv, "Admin", rewardName);
            return true;   

        } 
        else {return false;}
    }

    // give votes (admin)
    private boolean handleGiveVote(PlayerEnv playerEnv, String[] args) {

        if (checkPerm(playerEnv, "admin")) {

            // Get target playerEnv
            String tgt_playerName;

            // Parse arguments
            if (args.length > 2) {
                tgt_playerName = args[2];
            } else {
                playerEnv.sendMessage(languageManager.getMessage("give_vote_fail"));
                return false;
            }
            
            PlayerEnv tgt_playerEnv = playerEnvManager.getPlayerEnv(tgt_playerName);

            // Process vote
            voteManager.handleVote(playerEnv, tgt_playerEnv, "Admin");
            return true;

        } 
        else {return false;}
    }

    // manually expire old rewards (admin)
    private boolean handleExpireRewards(PlayerEnv playerEnv) {

        if (checkPerm(playerEnv, "admin")) {

            rewardManager.expireRewards();
            return true;

        } 
        else {return false;}
    }

    // see help menu
    private boolean handleHelp(PlayerEnv playerEnv) {
        playerEnv.sendMessage(languageManager.getMessage("help_header"));

        if (checkPerm(playerEnv, "player")) {
            playerEnv.sendMessage(languageManager.getMessage("help_vote"));
            playerEnv.sendMessage(languageManager.getMessage("help_stats"));
            playerEnv.sendMessage(languageManager.getMessage("help_stats_player"));
        }

        if (checkPerm(playerEnv, "admin")) {
            playerEnv.sendMessage(languageManager.getMessage("help_info"));
            playerEnv.sendMessage(languageManager.getMessage("help_list_rewards"));
            playerEnv.sendMessage(languageManager.getMessage("help_give_reward"));
            playerEnv.sendMessage(languageManager.getMessage("help_give_vote"));
            playerEnv.sendMessage(languageManager.getMessage("help_reload"));
            playerEnv.sendMessage(languageManager.getMessage("help_addplayer"));
        }

        playerEnv.sendMessage(languageManager.getMessage("help_footer"));
        return true;
    }

    // see plugin info (admin)
    private boolean handleInfo(PlayerEnv playerEnv) {

        if (checkPerm(playerEnv, "admin")) {

            String pluginUrl = plugin.getDescription().getWebsite();
            TextComponent urlMessage = new TextComponent(languageManager.getMessage("plugin_info_url", Map.of("url", pluginUrl)));
            urlMessage.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, pluginUrl));
            // urlMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(languageManager.getMessage("plugin_info_url_hover")).create()));

            urlMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(languageManager.getMessage("plugin_info_url_hover"))));

            playerEnv.sendMessage(languageManager.getMessage("plugin_info_header"));
            playerEnv.sendMessage(languageManager.getMessage("plugin_info_name", Map.of("name", plugin.getDescription().getName())));
            playerEnv.sendMessage(languageManager.getMessage("plugin_info_version", Map.of("version", plugin.getDescription().getVersion())));
            playerEnv.sendMessage(languageManager.getMessage("plugin_info_author", Map.of("author", String.join(", ", plugin.getDescription().getAuthors()))));
            playerEnv.player.spigot().sendMessage(urlMessage);
            playerEnv.sendMessage(languageManager.getMessage("plugin_info_footer"));
            return true;

        } 
        else {return false;}
    }

    // list playerEnvs (admin)
    private boolean handleListPlayerEnvs(PlayerEnv playerEnv) {

        if (checkPerm(playerEnv, "admin")) {

            playerEnvManager.listActivePlayerEnvs();
            return true;
        } 
        else {return false;}
    }

    // list players (admin)
    private boolean handleListPlayers(PlayerEnv playerEnv) {

        if (checkPerm(playerEnv, "admin")) {
    
            List<UUID> allPlayers = databaseManager.getAllPlayersUUID();
            logger.info("All Player UUIDs: " + allPlayers.toString());
            return true;
        } 
        else {return false;}
    }

    // list configured rewards (admin)
    private boolean handleListRewards(PlayerEnv playerEnv) {

        if (checkPerm(playerEnv, "admin")) {

            rewardManager.listRewards(playerEnv);
            return true;

        } 
        else {return false;}
    }

    // list configured triggers (admin)
    private boolean handleListTriggers(PlayerEnv playerEnv) {

        if (checkPerm(playerEnv, "admin")) {

            rewardManager.listTriggers(playerEnv);
            return true;
        } 
        else {return false;}
    }

    // reload plugin configs (admin)
    private boolean handleReload(PlayerEnv playerEnv) {

        if (checkPerm(playerEnv, "admin")) {
    
            plugin.reloadConfig();
            configManager.loadConfigs();
            languageManager.loadLanguageConfigs();
            playerEnv.sendMessage(languageManager.getMessage("plugin_reload_true"));
            return true;
        } 
        else {return false;}
    }

    // see player vote stats
    private boolean handleStats(PlayerEnv playerEnv, String[] args) {

        if (checkPerm(playerEnv, "player")) {
    
            if (args.length == 1) {
                playerEnv.sendMessage(languageManager.getMessage("top_voters_header"));
    
                // total vote count message
                int totalVotes = databaseManager.getServerVoteCount();
                playerEnv.sendMessage(languageManager.getMessage("total_server_votes", Map.of("total_votes", String.valueOf(totalVotes))));
    
                // top 10 voters
                List<Map.Entry<String, Integer>> topVoters = databaseManager.getTopVoters(10);
                int rank = 1;
                for (Map.Entry<String, Integer> entry : topVoters) {
                    playerEnv.sendMessage(languageManager.getMessage("top_voter_item", Map.of("rank", String.valueOf(rank), "player", entry.getKey(), "votes", String.valueOf(entry.getValue()))));
                    rank++;
                }
                playerEnv.sendMessage(languageManager.getMessage("top_voters_last_item"));
                playerEnv.sendMessage(languageManager.getMessage("top_voters_footer"));
            } else {
                // Show stats for a specific player
                String tgt_playerName = args[1];
    
                // Get target PlayerEnv
                PlayerEnv tgt_playerEnv = playerEnvManager.getPlayerEnv(tgt_playerName);
    
                Map<String, Object> playerStats = databaseManager.getPlayerVoteStats(tgt_playerEnv);
                if (playerStats != null) {
                    playerEnv.sendMessage(languageManager.getMessage("player_stats_header", Map.of("player", tgt_playerEnv.name)));
                    playerEnv.sendMessage(languageManager.getMessage("player_stats_votes", Map.of("votes", String.valueOf(playerStats.get("totalVotes")))));
                    if (playerStats.get("lastVoteDate") != null) {
                        playerEnv.sendMessage(languageManager.getMessage("player_stats_last_vote", Map.of("last_vote", String.valueOf(playerStats.get("lastVoteDate")))));
                    }
                } else {
                    playerEnv.sendMessage(languageManager.getMessage("player_not_found", Map.of("player", tgt_playerEnv.name)));
                }
                playerEnv.sendMessage(languageManager.getMessage("player_stats_footer"));
            }
            return true;
        } 
        else {return false;}
    }

    // list vote sites
    private boolean handleVoteList(PlayerEnv playerEnv) {

        if (checkPerm(playerEnv, "player")) {
    
            playerEnv.sendMessage(languageManager.getMessage("vote_site_header"));
            playerEnv.sendMessage(languageManager.getMessage("vote_site_first_item"));
    
            ConfigurationSection sitesConfig = configManager.getConfig("sites");
            if (sitesConfig == null || sitesConfig.getKeys(false).isEmpty()) {
                playerEnv.sendMessage(languageManager.getMessage("no_vote_sites"));
                return false;
            }
    
            for (String siteKey : sitesConfig.getKeys(false)) {
                ConfigurationSection siteSection = sitesConfig.getConfigurationSection(siteKey);
                if (siteSection != null && siteSection.getBoolean("site_active", true)) {
                    String siteName = siteSection.getString("site_name", "Unknown");
                    String siteUrl = siteSection.getString("site_vote_url", "");
    
                    TextComponent message = new TextComponent(languageManager.getMessage("vote_site_entry", Map.of("site_name", siteName)));
                    message.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, siteUrl));
                    message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(languageManager.getMessage("vote_site_hover")).create()));
    
                    playerEnv.player.spigot().sendMessage(message);
                }
            }
    
            playerEnv.sendMessage(languageManager.getMessage("vote_site_footer"));
            return true;
        } 
        else {return false;}
    }
}