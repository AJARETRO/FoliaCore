# ⚡ FoliaCore
> **The native essential suite for the next generation of Minecraft servers.**

![Platform](https://img.shields.io/badge/platform-Folia-7289DA?style=for-the-badge&logo=paper&logoColor=white)
![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge)
![License](https://img.shields.io/badge/license-MIT-blue?style=for-the-badge)
![Build Status](https://img.shields.io/badge/build-passing-brightgreen?style=for-the-badge)

**FoliaCore** is a high-performance, multithreaded replacement for plugins like *EssentialsX* or *CMI*, designed specifically for **[Folia](https://github.com/PaperMC/Folia)**.

If you are running a Folia server, you know that standard plugins break. They freeze the server, cause "Unsafe Teleport" crashes, or corrupt player data because they don't understand Region Scheduling. **FoliaCore fixes this.**

---

## 🆚 Why FoliaCore? (vs EssentialsX)

Standard core plugins were built for Spigot—a single-threaded software. Folia is multi-threaded. Here is why you need FoliaCore:

| Feature | ❌ EssentialsX (on Folia) | ✅ FoliaCore |
| :--- | :--- | :--- |
| **Teleportation** | Often causes server crashes or "Unsafe" exceptions. | Uses `teleportAsync` to safely move players between regions. |
| **Economy** | Can lag the main thread or cause race conditions. | **Thread-safe implementation** using `ConcurrentHashMap` & atomic operations. |
| **User Data** | Saves data on the main thread (Lag spikes). | Saves data asynchronously using snapshots (No lag). |
| **Scheduling** | Uses standard Bukkit Scheduler (Breaks on Folia). | Built natively on the **Folia Region Scheduler**. |
| **Chat** | Standard listeners. | Async Chat Event handling with radius support. |

---

## 🚀 Features

### 💰 Native Economy
FoliaCore includes a built-in, **Vault-compatible** economy provider. You do not need an external economy plugin.
* Fully Thread-Safe.
* Commands: `/balance`, `/pay`, `/eco`.
* Data persistence to `economy.yml`.

### 📍 Smart Teleportation
Move players across threaded regions without crashing your server.
* **Homes:** `/sethome`, `/home`, `/delhome` (With limit permissions).
* **Warps:** `/setwarp`, `/warp` (Global server waypoints).
* **TPA System:** `/tpa`, `/tpahere`, `/tpaccept` (With timeout expiry).
* **Spawn:** Safe `/spawn` handling.

### 🎒 Kits & GUI
* **In-Game Creation:** Create kits instantly by arranging your inventory and typing `/createkit <name> <cooldown>`.
* **GUI Menu:** Beautiful, automatic `/kit` GUI.
* **NBT Support:** Preserves custom item names, lore, and enchantments perfectly.

### 🧭 GPS Navigation
* Save personal waypoints with `/marker set <name>`.
* Navigate to them using `/gps <name>`.
* **Visual Guide:** Shows an arrow and distance in the Action Bar (e.g., `⬆ MyBase | 150m`).

### 💬 Chat Management
* **Channels:** Switch between Global, World, and Regional chat (`/chat`).
* **Formatting:** customizable prefixes and suffixes.
* **Moderation:** `/mute`, `/unmute` (Time-based), `/block`.
* **Mail:** Send offline messages with `/mail`.

---

## 📥 Installation

1.  Stop your server.
2.  Download the latest `FoliaCore.jar` from [Releases](https://github.com/AJA-Retro/FoliaCore/releases).
3.  **Soft-Required:** Install [Vault](https://www.spigotmc.org/resources/vault.34315/) (FoliaCore hooks into Vault to provide the economy).
4.  Place both JARs in your `plugins` folder.
5.  Start the server.

---

## 📜 Commands & Permissions

### Player Commands
| Command | Permission | Description |
| :--- | :--- | :--- |
| `/balance` | `foliacore.balance.self` | Check your current balance. |
| `/pay <player> <amount>` | `foliacore.pay` | Send money to another player. |
| `/tpa <player>` | `foliacore.tpa` | Request to teleport to someone. |
| `/sethome [name]` | `foliacore.sethome` | Set a home at your location. |
| `/kit` | `foliacore.kit` | Open the Kit GUI. |
| `/marker set <name>` | `foliacore.marker` | Set a GPS waypoint. |
| `/gps <name>` | `foliacore.gps` | Start navigation to a waypoint. |
| `/msg <player>` | `foliacore.msg` | Send a private message. |

### Admin Commands
| Command | Permission | Description |
| :--- | :--- | :--- |
| `/eco <give/take/set>` | `foliacore.eco` | Modify player balances. |
| `/createkit <name>` | `foliacore.kit.admin` | Create a kit from inventory. |
| `/setwarp <name>` | `foliacore.setwarp` | Set a server warp. |
| `/setspawn` | `foliacore.setspawn` | Set the global spawn point. |
| `/mute <player> <time>` | `foliacore.mute` | Temporarily mute a player. |
| `/invsee <player>` | `foliacore.invsee` | *(Coming in Admin Addon)* |

---

## 🔧 Developer API

FoliaCore is designed to be a library for your server. You can access its managers to handle logic safely.

```java
public class MyPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        // Get the API instance
        FoliaCore core = FoliaCore.getInstance();
        
        // Example: Send mail to a player safely
        core.getChatManager().sendMail(senderUUID, targetUUID, "Welcome to the server!");
        
        // Example: Check a balance directly (or use Vault)
        double bal = core.getEconomyManager().getBalance(player);
    }
}
```
## 🤝 Support & Links

* **Website:** [ajaretro.dev](https://ajaretro.dev)
* **Bug Reports:** [GitHub Issues](https://github.com/AJA-Retro/FoliaCore/issues)

---
**Developed with ❤️ by [Niloy](https://ajaretro.dev)**
