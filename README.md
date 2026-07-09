# ✨ FoliaCore v5.8 BloodyMary

FoliaCore is a Folia-first essentials and administration suite for Minecraft servers that want a single polished toolkit instead of a pile of separate plugins. It is built from the ground up for regionalized runtime behavior, multi-threaded safety, visual flair, and day-to-day convenience without dragging in a heavy dependency stack.

This release, **v5.8 BloodyMary**, is a massive expansion that upgrades the plugin into a complete, native Essentials suite, featuring 160+ commands, a unified flat-file/MySQL storage layer, a lightweight built-in Discord Bot gateway, and full integration with the **VaultUnlocked** economy API fork.

---

## 💙 Why Use FoliaCore

FoliaCore exists because modern servers need more than a bunch of legacy Spigot/Paper commands with a Folia patch on top. A good essentials suite should feel alive, organized, and thread-safe under the server model it runs on.

### Key Features:
*   **Folia-Native Scheduling:** All tick-reliant operations (like projectile explosions, item interactions, block edits) run on location/entity regional threads via the `FoliaScheduler`.
*   **Unified Multi-Engine Storage:** Supports flat-file/YAML caches and a remote MySQL database utilizing HikariCP connection pooling for multi-server synchronization.
*   **VaultUnlocked Economy:** Asynchronous database calls wrap around VaultUnlocked for lag-free payments.
*   **Lightweight Discord Gateway:** Dynamic chat linking and server broadcasts utilizing native WebSockets.
*   **Modular Toggles:** Easily enable or disable modules in `config.yml`.
*   **160+ Built-In Commands:** Complete command dictionary covering all server needs.

---

## 🚀 Quick Start

1. Put the `folia_core-v5.8-BloodyMary.jar` in your server `plugins/` folder.
2. Start the server on Folia with Java 21.
3. Configure `plugins/FoliaCore/config.yml` to specify your storage type (`yaml` or `mysql`) and toggle modules.
4. Assign permissions to players or groups using your permission manager.

---

## 📟 Comprehensive Command & Permission Reference (160+ Commands)

### 📌 Teleport & Coordinates Modules

| Command | Permission | Description | Scope |
| :--- | :--- | :--- | :--- |
| `/back` | `foliacore.back` | Teleports you back to your previous location. | Regional Thread |
| `/delhome` | `foliacore.delhome` | Deletes a home coordinate point. | Regional Thread |
| `/delwarp` | `foliacore.delwarp` | Deletes a warp point. | Regional Thread |
| `/home` | `foliacore.home` | Teleport to your home. | Regional Thread |
| `/homes` | `foliacore.homes` | Lists your set home locations. | Regional Thread |
| `/renamehome` | `foliacore.renamehome` | Renames one of your homes. | Regional Thread |
| `/setfirstspawn` | `foliacore.setfirstspawn` | Sets the first-join spawn location. | Regional Thread |
| `/sethome` | `foliacore.sethome` | Creates a home coordinate point. | Regional Thread |
| `/setspawn` | `foliacore.setspawn` | Sets the global spawn location. | Regional Thread |
| `/settpr` | `foliacore.settpr` | Sets random teleport boundaries. | Regional Thread |
| `/setwarp` | `foliacore.setwarp` | Creates a new warp point. | Regional Thread |
| `/spawn` | `foliacore.spawn` | Teleports you to the server spawn location. | Regional Thread |
| `/tp` | `foliacore.tp` | Teleports you to a player. | Regional Thread |
| `/tpa` | `foliacore.tpa` | Requests to teleport to a player. | Regional Thread |
| `/tpacancel` | `foliacore.tpacancel` | Cancels pending teleport requests. | Regional Thread |
| `/tpaccept` | `foliacore.tpaccept` | Accepts a teleport request. | Regional Thread |
| `/tpahere` | `foliacore.tpahere` | Requests a player teleport to you. | Regional Thread |
| `/tpall` | `foliacore.tpall` | Teleports all online players to you. | Regional Thread |
| `/tpauto` | `foliacore.tpauto` | Toggles automatic teleport acceptance. | Regional Thread |
| `/tpdeny` | `foliacore.tpdeny` | Denies a teleport request. | Regional Thread |
| `/tphere` | `foliacore.tphere` | Teleports a player to you. | Regional Thread |
| `/tpo` | `foliacore.tpo` | Teleport override for tptoggle. | Regional Thread |
| `/tpoffline` | `foliacore.tpoffline` | Teleport to a player's last known logout spot. | Regional Thread |
| `/tpohere` | `foliacore.tpohere` | Teleport here override for tptoggle. | Regional Thread |
| `/tppos` | `foliacore.tppos` | Teleport to specific coordinate points. | Regional Thread |
| `/tpr` | `foliacore.tpr` | Teleports you to a random location in the world. | Regional Thread |
| `/tptoggle` | `foliacore.tptoggle` | Blocks all incoming teleport requests. | Regional Thread |
| `/warp` | `foliacore.warp` | Teleports to a specific saved warp spot. | Regional Thread |
| `/warps` | `foliacore.warps.list` | Lists all saved warp points. | Regional Thread |

