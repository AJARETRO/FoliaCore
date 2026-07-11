# FoliaCore vs Legacy Plugins

This document compares FoliaCore v6.0-Ragnarok and legacy essentials plugins on a regionalized server framework (like Folia).

| Aspect | Legacy Plugins (Spigot/Paper) | FoliaCore (Folia-Native) |
| :--- | :--- | :--- |
| **Scheduler Compliance** | Sync-dependent Bukkit scheduler, causing thread warnings or exceptions on Folia. | Region-aware scheduler, processing tasks on chunk threads. |
| **Storage Architecture** | Flat-file locks per-player. | Dual YAML and MySQL backend storage with HikariCP connection pooling. |
| **Economy Engine** | Synchronous Vault calls that block tick loops. | Asynchronous VaultUnlocked transaction wrappers. |
| **Discord Bot Integration** | Heavy library stacks (like JDA) running on main threads. | Lightweight WebSocket and HTTP calls with zero heavy dependencies. |
| **Area Actions** | Global checks that span loaded regions. | Location-aware regional tasks matching player region loops. |

---

## Technical Performance

Folia separates chunk ticking into distinct region threads. Standard plugins written for single-threaded Spigot/Paper can cause performance spikes or instability when running commands, accessing player data, or performing teleports. FoliaCore scheduling conforms to Folia's regional lifecycle, protecting stability and avoiding thread-lock issues.

---

## 💬 Contact & Custom Quotes

Need direct support or looking to quote a custom plugin? We design and build everything from **lightweight server utility plugins** up to **high-concurrency enterprise-grade server systems**:
*   🟢 **WhatsApp:** [+880 1989-208751](https://wa.me/8801989208751)
*   ✉️ **Quotes & Support:** Get in touch directly on WhatsApp for dedicated support, licensing, or custom software inquiries.
