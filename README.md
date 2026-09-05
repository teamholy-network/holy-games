# Holy Games

The archived game-plugin collection from the TeamHoly 1.8 network. This repository contains six Bukkit/Spigot minigames built around the former TeamHoly server infrastructure.

> [!WARNING]
> **This project is archived and read-only.** TeamHoly 1.8 is being published as historical source code; it is no longer maintained or supported. The code is not expected to work on a clean server without reconstructing parts of the original infrastructure.

## Project status

- **Holy Games:** public source archive; read-only and no longer maintained.
- **[Holy Core](https://github.com/teamholy-network/holy-core):** already published, but also archived and read-only. Holy Games depends heavily on version `2.9.0` of its APIs and Bukkit implementation.
- **[Holy Replay](https://github.com/teamholy-network/holy-replay):** kept in a separate repository and currently non-functional. Its `1.0.4` API is still a compile-time dependency here, and KnockbackFFA calls it directly.

This is a snapshot of production-oriented network code, not a self-contained plugin bundle. There are no compatibility guarantees, releases, support channels, or plans for further development.

## Included games

| Module | What it contains |
| --- | --- |
| `Bedwars` | Team-based Bedwars and RushBW, map and gold voting, team selection, resource spawners, item shops, beds, spectators, stats, and CloudNet service-state updates. Supported variants in the code are `2x1`, `4x2`, `8x1`, `8x2`, and `4x4`. |
| `Bridge` | Solo bridging practice with timed runs, personal and global best times, four course types, map skins, sound/song perks, block animations, and spectating. Arenas are created from FAWE/WorldEdit schematics. |
| `Clutches` | NPC-driven clutch and knockback training with Reduce, Clutch, Multi Reduce, and Diagonal Clutch modes. It also contains private playground worlds and configurable hit presets backed by MongoDB through Holy Core. |
| `KnockbackFFA` | Multi-arena free-for-all combat with signs, respawning, teams, inventory sorting, armor colors, bow trails, kill-streak effects, stats, and replay hooks. |
| `MLGRush` | `2x1` and `4x1` MLG Rush matches with queues, map templates, bed scoring, trophies, spectators, configurable knockback behavior, and several block-reset modes. |
| `SGFFA` | Survival Games-style free-for-all with rotating maps, randomized chest loot, automatic respawning, trophies, kill streaks, teams, and staff vanish. |

The repository contains roughly 200 Java source files across the six modules. Game state, player state, map handling, listeners, commands, inventories, and scheduled tasks live inside each module rather than in a shared game API.

## Technology and runtime assumptions

The code targets the original TeamHoly environment:

- Bukkit/Spigot and CraftBukkit `1.8.8-R0.1-SNAPSHOT`, including direct `v1_8_R3` NMS access
- Gradle `8.8` with the Shadow plugin and a multi-project build
- Holy Core `2.9.0` (`holy-core-api`, `bukkit-core-api`, `bungee-core-api`, and `bukkit-markupapi`)
- Holy Replay `1.0.4`
- CloudNet `3.4.0-RELEASE`
- SlimeWorldManager `2.2.1`
- HolographicDisplays `2.4.9`
- NoteBlockAPI `1.6.2`
- EffectLib `9.4`
- FAWE and WorldEdit JARs bundled under `Bridge/lib`
- MongoDB- and Redis-backed services exposed through Holy Core

Additional compile-only dependencies are declared in the root build, including BungeeCord APIs, en2do, Gson, ProtocolLib, Netty, MongoDB, and Redisson. Most external libraries are not shaded into the output, so the resulting plugin JARs are not standalone.

No Java toolchain or bytecode target is configured. Some sources use records, pattern matching for `instanceof`, `Stream.toList()`, and `var`; use JDK 17 or newer to compile the current snapshot. Running a Minecraft 1.8.8 server on a modern JVM may require additional server patches and JVM flags that are not part of this repository.

## Building

List the modules:

```bash
./gradlew projects
```

Build everything:

```bash
./gradlew build
```

Build one shaded plugin, for example Bedwars:

```bash
./gradlew :Bedwars:shadowJar
```

On Windows, use `gradlew.bat` instead of `./gradlew`. Generated JARs are written below each module's `build/libs` directory.

### Why a clean build currently fails

A clean checkout does not compile without restoring private or missing build inputs:

1. Holy Core artifacts are read from GitHub Packages. Gradle expects either `gpr.user` and `gpr.key` properties or the `GITHUB_ACTOR` and `GITHUB_TOKEN` environment variables. The token needs package-read access.
2. `de.teamholy.replay:holy-replay:1.0.4` is not available from any repository declared in this build. It must be supplied locally, published to a reachable Maven repository, or removed/replaced in the build and KnockbackFFA integration.
3. The Replay dependency is declared for every subproject even though only KnockbackFFA imports its API. Its absence therefore blocks compilation of all six modules.

Example `~/.gradle/gradle.properties` entries for GitHub Packages:

```properties
gpr.user=YOUR_GITHUB_USERNAME
gpr.key=YOUR_GITHUB_TOKEN
```

The last verification of this archive reached Gradle project configuration successfully, but `compileJava` stopped during dependency resolution because GitHub Packages credentials were missing and Holy Replay `1.0.4` could not be found.

## Running the plugins

There is no complete server package or deployment configuration in this repository. Recreating the environment requires more than copying the generated JARs into a Spigot server:

- Install compatible versions of Holy Core and the external plugins used by the selected game.
- Restore the CloudNet service groups, shared locations, database connections, permissions, and network messaging expected by Holy Core.
- Create each game's maps, regions, spawns, signs, NPC locations, and death heights with its setup commands or matching YAML data.
- Supply the SlimeWorldManager MongoDB worlds required by Bedwars and Clutches.
- Supply Bridge schematics under `plugins/Bridge/schematics`. The source tree includes the FAWE/WorldEdit libraries and two NoteBlock songs, but not the schematic files.
- Decide how to handle Holy Replay before enabling KnockbackFFA. The referenced replay implementation is currently non-functional.

The `depend` entries in the individual `plugin.yml` files are incomplete compared with the imports and services used by the source. Treat the source and root `build.gradle` as the more complete dependency inventory.

## Commands

The following commands are registered in the module descriptors. Many administrative paths also check hard-coded permission nodes in their executors.

| Module | Commands |
| --- | --- |
| Bedwars | `/npcshop`, `/forcemap`, `/start`, `/setup`, `/resetinv` |
| Bridge | `/bridge`, `/spectate` (`/spec`) |
| Clutches | `/playgroundpreset`, `/playworld`, `/quit`, `/spawn`, `/vanish` (`/v`) |
| KnockbackFFA | `/setup`, `/quit`, `/teaming` (`/tm`, `/teams`), `/vanish` (`/v`) |
| MLGRush | `/setup`, `/quit`, `/spawn`, `/spectate` (`/spec`, `/watch`) |
| SGFFA | `/setup`, `/teaming`, `/vanish` (`/v`) |

Examples of permissions referenced in code include `system.setup`, `command.setup`, `teamholy.setup`, `teamholy.team`, `teamholy.start`, `teamholy.forcemap`, `teamholy.presentedpreset`, and `bridge.setup`. They are not consistently declared in `plugin.yml`.

## Configuration and data

Runtime configuration is written below each plugin's data folder:

| Module | Main local data |
| --- | --- |
| Bedwars | `plugins/Bedwars/Config.yml`, `plugins/Bedwars/locations.yml`, and `plugins/Bedwars/songs/` |
| Bridge | `plugins/Bridge/schematics/`; player settings and stats use Holy Core services |
| Clutches | `plugins/Clutches/playground.yml` plus SlimeWorldManager worlds named `PLAYGROUND-<map>` |
| KnockbackFFA | `plugins/KnockbackFFA/locations.yml` |
| MLGRush | `plugins/MLGRush/ChefConfig.yml` |
| SGFFA | `plugins/SGFFA/locations.yml` |

Several shared locations such as `lobby`, `queue`, `spectate`, and `topHolo` are loaded from Holy Core rather than from this repository.

## Repository layout

```text
holy-games/
├── Bedwars/          # Bedwars and RushBW
├── Bridge/           # Bridging practice and timed courses
├── Clutches/         # Clutch training and playground worlds
├── KnockbackFFA/     # Knockback free-for-all
├── MLGRush/          # MLG Rush matches
├── SGFFA/            # Survival Games free-for-all
├── gradle/wrapper/   # Gradle 8.8 wrapper
├── build.gradle      # Shared dependencies and publishing setup
└── settings.gradle   # Multi-project module list
```

Each module follows the conventional `src/main/java` and `src/main/resources` layout and produces its own Bukkit plugin.

## Known limitations

- The project has no automated tests or test fixtures.
- The build depends on archived, authentication-gated, or unavailable artifacts.
- Holy Replay is non-functional and prevents an unmodified clean build.
- Runtime worlds, schematics, CloudNet configuration, databases, and most server data are not included.
- The code mixes legacy Minecraft internals with modern Java language features.
- Several listeners use reflection-based package scanning and suppress initialization exceptions, which can hide missing integrations at startup.
- Command metadata and dependency declarations are incomplete in multiple `plugin.yml` files.
- This snapshot has not been validated as a working production deployment.

## Contributions and support

This repository is read-only. Issues, pull requests, feature requests, compatibility work, and operational support are not accepted. Fork the project if you want to study or restore it, but expect substantial work to replace the archived infrastructure.

## License

There is currently no root `LICENSE` file, and several source files still contain older copyright or proprietary notices. Publishing source code alone does not grant permission to use, modify, or redistribute it. A license and any conflicting file headers must be resolved by the copyright holders before this repository can be treated as open source in the licensing sense.