### 📌 VaultUnlocked Economy Modules

| Command | Permission | Description | Scope |
| :--- | :--- | :--- | :--- |
| `/bal` | `foliacore.balance` | Alias for /balance. | Entity Tick |
| `/balance` | `foliacore.balance` | View your current account balance. | Entity Tick |
| `/balancetop` | `foliacore.balancetop` | Check the top balances leaderboard. | Entity Tick |
| `/eco` | `foliacore.eco` | Modify player balances (give/take/set). | Entity Tick |
| `/pay` | `foliacore.pay` | Send money to another player. | Entity Tick |
| `/payconfirmtoggle` | `foliacore.payconfirmtoggle` | Toggle pay confirmations. | Entity Tick |
| `/paytoggle` | `foliacore.paytoggle` | Toggle receiving payments from players. | Entity Tick |
| `/sell` | `foliacore.sell` | Sell items in your hand. | Entity Tick |
| `/setworth` | `foliacore.setworth` | Sets worth value of an item. | Entity Tick |
| `/worth` | `foliacore.worth` | Check the worth of the held item. | Entity Tick |

### 📌 Jails & Isolation Modules

| Command | Permission | Description | Scope |
| :--- | :--- | :--- | :--- |
| `/deljail` | `foliacore.deljail` | Deletes a jail cell location. | Entity Tick |
| `/jail` | `foliacore.jail` | Jails a player to a specific cell. | Entity Tick |
| `/jailedplayers` | `foliacore.jailedplayers` | Lists all currently jailed players. | Entity Tick |
| `/jails` | `foliacore.jails` | Lists all created jail cells. | Entity Tick |
| `/setjail` | `foliacore.setjail` | Creates a new jail cell location. | Entity Tick |
| `/unjail` | `foliacore.unjail` | Releases a player from jail. | Entity Tick |

### 📌 Chat, Discord & Message Modules

| Command | Permission | Description | Scope |
| :--- | :--- | :--- | :--- |
| `/block` | `foliacore.block` | Blocks player messages globally. | Entity Tick |
| `/chat` | `foliacore.chat` | Toggle chat rooms (global/regional). | Entity Tick |
| `/discord` | `foliacore.discord` | Displays Discord invite link. | Entity Tick |
| `/discordbroadcast` | `foliacore.discordbroadcast` | Broadcasts to Discord channel. | Entity Tick |
| `/ignore` | `foliacore.ignore` | Ignore all messages from a player. | Entity Tick |
| `/ignorelist` | `foliacore.ignorelist` | View list of ignored players. | Entity Tick |
| `/link` | `foliacore.link` | Generates Discord linking code. | Entity Tick |
| `/mail` | `foliacore.mail.send` | Sends offline mail to a player. | Entity Tick |
| `/msg` | `foliacore.msg` | Sends a private message to a player. | Entity Tick |
| `/msgtoggle` | `foliacore.msgtoggle` | Block direct private messages. | Entity Tick |
| `/nick` | `foliacore.nick` | Sets a custom chat nickname. | Entity Tick |
| `/realname` | `foliacore.realname` | Lookup real player name from nickname. | Entity Tick |
| `/reply` | `foliacore.reply` | Replies to the last message received. | Entity Tick |
| `/rtoggle` | `foliacore.rtoggle` | Toggle reply target behavior. | Entity Tick |
| `/toggleshout` | `foliacore.toggleshout` | Toggle auto-shouting across regions. | Entity Tick |
| `/unblock` | `foliacore.unblock` | Unblocks direct messages. | Entity Tick |
| `/unignore` | `foliacore.unignore` | Unignore a player. | Entity Tick |
| `/unlink` | `foliacore.unlink` | Unlinks your Discord account. | Entity Tick |

### 📌 Virtual Worktable Containers

| Command | Permission | Description | Scope |
| :--- | :--- | :--- | :--- |
| `/anvil` | `foliacore.anvil` | Opens a virtual anvil container. | Entity Tick |
| `/cartographytable` | `foliacore.cartographytable` | Opens a virtual cartography table. | Entity Tick |
| `/grindstone` | `foliacore.grindstone` | Opens a virtual grindstone. | Entity Tick |
| `/loom` | `foliacore.loom` | Opens a virtual loom interface. | Entity Tick |
| `/smithingtable` | `foliacore.smithingtable` | Opens a virtual smithing table. | Entity Tick |
| `/stonecutter` | `foliacore.stonecutter` | Opens a virtual stonecutter. | Entity Tick |

### 📌 Moderation & Staff Utilities

