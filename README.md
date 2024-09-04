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
- [NuVotifier](https://github.com/NuVotifier/NuVotifier)
- [HeadDatabase](https://www.spigotmc.org/resources/head-database.14280/) (optional, for custom head rewards)

### Support

* Visit the [Wiki](https://github.com/obzidi4n/delphivote/wiki) for complete documentation and reward recipes.
* For live support, visit our [Discord](https://discord.gg/2BbV34jUDT) or open an issue on [GitHub](https://github.com/obzidi4n/delphivote).
* Try it out first on Delphicraft - [playdelphi.com](https://playdelphi.com)

## Installation

1. Place the DelphiVote.jar file in your server's `plugins` folder.
2. Restart your server to generate configuration files.
3. Stop the server and configure the files `config.yml`, `sites.yml`, `reward_items.yml`, and `reward_triggers.yml`.
4. For a quickstart, copy over the example configs from the examples folder!
5. Assign permissions with your perms manager
6. Restart your server.

## Permissions

- `delphivote.player`: Access to basic voting commands
- `delphivote.admin`: Access to all admin commands (inherits `delphivote.player`)

## Configuration Files

- `config.yml`: Main plugin settings
- `sites.yml`: Voting site configurations
- `reward_items.yml`: Define reward items
- `reward_triggers.yml`: Set up reward triggers
- `lang/messages-xx.yml`: Language files

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