# ⚡ FoliaCore

![Java](https://img.shields.io/badge/Java-21-orange) ![Platform](https://img.shields.io/badge/Platform-Folia-blue) ![Vault](https://img.shields.io/badge/Vault-Compatible-green)

**The essential plugin suite, rebuilt natively for Folia.**

FoliaCore provides the fundamental features every survival or anarchy server needs—Essentials-like commands, Economy, Chat formatting, and Teleportation—but engineered specifically for the multi-threaded environment of [Folia](https://github.com/PaperMC/Folia).

## 🚀 Features

Unlike standard Spigot plugins that crash on Folia due to thread safety issues, **FoliaCore** uses:
* **Region Schedulers** for block/player interactions.
* **Global Schedulers** for server-wide tasks.
* **Async Teleportation** (`player.teleportAsync`) to prevent chunk loading lag.
* **Snapshot Persistence** to save data asynchronously without freezing the main thread.

### 🛠️ Modules
* **Economy:** Built-in **Vault** Economy Provider. No other economy plugin needed.
* **Chat System:** Global, World, and Regional (Radius) chat channels. Includes muting, blocking, and nicknames.
* **Teleportation:** `/home`, `/tpa`, `/warp`, and `/spawn` logic handling cross-region movement safely.
* **Kits:** GUI-based kit preview and redemption with cooldowns.
* **Teams:** Create clans/teams to group up with friends.
* **GPS/Markers:** Save personal waypoints and navigate to them using a real-time Action Bar compass.
* **Mail:** Send offline messages to players.

---

## 📥 Installation

1.  Ensure you are running **Java 21** or higher.
2.  Download `FoliaCore.jar` and place it in your `plugins` folder.
3.  **Required:** Install **[Vault](https://www.spigotmc.org/resources/vault.34315/)**. FoliaCore handles the money, but Vault is required for the API.
4.  Restart your server.

---

## 📜 Commands & Permissions

### 🟢 Player Commands

| Command | Usage | Description | Permission |
| :--- | :--- | :--- | :--- |
| **Chat & Social** | | | |
| `/chat` | `<global/world/local>` | Switch chat channel. | `foliacore.chat` |
| `/msg` | `<player> <msg>` | Send private message. | `foliacore.msg` |
| `/reply` | `<msg>` | Reply to last message. | `foliacore.reply` |
| `/mail` | `send/read/clear` | Manage offline mail. | `foliacore.mail` |
| `/block` | `<player>` | Block a player. | `foliacore.block` |
| `/nick` | `<name/off>` | Set display nickname. | `foliacore.nick` |
| **Teleportation** | | | |
| `/spawn` | | Teleport to spawn. | `foliacore.spawn` |
| `/sethome` | `<name>` | Set a home. | `foliacore.sethome` |
| `/home` | `<name>` | Teleport to home. | `foliacore.home` |
| `/delhome` | `<name>` | Delete a home. | `foliacore.delhome` |
| `/homes` | | List your homes. | `foliacore.homes.list` |
| `/tpa` | `<player>` | Request to teleport to player. | `foliacore.tpa` |
| `/tpahere` | `<player>` | Request player teleport to you. | `foliacore.tpahere` |
| `/tpaccept`| | Accept TPA request. | `foliacore.tpaccept` |
| `/tpdeny` | | Deny TPA request. | `foliacore.tpdeny` |
| `/warp` | `<name>` | Teleport to server warp. | `foliacore.warp` |
| **Gameplay** | | | |
| `/balance` | | Check your wallet. | `foliacore.balance.self` |
| `/pay` | `<player> <amt>` | Send money to players. | `foliacore.pay` |
| `/kit` | `[name]` | Open Kit GUI or claim kit. | `foliacore.kit` |
| `/marker` | `set/del/list` | Manage personal waypoints. | `foliacore.marker` |
| `/gps` | `<name>` | Start compass navigation. | `foliacore.gps` |
| `/team` | `create/invite/etc` | Manage player teams. | `foliacore.team` |

### 🔴 Admin / Staff Commands

| Command | Usage | Description | Permission |
| :--- | :--- | :--- | :--- |
| `/setspawn`| | Set server spawn point. | `foliacore.setspawn` |
| `/setwarp` | `<name>` | Create a server warp. | `foliacore.setwarp` |
| `/delwarp` | `<name>` | Delete a server warp. | `foliacore.delwarp` |
| `/createkit`| `<name> <time>` | Create kit from inventory. | `foliacore.kit.admin` |
| `/delkit` | `<name>` | Delete a kit. | `foliacore.kit.admin` |
| `/eco` | `give/take/set` | Manage player money. | `foliacore.eco` |
| `/mute` | `<player> <time>` | Mute a player. | `foliacore.mute` |
| `/unmute` | `<player>` | Unmute a player. | `foliacore.unmute` |
| `/realname`| `<nickname>` | See real name of nicked user.| `foliacore.realname` |

---

## ⚙️ Configuration

Configuration is split into module-specific files found in `/plugins/FoliaCore/`:

* `chat_data.yml`: Edit chat formatting, channel radius, and global prefixes.
* `kits.yml`: Edit kit items (Base64) and cooldowns manually if needed.
* `warps.yml`: Stores warp locations.
* `teleport_data.yml`: Stores spawn location and player homes.

**Example `chat_data.yml`:**
```yaml
chat-settings:
  enabled: true
  default-mode: "GLOBAL"
  regional-chat-radius: 100
  global-chat-prefix: "!"
  format: "<{DISPLAYNAME}> {MESSAGE}"
```

  
  **🏗️ For Developers**
FoliaCore is a standalone plugin, but if you need to interface with it, it uses standard Bukkit/Paper APIs.

*Building the project:*



`mvn clean package`
Dependency (Maven):

```
<dependency>
    <groupId>dev.ajaretro</groupId>
    <artifactId>folia_core</artifactId>
    <version>1.0-RELEASE</version>
    <scope>provided</scope>
</dependency>
```
**📊 bStats**
This plugin utilizes bStats to collect anonymous usage data. You can disable this in the plugins/bStats/config.yml file.

Plugin ID: 28430

**Developed by AJA RETRO**
