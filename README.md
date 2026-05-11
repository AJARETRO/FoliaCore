⚡ FoliaCore v-3.1 Frozen Nightingale

The Folia-Native Essentials Suite - Built for regionalized servers from the ground up.

FoliaCore is built for Folia first, not adapted to it later. The result is a cleaner essentials stack for modern servers: native Paper command registration, modular feature toggles, Folia-safe scheduling, animated displays, and a smaller dependency surface than the usual all-in-one plugins.

## 🎯 The FoliaCore Advantage

### Why FoliaCore Exists

Folia introduced regionalized multi-threading to Minecraft servers. Most legacy essentials frameworks were designed for older single-threaded Bukkit assumptions, then retrofitted later. That usually causes the same problems:

- Brittle command startup that assumes legacy registration paths.
- Fake or incomplete status reporting instead of actual server data.
- Race-condition-prone teleport and data management.
- Bloated monoliths with a long list of commands and dependencies you cannot meaningfully trim.
- GUI and inventory logic that ignores region-safe execution.

### What FoliaCore Does Differently

- Modern Paper command registration through the Paper command bridge instead of old startup-only patterns.
- Real Folia-safe scheduling and player scheduling.
- Thread-safe managers and defensive null checks around optional modules.
- Modular configuration so major systems can be enabled or disabled in `config.yml`.
- Animated tab and sidebar presentation with configurable frame methods.
- Optional PlaceholderAPI support for display text.
- No Vault economy layer, so the runtime stays focused on administration and quality-of-life.

Result: one plugin that is easier to maintain than a pile of separate essentials add-ons.

## 📊 Market Comparison

| Feature | FoliaCore | Legacy Essentials Stacks | Typical Paid All-in-One Plugins |
| --- | --- | --- | --- |
| Folia 26+ support | Native | Usually adapted later | Often partial or adapter-based |
| Command registration | Modern Paper bridge | Legacy Bukkit assumptions | Usually legacy or mixed |
| Module toggles | Major systems configurable | Usually all-or-nothing | Varies, often limited |
| Tab and sidebar animation | Configurable frame methods | Usually basic or separate plugin | Sometimes present, often paywalled |
| PlaceholderAPI support | Optional | Common | Common |
| Thread-safe state design | ConcurrentHashMap and guarded managers | Mixed | Mixed |
| Dependency surface | Kept lean | Usually larger | Often larger |
| Economy/Vault layer | Removed by design | Common | Common |
| Update checks | Modrinth-based | Varies | Varies |
| Maintenance style | Single focused release track | Fragmented | Fragmented |

The difference is simple: FoliaCore is built for Folia, while most alternatives are adapted around Folia.

## 📚 Table of Contents

- The FoliaCore Advantage
- Market Comparison
- Core Features
- Installation
- Configuration Reference
- Command Reference
- FAQ
- Sponsor & Support

## 🚀 Core Features

### System & Operations

- `/status` - server status output and operational summary.
- `/ping` - client latency check.
- `/clearchat` - clear chat for the server.
- Entity cleanup task - configurable automatic cleanup.
- Auto broadcaster - scheduled broadcast messages.
- Startup banner - branded console output on enable.
- Async Modrinth update checks with op notification.

### Chat & Social

- `/msg <player> <message>` and `/reply <message>`.
- `/mail` - mailbox workflow.
- `/mute` and `/unmute`.
- `/nick` and `/realname`.
- `/block` and `/unblock`.
- `/chat` - chat mode control.
- Social spy for staff monitoring.

### Teleportation & Movement

- `/sethome`, `/home`, `/homes`, `/delhome`.
- `/tpa`, `/tpahere`, `/tpaccept`, `/tpdeny`.
- `/setwarp`, `/warp`, `/warps`, `/delwarp`.
- `/setspawn`, `/spawn`, `/setfirstspawn`.
- `/back` - return to the previous location.

### Teams & Kits

- `/team` - team management.
- `/kit`, `/createkit`, `/delkit`.
- Thread-safe persistence for saved team, kit, and travel data.

### Utility & Staff Tools

