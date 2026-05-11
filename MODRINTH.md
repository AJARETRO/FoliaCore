FoliaCore

Summary
FoliaCore is a Folia-native essentials and administration plugin built for regionalized Minecraft servers. It combines moderation, teleportation, staff tools, utility commands, animated displays, and status output in one configurable package.

Description
FoliaCore is designed for Folia first, not adapted to it later.

Most servers do not need a pile of separate plugins for chat, teleports, staff moderation, display animation, repair/trash utilities, and update checks. FoliaCore packages those into one smaller, Folia-safe codebase with a cleaner configuration model.

Version v-3.2 Blue Nightingale adds a dedicated visual blueprint for tablist, sidebar, nametags, bossbar, multi-server placeholders, and toggle persistence. Major systems can still be enabled or disabled through `config.yml`, and the visual surface lives in `tab-sidebar.yml`.

Compared with many paid essentials plugins, FoliaCore aims to deliver stronger native Folia behavior, less dependency bloat, and a more operator-friendly workflow.

Core Features
- Folia-safe scheduling and player updates.
- Native Paper command registration.
- Modular feature toggles for major systems.
- Animated tab header and footer.
- Animated sidebar title and lines.
- Optional PlaceholderAPI support for display text.
- Calculator command for quick math.
- Trash and dispose commands.
- Repair commands for items and inventories.
- Moderation, teleportation, staff, and utility command suites.
- Async Modrinth update checking.

Feature Highlights
- Chat and social tools: `/msg`, `/reply`, `/mail`, `/nick`, `/realname`, `/mute`, `/unmute`, `/block`, `/unblock`, `/chat`.
- Teleport tools: `/home`, `/sethome`, `/homes`, `/delhome`, `/warp`, `/setwarp`, `/warps`, `/delwarp`, `/tpa`, `/tpahere`, `/tpaccept`, `/tpdeny`, `/spawn`, `/setspawn`, `/back`.
- Staff and utility tools: `/status`, `/ping`, `/clearchat`, `/fly`, `/heal`, `/feed`, `/god`, `/give`, `/clear`, `/invsee`, `/enderchest`, `/workbench`, `/hat`, `/broadcast`, `/time`, `/weather`, `/antiraid`, `/vanish`, `/socialspy`, `/staffchat`, `/sc`.
- Quality-of-life additions: `/calc`, `/trash`, `/dispose`, `/repair`.

Configuration
- Major modules can be toggled in `config.yml`.
- Tab and sidebar methods use configurable frame lists.
- Update intervals are configurable.
- Built-in placeholders cover player, world, ping, TPS, coordinates, and online counts.
- PlaceholderAPI placeholders are resolved automatically when the plugin is present.

Visual Blueprint
- `tab-sidebar.yml` includes MySQL-backed toggle persistence.
- `tab-sidebar.yml` supports tab sorting, objectives, spectator handling, and header/footer animation.
- `tab-sidebar.yml` supports animated sidebars with 15-line layouts and flickerless rendering.
- `tab-sidebar.yml` supports nametags, belowname text, bossbars, ping spoof, fonts, and sprites.
- `tab-sidebar.yml` supports Bungee/Velocity placeholder fields and conditional expressions.

Installation
1. Download the latest release jar.
2. Place it in your server `plugins/` folder.
3. Start the server on Folia with Java 21 or newer.
4. Edit `plugins/FoliaCore/config.yml` for core modules.
5. Edit `plugins/FoliaCore/tab-sidebar.yml` for the advanced visual stack.
6. Restart the server.

FAQ
- Does it require Vault? No. The economy layer was removed.
- Does it support PlaceholderAPI? Yes, for tab and sidebar text.
- Is it only for Folia? Yes.
- Can I turn features off? Yes, major systems are configurable.
- Is it free? Yes.

Sponsor
TrueCloud Hosting sponsors FoliaCore.

South Asia hosting pricing: $0.50 per GB.

WhatsApp: +8801989208751

This description is plain text so it stays readable in Modrinth's fields.

FoliaCore does not ship cheats, x-ray tools, combat automation, duplication features, or hidden bypass systems.
