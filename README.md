

# FoliaCore

**The Native, Thread-Safe Essentials Suite for Folia Servers.**

-----

Hey there. So, you're running a Folia server? That's awesome. You're on the cutting edge, but you've probably realized it breaks *everything*.

Your old "essentials" plugin (like EssentialsX) is built on a single "main thread." The second you run it on Folia, it will cause crashes, data corruption, and lag. Other "Folia-compatible" plugins are often just quick patches, not true fixes.

**FoliaCore is not a patch. It's a from-scratch replacement.**

We built this plugin with a "Folia-first" design. We use Folia's native schedulers for *everything*. The result? A rock-solid, thread-safe core plugin that will **never** lag your server just because someone saved a home or sent a TPA request.

## Why FoliaCore is Better (The Technical Facts)

We're not just another essentials plugin. We are an architectural upgrade.

| Feature | FoliaCore (Our Code) | EssentialsX / CMI | `Essentials-Folia` (Fork) | `zEssentials` (Paid) |
| :--- | :--- | :--- | :--- | :--- |
| **Threading Model** | **Folia-Native.** Uses `EntityScheduler` & `RegionScheduler` for all tasks. 100% thread-safe. | **Single-Threaded.** Incompatible. Will crash, stall, or corrupt data on Folia. | **Patched Legacy Code.** Still built on a single-thread mindset. Risky. | **Folia-Compatible.** Uses schedulers, but as a patch to a Paper design. |
| **Data Saving (Performance)** | **Asynchronous YAML.** All file saves (`/sethome`, `/mute`) use the `AsyncScheduler`. **Guarantees zero tick-skips.** | **Synchronous YAML.** Pauses the entire server to write to a file, causing lag spikes. | **Synchronous YAML.** Inherits the same lag-inducing file saving. | **Requires External MySQL.** The developer *warns* not to use its default file storage, forcing you to set up a database. |
| **Simplicity** | **True "Drag & Drop".** Our async saving is fast *and* simple. No setup needed. | N/A (Doesn't work) | Just drag-and-drop, but has the lag problems above. | **Complex Setup.** Requires a database and multiple dependencies (like `zMenu`) to get full use. |
| **API for Devs** | **Clean Singleton API.** Built from day one to be an API. All managers are thread-safe. | Messy, static-heavy. Unsafe to call from other plugins. | Messy. Still not thread-safe. | Good, but built around its own ecosystem. |
| **The Price** | **100% Free & Open Source.** (We're backed by sponsors\!) | Free | Free | **Paid.** |

## Features (Modules)

  * **Chat Module:** Mute, Unmute, Block, Unblock, Msg, Reply.
  * **Mail Module:** Offline `mail` system (send, read, clear).
  * **Ranged Chat:** Local, World, and Global chat modes.
  * **Teleport Module:** `/tpa`, `/tpahere`, `/tpaccept`, `/tpdeny` with thread-safe request management.
  * **Home Module:** Permission-based homes (`/sethome`, `/home`, `/delhome`, `/homes`).
  * **Spawn Module:** `/setspawn` and `/spawn`.
  * **Team System:** Create, invite, kick, and manage teams.
  * **Kit System:** Full GUI-based kit system with cooldowns.
  * **Warp System:** Admin-defined, permission-based server warps.
  * **GPS System:** Player-based waypoints (`/marker`) with an action-bar compass (`/gps`).

-----

## Commands & Permissions (The "How-To")

### Chat & Mail Module

  * `/mute <player> <time|perm>` - Mutes a player.
      * `foliacore.mute` (default: op)
  * `/unmute <player>` - Unmutes a player.
      * `foliacore.unmute` (default: op)
  * `/msg <player> <message...>` - Sends a private message.
      * `foliacore.msg` (default: true)
  * `/reply <message...>` - Replies to your last private message.
      * `foliacore.reply` (default: true)
  * `/block <player>` - Blocks a player from messaging you.
      * `foliacore.block` (default: true)
  * `/unblock <player>` - Unblocks a player.
      * `foliacore.unblock` (default: true)
  * `/mail <send|read|clear>` - Manages offline mail.
      * `foliacore.mail`, `foliacore.mail.send`, `foliacore.mail.read`, `foliacore.mail.clear` (default: true)
  * `/chat <global|world|regional>` - Toggles your chat mode.
      * `foliacore.chat`, `foliacore.chat.global`, `foliacore.chat.world`, `foliacore.chat.regional` (default: true)

### Teleportation Module

  * `/tpa <player>` - Sends a teleport request to a player.
      * `foliacore.tpa` (default: true)
  * `/tpahere <player>` - Requests a player to teleport to you.
      * `foliacore.tpahere` (default: true)
  * `/tpaccept` - Accepts a pending teleport request.
      * `foliacore.tpaccept` (default: true)
  * `/tpdeny` - Denies a pending teleport request.
      * `foliacore.tpdeny` (default: true)
  * `/sethome [name]` - Sets a home. Defaults to "home" if 1 home is allowed.
      * `foliacore.sethome` (default: true)
  * `/home [name]` - Teleports to your home. Defaults to "home".
      * `foliacore.home` (default: true)
  * `/delhome <name>` - Deletes a home.
      * `foliacore.delhome` (default: true)
  * `/homes` - Lists all your homes.
      * `foliacore.homes.list` (default: true)
  * **Home Permissions:**
      * `foliacore.homes.default` - Gives the player 1 home.
      * `foliacore.homes.5` - Gives the player 5 homes.
      * `foliacore.homes.unlimited` - Gives unlimited homes.

### Spawn & Warp Module

  * `/setspawn` - Sets the server's main spawn.
      * `foliacore.setspawn` (default: op)
  * `/spawn` - Teleports you to the server spawn.
      * `foliacore.spawn` (default: true)
  * `/setwarp <name>` - Creates a server-wide warp.
      * `foliacore.setwarp` (default: op)
  * `/delwarp <name>` - Deletes a server warp.
      * `foliacore.delwarp` (default: op)
  * `/warp <name>` - Teleports to a warp.
      * `foliacore.warp` (default: true)
      * `foliacore.warp.<name>` (for specific warp perms)
      * `foliacore.warp.all` (access to all warps)
  * `/warps` - Lists all warps you have permission for.
      * `foliacore.warps.list` (default: true)

### Team Module

  * `/team create <name>` - Creates a new team.
      * `foliacore.team.create` (default: true)
  * `/team disband` - Disbands your team (owner only).
      * `foliacore.team.disband` (default: true)
  * `/team invite <player>` - Invites a player to your team.
      * `foliacore.team.invite` (default: true)
  * `/team accept [name]` - Accepts a team invite.
      * `foliacore.team.accept` (default: true)
  * `/team decline` - Declines a team invite.
      * `foliacore.team` (default: true)
  * `/team leave` - Leaves your current team.
      * `foliacore.team.leave` (default: true)
  * `/team kick <player>` - Kicks a player from your team.
      * `foliacore.team.kick` (default: true)
  * `/team info [name]` - Shows info for your team or a specific team.
      * `foliacore.team` (default: true)

### Kit Module

  * `/kit [name]` - Opens the Kit GUI or redeems a specific kit.
      * `foliacore.kit` (default: true)
      * `foliacore.kit.<name>` (permission for each specific kit)
  * `/createkit <name> <cooldown>` - Creates a kit from your inventory.
      * `foliacore.kit.admin` (default: op)
  * `/delkit <name>` - Deletes a kit.
      * `foliacore.kit.admin` (default: op)

### GPS Module

  * `/marker set <name>` - Saves your current location as a marker.
      * `foliacore.marker.set` (default: true)
  * `/marker del <name>` - Deletes a marker.
      * `foliacore.marker.delete` (default: true)
  * `/marker list` - Lists all your saved markers.
      * `foliacore.marker.list` (default: true)
  * `/gps <name|off>` - Starts/stops the action bar compass to a marker.
      * `foliacore.gps` (default: true)

-----

## For Developers (Our API)

We built this to be a platform. All our managers are thread-safe and accessible. Just add FoliaCore as a dependency and grab our instance.

```java
import dev.ajaretro.foliaCore.FoliaCore;
import dev.ajaretro.foliaCore.managers.ChatManager;
import dev.ajaretro.foliaCore.managers.TeamManager;

public class MyPlugin extends JavaPlugin {

    public void doSomething(Player player) {
        // You can call our managers from any thread, safely.
        // We already handled the concurrency for you.
        
        ChatManager chat = FoliaCore.getInstance().getChatManager();
        if (chat.isMuted(player.getUniqueId())) {
            //...
        }
        
        TeamManager teams = FoliaCore.getInstance().getTeamManager();
        if (teams.getTeam(player.getUniqueId()) != null) {
            //...
        }
    }
}
```

## Installation

1.  Make sure you are running a **Paper 1.20+** or **Folia** server.
2.  Download the `FoliaCore.jar` from our [Releases Page](https://github.com/AJARETRO/FoliaCore/releases).
3.  Drop it in your `/plugins` folder.
4.  Restart your server. (Do not use `/reload`\!)


**This plugin is proudly developed and tested on hardware from:**

`[ YOUR SPONSOR'S BANNER HERE ]`
