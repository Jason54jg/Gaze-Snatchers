# Gaze Snatchers

Dual-loader Minecraft mod (**Fabric** + **NeoForge**) for **Minecraft 1.21.11** and **26.1 / 26.1.1 / 26.1.2 / 26.2 / 26.3**.

A creepy mob that appears when you sleep at night, stares you down, and may steal an item if you ignore it. Kill it and it splits into 2–4 aggressive babies.

## Project layout

```text
common/     Shared logic (java + javaMojmap26 for MC 26.x)
fabric/     Fabric entry + sleep hooks + client
neoforge/   NeoForge entry + sleep hooks + client
gradle/versions/  Per-Minecraft-version profiles
docs/       Art/audio specs + MINECRAFT-26.md
wiki/       Player wiki (GitHub Pages)
```

## Build

Current profile (default **1.21.11**, JDK 21):

```bash
./gradlew build
```

All versions (1.21.11 + 26.x matrix with JDK 25):

```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-25.0.3"   # required for 26.x
$env:BUILD_26 = "true"
.\scripts\build-all-versions.ps1
```

JARs land under `release/<minecraft_version>/`. 26.x details: [docs/MINECRAFT-26.md](docs/MINECRAFT-26.md).

Dev client (current profile):

```bash
./gradlew :fabric:runClient
./gradlew :neoforge:runClient
```

## Commands (OP / level 2)

| Command | Description |
|---------|-------------|
| `/gazesnatchers spawn adult` | Spawn a Gaze Snatcher in front of you |
| `/gazesnatchers spawn baby` | Spawn a baby |
| `/gazesnatchers trigger_sleep` | Simulate sleep spawn (no bed) |
| `/gazesnatchers reload_config` | Reload `config/gazesnatchers.json` |

## Configuration

File `config/gazesnatchers.json` (created on first launch):

| Key | Default | Description |
|-----|---------|-------------|
| `spawnChance` | 0.72 | Chance to spawn when sleeping |
| `ticksBeforeSteal` | 160 | Ticks without attention before steal |
| `minBabiesOnSplit` / `maxBabiesOnSplit` | 2 / 4 | Babies on adult death |
| `amuletStealReduction` | 0.55 | Slows steal if amulet is in inventory |
| `despawnAfterSteal` | true | Despawn after a successful steal |
| `alwaysDropStolenFromCarrier` | false | Carrier baby always drops the stolen item |

## Gameplay

- **Adult**: passive by day; at night, may spawn in front of a sleeping player, stares, steals an item, burns in sunlight.
- **Babies**: always hostile, fast, drop 1–3 Gaze Shards.
- **Gaze Ward Amulet**: craft 4 shards + amethyst shard (reduces steal speed).
- **Gaze Binding Charm**: rare craft (shards + ward amulet + echo shard + gold + netherite scrap). Right-click an adult to tame — it follows and defends you (empty hand click to sit/stand).

Quick test: spawn eggs in the creative tab, or `/summon gazesnatchers:gaze_snatcher`.

## Multiplayer

All logic (spawn, steal, split) is **server-side**; works on dedicated servers.

## Download

[Modrinth](https://modrinth.com/project/gaze-snatcher) (primary).

**Requires [GeckoLib](https://modrinth.com/mod/geckolib)** (and Fabric API on Fabric). Match GeckoLib to your Minecraft version.

Publication copy (English summary, full description, changelog, upload checklist): [docs/MODRINTH.md](docs/MODRINTH.md).

Source / issues: [GitHub](https://github.com/Jason54jg/Gaze-Snatchers).

## Wiki

Player site (GitHub Pages, deployed from `wiki/` via Actions):
https://jason54jg.github.io/Gaze-Snatchers/

## Credits & license

MIT — see `LICENSE`. Placeholder textures/sounds: replace before publishing (see `docs/ART_AND_AUDIO.md`).
