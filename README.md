# ⚡ FoliaCore
> **The native essential suite for modern Folia servers.**

![Platform](https://img.shields.io/badge/platform-Folia-7289DA?style=for-the-badge&logo=paper&logoColor=white)
![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge)
![License](https://img.shields.io/badge/license-MIT-blue?style=for-the-badge)
![Build Status](https://img.shields.io/badge/build-passing-brightgreen?style=for-the-badge)

**FoliaCore** is a Folia-first essentials suite built to provide the everyday commands players expect, without fighting Folia's region-based threading model.

If you are running Folia, you already know the usual Spigot-era core plugins need extra care. FoliaCore is designed around safe teleportation, async-friendly state handling, and clean command coverage so your server feels familiar while staying compatible with Folia's rules.

> 📚 **Quick Navigation:** [Why FoliaCore?](#-why-foliacore-vs-legacy-plugins) • [Features](#-features) • [Setup](#-setup) • [Commands](#-commands--permissions) • [Data Files](#-data-files) • [Developer API](#-developer-api) • [📊 Detailed Comparison](COMPARISON.md)

---

## 🆚 Why FoliaCore? (vs Legacy Plugins)

Legacy core plugins were written for single-threaded servers. Folia is not. FoliaCore keeps the familiar essentials experience while using Folia-native patterns where it matters most.

| Feature | Legacy Plugins on Folia | FoliaCore |
| :--- | :--- | :--- |
| **Teleportation** | Can trigger unsafe teleports or region issues. | Uses Folia-safe teleport handling and async teleports. |
| **Economy** | Often depends on main-thread state and shared mutable data. | Uses thread-safe storage with Vault integration. |
| **Persistence** | Frequently blocks the main thread when saving. | Saves module data through dedicated managers and YAML storage. |
| **Scheduling** | Relies on the standard Bukkit scheduler. | Built for Folia's region-aware scheduling model. |
| **Chat** | Standard listeners without region-aware behavior. | Supports chat modes, moderation, mail, and nicknames. |

> **📊 Want the full comparison?** See [COMPARISON.md](COMPARISON.md) for a feature-by-feature breakdown and migration notes.

---

## 🚀 Features

### 💰 Economy
FoliaCore includes Vault integration and a thread-safe economy layer.

- `/balance` and `/pay` for players.
- `/eco` for administration.
- Local balance storage when the bundled economy provider is active.

### 📍 Teleportation, Homes, Warps, and Spawn
Everything you expect from a core utility plugin, with Folia-safe movement logic.

- Homes: `/sethome`, `/home`, `/delhome`, `/homes`.
- Teleport requests: `/tpa`, `/tpahere`, `/tpaccept`, `/tpdeny`.
- Spawn control: `/setspawn`, `/spawn`.
- Warps: `/setwarp`, `/warp`, `/warps`, `/delwarp`.

### 🎒 Kits
Kits are managed from the game and through the command set.

- `/kit` opens the kit GUI or redeems a kit.
- `/createkit <name> <cooldown>` captures a kit from your inventory.
- `/delkit <name>` removes a kit.
- Item metadata such as names, lore, and enchantments are preserved.

### 🧭 GPS Markers
FoliaCore can act like a lightweight waypoint system for players.

- `/marker set <name>` creates a personal marker.
- `/marker del <name>` removes one.
- `/marker list` shows saved markers.
- `/gps <name>` guides the player to a marker.

### 💬 Chat, Mail, and Moderation
Chat management is a first-class part of the plugin.

- `/chat` supports global, world, and regional chat modes.
- `/msg`, `/reply`, `/block`, and `/unblock` handle private messaging.
- `/mail` provides offline messaging.
- `/mute`, `/unmute`, `/nick`, and `/realname` round out the moderation and identity tools.

### 🧑‍🤝‍🧑 Teams
`/team` provides team management with subcommands for creation, invites, joins, kicks, and disbanding.

---

## 📥 Setup

### From a release
1. Stop your server.
2. Download the latest release jar from [Releases](https://github.com/AJA-Retro/FoliaCore/releases).
3. Install [Folia](https://github.com/PaperMC/Folia) on a supported Java 21 runtime.
4. Add the FoliaCore jar to your `plugins` folder.
5. Install [Vault](https://www.spigotmc.org/resources/vault.34315/) if you want the economy features to register through Vault.
6. Start the server and let the plugin generate its data files.

### From source
1. Install Java 21 and Maven.
2. Clone the repository.
3. Run:

```bash
mvn clean package
```

4. Use the shaded jar from `target/` and place it in your server's `plugins` folder.

### First run checklist
- Confirm the plugin loads on Folia, not Spigot/Paper.
- Check the console for the Vault economy registration message.
- Review the generated YAML files in the plugin directory.
- Adjust permissions and economy settings to match your server setup.

---

## 📜 Commands & Permissions

### Chat, Mail, and Identity
| Command | Permission | Aliases | Description |
| :--- | :--- | :--- | :--- |
| `/mute <player> <time|permanent>` | `foliacore.mute` |  | Mutes a player for a time or permanently. |
| `/unmute <player>` | `foliacore.unmute` |  | Removes a mute. |
| `/msg <player> <message...>` | `foliacore.msg` | `m`, `tell`, `w` | Sends a private message. |
| `/reply <message...>` | `foliacore.reply` | `r` | Replies to the last private message. |
| `/block <player>` | `foliacore.block` |  | Blocks direct messages from a player. |
| `/unblock <player>` | `foliacore.unblock` |  | Unblocks a player. |
| `/mail <send|read|clear>` | `foliacore.mail` | `email` | Manages offline mail. |
| `/chat <global|world|regional>` | `foliacore.chat` | `ch` | Switches chat mode. |
| `/nick <name|off>` | `foliacore.nick` |  | Changes your nickname. |
| `/realname <nickname>` | `foliacore.realname` |  | Looks up the real name behind a nickname. |

### Teleportation, Homes, Warps, and Spawn
| Command | Permission | Aliases | Description |
| :--- | :--- | :--- | :--- |
| `/sethome [name]` | `foliacore.sethome` |  | Sets a home at your location. |
| `/home [name]` | `foliacore.home` |  | Teleports to one of your homes. |
| `/delhome <name>` | `foliacore.delhome` |  | Deletes a home. |
| `/homes` | `foliacore.homes.list` |  | Lists your homes. |
| `/tpa <player>` | `foliacore.tpa` |  | Requests to teleport to another player. |
| `/tpahere <player>` | `foliacore.tpahere` |  | Requests that another player teleports to you. |
| `/tpaccept` | `foliacore.tpaccept` |  | Accepts a pending teleport request. |
| `/tpdeny` | `foliacore.tpdeny` |  | Denies a pending teleport request. |
| `/setspawn` | `foliacore.setspawn` |  | Sets the server spawn. |
| `/spawn` | `foliacore.spawn` |  | Teleports to spawn. |
| `/setwarp <name>` | `foliacore.setwarp` |  | Creates a warp. |
| `/delwarp <name>` | `foliacore.delwarp` |  | Deletes a warp. |
| `/warp <name>` | `foliacore.warp` |  | Teleports to a warp. |
| `/warps` | `foliacore.warps.list` | `warplist` | Lists available warps. |

### Kits, Teams, Markers, and GPS
| Command | Permission | Aliases | Description |
| :--- | :--- | :--- | :--- |
| `/team <subcommand>` | `foliacore.team` | `t`, `party` | Manages your team. |
| `/kit [name]` | `foliacore.kit` |  | Opens the kit GUI or redeems a kit. |
| `/createkit <name> <cooldown>` | `foliacore.kit.admin` |  | Creates a kit from your inventory. |
| `/delkit <name>` | `foliacore.kit.admin` |  | Deletes a kit. |
| `/marker <set|del|list>` | `foliacore.marker` |  | Manages personal markers. |
| `/gps <name|off>` | `foliacore.gps` |  | Navigates to a saved marker or turns GPS off. |

### Economy
| Command | Permission | Aliases | Description |
| :--- | :--- | :--- | :--- |
| `/balance [player]` | `foliacore.balance.self` | `bal`, `money` | Checks a balance. |
| `/pay <player> <amount>` | `foliacore.pay` |  | Sends money to another player. |
| `/eco <give|take|set> <player> <amount>` | `foliacore.eco` |  | Admin economy control. |

### Permission Notes
- Most player-facing commands default to `true` in `paper-plugin.yml`.
- Admin-only commands default to `op` unless explicitly noted otherwise.
- Home limits are controlled through permissions such as `foliacore.homes.default`, `foliacore.homes.5`, `foliacore.homes.10`, and `foliacore.homes.unlimited`.

---

## 🗂 Data Files

FoliaCore stores module data in the plugin folder and reloads it on startup.

- `chat_data.yml` for chat modes, mutes, blocks, mail, and nicknames.
- `teleport_data.yml` for homes, spawn, and teleport state.
- `kits.yml` for kit definitions.
- `markers.yml` for player markers.
- `team_data.yml` for team data.
- `warps.yml` for warp locations.

These files are created and maintained automatically by the plugin. Back them up if you want to preserve player progress across server moves or resets.

---

## 🔧 Developer API

FoliaCore is also designed to be used as a library inside other plugins.

```java
public final class MyPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        FoliaCore core = FoliaCore.getInstance();

        if (core != null) {
            core.getChatManager().sendMail(senderUuid, targetUuid, "Welcome to the server!");
            double balance = core.getEconomyManager().getBalance(player);
            core.getMessenger().sendSuccess(player, "FoliaCore is ready.");
        }
    }
}
```

Available managers include chat, teleportation, teams, kits, warps, markers, economy, and the shared messenger utility.

---

## ❓ Frequently Asked Questions

### Can I use EssentialsX with Folia?
You generally should not. FoliaCore is built for Folia-native behavior, while legacy plugins are usually designed for single-threaded server models.

### Can I migrate from EssentialsX or CMI?
Yes, but the process is mostly manual today. Export the data you care about, then map it into FoliaCore's YAML files or command set. See [COMPARISON.md](COMPARISON.md) for guidance and feature coverage.

### Is FoliaCore feature-complete?
It covers the core essentials: chat, mail, moderation, homes, warps, spawn, kits, GPS, teams, and economy. Check [COMPARISON.md](COMPARISON.md) for the current scope and any planned gaps.

### Can I run it on Paper or Spigot?
No. FoliaCore is designed specifically for Folia.

### Do I need Vault?
Vault is the integration layer used for economy support. Install it if you want the economy features to register and function as intended.

---

## 🤝 Support & Links

- **Website:** [ajaretro.dev](https://ajaretro.dev)
- **Bug Reports:** [GitHub Issues](https://github.com/AJA-Retro/FoliaCore/issues)

---

**Developed with ❤️ by [Niloy](https://ajaretro.dev)**
