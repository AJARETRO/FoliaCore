# 📊 FoliaCore vs Legacy Essential Plugins

## Overview

This document provides a comprehensive comparison between **FoliaCore** and traditional essential plugins designed for Spigot/Paper servers, such as **EssentialsX**, **CMI**, and **EssentialsXSpawn**.

### Why This Matters

Folia is Paper's experimental multi-threaded fork that fundamentally changes how Minecraft servers handle game logic. Traditional plugins that worked perfectly on Spigot/Paper often **break catastrophically** on Folia due to their assumptions about single-threaded execution.

**FoliaCore** is built from the ground up for Folia's architecture, ensuring thread-safety, region awareness, and async operations at its core.

---

## 🏗️ Architecture Comparison

### Thread Model

| Aspect | Legacy Plugins (Spigot/Paper) | FoliaCore (Folia) |
|--------|------------------------------|-------------------|
| **Execution Model** | Single-threaded (Main Thread) | Multi-threaded (Region-based) |
| **Scheduler** | Bukkit Scheduler (broken on Folia) | Folia Region Scheduler & Entity Scheduler |
| **Teleportation** | `player.teleport()` (synchronous) | `teleportAsync()` (cross-region safe) |
| **Data Access** | Standard HashMap/ArrayList | `ConcurrentHashMap` & atomic operations |
| **Task Scheduling** | `Bukkit.getScheduler()` | `server.getGlobalRegionScheduler()` |
| **Event Handling** | Main thread only | Async-safe with regional context |

### Safety Guarantees

**Legacy Plugins on Folia:**
- ❌ Frequent "Unsafe Teleport" crashes
- ❌ Race conditions in economy/data systems
- ❌ Server freezes from main-thread blocking
- ❌ Data corruption during cross-region interactions
- ❌ Plugin errors: "Scheduler not supported"

**FoliaCore:**
- ✅ Zero unsafe teleport exceptions
- ✅ Thread-safe economy with atomic transactions
- ✅ Non-blocking async data saves
- ✅ Region-aware command execution
- ✅ Native Folia scheduler integration

---

## 🎯 Feature Parity Matrix

### Core Features

| Feature | EssentialsX | CMI | FoliaCore |
|---------|-------------|-----|-----------|
| **Economy System** | ✅ | ✅ | ✅ (Thread-safe) |
| **Vault Integration** | ✅ | ✅ | ✅ |
| **Homes** | ✅ | ✅ | ✅ (Async) |
| **Warps** | ✅ | ✅ | ✅ (Async) |
| **TPA System** | ✅ | ✅ | ✅ (Cross-region safe) |
| **Spawn** | ✅ (separate plugin) | ✅ | ✅ (Built-in) |
| **Kits** | ✅ (config-based) | ✅ (GUI) | ✅ (In-game creation + GUI) |
| **Private Messages** | ✅ | ✅ | ✅ |
| **Mail System** | ✅ | ✅ | ✅ |
| **Nicknames** | ✅ | ✅ | ✅ |
| **Mute System** | ✅ | ✅ | ✅ (Time-based) |
| **Chat Channels** | ❌ | ✅ | ✅ (Global/World/Regional) |
| **GPS Navigation** | ❌ | ✅ (limited) | ✅ (Action Bar guide) |
| **Teams** | ❌ | ❌ | ✅ |
| **Folia Compatible** | ❌ **Broken** | ❌ **Broken** | ✅ **Native** |

### Teleportation Features

| Feature | EssentialsX | CMI | FoliaCore |
|---------|-------------|-----|-----------|
| `/home` | ✅ (crashes on Folia) | ✅ (crashes on Folia) | ✅ (Safe) |
| `/sethome` | ✅ | ✅ | ✅ |
| `/delhome` | ✅ | ✅ | ✅ |
| Home Limits | ✅ (permissions) | ✅ | ✅ (permissions) |
| `/warp` | ✅ (unsafe) | ✅ (unsafe) | ✅ (Region-aware) |
| `/tpa` | ✅ (often crashes) | ✅ (often crashes) | ✅ (Cross-region safe) |
| `/tpahere` | ✅ | ✅ | ✅ |
| TPA Timeout | ✅ | ✅ | ✅ |
| `/spawn` | ✅ (separate plugin) | ✅ | ✅ |
| **Async Implementation** | ❌ | ❌ | ✅ |

### Economy Features

