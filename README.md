# DelphiVoting

DelphiVoting is a powerful and flexible Minecraft plugin for managing voting rewards and interactions. It integrates with NuVotifier to process votes and provides a customizable reward system.

## Features

- Integrates with NuVotifier for vote processing
- Customizable voting sites and rewards
- Multi-language support
- Clickable voting links in-game
- Configurable vote triggers and actions

## Dependencies

- [NuVotifier](https://github.com/NuVotifier/NuVotifier): Required for receiving votes from voting sites

## Installation

1. Ensure you have NuVotifier installed and configured on your server
2. Download the latest DelphiVoting.jar from the releases page
3. Place the jar file in your server's `plugins` folder
4. Restart your server or load the plugin
5. Configure the plugin settings in the generated config files

## Configuration

DelphiVoting uses several YAML configuration files:

- `config.yml`: Main configuration file
- `messages-<language>.yml`: Language-specific messages
- `triggers.yml`: Define vote triggers and actions
- `rewards.yml`: Configure rewards for voting
- `sites.yml`: Set up voting sites

## Commands

- `/vote`: List all active voting sites with clickable links
- `/vote <site>`: Get a specific voting site link
- `/dv reload`: Reload the plugin configuration
- `/dv info`: Display plugin information
- `/dv stats`: View voting statistics

## Permissions

- `delphivoting.admin`: Access to all admin commands
- `delphivoting.stats`: Ability to view voting statistics

## Support

For issues, feature requests, or general questions, please open an issue on this repo.

---

Crafted with ❤️ by Obzidi4n