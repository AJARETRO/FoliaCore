# FoliaCore v3.4 Blue Nightingale

FoliaCore is a Folia-first essentials and administration plugin for Minecraft servers that want one clean toolkit instead of many separate add-ons. It blends player convenience, staff utilities, teleports, moderation, visual polish, and permission control into a single package that feels intentionally built for regionalized servers.

## Why server owners choose it

FoliaCore is made for servers that want the essentials experience without the usual clutter.

- Built for Folia from the ground up.
- Modern Paper command registration.
- Modular feature toggles in `config.yml`.
- Separate permission nodes for nearly every command.
- Dynamic per-warp and per-kit access control.
- A visually rich tab/sidebar blueprint in `tab-sidebar.yml`.
- Modrinth update checks for easy release tracking.
- No Vault economy layer, so the focus stays on gameplay, administration, and polish.

## Why it feels better to use

The difference is not just technical. It is practical.

Players get fast access to homes, warps, kits, chat tools, mail, teleport requests, and small quality-of-life commands that reduce friction without turning the server into a plugin maze.

Staff get moderation, vanish, social spy, inventory tools, repair tools, time and weather control, anti-raid support, and clean access to the tools they actually need.

Developers and operators get something even more important: permission nodes that are easy to audit, easy to grant, and easy to document.

## What is new in v3.4

This release updates the plugin branding to v3.4 Blue Nightingale and formalizes the permission tree in the plugin metadata. The result is a cleaner release story and a more complete server-side permission setup.

Notable changes:

- Added a permission gate for `/kit`.
- Declared all core permission defaults in `paper-plugin.yml`.
- Updated the runtime banner to v3.4.
- Standardized the release version across the build files.
- Reworked the docs so operators and developers can scan them quickly.

## Installation

1. Download the release jar.
2. Put it in `plugins/`.
3. Start the server once on Folia with Java 21.
4. Edit `plugins/FoliaCore/config.yml` to enable or disable the systems you want.
5. Edit `plugins/FoliaCore/tab-sidebar.yml` if you want the animated visual stack.
6. Assign permissions per group or per player.

## Permission design

The plugin follows a simple rule set.

- Player-friendly commands default to `true`.
- Staff, admin, destructive, or high-impact commands default to `false`.
- Warp and kit access can be narrowed further with individual nodes.

### Important patterns

- `/warp` can be granted globally with `foliacore.warp.all`.
- Specific warps can be granted with `foliacore.warp.<warpname>`.
- `/kit` uses `foliacore.kit`.
- Kits created by staff generate their own node, such as `foliacore.kit.<kitname>`.
- Kit creation and deletion stay on `foliacore.kit.admin`.

## Command and permission reference

### Social and chat

| Command | Permission node(s) | Default |
| --- | --- | --- |
| `/msg` | `foliacore.msg` | `true` |
| `/reply` | `foliacore.reply` | `true` |
| `/mail` | `foliacore.mail.send`, `foliacore.mail.read`, `foliacore.mail.clear` | mixed |
| `/nick` | `foliacore.nick`, `foliacore.nick.color` | mixed |
| `/realname` | `foliacore.realname` | `true` |
| `/block` | `foliacore.block` | `true` |
| `/unblock` | `foliacore.unblock` | `true` |
| `/chat` | `foliacore.chat.global`, `foliacore.chat.world`, `foliacore.chat.regional` | `true` |
| `/mute` | `foliacore.mute` | `false` |
| `/unmute` | `foliacore.unmute` | `false` |
| `/socialspy` | `foliacore.socialspy` | `false` |
| `/staffchat`, `/sc` | `foliacore.staffchat` | `false` |

### Teleport and travel

| Command | Permission node(s) | Default |
| --- | --- | --- |
| `/sethome` | `foliacore.sethome` | `true` |
| `/home` | `foliacore.home` | `true` |
| `/homes` | `foliacore.homes.list` | `true` |
| `/delhome` | `foliacore.delhome` | `true` |
| `/tpa` | `foliacore.tpa` | `true` |
| `/tpahere` | `foliacore.tpahere` | `true` |
| `/tpaccept` | `foliacore.tpaccept` | `true` |
| `/tpdeny` | `foliacore.tpdeny` | `true` |
| `/spawn` | `foliacore.spawn` | `true` |
| `/back` | `foliacore.back` | `true` |
| `/warp` | `foliacore.warp.all`, `foliacore.warp.<warpname>` | mixed |
| `/warps` | `foliacore.warps.list` | `true` |
| `/setwarp` | `foliacore.setwarp` | `false` |
| `/delwarp` | `foliacore.delwarp` | `false` |
| `/setspawn` | `foliacore.setspawn` | `false` |
| `/setfirstspawn` | `foliacore.setfirstspawn` | `false` |
| `/tp` | `foliacore.tp`, `foliacore.tp.others` | `false` |
| `/tphere` | `foliacore.tphere` | `false` |
| `/gps` | `foliacore.gps` | `true` |