| Command | Permission | Description | Scope |
| :--- | :--- | :--- | :--- |
| `/antiraid` | `foliacore.admin.antiraid` | Locks server block breaks and interactions. | Entity Tick |
| `/ban` | `foliacore.ban` | Permanently bans a player. | Entity Tick |
| `/banip` | `foliacore.banip` | Bans an IP address. | Entity Tick |
| `/kick` | `foliacore.kick` | Kicks a player from the server. | Entity Tick |
| `/sc` | `foliacore.staffchat` | Alias for /staffchat. | Entity Tick |
| `/socialspy` | `foliacore.socialspy` | Allows viewing private direct messages. | Entity Tick |
| `/staffchat` | `foliacore.staffchat` | Enters private staff channel chat. | Entity Tick |
| `/tempban` | `foliacore.tempban` | Temporarily bans a player. | Entity Tick |
| `/tempbanip` | `foliacore.tempbanip` | Temporarily bans an IP address. | Entity Tick |
| `/unban` | `foliacore.unban` | Revokes a player ban. | Entity Tick |
| `/unbanip` | `foliacore.unbanip` | Unbans an IP address. | Entity Tick |
| `/vanish` | `foliacore.vanish` | Hides player model and coordinate data. | Entity Tick |

### 📌 Thor (Chaos & Explosives)

| Command | Permission | Description | Scope |
| :--- | :--- | :--- | :--- |
| `/antioch` | `foliacore.antioch` | Spawns active TNT at crosshairs. | Regional Thread |
| `/beezooka` | `foliacore.beezooka` | Shoots an exploding bee projectile. | Regional Thread |
| `/fireball` | `foliacore.fireball` | Shoots a chosen projectile entity. | Regional Thread |
| `/kittycannon` | `foliacore.kittycannon` | Launches an exploding cat. | Regional Thread |
| `/lightning` | `foliacore.lightning` | Strikes lightning at crosshairs. | Regional Thread |
| `/nuke` | `foliacore.nuke` | Spawns a rain of primed TNT. | Regional Thread |
| `/remove` | `foliacore.remove` | Butchers entities within radius. | Regional Thread |
| `/spawnmob` | `foliacore.spawnmob` | Spawns a mob type at crosshairs. | Regional Thread |
| `/thunder` | `foliacore.thunder` | Toggle thunder storms. | Regional Thread |
| `/tree` | `foliacore.tree` | Spawns a tree type at crosshairs. | Regional Thread |

### 📌 System & Core Administration

| Command | Permission | Description | Scope |
| :--- | :--- | :--- | :--- |
| `/foliacore` | `foliacore.reload` | Plugin admin commands (reload/stats). | Entity Tick |
| `/marker` | `foliacore.marker` | Manage GPS markers. | Entity Tick |
| `/status` | `foliacore.status` | Displays CPU thread stats and loaded chunks. | Entity Tick |
| `/team` | `foliacore.team` | Creates or edits player teams. | Entity Tick |

### 📌 Gameplay & General Utilities

