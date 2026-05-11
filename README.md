# FoliaCore v-3.1 Frozen Nightingale

FoliaCore is a Folia-native utility and administration plugin focused on performance, moderation workflow, and clean server operations.

## Highlights
- Economy system removed for leaner runtime and simpler dependency management.
- New QoL commands: `/calc`, `/trash` (`/dispose`), `/repair`.
- Animated tablist system with user-defined methods and frame sequences.
- Animated sidebar system with user-defined methods and frame sequences.
- PlaceholderAPI support for tab and sidebar text (optional, auto-detected).
- Async Modrinth update checks with in-game op notification when an update is available.

## New Commands
- `/calc <expression>`: Evaluate expressions like `10*4`, `(15+5)/2`, `2^8`.
- `/trash` and `/dispose`: Open a disposable inventory.
- `/repair [all]`: Repair held item or full inventory.
- `/repair <player> [all]`: Repair another player's items.

## Tab and Sidebar Animation
Tab and sidebar are configured with method names and frame lists. You can rename methods freely and point each display section to any method.

Example pattern:
- `tab.header-method: method-1`
- `tab.methods.method-1:` each line is one frame.
- `sidebar.title-method: method-1`
- `sidebar.line-methods:` each entry points to one animated method.

Each update cycle advances frames automatically and applies placeholders.

## Placeholder Support
Built-in placeholders include:
- `%player_name%`
- `%world_name%`
- `%online_players%`
- `%max_players%`
- `%player_ping%`
- `%server_tps%`
- `%x%`, `%y%`, `%z%`

If PlaceholderAPI is present, PAPI placeholders are also resolved in tab and sidebar output.

Release: v-3.1 Frozen Nightingale

## Installation
1. Download the latest v-3 Nightingale jar from Releases.
2. Place jar in your server `plugins/` folder.
3. Start server with Folia 26.1.2+ and Java 21+.
4. Edit `plugins/FoliaCore/config.yml` to customize tab/sidebar methods and frames.
5. Restart server.

## Configuration Notes
Important module toggles:
- `modules.tab`
- `modules.sidebar`
- `modules.chat`
- `modules.teleport`
- `modules.kits`
- `modules.utility`
- `modules.staff`
- `modules.system`

Animation config sections:
- `tab.enabled`
- `tab.update-interval-ticks`
- `tab.header-method`
- `tab.footer-method`
- `tab.methods.*`
- `sidebar.enabled`
- `sidebar.update-interval-ticks`
- `sidebar.title-method`
- `sidebar.line-methods`
- `sidebar.methods.*`

## Removed in v-3
- Balance command
- Pay command
- Eco admin command
- Vault economy bridge and manager
- Vault plugin soft dependency

## Runtime
- Folia 26.1.2+
- Java 21+
- Paper API 1.21.8-R0.1-SNAPSHOT

## License
MIT
