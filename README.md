⚡ FoliaCore v-3.2 Blue Nightingale

The Folia-Native Essentials Suite - Built for regionalized servers from the ground up.

FoliaCore is built for Folia first, not adapted to it later. Blue Nightingale adds the advanced visual-system blueprint on top of the existing core so server owners can run a tighter essentials stack with native Paper command registration, modular feature toggles, Folia-safe scheduling, and a dedicated tab/sidebar design surface.

## 🎯 The FoliaCore Advantage

### Why FoliaCore Exists

Folia introduced regionalized multi-threading to Minecraft servers. Older essentials plugins were built for single-threaded Bukkit assumptions, then patched for Folia later. That usually shows up as brittle command startup, fake status reporting, unsafe shared state, bloated monoliths, and inventory logic that does not belong in a regioned runtime.

### What FoliaCore Does Differently

- Modern Paper command registration instead of legacy startup-only patterns.
- Real Folia-safe scheduling and player scheduling.
- Thread-safe managers and defensive null checks around optional modules.
- Modular configuration so major systems can be enabled or disabled in `config.yml`.
- An advanced visual blueprint in `tab-sidebar.yml` for tablist, sidebar, nametags, belowname, bossbar, and proxy-aware data.
- Optional PlaceholderAPI support for display text.
- No Vault economy layer, so the runtime stays focused on administration and quality-of-life.

Result: one plugin that is easier to maintain than a pile of separate essentials add-ons.

## 📊 Market Comparison

| Feature | FoliaCore | EssentialsX | Essentials-Folia | Typical Premium Suite |
| --- | --- | --- | --- | --- |
| Folia 26+ support | Native | Adapter-based | Partial | Varies |
| Command registration | Modern Paper bridge | Legacy Bukkit | Mixed | Mixed |
| Module toggles | Major systems configurable | Mostly all-or-nothing | Limited | Varies |
| Tab/sidebar blueprint | Dedicated visual config | Minimal | Limited | Sometimes paywalled |
| PlaceholderAPI support | Optional | Common | Common | Common |
| Thread-safe state design | ConcurrentHashMap and guarded managers | Mixed | Mixed | Mixed |
| Dependency surface | Kept lean | Larger | Larger | Usually larger |
| Economy/Vault layer | Removed by design | Common | Common | Common |
| Update checks | Modrinth-based | Varies | Varies | Varies |
| Maintenance style | Single focused release track | Fragmented | Fragmented | Fragmented |

The difference is simple: FoliaCore is built for Folia, while the others are usually adapted around Folia.

## 📚 Table of Contents

- The FoliaCore Advantage
- Market Comparison
- Core Features
- Visual Blueprint
- Installation
- Configuration Reference
- Command Reference
- Architecture Overview
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

- `/msg`, `/reply`, `/mail`, `/nick`, `/realname`.
- `/mute`, `/unmute`, `/block`, `/unblock`.
- `/chat` for mode switching.
- Social spy for staff monitoring.

### Teleportation & Movement

- `/sethome`, `/home`, `/homes`, `/delhome`.
- `/tpa`, `/tpahere`, `/tpaccept`, `/tpdeny`.
- `/setwarp`, `/warp`, `/warps`, `/delwarp`.
- `/setspawn`, `/spawn`, `/setfirstspawn`.
- `/back` for return-teleport support.

### Teams & Kits

- `/team` for team management.
- `/kit`, `/createkit`, `/delkit`.
- Thread-safe persistence for saved team, kit, and travel data.

### Utility & Staff Tools

- `/calc` for expression evaluation.
- `/trash` and `/dispose` for disposable inventory access.
- `/repair` for held item, full inventory, or another player.
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

## 🧩 Visual Blueprint

Blue Nightingale includes a dedicated `tab-sidebar.yml` blueprint for premium-style visual systems. It is structured around these areas:

- `database` for MySQL-backed toggle persistence.
- `settings` for debug levels, PlaceholderAPI, MiniMessage, hex, gradients, fonts, and sprites.
- `animations` for frame update intervals.
- `placeholders` for conditional expressions and output replacements.
- `tablist` for sorting, objective display, tab names, spectator handling, and header/footer animation.
- `sidebar` for multiple layouts, 15-line rendering, and flickerless updates.
- `entities` for nametags, belowname, bossbar, and ping spoofing.
- `multiserver` for Bungee/Velocity-aware placeholders.
- `output` for text replacement rules.
- `fonts` and `sprites` for custom rendering support.
- `debug` for logging controls.

The full template lives in [src/main/resources/tab-sidebar.yml](src/main/resources/tab-sidebar.yml).

### Example Concepts

- Global playerlist or per-world playerlist.
- Sorting by weight, permission, or alphabetic order.
- Playerlist objective for ping or health.
- Sidebar layouts with up to 15 lines and no flicker.
- Conditional placeholder expressions such as `%if_ping_>_100%_&cHigh_&aGood%`.
- Output replacement rules such as `TRUE -> &aEnabled`.
- Bossbar color and style control.
- Persistent per-player visual toggles stored in MySQL.

## ⚙ Installation & Setup

### Requirements

- Folia 26.1.2 or later.
- Java 21 or newer.
- A server that can run a modern Paper/Folia plugin.

### Installation Steps

1. Download the latest `folia_core-v-3.2-Blue-Nightingale.jar` from Releases.
2. Place the jar in your server `plugins/` folder.
3. Start the server.
4. Edit `plugins/FoliaCore/config.yml` for core modules.
5. Edit `plugins/FoliaCore/tab-sidebar.yml` for the advanced visual stack.
6. Restart the server to apply changes.

### Verify Installation

- Check the startup banner in console.
- Run `/status`.
- Test `/calc 10*4`, `/trash`, and `/repair`.
- Confirm your visual settings load from `tab-sidebar.yml`.

## 📋 Configuration Reference

### Core Config

`config.yml` controls the general plugin modules:

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

### Visual Config

`tab-sidebar.yml` handles the advanced visual surface:

- `database.mysql-enabled`
- `settings.debug-level`
- `settings.save-user-toggles`
- `animations.global-update-interval-ticks`
- `tablist.sort.*`
- `tablist.objective.*`
- `tablist.names.format`
- `tablist.header-footer.methods.*`
- `sidebar.layouts.*`
- `entities.nametags.*`
- `entities.belowname.*`
- `entities.bossbar.*`
- `multiserver.*`
- `fonts.*`
- `sprites.*`

### Built-in Placeholders

- `%player_name%`
- `%world_name%`
- `%online_players%`
- `%max_players%`
- `%player_ping%`
- `%server_tps%`
- `%server_mspt%`
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

FoliaCore uses a manager-based design, and Blue Nightingale adds a dedicated visual manager skeleton to that structure:

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
- `DisplayManager` handles the current animated tab and sidebar logic.
- `AdvancedDisplayManager` is the advanced blueprinted surface for tablist, sidebar, nametags, bossbar, and toggles.

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

The startup banner is intentionally loud because Blue Nightingale is meant to be noticed instantly on boot.

## 💪 Performance Notes

FoliaCore is intentionally lightweight where it matters:

- No Vault economy layer.
- Thread-safe state containers for shared data.
- Async update checking.
- Folia-safe player scheduling for display and notification work.
- A dedicated visual blueprint so the tab/sidebar logic can stay organized instead of growing into a monolith.

The goal is to keep the server lean while still covering the essentials that operators actually use.

## 🆘 Troubleshooting

### Commands Do Not Appear

- Confirm the feature module is enabled in `config.yml`.
- Check console output during startup.
- Make sure you are running Folia with Java 21.

### Visuals Look Empty

- Confirm the visual module is enabled.
- Check `tab-sidebar.yml` for the correct layout and method names.
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
Yes, for display text and the visual blueprint surface.

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