| Feature | EssentialsX | CMI | FoliaCore |
|---------|-------------|-----|-----------|
| Basic Economy | ✅ | ✅ | ✅ |
| Vault Provider | ✅ | ✅ | ✅ |
| `/balance` | ✅ | ✅ | ✅ |
| `/pay` | ✅ | ✅ | ✅ |
| `/eco` (admin) | ✅ | ✅ | ✅ |
| Thread Safety | ❌ | ⚠️ Partial | ✅ **Full** |
| Race Condition Protection | ❌ | ⚠️ Partial | ✅ (Atomic ops) |
| **Safe on Folia** | ❌ | ❌ | ✅ |

### Chat & Communication

| Feature | EssentialsX | CMI | FoliaCore |
|---------|-------------|-----|-----------|
| Private Messages | ✅ | ✅ | ✅ |
| `/reply` | ✅ | ✅ | ✅ |
| Offline Mail | ✅ | ✅ | ✅ |
| Chat Formatting | ✅ | ✅ | ✅ |
| Chat Channels | ❌ | ✅ | ✅ (Global/World/Regional) |
| `/mute` | ✅ | ✅ | ✅ |
| Time-based Mute | ✅ | ✅ | ✅ |
| `/block` (player blocking) | ❌ | ✅ | ✅ |
| Regional Chat | ❌ | ❌ | ✅ **Unique** |

### Kit System

| Feature | EssentialsX | CMI | FoliaCore |
|---------|-------------|-----|-----------|
| Kits | ✅ (config files) | ✅ (GUI) | ✅ (GUI) |
| Kit Cooldowns | ✅ | ✅ | ✅ |
| In-Game Creation | ❌ | ✅ (complex) | ✅ **Simple** |
| NBT Preservation | ✅ | ✅ | ✅ |
| Automatic GUI | ❌ | ✅ | ✅ |
| Kit Deletion | ❌ (manual config edit) | ✅ | ✅ |

### Advanced Features

| Feature | EssentialsX | CMI | FoliaCore |
|---------|-------------|-----|-----------|
| GPS Navigation | ❌ | ⚠️ Basic | ✅ (Action Bar) |
| Personal Waypoints | ❌ | ⚠️ Limited | ✅ (`/marker`) |
| Team System | ❌ | ❌ | ✅ |
| Developer API | ⚠️ Limited | ✅ | ✅ |
| Async Data Saving | ❌ | ⚠️ Partial | ✅ |

---

## 🚀 Performance Comparison

### Data Persistence

| Plugin | Save Method | Performance Impact | Folia-Safe |
|--------|-------------|-------------------|------------|
| **EssentialsX** | Main thread (blocking) | ⚠️ Lag spikes | ❌ |
| **CMI** | Mixed (mostly main thread) | ⚠️ Moderate lag | ❌ |
| **FoliaCore** | Async snapshots | ✅ Zero lag | ✅ |

### Memory Usage

| Plugin | Data Structures | Thread Safety Overhead |
|--------|----------------|------------------------|
| **EssentialsX** | Standard Java collections | None (single-threaded) |
| **CMI** | Mixed collections | Low |
| **FoliaCore** | Concurrent collections | Minimal (optimized) |

### Teleportation Performance

**Scenario:** Player teleports from Region A to Region B (1000 blocks away)

| Plugin | Method | Result on Folia |
|--------|--------|----------------|
| **EssentialsX** | `player.teleport(location)` | 🔴 **Server crash** or "Unsafe Teleport" error |
| **CMI** | `player.teleport(location)` | 🔴 **Server crash** or rejection |
| **FoliaCore** | `teleportAsync()` | 🟢 **Smooth transition** |

---

## 🔄 Migration Guide

### From EssentialsX to FoliaCore

#### 1. **Pre-Migration**
```bash
# Backup your data
cp -r plugins/Essentials plugins/Essentials.backup
```

#### 2. **Data Conversion**

**Homes:**
- EssentialsX: Stored in `userdata/*.yml`
- FoliaCore: Stored in `homes.yml` (auto-imports if needed)

**Economy:**
- EssentialsX: Stored in `userdata/*.yml`
- FoliaCore: Stored in `economy.yml` (manual migration recommended)

**Warps:**
- EssentialsX: Stored in `warps/*.yml`
- FoliaCore: Stored in `warps.yml` (can be manually converted)

#### 3. **Command Mapping**

Most commands are **identical**:

| EssentialsX | FoliaCore | Notes |
|-------------|-----------|-------|
| `/home` | `/home` | Same |
| `/sethome` | `/sethome` | Same |
| `/balance` | `/balance` | Same |
| `/pay` | `/pay` | Same |
| `/tpa` | `/tpa` | Same |
| `/msg` | `/msg` | Same |
| `/warp` | `/warp` | Same |
| `/spawn` | `/spawn` | No separate plugin needed |

