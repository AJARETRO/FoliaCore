# ✨ FoliaCore v3.4 Blue Nightingale

FoliaCore is a Folia-first essentials and administration suite for servers that want one polished toolkit instead of a pile of separate plugins. It is built for regionalized runtime behavior, command safety, visual flair, and day-to-day convenience without dragging in a heavy dependency stack.

If you want something that feels native to Folia, reads cleanly in config, and gives staff and players a smarter command set, FoliaCore is the kind of plugin that keeps the server tidy and the experience smooth. This release, v3.4, also ships with a full permission map so every command can be controlled per player instead of being locked behind one all-or-nothing switch.

## 💙 Why Use FoliaCore

FoliaCore exists because modern servers need more than a bunch of legacy Bukkit commands with a Folia patch on top. A good essentials suite should feel alive, organized, and safe under the server model it runs on.

### What makes it worth using

- Folia-native scheduling and thread-aware data handling.
- Clean command registration through Paper's modern APIs.
- Modular feature toggles in `config.yml` so you only load what you want.
- Separate permission nodes for almost every action, with sane defaults for common player tools.
- Dynamic per-warp and per-kit permissions for fine-grained access control.
- Staff utilities for moderation, world control, teleportation, and support tasks.
- Visual polish through the tab and sidebar blueprint in `tab-sidebar.yml`.
- Modrinth update checks so operators can spot new releases quickly.

### What it feels like in practice

- Players get quick access to homes, warps, chat tools, mail, kits, and quality-of-life commands.
- Staff get moderation, vanish, social spy, inventory inspection, and anti-raid tools.
- Developers get a cleaner command map that is easier to document, grant, and audit.

## 🌈 What Changed in v3.4

This release refreshes the versioning to v3.4 Blue Nightingale and formalizes the permissions tree in the plugin metadata. That means server owners can now assign or remove command access more precisely, and the documentation below matches the actual runtime behavior.

Highlights:

- Added a missing permission gate for `/kit`.
- Declared the permission tree in `paper-plugin.yml`.
- Standardized the release version to `v3.4`.
- Refreshed the startup banner and release branding.
- Rewrote the docs so the command and permission model is easy to scan.

## 🚀 Quick Start

1. Download the v3.4 jar from your release source.
2. Place it in your server `plugins/` folder.
3. Start the server on Folia with Java 21.
4. Review `plugins/FoliaCore/config.yml` for module toggles.
5. Review `plugins/FoliaCore/tab-sidebar.yml` for the visual blueprint.
6. Grant permissions to players or groups using your permission manager.

## 🛠 Installation

### Requirements

- Folia or a compatible Folia-supported Paper build.
- Java 21.
- A permissions plugin such as LuckPerms if you want full per-player control.

### Suggested setup flow

1. Install the jar.
2. Start the server once to generate the config files.
3. Edit `config.yml` to enable or disable major modules.
4. Edit `tab-sidebar.yml` if you want the animated display system.
5. Assign command permissions to groups or individual players.
6. Restart or reload carefully, depending on your server policy.

### Files worth knowing

- `src/main/resources/config.yml` controls the major modules.
- `src/main/resources/tab-sidebar.yml` controls the visual system.
- `src/main/resources/paper-plugin.yml` now defines the permission tree.

## 🧭 Permission Philosophy

The default permission layout is split into two ideas:

- Basic player conveniences are set to `true` by default.
- Staff, admin, and destructive tools are set to `false` by default.

That keeps the plugin friendly for normal players while still letting you grant exactly what you want to specific users or groups.

### Special patterns

- `/warp` supports `foliacore.warp.all` for full access.
- Individual warps can be granted with `foliacore.warp.<warpname>`.
- `/kit` uses `foliacore.kit` for the base command.
- Kits created with `/createkit` get their own node in the form `foliacore.kit.<kitname>`.
- Kit administration stays on `foliacore.kit.admin`.

## 📚 Command Reference

### Social and Chat

