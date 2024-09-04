# DelphiVote

DelphiVote is a powerful, flexible Minecraft plugin for vote listening and reward management. It integrates with NuVotifier to handle player votes and provides a simple system for configuring and distributing rewards.

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
- HeadDatabase (optional, for custom head rewards)

## Installation

1. Place the DelphiVote.jar file in your server's `plugins` folder.
2. Restart your server to generate configuration files.
3. Configure the plugin in `config.yml`, `reward_items.yml`, and `reward_triggers.yml`.
4. Restart your server.

## Configuration

- `config.yml`: Main plugin settings
- `sites.yml`: Voting site configurations
- `reward_items.yml`: Define reward items
- `reward_triggers.yml`: Set up reward triggers
- `lang/messages-xx.yml`: Language files

## Commands

- `/vote`: Main command for players to see voting sites
- `/vote help`: Show available commands
- `/vote stats [player]`: View voting statistics
- `/vote reload`: Reload plugin configuration (admin only)
- `/vote give vote <player>`: Give a player vote (admin only)
- `/vote give reward <player>`: Give a vote reward (admin only)

## Permissions

- `delphivote.player`: Access to basic voting commands
- `delphivote.admin`: Access to all admin commands (inherits `delphivote.player`)

## Support

For support, visit our [Discord server](https://discord.gg/2BbV34jUDT) or open an issue on [GitHub](https://github.com/obzidi4n/delphivote).