#### 4. **Permission Mapping**

| EssentialsX | FoliaCore | Notes |
|-------------|-----------|-------|
| `essentials.home` | `foliacore.home` | Change prefix |
| `essentials.sethome` | `foliacore.sethome` | Change prefix |
| `essentials.balance` | `foliacore.balance.self` | Slightly different |
| `essentials.pay` | `foliacore.pay` | Change prefix |

### From CMI to FoliaCore

CMI is more feature-rich but **completely broken on Folia**. Focus on core features:

1. Export economy data
2. Export homes/warps
3. Manually recreate kits in FoliaCore using `/createkit`
4. Update permissions to use `foliacore.*` prefix

---

## ⚡ Why Choose FoliaCore?

### ✅ If You're Using Folia

**You MUST use FoliaCore** (or similar Folia-native plugins). Legacy plugins will:
- Crash your server
- Corrupt player data
- Cause "Unsafe Teleport" errors constantly
- Fail to load with scheduler errors

### ✅ Future-Proofing

Even if you're on Paper today, Folia represents the future of Minecraft server performance. FoliaCore ensures you're ready to migrate when needed.

### ✅ Simplicity

- **All-in-one**: No need for EssentialsX + EssentialsXChat + EssentialsXSpawn
- **In-game configuration**: Create kits without editing YAML
- **Native economy**: No external economy plugin required

### ✅ Modern Features

- GPS navigation with action bar guidance
- Regional chat for Folia's region-based worlds
- Team system for player collaboration
- Clean, intuitive API for developers

---

## 🎮 Real-World Usage Scenarios

### Scenario 1: Player Uses `/home` on Folia

**EssentialsX:**
```
[ERROR] Unsafe teleport detected!
[SEVERE] Player cannot be teleported across regions
Server crashes or player is stuck
```

**FoliaCore:**
```
✅ Teleporting to home "base"...
Player smoothly transitions between regions
```

### Scenario 2: Economy Transaction During Combat

**EssentialsX on Folia:**
```
[ERROR] Race condition in economy
Player A and Player B both access same balance
Data corruption: Money duplicated or lost
```

**FoliaCore:**
```
✅ Thread-safe atomic transaction
Correct balance maintained for both players
No data loss or duplication
```

### Scenario 3: Player Claims Kit While Server Saves

**EssentialsX:**
```
Server freezes for 2-3 seconds (main thread blocked)
Player experiences lag spike
```

**FoliaCore:**
```
✅ Async snapshot taken in background
Zero server lag
Player receives kit instantly
```

---

## 📈 Feature Roadmap

### Current (v1.0)
- ✅ Thread-safe economy
- ✅ Async teleportation
- ✅ Kit GUI system
- ✅ GPS navigation
- ✅ Chat channels
- ✅ Team system

### Planned (v1.1+)
- 🔄 Automatic data import from EssentialsX
- 🔄 More admin tools (`/invsee`, `/enderchest`)
- 🔄 Advanced punishment system (`/ban`, `/kick`, `/tempban`)
- 🔄 Back command for teleport history
- 🔄 More chat features (formatting, mentions)

---

## 🤝 Support & Contribution

### Getting Help

- **Issues:** [GitHub Issues](https://github.com/AJARETRO/FoliaCore/issues)
- **Website:** [ajaretro.dev](https://ajaretro.dev)

### Contributing

FoliaCore is open-source under the MIT License. Contributions are welcome!

---

## 📝 Summary Table

| Criteria | EssentialsX | CMI | FoliaCore |
|----------|-------------|-----|-----------|
| **Folia Compatible** | ❌ No | ❌ No | ✅ **Yes** |
| **Thread-Safe** | ❌ No | ⚠️ Partial | ✅ **Yes** |
| **Feature Complete** | ✅ Yes | ✅ Yes | ⚠️ Growing |
| **Economy Included** | ✅ Yes | ✅ Yes | ✅ Yes |
| **Easy Setup** | ⚠️ Medium | ⚠️ Complex | ✅ **Easy** |
| **Performance** | ⚠️ Moderate | ⚠️ Moderate | ✅ **High** |
| **Active Development** | ✅ Yes | ✅ Yes | ✅ Yes |
| **Price** | 💰 Free | 💰 $15-20 | 💰 **Free** |
| **Best For** | Paper/Spigot | Paper/Spigot | **Folia** |

---

**TL;DR:** If you're running a **Folia** server, FoliaCore is your only viable option for essential features. Legacy plugins will crash your server. If you're on Paper/Spigot, you can use any plugin, but FoliaCore future-proofs your server for eventual Folia migration while offering modern features and superior performance.