| Command | Permission node(s) | Default | Notes |
| --- | --- | --- | --- |
| `/msg` | `foliacore.msg` | `true` | Send private messages. |
| `/reply` | `foliacore.reply` | `true` | Reply to the last private message. |
| `/mail` | `foliacore.mail.send`, `foliacore.mail.read`, `foliacore.mail.clear` | mixed | `send` and `read` are basic; `clear` is restricted. |
| `/nick` | `foliacore.nick`, `foliacore.nick.color` | mixed | Basic nicking is open; color control is restricted. |
| `/realname` | `foliacore.realname` | `true` | Resolve a nickname back to a player. |
| `/block` | `foliacore.block` | `true` | Block a player from contacting you. |
| `/unblock` | `foliacore.unblock` | `true` | Remove a block. |
| `/chat` | `foliacore.chat.global`, `foliacore.chat.world`, `foliacore.chat.regional` | `true` | Mode-based chat switching. |
| `/mute` | `foliacore.mute` | `false` | Moderation command. |
| `/unmute` | `foliacore.unmute` | `false` | Moderation command. |
| `/socialspy` | `foliacore.socialspy` | `false` | Staff monitoring tool. |
| `/staffchat`, `/sc` | `foliacore.staffchat` | `false` | Staff-only channel. |

### Teleport and Travel

| Command | Permission node(s) | Default | Notes |
| --- | --- | --- | --- |
| `/sethome` | `foliacore.sethome` | `true` | Save a home location. |
| `/home` | `foliacore.home` | `true` | Teleport to your home. |
| `/homes` | `foliacore.homes.list` | `true` | List your saved homes. |
| `/delhome` | `foliacore.delhome` | `true` | Delete one of your homes. |
| `/tpa` | `foliacore.tpa` | `true` | Request teleportation to a player. |
| `/tpahere` | `foliacore.tpahere` | `true` | Request a player come to you. |
| `/tpaccept` | `foliacore.tpaccept` | `true` | Accept a teleport request. |
| `/tpdeny` | `foliacore.tpdeny` | `true` | Deny a teleport request. |
| `/spawn` | `foliacore.spawn` | `true` | Return to spawn. |
| `/back` | `foliacore.back` | `true` | Return to your previous location. |
| `/warp` | `foliacore.warp.all`, `foliacore.warp.<warpname>` | mixed | Use a warp by global or per-warp access. |
| `/warps` | `foliacore.warps.list` | `true` | List the warps you are allowed to see. |
| `/setwarp` | `foliacore.setwarp` | `false` | Create a warp. |
| `/delwarp` | `foliacore.delwarp` | `false` | Remove a warp. |
| `/setspawn` | `foliacore.setspawn` | `false` | Set the server spawn point. |
| `/setfirstspawn` | `foliacore.setfirstspawn` | `false` | Set the first join spawn point. |
| `/tp` | `foliacore.tp`, `foliacore.tp.others` | `false` | Teleport yourself or other players depending on the target form. |
| `/tphere` | `foliacore.tphere` | `false` | Bring another player to you. |
| `/gps` | `foliacore.gps` | `true` | GPS helper command. |

### Teams and Kits

| Command | Permission node(s) | Default | Notes |
| --- | --- | --- | --- |
| `/team` | `foliacore.team.create`, `foliacore.team.disband`, `foliacore.team.invite`, `foliacore.team.accept`, `foliacore.team.leave`, `foliacore.team.kick` | `true` | Team management is split into separate actions. |
| `/kit` | `foliacore.kit` | `true` | Open the kit GUI or claim an allowed kit. |
| `/createkit` | `foliacore.kit.admin` | `false` | Create a new kit and its permission node. |
| `/delkit` | `foliacore.kit.admin` | `false` | Remove a kit. |

### Utility and Player Tools