### Teams and kits

| Command | Permission node(s) | Default |
| --- | --- | --- |
| `/team` | `foliacore.team.create`, `foliacore.team.disband`, `foliacore.team.invite`, `foliacore.team.accept`, `foliacore.team.leave`, `foliacore.team.kick` | `true` |
| `/kit` | `foliacore.kit` | `true` |
| `/createkit` | `foliacore.kit.admin` | `false` |
| `/delkit` | `foliacore.kit.admin` | `false` |

### Utility and staff tools

| Command | Permission node(s) | Default |
| --- | --- | --- |
| `/calc` | `foliacore.calc` | `true` |
| `/trash`, `/dispose` | `foliacore.trash` | `true` |
| `/workbench`, `/wb` | `foliacore.workbench` | `true` |
| `/hat` | `foliacore.hat` | `true` |
| `/enderchest`, `/ec` | `foliacore.enderchest`, `foliacore.enderchest.others` | mixed |
| `/ping` | `foliacore.ping`, `foliacore.ping.others` | mixed |
| `/scoreboard`, `/sidebar` | `foliacore.scoreboard.toggle` | `true` |
| `/feed` | `foliacore.feed`, `foliacore.feed.others` | mixed |
| `/fly` | `foliacore.fly`, `foliacore.fly.others` | mixed |
| `/heal` | `foliacore.heal`, `foliacore.heal.others` | mixed |
| `/god` | `foliacore.god`, `foliacore.god.others` | mixed |
| `/repair` | `foliacore.repair`, `foliacore.repair.all`, `foliacore.repair.others`, `foliacore.repair.others.all` | mixed |
| `/give` | `foliacore.give` | `false` |
| `/clear` | `foliacore.clear`, `foliacore.clear.others` | mixed |
| `/invsee` | `foliacore.invsee` | `false` |
| `/gamemode`, `/gms`, `/gmc`, `/gma`, `/gmsp` | `foliacore.gamemode`, `foliacore.gamemode.others` | mixed |

### Administration and control

| Command | Permission node(s) | Default |
| --- | --- | --- |
| `/broadcast` | `foliacore.broadcast` | `false` |
| `/time` | `foliacore.time` | `false` |
| `/weather` | `foliacore.weather` | `false` |
| `/status` | `foliacore.status` | `false` |
| `/clearchat` | `foliacore.clearchat`, `foliacore.clearchat.bypass` | mixed |
| `/vanish` | `foliacore.vanish` | `false` |
| `/antiraid` | `foliacore.admin.antiraid` | `false` |
| `/ban` | `foliacore.ban`, `foliacore.ban.exempt` | mixed |
| `/tempban` | `foliacore.tempban`, `foliacore.ban.exempt` | mixed |
| `/unban` | `foliacore.unban` | `false` |
| `/kick` | `foliacore.kick`, `foliacore.kick.exempt` | mixed |
| `/mute` | `foliacore.mute` | `false` |
| `/unmute` | `foliacore.unmute` | `false` |
| `/marker` | `foliacore.marker.set`, `foliacore.marker.delete`, `foliacore.marker.list` | `false` |

## Visual systems

`tab-sidebar.yml` is the optional visual blueprint for server owners who want more than plain text.

It covers:

- Tab header and footer animation.
- Sidebar layouts and line control.
- Nametags, belowname, bossbars, and ping spoofing.
- Fonts, sprites, placeholders, and output formatting.
- Toggle persistence for per-player visual settings.

## For developers and server admins

FoliaCore is clean to reason about because the code is split across clear surfaces:

- `FoliaCore.java` handles registration.
- Command classes enforce permission checks.
- Managers handle persistence and runtime logic.
- The plugin descriptor now publishes the permission defaults directly.

That makes the plugin easier to audit, easier to document, and easier to grant to the right people.

## Final note

If you want a Folia-friendly essentials suite that is practical first and polished second, FoliaCore is built for exactly that space.

Enjoy the release, and shape the server around it.