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
| `/actionbarmsg` | `foliacore.actionbarmsg` | Core administration or gameplay trigger for /actionbarmsg. | Entity Tick |
| `/afk` | `foliacore.afk` | Toggles away-from-keyboard state. | Entity Tick |
| `/afkcheck` | `foliacore.afkcheck` | Core administration or gameplay trigger for /afkcheck. | Entity Tick |
| `/air` | `foliacore.air` | Core administration or gameplay trigger for /air. | Entity Tick |
| `/alert` | `foliacore.alert` | Core administration or gameplay trigger for /alert. | Entity Tick |
| `/aliaseditor` | `foliacore.aliaseditor` | Core administration or gameplay trigger for /aliaseditor. | Entity Tick |
| `/anvilrepaircost` | `foliacore.anvilrepaircost` | Core administration or gameplay trigger for /anvilrepaircost. | Entity Tick |
| `/armoreffect` | `foliacore.armoreffect` | Core administration or gameplay trigger for /armoreffect. | Entity Tick |
| `/armorstand` | `foliacore.armorstand` | Core administration or gameplay trigger for /armorstand. | Entity Tick |
| `/attachcommand` | `foliacore.attachcommand` | Core administration or gameplay trigger for /attachcommand. | Entity Tick |
| `/autorecharge` | `foliacore.autorecharge` | Core administration or gameplay trigger for /autorecharge. | Entity Tick |
| `/baltop` | `foliacore.baltop` | Core administration or gameplay trigger for /baltop. | Entity Tick |
| `/banlist` | `foliacore.banlist` | Core administration or gameplay trigger for /banlist. | Entity Tick |
| `/bbroadcast` | `foliacore.bbroadcast` | Core administration or gameplay trigger for /bbroadcast. | Entity Tick |
| `/blockcycling` | `foliacore.blockcycling` | Core administration or gameplay trigger for /blockcycling. | Entity Tick |
| `/blockinfo` | `foliacore.blockinfo` | Core administration or gameplay trigger for /blockinfo. | Entity Tick |
| `/blocknbt` | `foliacore.blocknbt` | Core administration or gameplay trigger for /blocknbt. | Entity Tick |
| `/book` | `foliacore.book` | Core administration or gameplay trigger for /book. | Entity Tick |
| `/bossbarmsg` | `foliacore.bossbarmsg` | Core administration or gameplay trigger for /bossbarmsg. | Entity Tick |
| `/broadcast` | `foliacore.broadcast` | Broadcasts message to all worlds. | Entity Tick |
| `/burn` | `foliacore.burn` | Core administration or gameplay trigger for /burn. | Entity Tick |
| `/calc` | `foliacore.calc` | Calculate math expression. | Entity Tick |
| `/charges` | `foliacore.charges` | Core administration or gameplay trigger for /charges. | Entity Tick |
| `/chatcolor` | `foliacore.chatcolor` | Core administration or gameplay trigger for /chatcolor. | Entity Tick |
| `/checkaccount` | `foliacore.checkaccount` | Core administration or gameplay trigger for /checkaccount. | Entity Tick |
| `/checkban` | `foliacore.checkban` | Core administration or gameplay trigger for /checkban. | Entity Tick |
| `/checkcommand` | `foliacore.checkcommand` | Core administration or gameplay trigger for /checkcommand. | Entity Tick |
| `/checkexp` | `foliacore.checkexp` | Core administration or gameplay trigger for /checkexp. | Entity Tick |
| `/checkperm` | `foliacore.checkperm` | Core administration or gameplay trigger for /checkperm. | Entity Tick |
| `/cheque` | `foliacore.cheque` | Core administration or gameplay trigger for /cheque. | Entity Tick |
| `/clear` | `foliacore.clear` | Clears inventory slots. | Entity Tick |
| `/clearchat` | `foliacore.clearchat` | Clears server chat history. | Entity Tick |
| `/clearender` | `foliacore.clearender` | Core administration or gameplay trigger for /clearender. | Entity Tick |
| `/colorlimits` | `foliacore.colorlimits` | Core administration or gameplay trigger for /colorlimits. | Entity Tick |
| `/colorpicker` | `foliacore.colorpicker` | Core administration or gameplay trigger for /colorpicker. | Entity Tick |
| `/colors` | `foliacore.colors` | Core administration or gameplay trigger for /colors. | Entity Tick |
| `/compass` | `foliacore.compass` | Check current bearing direction. | Entity Tick |
| `/condense` | `foliacore.condense` | Core administration or gameplay trigger for /condense. | Entity Tick |
| `/counter` | `foliacore.counter` | Core administration or gameplay trigger for /counter. | Entity Tick |
| `/cplaytime` | `foliacore.cplaytime` | Core administration or gameplay trigger for /cplaytime. | Entity Tick |
| `/createkit` | `foliacore.createkit` | Core administration or gameplay trigger for /createkit. | Entity Tick |
| `/ctellraw` | `foliacore.ctellraw` | Core administration or gameplay trigger for /ctellraw. | Entity Tick |
| `/ctext` | `foliacore.ctext` | Core administration or gameplay trigger for /ctext. | Entity Tick |
| `/cuff` | `foliacore.cuff` | Core administration or gameplay trigger for /cuff. | Entity Tick |
| `/customrecipe` | `foliacore.customrecipe` | Core administration or gameplay trigger for /customrecipe. | Entity Tick |
| `/database` | `foliacore.database` | Core administration or gameplay trigger for /database. | Entity Tick |
| `/dback` | `foliacore.dback` | Core administration or gameplay trigger for /dback. | Entity Tick |
| `/delkit` | `foliacore.delkit` | Core administration or gameplay trigger for /delkit. | Entity Tick |
| `/dialogs` | `foliacore.dialogs` | Core administration or gameplay trigger for /dialogs. | Entity Tick |
| `/disableenchant` | `foliacore.disableenchant` | Core administration or gameplay trigger for /disableenchant. | Entity Tick |
| `/dispose` | `foliacore.trash` | Alias for /trash. | Entity Tick |
| `/distance` | `foliacore.distance` | Core administration or gameplay trigger for /distance. | Entity Tick |
| `/donate` | `foliacore.donate` | Core administration or gameplay trigger for /donate. | Entity Tick |
| `/down` | `foliacore.down` | Core administration or gameplay trigger for /down. | Entity Tick |
| `/dsign` | `foliacore.dsign` | Core administration or gameplay trigger for /dsign. | Entity Tick |
| `/dye` | `foliacore.dye` | Core administration or gameplay trigger for /dye. | Entity Tick |
| `/ec` | `foliacore.enderchest` | Alias for /enderchest. | Entity Tick |
| `/editctext` | `foliacore.editctext` | Core administration or gameplay trigger for /editctext. | Entity Tick |
| `/editlocale` | `foliacore.editlocale` | Core administration or gameplay trigger for /editlocale. | Entity Tick |
| `/editplaytime` | `foliacore.editplaytime` | Core administration or gameplay trigger for /editplaytime. | Entity Tick |
| `/editsign` | `foliacore.editsign` | Edits coordinates of targeted sign. | Entity Tick |
| `/editwarnings` | `foliacore.editwarnings` | Core administration or gameplay trigger for /editwarnings. | Entity Tick |
| `/editwarp` | `foliacore.editwarp` | Core administration or gameplay trigger for /editwarp. | Entity Tick |
| `/effect` | `foliacore.effect` | Core administration or gameplay trigger for /effect. | Entity Tick |
| `/enchant` | `foliacore.enchant` | Applies enchantment to held item. | Entity Tick |
| `/ender` | `foliacore.ender` | Core administration or gameplay trigger for /ender. | Entity Tick |
| `/enderchest` | `foliacore.enderchest` | Opens ender chest slots. | Entity Tick |
| `/endgateway` | `foliacore.endgateway` | Core administration or gameplay trigger for /endgateway. | Entity Tick |
| `/entityinfo` | `foliacore.entityinfo` | Core administration or gameplay trigger for /entityinfo. | Entity Tick |
| `/entitynbt` | `foliacore.entitynbt` | Core administration or gameplay trigger for /entitynbt. | Entity Tick |
| `/exp` | `foliacore.exp` | Inspect or modify player experience. | Entity Tick |
| `/ext` | `foliacore.ext` | Extinguishes a player on fire. | Entity Tick |
| `/falldistance` | `foliacore.falldistance` | Core administration or gameplay trigger for /falldistance. | Entity Tick |
| `/fc` | `foliacore.fc` | Core administration or gameplay trigger for /fc. | Entity Tick |
| `/feed` | `foliacore.feed` | Restore player hunger meter. | Entity Tick |
| `/findbiome` | `foliacore.findbiome` | Core administration or gameplay trigger for /findbiome. | Entity Tick |
| `/firework` | `foliacore.firework` | Modify a stack of fireworks. | Entity Tick |
| `/fixchunk` | `foliacore.fixchunk` | Core administration or gameplay trigger for /fixchunk. | Entity Tick |
| `/flightcharge` | `foliacore.flightcharge` | Core administration or gameplay trigger for /flightcharge. | Entity Tick |
| `/fly` | `foliacore.fly` | Toggle player flight. | Entity Tick |
| `/flyc` | `foliacore.flyc` | Core administration or gameplay trigger for /flyc. | Entity Tick |
| `/flyspeed` | `foliacore.flyspeed` | Core administration or gameplay trigger for /flyspeed. | Entity Tick |
| `/gamemode` | `foliacore.gamemode` | Changes player gamemode. | Entity Tick |
| `/gamerule` | `foliacore.gamerule` | Core administration or gameplay trigger for /gamerule. | Entity Tick |
| `/generateworth` | `foliacore.generateworth` | Core administration or gameplay trigger for /generateworth. | Entity Tick |
| `/getbook` | `foliacore.getbook` | Core administration or gameplay trigger for /getbook. | Entity Tick |
| `/give` | `foliacore.give` | Gives item to player. | Entity Tick |
| `/giveall` | `foliacore.giveall` | Core administration or gameplay trigger for /giveall. | Entity Tick |
| `/glow` | `foliacore.glow` | Core administration or gameplay trigger for /glow. | Entity Tick |
| `/gm` | `foliacore.gm` | Core administration or gameplay trigger for /gm. | Entity Tick |
| `/gma` | `foliacore.gamemode` | Switch to adventure gamemode. | Entity Tick |
| `/gmc` | `foliacore.gamemode` | Switch to creative gamemode. | Entity Tick |
| `/gms` | `foliacore.gamemode` | Switch to survival gamemode. | Entity Tick |
| `/gmsp` | `foliacore.gamemode` | Switch to spectator gamemode. | Entity Tick |
| `/god` | `foliacore.god` | Toggle invulnerability mode. | Entity Tick |
| `/gps` | `foliacore.gps` | Core administration or gameplay trigger for /gps. | Entity Tick |
| `/groundclean` | `foliacore.groundclean` | Core administration or gameplay trigger for /groundclean. | Entity Tick |
| `/haspermission` | `foliacore.haspermission` | Core administration or gameplay trigger for /haspermission. | Entity Tick |
| `/hat` | `foliacore.hat` | Puts held item on your head. | Entity Tick |
| `/head` | `foliacore.head` | Core administration or gameplay trigger for /head. | Entity Tick |
| `/heal` | `foliacore.heal` | Restore player health/hunger. | Entity Tick |
| `/helpop` | `foliacore.helpop` | Core administration or gameplay trigger for /helpop. | Entity Tick |
| `/hideflags` | `foliacore.hideflags` | Core administration or gameplay trigger for /hideflags. | Entity Tick |
| `/hologram` | `foliacore.hologram` | Core administration or gameplay trigger for /hologram. | Entity Tick |
| `/hologrampages` | `foliacore.hologrampages` | Core administration or gameplay trigger for /hologrampages. | Entity Tick |
| `/hunger` | `foliacore.hunger` | Core administration or gameplay trigger for /hunger. | Entity Tick |
| `/ic` | `foliacore.ic` | Core administration or gameplay trigger for /ic. | Entity Tick |
| `/ifoffline` | `foliacore.ifoffline` | Core administration or gameplay trigger for /ifoffline. | Entity Tick |
| `/ifonline` | `foliacore.ifonline` | Core administration or gameplay trigger for /ifonline. | Entity Tick |
| `/importfrom` | `foliacore.importfrom` | Core administration or gameplay trigger for /importfrom. | Entity Tick |
| `/importoldusers` | `foliacore.importoldusers` | Core administration or gameplay trigger for /importoldusers. | Entity Tick |
| `/info` | `foliacore.info` | Core administration or gameplay trigger for /info. | Entity Tick |
| `/inv` | `foliacore.inv` | Core administration or gameplay trigger for /inv. | Entity Tick |
| `/invcheck` | `foliacore.invcheck` | Core administration or gameplay trigger for /invcheck. | Entity Tick |
| `/invlist` | `foliacore.invlist` | Core administration or gameplay trigger for /invlist. | Entity Tick |
| `/invload` | `foliacore.invload` | Core administration or gameplay trigger for /invload. | Entity Tick |
| `/invremove` | `foliacore.invremove` | Core administration or gameplay trigger for /invremove. | Entity Tick |
| `/invremoveall` | `foliacore.invremoveall` | Core administration or gameplay trigger for /invremoveall. | Entity Tick |
| `/invsave` | `foliacore.invsave` | Core administration or gameplay trigger for /invsave. | Entity Tick |
| `/invsee` | `foliacore.invsee` | Inspects another player's inventory. | Entity Tick |
| `/ipban` | `foliacore.ipban` | Core administration or gameplay trigger for /ipban. | Entity Tick |
| `/ipbanlist` | `foliacore.ipbanlist` | Core administration or gameplay trigger for /ipbanlist. | Entity Tick |
| `/item` | `foliacore.item` | Core administration or gameplay trigger for /item. | Entity Tick |
| `/itemcmdata` | `foliacore.itemcmdata` | Core administration or gameplay trigger for /itemcmdata. | Entity Tick |
| `/itemframe` | `foliacore.itemframe` | Core administration or gameplay trigger for /itemframe. | Entity Tick |
| `/iteminfo` | `foliacore.iteminfo` | Core administration or gameplay trigger for /iteminfo. | Entity Tick |
| `/itemlore` | `foliacore.itemlore` | Core administration or gameplay trigger for /itemlore. | Entity Tick |
| `/itemname` | `foliacore.itemname` | Core administration or gameplay trigger for /itemname. | Entity Tick |
| `/itemnbt` | `foliacore.itemnbt` | Core administration or gameplay trigger for /itemnbt. | Entity Tick |
| `/jailedit` | `foliacore.jailedit` | Core administration or gameplay trigger for /jailedit. | Entity Tick |
| `/jaillist` | `foliacore.jaillist` | Core administration or gameplay trigger for /jaillist. | Entity Tick |
| `/jump` | `foliacore.jump` | Jump to targeted coordinate block. | Entity Tick |
| `/kickall` | `foliacore.kickall` | Kick all online players. | Entity Tick |
| `/kill` | `foliacore.kill` | Core administration or gameplay trigger for /kill. | Entity Tick |
| `/killall` | `foliacore.killall` | Core administration or gameplay trigger for /killall. | Entity Tick |
| `/kit` | `foliacore.kit` | Core administration or gameplay trigger for /kit. | Entity Tick |
| `/kitcdreset` | `foliacore.kitcdreset` | Core administration or gameplay trigger for /kitcdreset. | Entity Tick |
| `/kiteditor` | `foliacore.kiteditor` | Core administration or gameplay trigger for /kiteditor. | Entity Tick |
| `/kitusagereset` | `foliacore.kitusagereset` | Core administration or gameplay trigger for /kitusagereset. | Entity Tick |
| `/lastonline` | `foliacore.lastonline` | Core administration or gameplay trigger for /lastonline. | Entity Tick |
| `/launch` | `foliacore.launch` | Core administration or gameplay trigger for /launch. | Entity Tick |
| `/lfix` | `foliacore.lfix` | Core administration or gameplay trigger for /lfix. | Entity Tick |
| `/list` | `foliacore.list` | List online players. | Entity Tick |
| `/lockip` | `foliacore.lockip` | Core administration or gameplay trigger for /lockip. | Entity Tick |
| `/mailall` | `foliacore.mailall` | Core administration or gameplay trigger for /mailall. | Entity Tick |
| `/maintenance` | `foliacore.maintenance` | Core administration or gameplay trigger for /maintenance. | Entity Tick |
| `/maxhp` | `foliacore.maxhp` | Core administration or gameplay trigger for /maxhp. | Entity Tick |
| `/maxplayers` | `foliacore.maxplayers` | Core administration or gameplay trigger for /maxplayers. | Entity Tick |
| `/me` | `foliacore.me` | Renders third-person narrative action. | Entity Tick |
| `/merchant` | `foliacore.merchant` | Core administration or gameplay trigger for /merchant. | Entity Tick |
| `/migratedatabase` | `foliacore.migratedatabase` | Core administration or gameplay trigger for /migratedatabase. | Entity Tick |
| `/mirror` | `foliacore.mirror` | Core administration or gameplay trigger for /mirror. | Entity Tick |
| `/mobhead` | `foliacore.mobhead` | Core administration or gameplay trigger for /mobhead. | Entity Tick |
| `/money` | `foliacore.money` | Core administration or gameplay trigger for /money. | Entity Tick |
| `/more` | `foliacore.more` | Restacks held item to max stack size. | Entity Tick |
| `/motd` | `foliacore.motd` | Show server message of the day. | Entity Tick |
| `/mute` | `foliacore.mute` | Core administration or gameplay trigger for /mute. | Entity Tick |
| `/mutechat` | `foliacore.mutechat` | Core administration or gameplay trigger for /mutechat. | Entity Tick |
| `/nameplate` | `foliacore.nameplate` | Core administration or gameplay trigger for /nameplate. | Entity Tick |
| `/near` | `foliacore.near` | Lists nearby player names. | Entity Tick |
| `/notarget` | `foliacore.notarget` | Core administration or gameplay trigger for /notarget. | Entity Tick |
| `/note` | `foliacore.note` | Core administration or gameplay trigger for /note. | Entity Tick |
| `/openbook` | `foliacore.openbook` | Core administration or gameplay trigger for /openbook. | Entity Tick |
| `/oplist` | `foliacore.oplist` | Core administration or gameplay trigger for /oplist. | Entity Tick |
| `/options` | `foliacore.options` | Core administration or gameplay trigger for /options. | Entity Tick |
| `/panimation` | `foliacore.panimation` | Core administration or gameplay trigger for /panimation. | Entity Tick |
| `/particlepicker` | `foliacore.particlepicker` | Core administration or gameplay trigger for /particlepicker. | Entity Tick |
| `/patrol` | `foliacore.patrol` | Core administration or gameplay trigger for /patrol. | Entity Tick |
| `/ping` | `foliacore.ping` | Check connection latency. | Entity Tick |
| `/placeholders` | `foliacore.placeholders` | Core administration or gameplay trigger for /placeholders. | Entity Tick |
| `/playercollision` | `foliacore.playercollision` | Core administration or gameplay trigger for /playercollision. | Entity Tick |
| `/playtime` | `foliacore.playtime` | View total playtime records. | Entity Tick |
| `/playtimetop` | `foliacore.playtimetop` | Core administration or gameplay trigger for /playtimetop. | Entity Tick |
| `/point` | `foliacore.point` | Core administration or gameplay trigger for /point. | Entity Tick |
| `/portals` | `foliacore.portals` | Core administration or gameplay trigger for /portals. | Entity Tick |
| `/pos` | `foliacore.pos` | Core administration or gameplay trigger for /pos. | Entity Tick |
| `/potion` | `foliacore.potion` | Applies potion effects. | Entity Tick |
| `/powertool` | `foliacore.powertool` | Bind command to held item. | Entity Tick |
| `/preview` | `foliacore.preview` | Core administration or gameplay trigger for /preview. | Entity Tick |
| `/prewards` | `foliacore.prewards` | Core administration or gameplay trigger for /prewards. | Entity Tick |
| `/pt` | `foliacore.powertool` | Alias for /powertool. | Entity Tick |
| `/ptime` | `foliacore.ptime` | Adjust player's client time. | Entity Tick |
| `/purge` | `foliacore.purge` | Core administration or gameplay trigger for /purge. | Entity Tick |
| `/pweather` | `foliacore.pweather` | Adjust player's client weather. | Entity Tick |
| `/rankdown` | `foliacore.rankdown` | Core administration or gameplay trigger for /rankdown. | Entity Tick |
| `/rankinfo` | `foliacore.rankinfo` | Core administration or gameplay trigger for /rankinfo. | Entity Tick |
| `/ranklist` | `foliacore.ranklist` | Core administration or gameplay trigger for /ranklist. | Entity Tick |
| `/rankset` | `foliacore.rankset` | Core administration or gameplay trigger for /rankset. | Entity Tick |
| `/rankup` | `foliacore.rankup` | Core administration or gameplay trigger for /rankup. | Entity Tick |
| `/recipe` | `foliacore.recipe` | Show recipe format. | Entity Tick |
| `/reload` | `foliacore.reload` | Core administration or gameplay trigger for /reload. | Entity Tick |
| `/removehome` | `foliacore.removehome` | Core administration or gameplay trigger for /removehome. | Entity Tick |
| `/removeuser` | `foliacore.removeuser` | Core administration or gameplay trigger for /removeuser. | Entity Tick |
| `/removewarp` | `foliacore.removewarp` | Core administration or gameplay trigger for /removewarp. | Entity Tick |
| `/repair` | `foliacore.repair` | Repairs held item durability. | Entity Tick |
| `/repaircost` | `foliacore.repaircost` | Core administration or gameplay trigger for /repaircost. | Entity Tick |
| `/replaceblock` | `foliacore.replaceblock` | Core administration or gameplay trigger for /replaceblock. | Entity Tick |
| `/resetback` | `foliacore.resetback` | Core administration or gameplay trigger for /resetback. | Entity Tick |
| `/resetdbfields` | `foliacore.resetdbfields` | Core administration or gameplay trigger for /resetdbfields. | Entity Tick |
| `/rest` | `foliacore.rest` | Rests a player (resets insomnia). | Entity Tick |
| `/ride` | `foliacore.ride` | Core administration or gameplay trigger for /ride. | Entity Tick |
| `/rt` | `foliacore.rt` | Core administration or gameplay trigger for /rt. | Entity Tick |
| `/rules` | `foliacore.rules` | View server rule list chapters. | Entity Tick |
| `/sameip` | `foliacore.sameip` | Core administration or gameplay trigger for /sameip. | Entity Tick |
| `/saturation` | `foliacore.saturation` | Core administration or gameplay trigger for /saturation. | Entity Tick |
| `/saveall` | `foliacore.saveall` | Core administration or gameplay trigger for /saveall. | Entity Tick |
| `/saveditems` | `foliacore.saveditems` | Core administration or gameplay trigger for /saveditems. | Entity Tick |
| `/scale` | `foliacore.scale` | Core administration or gameplay trigger for /scale. | Entity Tick |
| `/scan` | `foliacore.scan` | Core administration or gameplay trigger for /scan. | Entity Tick |
| `/scavenge` | `foliacore.scavenge` | Core administration or gameplay trigger for /scavenge. | Entity Tick |
| `/schedule` | `foliacore.schedule` | Core administration or gameplay trigger for /schedule. | Entity Tick |
| `/scoreboard` | `foliacore.scoreboard.toggle` | Toggle scoreboard display visibility. | Entity Tick |
| `/se` | `foliacore.se` | Core administration or gameplay trigger for /se. | Entity Tick |
| `/search` | `foliacore.search` | Core administration or gameplay trigger for /search. | Entity Tick |
| `/seen` | `foliacore.seen` | Check last logout time. | Entity Tick |
| `/select` | `foliacore.select` | Core administration or gameplay trigger for /select. | Entity Tick |
| `/sendall` | `foliacore.sendall` | Core administration or gameplay trigger for /sendall. | Entity Tick |
| `/server` | `foliacore.server` | Core administration or gameplay trigger for /server. | Entity Tick |
| `/serverlinks` | `foliacore.serverlinks` | Core administration or gameplay trigger for /serverlinks. | Entity Tick |
| `/serverlist` | `foliacore.serverlist` | Core administration or gameplay trigger for /serverlist. | Entity Tick |
| `/servertime` | `foliacore.servertime` | Core administration or gameplay trigger for /servertime. | Entity Tick |
| `/setenchantworth` | `foliacore.setenchantworth` | Core administration or gameplay trigger for /setenchantworth. | Entity Tick |
| `/setmotd` | `foliacore.setmotd` | Core administration or gameplay trigger for /setmotd. | Entity Tick |
| `/setrt` | `foliacore.setrt` | Core administration or gameplay trigger for /setrt. | Entity Tick |
| `/shadowmute` | `foliacore.shadowmute` | Core administration or gameplay trigger for /shadowmute. | Entity Tick |
| `/shakeitoff` | `foliacore.shakeitoff` | Core administration or gameplay trigger for /shakeitoff. | Entity Tick |
| `/shoot` | `foliacore.shoot` | Core administration or gameplay trigger for /shoot. | Entity Tick |
| `/showkit` | `foliacore.showkit` | Preview kit items list. | Entity Tick |
| `/sidebar` | `foliacore.scoreboard.toggle` | Toggle sidebar display visibility. | Entity Tick |
| `/silence` | `foliacore.silence` | Core administration or gameplay trigger for /silence. | Entity Tick |
| `/silentchest` | `foliacore.silentchest` | Core administration or gameplay trigger for /silentchest. | Entity Tick |
| `/sit` | `foliacore.sit` | Core administration or gameplay trigger for /sit. | Entity Tick |
| `/skin` | `foliacore.skin` | Core administration or gameplay trigger for /skin. | Entity Tick |
| `/skull` | `foliacore.skull` | Spawns a player skull block item. | Entity Tick |
| `/smite` | `foliacore.smite` | Core administration or gameplay trigger for /smite. | Entity Tick |
| `/solve` | `foliacore.solve` | Core administration or gameplay trigger for /solve. | Entity Tick |
| `/sound` | `foliacore.sound` | Core administration or gameplay trigger for /sound. | Entity Tick |
| `/spawner` | `foliacore.spawner` | Core administration or gameplay trigger for /spawner. | Entity Tick |
| `/spawnereditor` | `foliacore.spawnereditor` | Core administration or gameplay trigger for /spawnereditor. | Entity Tick |
| `/speed` | `foliacore.speed` | Set walk or fly velocity limits. | Entity Tick |
| `/staffmsg` | `foliacore.staffmsg` | Core administration or gameplay trigger for /staffmsg. | Entity Tick |
| `/stats` | `foliacore.stats` | Core administration or gameplay trigger for /stats. | Entity Tick |
| `/statsedit` | `foliacore.statsedit` | Core administration or gameplay trigger for /statsedit. | Entity Tick |
| `/sudo` | `foliacore.sudo` | Forces player to run command. | Entity Tick |
| `/suicide` | `foliacore.suicide` | Instantly kills yourself. | Entity Tick |
| `/switchplayerdata` | `foliacore.switchplayerdata` | Core administration or gameplay trigger for /switchplayerdata. | Entity Tick |
| `/tablistupdate` | `foliacore.tablistupdate` | Core administration or gameplay trigger for /tablistupdate. | Entity Tick |
| `/tempipban` | `foliacore.tempipban` | Core administration or gameplay trigger for /tempipban. | Entity Tick |
| `/tfly` | `foliacore.tfly` | Core administration or gameplay trigger for /tfly. | Entity Tick |
| `/tgod` | `foliacore.tgod` | Core administration or gameplay trigger for /tgod. | Entity Tick |
| `/time` | `foliacore.time` | Adjust world time ticks. | Entity Tick |
| `/titlemsg` | `foliacore.titlemsg` | Core administration or gameplay trigger for /titlemsg. | Entity Tick |
| `/toast` | `foliacore.toast` | Core administration or gameplay trigger for /toast. | Entity Tick |
| `/top` | `foliacore.top` | Core administration or gameplay trigger for /top. | Entity Tick |
| `/tpaall` | `foliacore.tpaall` | Core administration or gameplay trigger for /tpaall. | Entity Tick |
| `/tpallworld` | `foliacore.tpallworld` | Core administration or gameplay trigger for /tpallworld. | Entity Tick |
| `/tpbypass` | `foliacore.tpbypass` | Core administration or gameplay trigger for /tpbypass. | Entity Tick |
| `/tpopos` | `foliacore.tpopos` | Core administration or gameplay trigger for /tpopos. | Entity Tick |
| `/tps` | `foliacore.tps` | Core administration or gameplay trigger for /tps. | Entity Tick |
| `/trash` | `foliacore.trash` | Opens portable disposal canvas. | Entity Tick |
| `/trim` | `foliacore.trim` | Core administration or gameplay trigger for /trim. | Entity Tick |
| `/unbreakable` | `foliacore.unbreakable` | Core administration or gameplay trigger for /unbreakable. | Entity Tick |
| `/uncondense` | `foliacore.uncondense` | Core administration or gameplay trigger for /uncondense. | Entity Tick |
| `/unlimited` | `foliacore.unlimited` | Unlimited placing of items. | Entity Tick |
| `/unloadchunks` | `foliacore.unloadchunks` | Core administration or gameplay trigger for /unloadchunks. | Entity Tick |
| `/unmute` | `foliacore.unmute` | Core administration or gameplay trigger for /unmute. | Entity Tick |
| `/unmutechat` | `foliacore.unmutechat` | Core administration or gameplay trigger for /unmutechat. | Entity Tick |
| `/usermeta` | `foliacore.usermeta` | Core administration or gameplay trigger for /usermeta. | Entity Tick |
| `/util` | `foliacore.util` | Core administration or gameplay trigger for /util. | Entity Tick |
| `/vanishedit` | `foliacore.vanishedit` | Core administration or gameplay trigger for /vanishedit. | Entity Tick |
| `/version` | `foliacore.version` | Core administration or gameplay trigger for /version. | Entity Tick |
| `/viewdistance` | `foliacore.viewdistance` | Core administration or gameplay trigger for /viewdistance. | Entity Tick |
| `/voteedit` | `foliacore.voteedit` | Core administration or gameplay trigger for /voteedit. | Entity Tick |
| `/votes` | `foliacore.votes` | Core administration or gameplay trigger for /votes. | Entity Tick |
| `/votetop` | `foliacore.votetop` | Core administration or gameplay trigger for /votetop. | Entity Tick |
| `/walkspeed` | `foliacore.walkspeed` | Core administration or gameplay trigger for /walkspeed. | Entity Tick |
| `/warn` | `foliacore.warn` | Core administration or gameplay trigger for /warn. | Entity Tick |
| `/warnings` | `foliacore.warnings` | Core administration or gameplay trigger for /warnings. | Entity Tick |
| `/warpgroups` | `foliacore.warpgroups` | Core administration or gameplay trigger for /warpgroups. | Entity Tick |
| `/warpinfo` | `foliacore.warpinfo` | Inspects warp coordinate data. | Entity Tick |
| `/wb` | `foliacore.workbench` | Alias for /workbench. | Entity Tick |
| `/weather` | `foliacore.weather` | Adjust world weather values. | Entity Tick |
| `/workbench` | `foliacore.workbench` | Opens mobile crafting interface. | Entity Tick |
| `/world` | `foliacore.world` | Core administration or gameplay trigger for /world. | Entity Tick |
| `/worthlist` | `foliacore.worthlist` | Core administration or gameplay trigger for /worthlist. | Entity Tick |



---

## 📞 Contact & Support

For support, custom developments, or other inquiries, contact AJA RETRO:
*   **WhatsApp:** +8801989208751

---

## 📦 AJA RETRO Plugin Suite
*   [DeMalware-RETRO](https://modrinth.com/mod/demalware-retro) - Advanced JVM agent early-boot malware protection scanner.
*   [CircuitBreaker](https://modrinth.com/mod/circuitbreaker) - Dynamic lag machine culler and entity optimizer.
*   [FoliaCore](https://modrinth.com/mod/folia-core) - Multi-threaded native essentials suite built for Folia.
*   [RetroWorldPurger](https://modrinth.com/plugin/retroworldpurger) - Automatic region file purger and storage optimizer.
*   [RetroMail](https://modrinth.com/plugin/retromail) - High-performance SMTP server integration for mail delivery.
