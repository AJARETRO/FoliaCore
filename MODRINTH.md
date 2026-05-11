FoliaCore

## Summary

FoliaCore is a Folia-first essentials and administration plugin for Minecraft servers that want one clean toolkit instead of many separate add-ons. It combines moderation, teleportation, staff tools, utility commands, animated displays, and update checks in a single configurable package.

## What It Is

FoliaCore is built for Folia first, not patched onto it later.

If you run a regionalized server, you usually want essentials commands, staff tools, and display systems that behave correctly under Folia's threading model. FoliaCore is meant to reduce plugin clutter while keeping the runtime safe, lightweight, and easier to manage.

This release, v-3.2 Blue Nightingale, focuses on a stronger visual stack and clearer configuration. Major systems can be turned on or off in `config.yml`, while the advanced tab and sidebar blueprint lives in `tab-sidebar.yml`.

## Why Server Owners Install It

- One plugin instead of many separate essentials add-ons.
- Folia-safe scheduling and player updates.
- Clean command registration through Paper APIs.
- Animated tab and sidebar text.
- Optional PlaceholderAPI support for richer server text.
- Modrinth update checks so operators can see when a new release is available.
- No Vault economy layer, so the plugin stays focused on administration and quality of life.
- Clear configuration files for fast setup and easier maintenance.

## Core Features

- Modular feature toggles for major systems.
- Animated tab header and footer.
- Animated sidebar title and lines.
- Chat, teleportation, staff, and utility command suites.
- Calculator command for quick math.
- Trash and dispose commands for item cleanup.
- Repair commands for items and inventories.
- Async Modrinth update checking.

## Feature Guide

### Chat and Social Tools

- `/msg`, `/reply`, `/mail`, `/nick`, `/realname`.
- `/mute`, `/unmute`, `/block`, `/unblock`, `/chat`.
- Useful for player communication, moderation, and name management.
- Good for SMPs, community servers, and staff-managed networks.

### Teleportation Tools

- `/home`, `/sethome`, `/homes`, `/delhome`.
- `/warp`, `/setwarp`, `/warps`, `/delwarp`.
- `/tpa`, `/tpahere`, `/tpaccept`, `/tpdeny`.
- `/spawn`, `/setspawn`, `/setfirstspawn`, `/back`.
- Useful for survival, SMP, and community servers.
- Keeps common travel commands in one place instead of scattered across plugins.

### Staff and Utility Tools

- `/status`, `/ping`, `/clearchat`, `/fly`, `/heal`, `/feed`, `/god`.
- `/give`, `/clear`, `/invsee`, `/enderchest`, `/workbench`, `/hat`.
- `/broadcast`, `/time`, `/weather`, `/antiraid`.
- `/vanish`, `/socialspy`, `/staffchat`, `/sc`.
- Useful for moderation, support, event management, and fast operator actions.
- Designed to keep everyday admin actions quick without adding extra dependencies.

### Quality-of-Life Tools

- `/calc` for expressions like `10*4` or `(15+5)/2`.
- `/trash` and `/dispose` for deleting unwanted items safely.
- `/repair` for held items, full inventories, or other players.
- Small commands like these reduce friction for staff and players alike.

## Configuration

- Major modules can be toggled in `config.yml`.
- Tab and sidebar animation methods use configurable frame lists.
- Update intervals are configurable.
- Built-in placeholders cover player, world, ping, TPS, coordinates, and online counts.
- PlaceholderAPI placeholders are resolved automatically when the plugin is installed.
- The design goal is simple: keep the essentials flexible without making the file layout hard to read.

## Visual Blueprint

`tab-sidebar.yml` is the advanced visual configuration file.

It supports:

- Tab sorting, objectives, spectator handling, and header/footer animation.
- Animated sidebars with 15-line layouts and flickerless rendering.
- Nametags, belowname text, bossbars, ping spoof, fonts, and sprites.
- Bungee and Velocity placeholder fields, conditional expressions, and persistence options.

This file exists for server owners who want a polished look without having to chain together multiple visual plugins.

## Example Setup Flow

1. Download the latest release jar.
2. Place it in your server `plugins/` folder.
3. Start the server on Folia with Java 21 or newer.
4. Edit `plugins/FoliaCore/config.yml` for the main modules.
5. Edit `plugins/FoliaCore/tab-sidebar.yml` for the advanced visual system.
6. Restart the server.

If you want a minimal setup, start with the default config and disable only the modules you do not need.

## Compatibility

- Folia support is native.
- Paper command registration is used.
- PlaceholderAPI is optional.
- Vault is not required.
- The economy layer was removed intentionally.
- The plugin is designed for operators who want administration, QoL, and visual polish in one install.

## FAQ

### Does it require Vault?

No. The economy layer was removed.

### Does it support PlaceholderAPI?

Yes, for tab and sidebar text.

### Is it only for Folia?

Yes.

### Can I turn features off?

Yes, the major systems are configurable.

### Is it free?

Yes.

### Does it have update checks?

Yes. It checks Modrinth asynchronously and can notify operators when updates are available.

## bStats

- bStats plugin ID: 28430.
- This is the analytics ID used by the plugin's bStats integration.

## Sponsor

TrueCloud Hosting sponsors FoliaCore.

South Asia hosting pricing: $0.50 per GB.

WhatsApp: +8801989208751

## Notes

This description is plain text so it stays readable in Modrinth's fields and easy for users to scan.

FoliaCore does not ship cheats, x-ray tools, combat automation, duplication features, or hidden bypass systems.
