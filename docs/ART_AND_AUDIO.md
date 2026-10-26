# Gaze Snatchers — art, Blockbench models & audio

## Blockbench models (reference)

### Adult (`gaze_snatcher`)
- **Format**: Java Entity / Minecraft 1.21+
- **Hierarchy**: `Root` → `Head`, `Body`, `LeftArm`, `RightArm`, `LeftLeg`, `RightLeg`
- **Proportions**: head 12×12×12, body 6×20×4, arms 3×26×3, legs 3×22×3
- **Texture**: `assets/gazesnatchers/textures/entity/gaze_snatcher.png` (128×128)
- **Emissive eyes**: `gaze_snatcher_emissive.png` (placeholder) — wire up as emissive / resource pack for bloom

### Baby (`baby_gaze_snatcher`)
- **Hierarchy**: `Root` → `Head`, `Body`, `Left_Foreleg`, `Right_Foreleg`, `Left_Hindleg`, `Right_Hindleg`
- **Texture**: `textures/entity/baby_gaze_snatcher.png` (64×64)

`GazeSnatcherModel` and `BabyGazeSnatcherModel` mirror this hierarchy for in-game rendering. To swap in a Blockbench `.java` export, replace the `LayerDefinition`s with the exported file and keep the same `ModelLayerLocation`.

## Placeholder textures

Current PNGs are procedural placeholders. Replace them with final art (veined grey-black, white emissive eyes).

## Audio specs (to replace)

| ID | Target description | Duration | Notes |
|----|--------------------|----------|-------|
| `gaze_snatcher_ambient` | Slow breath + murmurs + creaks | 3–6 s loop | Audible at 8 blocks, spatial |
| `gaze_snatcher_spawn` | Wet pop + inhale | ~1 s | One-shot |
| `gaze_snatcher_steal` | Muffled laugh / suction | ~0.6 s | One-shot |
| `gaze_snatcher_death` | Long cry → burst into small cries | ~1.5 s | Chain with baby spawn |
| `baby_*` | Distorted high whimpers | short loops | Pitch 1.2–1.5 |

The `.ogg` files under `assets/gazesnatchers/sounds/` are **placeholders** (light noise/sine mix): replace them without renaming.

## Eye rendering (in-game)

- `EyesLayer` + `RenderTypes.eyes()` on adult/baby renderers
- Textures: `gaze_snatcher_emissive.png`, `baby_gaze_snatcher_emissive.png` (white on transparent)

## Stolen items

- Custom name: `item.gazesnatchers.stolen_item_label`
- A random baby inherits loot on adult death; rare drop (`rareStolenItemDropChance` in config)
