# Gaze Snatchers — art, modèles Blockbench & audio

## Modèles Blockbench

Source : `blockbench/gaze_snatcher.bbmodel` et `blockbench/baby_gaze_snatcher.bbmodel` (format Modded Entity, box-UV). `tools/build_art.py` réécrit les textures, les classes Java et les rendus à partir de cette hiérarchie.

Les os exportés sont `Head`, `Body`, `LeftArm`, `RightArm`, `LeftLeg`, `RightLeg` (adulte, plus `Jaw` et `Claw`) et `Head`, `Body`, `Left_Foreleg`, `Right_Foreleg`, `Left_Hindleg`, `Right_Hindleg` (bébé). `setupAnim` reprend le balancement `idle` du fichier et un cycle de marche sur ces os : Minecraft ne charge pas les keyframes Blockbench tout seul.

## Proportions

### Adulte (`gaze_snatcher`)
- **Format** : Java Entity / Minecraft 1.21+
- **Hiérarchie** : `Root` → `Head`, `Body`, `LeftArm`, `RightArm`, `LeftLeg`, `RightLeg`
- **Proportions** : tête 12×12×12, corps 6×20×4, bras 3×26×3, jambes 3×22×3
- **Texture** : `assets/gazesnatchers/textures/entity/gaze_snatcher.png` (128×128)
- **Yeux émissifs** : `gaze_snatcher_emissive.png`, branchés par `GazeSnatcherEyesLayer`

### Bébé (`baby_gaze_snatcher`)
- **Hiérarchie** : `Root` → `Head`, `Body`, `Left_Foreleg`, `Right_Foreleg`, `Left_Hindleg`, `Right_Hindleg`
- **Texture** : `textures/entity/baby_gaze_snatcher.png` (64×64)

## Textures

`gaze_snatcher.png` (128×128) et `baby_gaze_snatcher.png` (64×64) suivent le dépliage box-UV des `.bbmodel` : gris-noir veiné, yeux blancs. Les PNG émissifs ne gardent que les yeux.

## Audio

Les `.ogg` de `assets/gazesnatchers/sounds/` portent les durées et le caractère ci-dessous (générés par `tools/build_art.py`, noms inchangés).

## Spécifications audio

| ID | Description cible | Durée | Notes |
|----|-------------------|-------|-------|
| `gaze_snatcher_ambient` | Souffle lent + murmures + craquements | 3–6 s loop | Audible à 8 blocs, spatialisé |
| `gaze_snatcher_spawn` | Pop humide + inspiration | ~1 s | One-shot |
| `gaze_snatcher_steal` | Rire étouffé / aspiration | ~0.6 s | One-shot |
| `gaze_snatcher_death` | Cri long → éclatement en petits cris | ~1.5 s | Enchaîner avec spawn bébés |
| `baby_*` | Pleurs/gémissements aigus déformés | loops courts | Pitch 1.2–1.5 |

## Rendu yeux (in-game)

- Couche `EyesLayer` + `RenderTypes.eyes()` sur les renderers adulte/bébé
- Textures : `gaze_snatcher_emissive.png`, `baby_gaze_snatcher_emissive.png` (blanc sur transparent)

## Items volés

- Nom custom : `item.gazesnatchers.stolen_item_label`
- Un bébé aléatoire hérite du loot à la mort de l'adulte ; drop rare (`rareStolenItemDropChance` dans la config)
