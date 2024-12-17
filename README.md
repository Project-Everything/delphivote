# DelphiVote

Tired of boring vote rewards? Say hello to DelphiVote, the ultimate vote listening and reward management plugin that'll make your players actually excited to vote!

With DelphiVote, you're the boss of your server's voting strategy. Want to give out diamond pickaxes for every 5th vote? Done. How about a server-wide celebration when you hit 1000 total votes? Easy peasy. You can even set up randomized reward packages to keep things spicy! Our super flexible config lets you create any combo of items, commands, and messages you can dream up.

But wait, there's more! DelphiVote speaks your language (literally - we've got English, Spanish, and Portuguese built-in), works with both SQLite and MySQL, and even remembers offline votes so no one misses out. Plus, with [HeadDatabase](https://www.spigotmc.org/resources/head-database.14280/) support, you can hand out cool custom heads as rewards.

So why settle for a plain old voting plugin when you can have a vote party with DelphiVote? Give it a spin and watch your server's vote count skyrocket! 🚀🎉

## Features

- Vote tracking and management
- Customizable reward system
- Trigger-based reward distribution
- Support for both SQLite and MySQL databases
- Multi-language support (English, Spanish and Portuguese out of the box)
- Integration with HeadDatabase for custom head rewards
- Offline vote and reward handling

## Requirements

- Spigot/Paper 1.21.1+
- [NuVotifier](https://www.spigotmc.org/resources/nuvotifier.13449/)
- [HeadDatabase](https://www.spigotmc.org/resources/head-database.14280/) (optional, for custom head rewards)

### Support

* Visit the [Wiki](https://github.com/obzidi4n/delphivote/wiki) for complete documentation and reward recipes.
* For live support, visit our [Discord](https://discord.gg/2BbV34jUDT) or open an issue on [GitHub](https://github.com/obzidi4n/delphivote).
* Try it out first on Delphicraft - [playdelphi.com](https://playdelphi.com)

### Installation - Solo Server (no proxy)

Follow these instructions if you are **not using a proxy** such as Bungeecord or Velocity.

1. Stop the server.
2. Place [NuVotifier.jar](https://www.spigotmc.org/resources/nuvotifier.13449/) and DelphiVote.jar in the `server/plugins` folder.
3. Start the server to generate configuration files, then stop the server again.
4. Configure NuVotifier, if needed. The defaults in `server/plugins/Votifier/config.yml` should work without any changes, but adjust as necessary:

```
host: 0.0.0.0
port: 8192
disable-v1-protocol: false
tokens:
  default: (your token id here)
forwarding:
  method: none
  pluginMessaging:
    channel: nuvotifier:votes
```

5. Configure DelphiVote and set permissions as needed (see below)
6. Restart the server.


### Installation - Using Proxy

Follow these instructions if you **are using a proxy** such as Bungeecord or Velocity.

1. Stop all the servers.
2. Place [NuVotifier.jar](https://www.spigotmc.org/resources/nuvotifier.13449/) **in both of** the `proxy/plugins` and `server/plugins` folders.
3. Place DelphiVote.jar in the `server/plugins` folder(s) for each backend server.
3. Start the servers to generate configuration files, then stop the servers again.
4. Configure NuVotifier on the backend server(s). Adjust the settings in `server/plugins/Votifier/config.yml` to fit your setup. The main thing is to enable vote forwarding; recommend using pluginMessaging to a channel called nuvotifier:votes.

```
host: 0.0.0.0
port: -1
disable-v1-protocol: false
tokens:
  default: (your token id here)
forwarding:
  pluginMessaging:
    channel: nuvotifier:votes
```

4. Configure NuVotifier on the proxy server. Adjust the settings in `proxy/plugins/nuvotifier/config.toml` to fit your setup.  Main thing is to assign your proxy server a token id and enable vote forwarding using the same settings as above.

```
host = "0.0.0.0"
port = 8192
disable-v1-protocol = false

[tokens]
default = "(your token id here)"

[forwarding]
method = "pluginMessaging"

[forwarding.pluginMessaging]
channel = "nuvotifier:votes"

(rest of the config is up to you)
```

5. Configure DelphiVote and set permissions as needed (see below)
6. Restart the server.

### DelphiVote Permissions
Assign these permission nodes to your players and mods/admins as needed:

* delphivote.player (Access to basic voting commands)
* delphivote.admin (Access to all admin commands)

### DelphiVote Configuration Files
Adjust these configs to set up your database, voting sites, voting triggers/rewards, and to customize all messaging!

* config.yml: Database, language and reward expiration settings
* sites.yml: List of voting sites with names and URLs
* reward_items.yml: Items or groups of items to be given as rewards
* reward_triggers.yml: Set up conditions for when rewards should be given
* lang/messages-xx.yml: Customizable messages in different languages

### Configuration Quickstart
Several example configurations are in the `server/plugins/DelphiVote/examples` folder to give you a quickstart, feel free to use them. 

* reward_items.yml
* reward_triggers.yml
* sites.yml

## Main Commands

- `/vote`: Main command for players to see voting sites
- `/vote help`: Show available commands
- `/vote stats`: View top voters
- `/vote stats [player]`: View a player's vote stats
- `/vote reload`: Reload plugin configuration (admin only)
- `/vote give vote <player>`: Give a player vote (admin only)
- `/vote give reward <player>`: Give a vote reward (admin only)

## Support your Devs

[![Buy me a Coffee!](https://cdn.buymeacoffee.com/buttons/v2/default-yellow.png 'Buy me a Coffee!')](https://www.buymeacoffee.com/obzidi4n)

Your generous support keeps us motivated and caffeinated!