| Command | Permission | Description | Scope |
| :--- | :--- | :--- | :--- |
| `/afk` | `foliacore.afk` | Toggles away-from-keyboard state. | Entity Tick |
| `/broadcast` | `foliacore.broadcast` | Broadcasts message to all worlds. | Entity Tick |
| `/calc` | `foliacore.calc` | Calculate math expression. | Entity Tick |
| `/clear` | `foliacore.clear` | Clears inventory slots. | Entity Tick |
| `/clearchat` | `foliacore.clearchat` | Clears server chat history. | Entity Tick |
| `/cmi` | `foliacore.cmi` | Core administration or gameplay trigger for /cmi. | Entity Tick |
| `/compass` | `foliacore.compass` | Check current bearing direction. | Entity Tick |
| `/createkit` | `foliacore.createkit` | Core administration or gameplay trigger for /createkit. | Entity Tick |
| `/delkit` | `foliacore.delkit` | Core administration or gameplay trigger for /delkit. | Entity Tick |
| `/dispose` | `foliacore.trash` | Alias for /trash. | Entity Tick |
| `/ec` | `foliacore.enderchest` | Alias for /enderchest. | Entity Tick |
| `/editsign` | `foliacore.editsign` | Edits coordinates of targeted sign. | Entity Tick |
| `/enchant` | `foliacore.enchant` | Applies enchantment to held item. | Entity Tick |
| `/enderchest` | `foliacore.enderchest` | Opens ender chest slots. | Entity Tick |
| `/exp` | `foliacore.exp` | Inspect or modify player experience. | Entity Tick |
| `/ext` | `foliacore.ext` | Extinguishes a player on fire. | Entity Tick |
| `/fc` | `foliacore.fc` | Core administration or gameplay trigger for /fc. | Entity Tick |
| `/feed` | `foliacore.feed` | Restore player hunger meter. | Entity Tick |
| `/firework` | `foliacore.firework` | Modify a stack of fireworks. | Entity Tick |
| `/fly` | `foliacore.fly` | Toggle player flight. | Entity Tick |
| `/gamemode` | `foliacore.gamemode` | Changes player gamemode. | Entity Tick |
| `/give` | `foliacore.give` | Gives item to player. | Entity Tick |
| `/gma` | `foliacore.gamemode` | Switch to adventure gamemode. | Entity Tick |
| `/gmc` | `foliacore.gamemode` | Switch to creative gamemode. | Entity Tick |
| `/gms` | `foliacore.gamemode` | Switch to survival gamemode. | Entity Tick |
| `/gmsp` | `foliacore.gamemode` | Switch to spectator gamemode. | Entity Tick |
| `/god` | `foliacore.god` | Toggle invulnerability mode. | Entity Tick |
| `/gps` | `foliacore.gps` | Core administration or gameplay trigger for /gps. | Entity Tick |
| `/hat` | `foliacore.hat` | Puts held item on your head. | Entity Tick |
| `/heal` | `foliacore.heal` | Restore player health/hunger. | Entity Tick |
| `/invsee` | `foliacore.invsee` | Inspects another player's inventory. | Entity Tick |
| `/jump` | `foliacore.jump` | Jump to targeted coordinate block. | Entity Tick |
| `/kickall` | `foliacore.kickall` | Kick all online players. | Entity Tick |
| `/kit` | `foliacore.kit` | Core administration or gameplay trigger for /kit. | Entity Tick |
| `/list` | `foliacore.list` | List online players. | Entity Tick |
| `/me` | `foliacore.me` | Renders third-person narrative action. | Entity Tick |
| `/more` | `foliacore.more` | Restacks held item to max stack size. | Entity Tick |
| `/motd` | `foliacore.motd` | Show server message of the day. | Entity Tick |
| `/mute` | `foliacore.mute` | Core administration or gameplay trigger for /mute. | Entity Tick |
| `/near` | `foliacore.near` | Lists nearby player names. | Entity Tick |
| `/ping` | `foliacore.ping` | Check connection latency. | Entity Tick |
| `/playtime` | `foliacore.playtime` | View total playtime records. | Entity Tick |
| `/potion` | `foliacore.potion` | Applies potion effects. | Entity Tick |
| `/powertool` | `foliacore.powertool` | Bind command to held item. | Entity Tick |
| `/pt` | `foliacore.powertool` | Alias for /powertool. | Entity Tick |
| `/ptime` | `foliacore.ptime` | Adjust player's client time. | Entity Tick |
| `/pweather` | `foliacore.pweather` | Adjust player's client weather. | Entity Tick |
| `/recipe` | `foliacore.recipe` | Show recipe format. | Entity Tick |
| `/repair` | `foliacore.repair` | Repairs held item durability. | Entity Tick |
| `/rest` | `foliacore.rest` | Rests a player (resets insomnia). | Entity Tick |
| `/rules` | `foliacore.rules` | View server rule list chapters. | Entity Tick |
| `/scoreboard` | `foliacore.scoreboard.toggle` | Toggle scoreboard display visibility. | Entity Tick |
| `/seen` | `foliacore.seen` | Check last logout time. | Entity Tick |
| `/showkit` | `foliacore.showkit` | Preview kit items list. | Entity Tick |
| `/sidebar` | `foliacore.scoreboard.toggle` | Toggle sidebar display visibility. | Entity Tick |
| `/skull` | `foliacore.skull` | Spawns a player skull block item. | Entity Tick |
| `/speed` | `foliacore.speed` | Set walk or fly velocity limits. | Entity Tick |
| `/sudo` | `foliacore.sudo` | Forces player to run command. | Entity Tick |
| `/suicide` | `foliacore.suicide` | Instantly kills yourself. | Entity Tick |
| `/time` | `foliacore.time` | Adjust world time ticks. | Entity Tick |
| `/trash` | `foliacore.trash` | Opens portable disposal canvas. | Entity Tick |
| `/unlimited` | `foliacore.unlimited` | Unlimited placing of items. | Entity Tick |
| `/unmute` | `foliacore.unmute` | Core administration or gameplay trigger for /unmute. | Entity Tick |
| `/warpinfo` | `foliacore.warpinfo` | Inspects warp coordinate data. | Entity Tick |
| `/wb` | `foliacore.workbench` | Alias for /workbench. | Entity Tick |
| `/weather` | `foliacore.weather` | Adjust world weather values. | Entity Tick |
| `/workbench` | `foliacore.workbench` | Opens mobile crafting interface. | Entity Tick |



---

## 📞 Contact & Support

For support, custom developments, or other inquiries, contact AJA RETRO:
*   **WhatsApp:** +8801989208751
