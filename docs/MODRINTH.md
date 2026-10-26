# Modrinth publication (English)

Copy-paste fields for [modrinth.com/project/gaze-snatcher](https://modrinth.com/project/gaze-snatcher).

## Project settings

| Field | Value |
|-------|--------|
| **Name** | Gaze Snatchers |
| **URL slug** | `gaze-snatcher` |
| **Visibility** | Public |
| **License** | MIT |
| **Source** | https://github.com/Jason54jg/Gaze-Snatchers |
| **Issues** | https://github.com/Jason54jg/Gaze-Snatchers/issues |
| **Wiki** | https://jason54jg.github.io/Gaze-Snatchers/ |
| **Discord** | _(optional)_ |

### Categories / tags

- `mobs`
- `adventure`
- `magic`
- `fabric`
- `neoforge`

### Environments

- Client: **required**
- Server: **required** (all gameplay is server-side)

---

## Short description (≤160 characters)

```text
A creepy mob that appears when you sleep, stares you down, and may steal an item if you ignore it.
```

---

## Full description (body)

Paste into the Modrinth **Description** editor (Markdown):

```markdown
# Gaze Snatchers

A creepy mob that appears when you sleep at night, stares you down… and may steal an inventory item if you look away too long.

**Fabric** + **NeoForge** · Minecraft **1.21.11** and **26.1 / 26.1.1 / 26.1.2 / 26.2 / 26.3**

## Features

- **Sleep spawn** — Lying down at night can summon a Gaze Snatcher that stands still and locks onto you.
- **Silent theft** — Ignore its gaze long enough and it steals one item, then may despawn.
- **Death split** — Killing the adult bursts it into 2–4 hostile, fast babies.
- **Gaze Ward Amulet** — Crafted from Gaze Shards; slows the steal if kept in your inventory.
- **Gaze Binding Charm** — Rare craft; right-click an adult Gaze Snatcher to tame it (follows & defends you).
- **Sunlight** — Adults and babies burn in daylight.
- **GeckoLib models** — Animated Blockbench / GeckoLib entity models.

## Dependencies

| Loader | Required mods |
|--------|----------------|
| Fabric | [Fabric API](https://modrinth.com/mod/fabric-api), [GeckoLib](https://modrinth.com/mod/geckolib) |
| NeoForge | [GeckoLib](https://modrinth.com/mod/geckolib) |

Install the **GeckoLib** build that matches your Minecraft version.

## Install

1. Install Fabric or NeoForge for your Minecraft version.
2. Install **GeckoLib** (and Fabric API on Fabric).
3. Drop this mod’s JAR into `mods/`.
4. Config file: `config/gazesnatchers.json` (created on first launch).

## Gameplay tips

- Look at the Snatcher (or interact) to reset its steal progress.
- Use `/gazesnatchers spawn adult` (OP) or creative spawn eggs to test.
- Full wiki: [jason54jg.github.io/Gaze-Snatchers](https://jason54jg.github.io/Gaze-Snatchers/)

## Multiplayer

Spawn, steal, and split logic run on the **server**. Safe for dedicated servers when the mod is installed on both sides.

## Links

- [Wiki](https://jason54jg.github.io/Gaze-Snatchers/)
- [Source / issues](https://github.com/Jason54jg/Gaze-Snatchers)
```

---

## Version changelog — `1.0.0`

Use this for the first release changelog on every game version you upload:

```markdown
### Initial release

- Gaze Snatcher adult: sleep spawn, stare / steal loop, sunlight burn, death split into babies
- Baby Gaze Snatchers: always hostile, Gaze Shard drops, optional stolen-item carrier
- Items: Gaze Shard, Gaze Ward Amulet, Gaze Draught, spawn eggs
- GeckoLib entity models & animations (adult + baby)
- Config: `config/gazesnatchers.json`
- Commands: `/gazesnatchers spawn`, `trigger_sleep`, `reload_config`
- Loaders: Fabric + NeoForge
- Minecraft: 1.21.11, 26.1, 26.1.1, 26.1.2, 26.2, 26.3
```

---

## Per-version upload checklist

For each Minecraft version under `release/<mc>/`:

1. Create a Modrinth version named e.g. `1.0.0` (or `1.0.0+1.21.11`).
2. Set **game versions** to that Minecraft version only.
3. Set **loaders**: `fabric` and/or `neoforge` to match the JAR(s).
4. Upload:
   - `gazesnatchers-fabric-1.0.0-<mc>+fabric.jar`
   - `gazesnatchers-neoforge-1.0.0-<mc>+neoforge.jar` (skip on 26.1 / 26.1.1)
5. Add **required** dependencies:
   - Fabric builds → Fabric API + GeckoLib
   - NeoForge builds → GeckoLib
6. Paste the changelog above.
7. Release channel: **Release**

### GeckoLib versions (reference)

| Minecraft | Suggested GeckoLib |
|-----------|--------------------|
| 1.21.11 | 5.4.5 |
| 26.1 / 26.1.1 | 5.5 |
| 26.1.2 | 5.5.2 |
| 26.2 | 5.5.5 |
| 26.3 | 5.5.7 |

---

## Featured gallery (suggested)

Upload from the wiki / Blockbench refs:

1. Adult front (hero)
2. Adult side
3. Baby model
4. In-game sleep encounter (screenshot when available)
5. Items / creative tab (screenshot when available)