- `/calc` - quick math expression evaluation.
- `/trash` and `/dispose` - disposable inventory access.
- `/repair` - repair held item, full inventory, or another player.
- `/fly`, `/heal`, `/feed`, `/god`.
- `/give`, `/clear`, `/invsee`, `/enderchest`, `/workbench`, `/hat`.
- `/broadcast`, `/time`, `/weather`.
- `/vanish`, `/socialspy`, `/staffchat`, `/sc`.
- `/antiraid`.

### Animated Displays

- Animated tab header and footer.
- Animated sidebar title and lines.
- Method-based frame lists so you can rename and reorder animations freely.
- Built-in placeholders for player name, world, ping, TPS, coordinates, and online counts.
- PlaceholderAPI integration when the plugin is installed.

## ⚙ Installation & Setup

### Requirements

- Folia 26.1.2 or later.
- Java 21 or newer.
- A server that can run a modern Paper/Folia plugin.

### Installation Steps

1. Download the latest `folia_core-v-3.1-Frozen-Nightingale.jar` from Releases.
2. Place the jar in your server `plugins/` folder.
3. Start the server.
4. Edit `plugins/FoliaCore/config.yml` to choose which modules you want and how tab/sidebar frames should look.
5. Restart the server to apply changes.

### Verify Installation

- Check the startup banner in console.
- Run `/status`.
- Test `/calc 10*4`, `/trash`, and `/repair`.
- If PlaceholderAPI is installed, confirm placeholders resolve in tab and sidebar output.

## 📋 Configuration Reference

Create or edit `plugins/FoliaCore/config.yml`.

### Module Toggles

- `modules.chat`
- `modules.teleport`
- `modules.kits`
- `modules.utility`
- `modules.tab`
- `modules.sidebar`
- `modules.staff`
- `modules.system`
- `modules.antiraid`
- `modules.security`

### Branding and System Settings

- `branding.startup-banner-enabled`
- `branding.owner-display`
- `system.maintenance-mode`
- `system.maintenance-kick-message`
- `system.entity-cleanup-enabled`
- `system.entity-cleanup-interval`
- `system.auto-broadcaster-enabled`
- `system.auto-broadcast-interval`

### Animated Display Settings

- `tab.enabled`
- `tab.update-interval-ticks`
- `tab.header-method`
- `tab.footer-method`
- `tab.methods.*`
- `sidebar.enabled`
- `sidebar.update-interval-ticks`
- `sidebar.title-method`
- `sidebar.line-methods`
- `sidebar.methods.*`

### Built-in Placeholders

- `%player_name%`
- `%world_name%`
- `%online_players%`
- `%max_players%`
- `%player_ping%`
- `%server_tps%`
- `%x%`, `%y%`, `%z%`

## 📖 Command Reference

### Common Commands

| Command | Purpose |
| --- | --- |
| `/status` | Server status output |
| `/ping` | Player ping check |
| `/calc` | Expression calculator |
| `/trash` | Disposable inventory |
| `/dispose` | Alias for `/trash` |
| `/repair` | Repair items |
| `/broadcast` | Global announcement |
| `/clearchat` | Clear chat |

### Social Commands

| Command | Purpose |
| --- | --- |
| `/msg` | Private message |
| `/reply` | Reply to last message |
| `/mail` | Mailbox workflow |
| `/nick` | Set nickname |
| `/realname` | Resolve nickname to real name |
| `/mute` | Mute player |
| `/unmute` | Unmute player |
| `/block` | Block player chat |
| `/unblock` | Remove block |

### Teleport Commands

| Command | Purpose |
| --- | --- |
| `/home` | Teleport to a saved home |
| `/sethome` | Save a home |
| `/homes` | List homes |
| `/delhome` | Delete a home |
| `/warp` | Teleport to a warp |
| `/setwarp` | Create a warp |
| `/warps` | List warps |
| `/delwarp` | Delete a warp |
| `/tpa` | Request teleport |
| `/tpahere` | Request target teleport here |
| `/tpaccept` | Accept teleport request |
| `/tpdeny` | Deny teleport request |
| `/spawn` | Teleport to spawn |
| `/setspawn` | Set spawn |
| `/back` | Return to previous location |

### Staff and Utility Commands

