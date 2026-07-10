# FoliaCore v6.0-Ragnarok

FoliaCore is an essentials and administration suite built specifically for Folia servers. Instead of running multiple separate plugins, it consolidates core utilities into a single toolkit designed from the ground up for multi-threaded safety and regionalized performance.

The **v6.0-Ragnarok** release features integrated command modules, a dual storage layer (YAML and MySQL), an optional lightweight Discord bridge, and native VaultUnlocked economy integration.

---

## Why FoliaCore?

Minecraft servers running Folia require utilities that are thread-safe and region-aware. FoliaCore avoids legacy synchronous scheduling blocking and schedules operations appropriately across region threads.

### Key Features:
*   **Folia-Native Scheduling:** Actions like projectile teleports, item interactions, and block checks run safely on regional threads using a custom `FoliaScheduler`.
*   **Multi-Engine Storage:** Supports local YAML file storage and MySQL database backends (with HikariCP connection pooling) for synchronizing data across multiple server instances.
*   **VaultUnlocked Economy:** Native integration with the VaultUnlocked economy API fork with asynchronous transaction queries.
*   **Built-in Discord Bridge:** WebSockets-based chat synchronization and server status broadcasting with minimal resource overhead.
*   **Modular Control:** Turn individual feature modules on or off inside the configuration file.

---

## Quick Start

1. Place the `folia_core-v6.0-Ragnarok.jar` inside your server's `plugins/` directory.
2. Run your server using Folia and Java 21.
3. Edit `plugins/FoliaCore/config.yml` to set your database storage type and toggle features.
4. Set up permissions in your preferred permission manager.

---

## Command & Permission Reference (160+ Commands)

*(For the full lists of commands, descriptions, and permissions, please visit the project's [GitHub Repository](https://github.com/AJARETRO/FoliaCore) or [Official Website](https://ajaretro.dev/FoliaCore.html))*

---

## Contact & Support

For support, custom developments, or other inquiries, contact AJA RETRO:
*   **WhatsApp:** +8801989208751
