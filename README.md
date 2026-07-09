# ✨ FoliaCore v5.8 BloodyMary

FoliaCore is a Folia-first essentials and administration suite for Minecraft servers that want a single polished toolkit instead of a pile of separate plugins. It is built from the ground up for regionalized runtime behavior, multi-threaded safety, visual flair, and day-to-day convenience without dragging in a heavy dependency stack.

This release, **v5.8 BloodyMary**, is a massive expansion that upgrades the plugin into a complete, native Essentials suite, adding 70+ commands, a unified flat-file/MySQL storage layer, a lightweight built-in Discord Bot gateway, and full integration with the **VaultUnlocked** economy API fork.

---

## 💙 Why Use FoliaCore

FoliaCore exists because modern servers need more than a bunch of legacy Spigot/Paper commands with a Folia patch on top. A good essentials suite should feel alive, organized, and thread-safe under the server model it runs on.

### Key Features:
*   **Folia-Native Scheduling:** All tick-reliant operations (like projectile explosions, item interactions, block edits) run on location/entity regional threads via the `FoliaScheduler`.
*   **Unified Multi-Engine Storage:** Supports flat-file/YAML caches and a remote MySQL database utilizing HikariCP connection pooling for multi-server synchronization.
*   **VaultUnlocked Economy:** Native integration with the VaultUnlocked API fork for regional economy balances.
*   **Lightweight Discord Gateway:** Dynamic chat linking and server broadcasts utilizing Java's native WebSockets and HTTP/JSON APIs (zero heavy external dependencies).
*   **Modular Architecture:** Every module (economy, jails, discord, etc.) can be toggled on/off in `config.yml`.
*   **Granular Permissions:** Sane permission structures for every single command.

---

## 🚀 Quick Start

1. Put the `folia_core-v5.8-BloodyMary.jar` in your server `plugins/` folder.
2. Start the server on Folia with Java 21.
3. Configure `plugins/FoliaCore/config.yml` to specify your storage type (`yaml` or `mysql`) and toggle modules.
4. Assign permissions to players or groups using your permission manager.

---

## 📟 Command & Permission Reference

### 1. Economy Module
| Command | Permission | Default | Description |
| :--- | :--- | :--- | :--- |
| `/balance` | `foliacore.balance` | `true` | View player balance. |
| `/balancetop` | `foliacore.balancetop` | `true` | View top balances. |
| `/pay` | `foliacore.pay` | `true` | Send money to another player. |
| `/paytoggle` | `foliacore.paytoggle` | `true` | Toggle accepting payments. |
| `/payconfirmtoggle` | `foliacore.payconfirmtoggle` | `true` | Toggle payment confirmation. |
| `/eco` | `foliacore.eco` | `false` | Admin economy management (give, take, set). |
| `/sell` | `foliacore.sell` | `true` | Sell held items. |
| `/worth` | `foliacore.worth` | `true` | Inspect item price. |
| `/setworth` | `foliacore.setworth` | `false` | Set item price. |

### 2. Jail Module
| Command | Permission | Default | Description |
| :--- | :--- | :--- | :--- |
| `/jail` | `foliacore.jail` | `false` | Jails a player. |
| `/unjail` | `foliacore.unjail` | `false` | Unjails a player. |
| `/setjail` | `foliacore.setjail` | `false` | Sets a jail location. |
| `/deljail` | `foliacore.deljail` | `false` | Deletes a jail location. |
| `/jails` | `foliacore.jails` | `true` | Lists jail locations. |
| `/jailedplayers` | `foliacore.jailedplayers` | `false` | Lists jailed players. |

### 3. Thor, Projectiles & Explosives
| Command | Permission | Default | Description |
| :--- | :--- | :--- | :--- |
| `/antioch` | `foliacore.antioch` | `false` | Spawns active TNT at crosshairs. |
| `/beezooka` | `foliacore.beezooka` | `false` | Shoots an exploding bee. |
| `/fireball` | `foliacore.fireball` | `false` | Fires projectiles (fireball, snowballs, eggs, etc.). |
| `/lightning` | `foliacore.lightning` | `false` | Strikes lightning at crosshairs. |
| `/nuke` | `foliacore.nuke` | `false` | Spawns a rain of primed TNT. |
| `/spawnmob` | `foliacore.spawnmob` | `false` | Spawns a mob type. |
| `/kittycannon` | `foliacore.kittycannon` | `false` | Launches an exploding ocelot. |
| `/tree` | `foliacore.tree` | `false` | Spawns a tree type. |
| `/remove` | `foliacore.remove` | `false` | Butchering cleanup utility. |

### 4. Discord Integration
| Command | Permission | Default | Description |
| :--- | :--- | :--- | :--- |
| `/discord` | `foliacore.discord` | `true` | View Discord information. |
| `/link` | `foliacore.link` | `true` | Links Minecraft account to Discord. |
| `/unlink` | `foliacore.unlink` | `true` | Unlinks Minecraft account from Discord. |
| `/discordbroadcast` | `foliacore.discordbroadcast` | `false` | Broadcasts message to Discord channel. |

### 5. Chat Filters & Ignores
| Command | Permission | Default | Description |
| :--- | :--- | :--- | :--- |
| `/ignore` | `foliacore.ignore` | `true` | Ignores a player's messages. |
| `/unignore` | `foliacore.unignore` | `true` | Unignores a player. |
| `/ignorelist` | `foliacore.ignorelist` | `true` | List ignored players. |
| `/msgtoggle` | `foliacore.msgtoggle` | `true` | Blocks incoming private messages. |
| `/rtoggle` | `foliacore.rtoggle` | `true` | Toggles message reply target filter. |

### 6. Worktable GUIs
| Command | Permission | Default | Description |
| :--- | :--- | :--- | :--- |
| `/anvil` | `foliacore.anvil` | `true` | Opens virtual anvil. |
| `/grindstone` | `foliacore.grindstone` | `true` | Opens virtual grindstone. |
| `/loom` | `foliacore.loom` | `true` | Opens virtual loom. |
| `/smithingtable` | `foliacore.smithingtable` | `true` | Opens virtual smithing table. |
| `/stonecutter` | `foliacore.stonecutter` | `true` | Opens virtual stonecutter. |
| `/cartographytable` | `foliacore.cartographytable` | `true` | Opens virtual cartography table. |

### 7. IP Ban Management
| Command | Permission | Default | Description |
| :--- | :--- | :--- | :--- |
| `/banip` | `foliacore.banip` | `false` | IP bans a player. |
| `/tempbanip` | `foliacore.tempbanip` | `false` | Temporarily IP bans a player. |
| `/unbanip` | `foliacore.unbanip` | `false` | Revokes an IP ban. |
