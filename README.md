# ⚡ FoliaCore
> The reinforced essentials suite for modern Folia servers.

![Platform](https://img.shields.io/badge/platform-Folia-7289DA?style=for-the-badge&logo=paper&logoColor=white)
![Java](https://img.shields.io/badge/Java-21+-orange?style=for-the-badge)
![Version](https://img.shields.io/badge/release-FoliaCore--V2--Reinforced-blue?style=for-the-badge)
![Build](https://img.shields.io/badge/build-passing-brightgreen?style=for-the-badge)
![License](https://img.shields.io/badge/license-MIT-lightgrey?style=for-the-badge)

FoliaCore is a Folia-first, region-safe core utility plugin designed to replace bloated legacy essentials stacks with a modular, thread-aware foundation.

If your server is on Folia, this is built for your reality:
- Regionized threading
- Programmatic command registration for modern Paper plugins
- Modular systems you can disable when not needed
- Real telemetry in `/status` without fake MSPT values

---

## 📚 Table of Contents
- [What Makes FoliaCore Different](#-what-makes-foliacore-different)
- [Core Philosophy](#-core-philosophy)
- [Feature Index](#-feature-index)
- [Installation](#-installation)
- [Configuration Reference](#-configuration-reference)
- [Command Wiki](#-command-wiki)
- [Permission Wiki](#-permission-wiki)
- [Architecture Notes](#-architecture-notes)
- [Performance and Stability Notes](#-performance-and-stability-notes)
- [Operations Playbook](#-operations-playbook)
- [Troubleshooting](#-troubleshooting)
- [Migration Notes](#-migration-notes)
- [FAQ](#-faq)
- [Support](#-support)

---

## 🧠 What Makes FoliaCore Different

### Compared to Typical Legacy Essentials Stacks
FoliaCore is intentionally engineered around Folia constraints instead of adapting old single-thread assumptions.

1. It embraces programmatic command registration for modern Paper/Folia plugin bootstrap rules.
2. It avoids fake timing output and reports telemetry defensively (`N/A` when unavailable).
3. It is module-driven, so you can turn off full systems instead of carrying dead weight.
4. It uses manager-based persistence instead of one giant monolith.
5. It is explicit about startup behavior and operational state in logs.

### Why this is typically less bloated
1. No giant all-in-one compatibility shim layer.
2. No forced dependency explosion for unrelated features.
3. No mandatory kitchen-sink command set in one hard-coded static schema.
4. No synthetic metrics pretending to be accurate on regionized runtime.

### Why this is typically more stable on Folia
1. Region-aware logic paths are prioritized.
2. Command lifecycle aligns with modern Paper plugin APIs.
3. Thread-sensitive systems (economy/state) are guarded with concurrent structures.
4. Status and ops output are conservative and avoid fabricated values.

Note: "Better" is context-dependent. If you need every niche feature from very large legacy suites, FoliaCore intentionally favors lean + safe over maximal surface area.

---

## 🛠 Core Philosophy

1. Stability before feature count.
2. Clear module boundaries.
3. Real operational insight over cosmetic numbers.
4. Predictable permissions.
5. Config-first behavior where it matters.

---

## 🧩 Feature Index

### System + Ops
- `/status` (regionalized status, compact mode)
- `/ping`
- `/clearchat`
- entity cleanup task
- auto broadcaster task
- startup reinforced banner (configurable)

### Chat + Social
- private messaging (`/msg`, `/reply`)
- block/unblock
- chat modes
- staff chat + social spy
- mail system
- mute/unmute
- nick/realname

### Teleport + Travel
- homes
- tpa flow
- spawn and first-spawn
- direct teleport commands
- back
- warps
- markers and gps

### Utility/Admin
- fly/feed/heal/god
- clear inventory
- gamemode shortcuts
- give
- enderchest/workbench
- time/weather
- broadcast
- punishments (ban/tempban/unban/kick)
- anti-raid controls
- vanish

### Economy + Kits + Teams
- balance/pay/eco
- kits CRUD + GUI usage
- teams flow (create/invite/accept/leave/kick/disband)

---

## 📥 Installation

### Requirements
- Folia server (modern Paper/Folia API generation)
- Java 21+
- Vault optional but recommended for economy integration

### Steps
1. Stop server.
2. Drop the release jar in plugins.
3. Start server and let files generate.
4. Review config values.
5. Grant permissions by your permission plugin.
6. Validate with `/status` and a smoke test command set.

### First Boot Success Signals
- Config manager loaded.
- Plugin initialized in regionized mode.
- No command registration warnings.
- `/status` returns data (or explicit `N/A` for unavailable telemetry).

---

## ⚙ Configuration Reference

### modules
Controls subsystem enable/disable.

- `modules.economy`
- `modules.teleport`
- `modules.kits`
- `modules.chat`
- `modules.markers`
- `modules.teams`
- `modules.staff`
- `modules.system`
- `modules.utility`
- `modules.antiraid`
- `modules.security`

### system
- `system.maintenance-mode`
- `system.maintenance-kick-message`
- `system.first-spawn-enabled`
- `system.entity-cleanup-enabled`
- `system.entity-cleanup-interval`
- `system.minimum-tps-threshold`
- `system.auto-broadcaster-enabled`
- `system.auto-broadcast-interval`

### branding
- `branding.startup-banner-enabled`
- `branding.owner-display`

### status
- `status.show-world-summary`
- `status.show-region-details`
- `status.max-regions`
- `status.region-chunk-span`

### antiraid
- `antiraid.enabled`
- `antiraid.threshold-per-second`
- `antiraid.auto-lockdown`
- `antiraid.notify-staff`

### security
- `security.enabled`
- `security.staff-ip-lock`
- `security.require-console-for-unlock`

---

## 📜 Command Wiki

Commands are programmatically registered at startup. Availability may depend on module toggles.

### Chat + Social
| Command | Permission | Scope | Description |
|---|---|---|---|
| `/mute <player> <time\|permanent>` | `foliacore.mute` | admin | Mute player |
| `/unmute <player>` | `foliacore.unmute` | admin | Unmute player |
| `/msg <player> <message...>` | `foliacore.msg` | player | Private message |
| `/reply <message...>` | `foliacore.reply` | player | Reply to last DM |
| `/block <player>` | `foliacore.block` | player | Block DMs from player |
| `/unblock <player>` | `foliacore.unblock` | player | Unblock player |
| `/mail <send/read/clear>` | `foliacore.mail` + sub-perms | player | Offline mail workflow |
| `/chat <global/world/regional>` | `foliacore.chat` + mode perms | player | Switch chat mode |
| `/nick <name/off>` | `foliacore.nick` | player | Set nickname |
| `/realname <nickname>` | `foliacore.realname` | mod | Resolve nickname to account |

### Teleport + Homes + Spawn
| Command | Permission | Scope | Description |
|---|---|---|---|
| `/sethome [name]` | `foliacore.sethome` | player | Set home |
| `/home [name]` | `foliacore.home` | player | Teleport home |
| `/delhome <name>` | `foliacore.delhome` | player | Delete home |
| `/homes` | `foliacore.homes.list` | player | List homes |
| `/tpa <player>` | `foliacore.tpa` | player | Request TP |
| `/tpahere <player>` | `foliacore.tpahere` | player | Request target TP to you |
| `/tpaccept` | `foliacore.tpaccept` | player | Accept TP request |
| `/tpdeny` | `foliacore.tpdeny` | player | Deny TP request |
| `/setspawn` | `foliacore.setspawn` | admin | Set spawn |
| `/spawn` | `foliacore.spawn` | player | Go spawn |
| `/tp` | `foliacore.tp` / `foliacore.tp.others` | staff | Teleport command |
| `/tphere` | `foliacore.tphere` | staff | Teleport player to you |
| `/back` | `foliacore.back` | player | Return previous location |
| `/setfirstspawn` | `foliacore.setfirstspawn` | admin | Set first-join spawn |

### Teams
| Command | Permission | Scope | Description |
|---|---|---|---|
| `/team ...` | `foliacore.team` | player | Team root command |
| `/team create` | `foliacore.team.create` | player | Create team |
| `/team disband` | `foliacore.team.disband` | player | Disband team |
| `/team invite` | `foliacore.team.invite` | player | Invite member |
| `/team accept` | `foliacore.team.accept` | player | Accept invite |
| `/team leave` | `foliacore.team.leave` | player | Leave team |
| `/team kick` | `foliacore.team.kick` | player | Kick teammate |

### Kits
| Command | Permission | Scope | Description |
|---|---|---|---|
| `/kit [name]` | `foliacore.kit` | player | Open/redeem kits |
| `/createkit <name> <cooldown>` | `foliacore.kit.admin` | admin | Create kit |
| `/delkit <name>` | `foliacore.kit.admin` | admin | Delete kit |

### Markers + Warps
| Command | Permission | Scope | Description |
|---|---|---|---|
| `/marker <set/del/list>` | marker sub-perms | player | Marker management |
| `/gps <name/off>` | `foliacore.gps` | player | GPS to marker |
| `/setwarp <name>` | `foliacore.setwarp` | admin | Create warp |
| `/delwarp <name>` | `foliacore.delwarp` | admin | Delete warp |
| `/warp <name>` | `foliacore.warp.<name>` or `foliacore.warp.all` | player/admin | Teleport to warp |
| `/warps` | `foliacore.warps.list` | player | List allowed warps |

### Economy
| Command | Permission | Scope | Description |
|---|---|---|---|
| `/balance [player]` | `foliacore.balance.self` / `foliacore.balance.other` | player/staff | Balance view |
| `/pay <player> <amount>` | `foliacore.pay` | player | Transfer funds |
| `/eco <give/take/set> ...` | `foliacore.eco` | admin | Economy admin |

### Utility + Moderation + Staff
| Command | Permission | Scope | Description |
|---|---|---|---|
| `/ban <player> [reason]` | `foliacore.ban` | mod | Ban player |
| `/tempban <player> <time> [reason]` | `foliacore.tempban` | mod | Temp ban |
| `/unban <player>` | `foliacore.unban` | mod | Unban |
| `/kick <player> [reason]` | `foliacore.kick` | mod | Kick player |
| `/fly [player]` | `foliacore.fly` / `foliacore.fly.others` | player/staff | Toggle flight |
| `/heal [player]` | `foliacore.heal` / `foliacore.heal.others` | player/staff | Heal |
| `/feed [player]` | `foliacore.feed` / `foliacore.feed.others` | player/staff | Feed |
| `/god [player]` | `foliacore.god` / `foliacore.god.others` | player/staff | God mode |
| `/gamemode ...` | `foliacore.gamemode` / `foliacore.gamemode.others` | player/staff | Change gamemode |
| `/gms` `/gmc` `/gma` `/gmsp` | gamemode perms | player/staff | Gamemode shortcuts |
| `/give <player> <item> [amount]` | `foliacore.give` | staff | Give item |
| `/clear [player]` | `foliacore.clear` / `foliacore.clear.others` | player/staff | Clear inventory |
| `/invsee <player>` | `foliacore.invsee` | staff | Inspect inventory |
| `/enderchest [player]` | `foliacore.enderchest` / `foliacore.enderchest.others` | player/staff | Open ender chest |
| `/ec [player]` | same as enderchest | player/staff | Shortcut |
| `/workbench` | `foliacore.workbench` | player | Open crafting table |
| `/wb` | `foliacore.workbench` | player | Shortcut |
| `/hat` | `foliacore.hat` | player | Wear held item |
| `/broadcast <message...>` | `foliacore.broadcast` | admin | Broadcast |
| `/time <set/add/day/night/...>` | `foliacore.time` | admin | Time control |
| `/weather <clear/rain/thunder>` | `foliacore.weather` | admin | Weather control |
| `/status [compact]` | `foliacore.status` | admin | Regionized status |
| `/ping [player]` | `foliacore.ping` / `foliacore.ping.others` | player/staff | Ping check |
| `/clearchat` | `foliacore.clearchat` | staff | Clear chat globally |
| `/antiraid <subcommand>` | `foliacore.admin.antiraid` | admin | Anti-raid controls |
| `/vanish` | `foliacore.vanish` | staff | Vanish toggle |
| `/socialspy` | `foliacore.socialspy` | staff | Social spy toggle |
| `/staffchat` | `foliacore.staffchat` | staff | Staff-only chat |
| `/sc` | `foliacore.staffchat` | staff | Staff chat shortcut |

---

## 🔐 Permission Wiki

### Core/User
- `foliacore.msg`
- `foliacore.reply`
- `foliacore.block`
- `foliacore.unblock`
- `foliacore.mail`
- `foliacore.mail.send`
- `foliacore.mail.read`
- `foliacore.mail.clear`
- `foliacore.chat`
- `foliacore.chat.global`
- `foliacore.chat.world`
- `foliacore.chat.regional`
- `foliacore.home`
- `foliacore.sethome`
- `foliacore.delhome`
- `foliacore.homes.list`
- `foliacore.tpa`
- `foliacore.tpahere`
- `foliacore.tpaccept`
- `foliacore.tpdeny`
- `foliacore.spawn`
- `foliacore.team`
- `foliacore.team.create`
- `foliacore.team.invite`
- `foliacore.team.accept`
- `foliacore.team.leave`
- `foliacore.kit`
- `foliacore.marker.set`
- `foliacore.marker.delete`
- `foliacore.marker.list`
- `foliacore.gps`
- `foliacore.balance.self`
- `foliacore.pay`
- `foliacore.nick`
- `foliacore.workbench`
- `foliacore.hat`

### Moderator/Staff/Admin
- `foliacore.mute`
- `foliacore.unmute`
- `foliacore.realname`
- `foliacore.kick`
- `foliacore.kick.exempt`
- `foliacore.ban`
- `foliacore.ban.exempt`
- `foliacore.tempban`
- `foliacore.unban`
- `foliacore.fly`
- `foliacore.fly.others`
- `foliacore.heal`
- `foliacore.heal.others`
- `foliacore.feed`
- `foliacore.feed.others`
- `foliacore.god`
- `foliacore.god.others`
- `foliacore.gamemode`
- `foliacore.gamemode.others`
- `foliacore.give`
- `foliacore.clear`
- `foliacore.clear.others`
- `foliacore.invsee`
- `foliacore.enderchest`
- `foliacore.enderchest.others`
- `foliacore.broadcast`
- `foliacore.time`
- `foliacore.weather`
- `foliacore.status`
- `foliacore.ping`
- `foliacore.ping.others`
- `foliacore.clearchat`
- `foliacore.clearchat.bypass`
- `foliacore.vanish`
- `foliacore.socialspy`
- `foliacore.staffchat`
- `foliacore.admin.antiraid`
- `foliacore.eco`
- `foliacore.balance.other`
- `foliacore.setspawn`
- `foliacore.setfirstspawn`
- `foliacore.tp`
- `foliacore.tp.others`
- `foliacore.tphere`
- `foliacore.back`
- `foliacore.setwarp`
- `foliacore.delwarp`
- `foliacore.warp.all`
- `foliacore.warps.list`
- `foliacore.kit.admin`
- `foliacore.team.disband`
- `foliacore.team.kick`

### Dynamic/Generated Patterns
- `foliacore.warp.<warpname>` (per-warp access)

### Suggested Bundles (example)
- `group.player`: core/user list
- `group.mod`: player + moderation basics
- `group.admin`: mod + economy/teleport/system controls

---

## 🏗 Architecture Notes

### Command lifecycle
FoliaCore uses programmatic command registration through modern Paper plugin APIs, avoiding YAML command declaration pitfalls in Paper plugins.

### Managers
Subsystem managers encapsulate each domain:
- chat
- teleport
- teams
- kits
- warps
- markers
- economy
- bans
- vanish/socialspy/spawn/antiraid

### Data model
YAML-backed manager persistence for independent module state:
- `chat_data.yml`
- `teleport_data.yml`
- `kits.yml`
- `markers.yml`
- `team_data.yml`
- `warps.yml`
- `bans.yml`
- `security.yml`
- `autobroadcasts.yml`

### Threading posture
- Designed for Folia regionized runtime behavior
- Avoids pretending global single-thread guarantees exist
- Uses defensive output for unavailable metrics

---

## 📈 Performance and Stability Notes

### `/status` behavior
- Reports actual available metrics
- If metric API unavailable, shows `N/A`
- No fake `0.00 mspt` values
- Region section uses active player region snapshots (configurable granularity)

### Why this improves operational trust
1. You see what the server can truthfully provide.
2. You avoid chasing phantom zero-latency readings.
3. Region activity is visible in one command output.

### Tuning knobs
- reduce `status.max-regions` if chat output is too long
- increase `status.region-chunk-span` for broader aggregation
- use `/status compact` for quick checks

---

## 🧪 Operations Playbook

### Smoke test after update
1. Start server and check startup banner.
2. Run `/status` and `/status compact`.
3. Validate one command per module.
4. Confirm no command registration warnings.
5. Confirm module toggles work by disabling one module and restarting.

### Recommended admin checks
- `plugins`
- `/status`
- `/ping <player>`
- warp/home roundtrip
- mute/unmute roundtrip
- ban/tempban/unban controlled test account

---

## 🆘 Troubleshooting

### "Vault NOT found"
Install Vault to enable full economy provider integration.

### Command appears unavailable
1. Check module toggle for that command family.
2. Check permission node.
3. Confirm plugin enabled without startup exceptions.

### `/status` shows N/A
This is expected when a metric API is not exposed by the active implementation/path. It is safer than fabricated output.

### Staff features not broadcasting
Validate permissions:
- `foliacore.staffchat`
- `foliacore.socialspy`
- `foliacore.vanish`

---

## 🔄 Migration Notes

From larger legacy suites, migrate in layers:
1. start with utility + teleport + moderation
2. move economy behavior
3. transition chat/social
4. validate permissions and command macros

Keep your old data backups until your test world passes all workflows.

---

## ❓ FAQ

### Is this for Folia only?
Yes, this project targets Folia-first behavior.

### Can I run this with another essentials plugin?
Technically possible, but not recommended due to command overlap and behavior conflicts.

### Does it try to be the biggest plugin?
No. It tries to be the safest operational core for Folia servers.

### Is the startup style configurable?
Yes, via `branding.startup-banner-enabled` and `branding.owner-display`.

### Why does `/status` not always print exact global mspt?
Folia’s runtime is regionized and APIs vary by implementation path. FoliaCore prefers honest output over guessed values.

---

## 🤝 Support
- Website: https://ajaretro.dev
- Issues: https://github.com/AJARETRO/FoliaCore/issues
- Releases: https://github.com/AJARETRO/FoliaCore/releases

---

Built for Folia operators who value stability, clarity, and control.