| Command | Permission node(s) | Default | Notes |
| --- | --- | --- | --- |
| `/calc` | `foliacore.calc` | `true` | Expression calculator. |
| `/trash`, `/dispose` | `foliacore.trash` | `true` | Disposable inventory. |
| `/workbench`, `/wb` | `foliacore.workbench` | `true` | Portable crafting table. |
| `/hat` | `foliacore.hat` | `true` | Wear your held item as a hat. |
| `/enderchest`, `/ec` | `foliacore.enderchest`, `foliacore.enderchest.others` | mixed | Open your own or another player's ender chest. |
| `/ping` | `foliacore.ping`, `foliacore.ping.others` | mixed | Check your own ping or another player's ping. |
| `/scoreboard`, `/sidebar` | `foliacore.scoreboard.toggle` | `true` | Toggle the sidebar display. |
| `/feed` | `foliacore.feed`, `foliacore.feed.others` | mixed | Feed yourself or another player. |
| `/fly` | `foliacore.fly`, `foliacore.fly.others` | mixed | Toggle flight. |
| `/heal` | `foliacore.heal`, `foliacore.heal.others` | mixed | Heal yourself or another player. |
| `/god` | `foliacore.god`, `foliacore.god.others` | mixed | Toggle invulnerability. |
| `/repair` | `foliacore.repair`, `foliacore.repair.all`, `foliacore.repair.others`, `foliacore.repair.others.all` | mixed | Repair held item, inventory, or another player. |
| `/give` | `foliacore.give` | `false` | Give items to a player. |
| `/clear` | `foliacore.clear`, `foliacore.clear.others` | mixed | Clear inventory. |
| `/invsee` | `foliacore.invsee` | `false` | Inspect another player's inventory. |
| `/gamemode`, `/gms`, `/gmc`, `/gma`, `/gmsp` | `foliacore.gamemode`, `foliacore.gamemode.others` | mixed | Game mode control with player or target forms. |

### Administration and World Control

| Command | Permission node(s) | Default | Notes |
| --- | --- | --- | --- |
| `/broadcast` | `foliacore.broadcast` | `false` | Global announcement. |
| `/time` | `foliacore.time` | `false` | Change time across worlds. |
| `/weather` | `foliacore.weather` | `false` | Change world weather. |
| `/status` | `foliacore.status` | `false` | Operational status output. |
| `/clearchat` | `foliacore.clearchat`, `foliacore.clearchat.bypass` | mixed | Staff clears chat; bypass protects selected players. |
| `/vanish` | `foliacore.vanish` | `false` | Toggle vanish mode. |
| `/antiraid` | `foliacore.admin.antiraid` | `false` | Anti-raid management. |
| `/ban` | `foliacore.ban`, `foliacore.ban.exempt` | mixed | Ban control and immunity. |
| `/tempban` | `foliacore.tempban`, `foliacore.ban.exempt` | mixed | Temporary ban control. |
| `/unban` | `foliacore.unban` | `false` | Remove a ban. |
| `/kick` | `foliacore.kick`, `foliacore.kick.exempt` | mixed | Kick players, with exempt protection. |
| `/mute` | `foliacore.mute` | `false` | Mute players. |
| `/unmute` | `foliacore.unmute` | `false` | Unmute players. |
| `/marker` | `foliacore.marker.set`, `foliacore.marker.delete`, `foliacore.marker.list` | `false` | Marker workflow. |

## 🎛 Feature Overview

### Player-friendly features

- Homes, warps, spawn travel, and backtracking.
- Chat switching and private communication tools.
- Kit access and kit-specific permissions.
- GPS, calculator, trash, workbench, hat, and scoreboard toggle.
- Mail, nicknames, and block lists.

### Staff-focused features

- Ban, tempban, mute, unmute, kick, and clear chat.
- Vanish, social spy, and staff chat.
- Inventory inspection, repair, feed, heal, fly, and god mode.
- Broadcast, time, weather, and anti-raid controls.

### Developer notes

- Command registration happens in `FoliaCore.java`.
- Permission checks live in the command executors.
- Permission defaults are declared in `paper-plugin.yml`.
- Per-warp and per-kit access can be controlled individually by permission groups.

## 🧩 Visual Blueprint

The animated tab and sidebar system lives in `tab-sidebar.yml`. It includes:

- Header and footer animation methods.
- Sidebar layouts and multi-line presentation.
- Nametags, belowname, bossbar, and ping spoof options.
- Placeholder parsing, fonts, and sprite support.
- MySQL-backed persistence for per-player toggles.

If you want a more visual server identity, this file is where the style lives.

## 🔍 Notes for Server Owners

- If a command is supposed to be public, leave the default permission on `true` or grant it to the group.
- If a command should be staff-only, keep the default on `false` and grant it only to the right people.
- For `/warp` and `/kit`, per-target permissions let you grant access one world, one warp, or one kit at a time.
- If you use LuckPerms, the command map in this README is ready to translate into groups immediately.

## 🧡 Final Word

FoliaCore is meant to reduce clutter, not add it. It gives you a cleaner essentials stack, a smarter permission model, and a more polished runtime experience in one package. For servers that care about appearance, control, and Folia-safe behavior, that combination is the point.

Enjoy the release, and have fun shaping the server around it.