| Command | Purpose |
| --- | --- |
| `/ban` | Ban player |
| `/tempban` | Temporary ban |
| `/unban` | Remove ban |
| `/kick` | Kick player |
| `/fly` | Toggle flight |
| `/heal` | Restore health |
| `/feed` | Restore hunger |
| `/god` | Toggle invulnerability |
| `/give` | Give items |
| `/clear` | Clear inventory |
| `/invsee` | View another inventory |
| `/enderchest` | Open ender chest |
| `/workbench` | Open crafting table |
| `/hat` | Wear held item |
| `/team` | Team management |
| `/kit` | Claim kit |
| `/createkit` | Create kit |
| `/delkit` | Delete kit |
| `/antiraid` | Anti-raid command |
| `/vanish` | Toggle vanish |
| `/socialspy` | Staff spy |
| `/staffchat` | Staff chat |
| `/sc` | Staff chat alias |

## 🏗 Architecture Overview

FoliaCore uses a manager-based design:

- `ConfigManager` loads module toggles and runtime settings.
- `ChatManager` handles messages, nicknames, and mail.
- `TeleportManager` handles homes, warps, spawn, and TPA state.
- `TeamManager` handles team data.
- `KitManager` handles kits.
- `WarpManager` handles warps.
- `MarkerManager` handles marker data.
- `BanManager` handles bans.
- `VanishManager` handles vanish state.
- `SocialSpyManager` handles staff spy.
- `SpawnManager` handles spawn data.
- `DisplayManager` handles animated tab and sidebar updates.

Managers are loaded from the plugin bootstrap and then used by listeners and commands through constructor injection.

## ⚡ Startup & Logging

On startup, FoliaCore:

- Loads configuration first.
- Verifies the server is Folia-supported.
- Starts core managers.
- Registers listeners and commands.
- Starts system tasks when enabled.
- Prints the branded AJA RETRO startup banner.
- Runs the Modrinth update check.

If the startup banner is enabled, console output is intentionally bold and branded to make the plugin visible immediately on boot.

## 💪 Performance Notes

FoliaCore is intentionally lightweight in the places that matter:

- No Vault economy layer.
- No extra economy manager stack.
- Thread-safe state containers for shared data.
- Async update checking.
- Folia-safe player scheduling for display and notification work.

The goal is not to cram in every possible feature. The goal is to keep the server lean while still covering the essentials that most operators actually use.

## 🆘 Troubleshooting

### Commands Do Not Appear

- Confirm the feature module is enabled in `config.yml`.
- Check console output during startup.
- Make sure you are running Folia with Java 21.

### Tab or Sidebar Looks Empty

- Confirm `modules.tab` or `modules.sidebar` is enabled.
- Check the frame lists under `tab.methods.*` and `sidebar.methods.*`.
- Verify PlaceholderAPI if you are using placeholders from another plugin.

### Teleport or Chat Features Seem Disabled

- Check the related module toggle in `config.yml`.
- Restart after changing toggles.
- Look for startup warnings in console.

### Update Check Does Not Show Anything

- Confirm the server has network access.
- Check the console for Modrinth warnings.
- Ensure the release version is up to date.

## 🎓 FAQ

### Does FoliaCore replace EssentialsX?
It is designed to cover a lot of the same day-to-day server work, but with a Folia-native design and a more modular architecture.

### Is this better than most paid plugins?
For Folia servers, that is the point. FoliaCore aims to provide a stronger native experience, less dependency bloat, and a cleaner operator workflow than many paid all-in-one bundles.

### Does it support PlaceholderAPI?
Yes, for tab and sidebar text.

### Does it require Vault?
No. The economy stack was removed.

### Can I disable parts of the plugin?
Yes. Major systems are controlled through `config.yml` toggles.

### Can I use this on non-Folia servers?
No. It is built for Folia.

### How do I update?
Replace the jar, restart the server, and review the console banner for the new version.

## 🤝 Sponsor & Infrastructure

TrueCloud Hosting sponsors FoliaCore.

South Asia hosting pricing: $0.50 per GB.

WhatsApp: +8801989208751

TrueCloud Hosting is positioned for regional Minecraft hosting with simple pricing and a Folia-friendly performance mindset.

## 🛠 Development & Support

- GitHub: AJARETRO/FoliaCore
- Issues: GitHub Issues
- Releases: GitHub Releases
- License: MIT

## 📣 Credits

- Developed by AJA RETRO.
- Assisted by AI.
- Built for Folia 26.1.2+ and Java 21+.

Proudly Folia-native. Built for the future of regionalized Minecraft servers.
