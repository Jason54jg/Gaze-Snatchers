# Gaze Snatchers — art, modèles Blockbench & audio

## Modèles Blockbench (référence fournie)

### Adulte (`gaze_snatcher`)
- **Format** : Java Entity / Minecraft 1.21+
- **Hiérarchie** : `Root` → `Head`, `Body`, `LeftArm`, `RightArm`, `LeftLeg`, `RightLeg`
- **Proportions** : tête 12×12×12, corps 6×20×4, bras 3×26×3, jambes 3×22×3
- **Texture** : `assets/gazesnatchers/textures/entity/gaze_snatcher.png` (128×128)
- **Yeux émissifs** : texture `gaze_snatcher_emissive.png` (placeholder fourni) — brancher en rendu émissif / resource pack pour le bloom

### Bébé (`baby_gaze_snatcher`)
- **Hiérarchie** : `Root` → `Head`, `Body`, `Left_Foreleg`, `Right_Foreleg`, `Left_Hindleg`, `Right_Hindleg`
- **Texture** : `textures/entity/baby_gaze_snatcher.png` (64×64)

Les classes `GazeSnatcherModel` et `BabyGazeSnatcherModel` reproduisent cette hiérarchie pour le rendu in-game. Pour remplacer par un export Blockbench `.java`, remplacez les `LayerDefinition` par le fichier exporté et gardez le même `ModelLayerLocation`.

## Textures placeholder

Les PNG actuels sont des placeholders procéduraux. Remplacez-les par vos textures finales (gris-noir veiné, yeux blancs émissifs).

## Spécifications audio (à remplacer)

| ID | Description cible | Durée | Notes |
|----|-------------------|-------|-------|
| `gaze_snatcher_ambient` | Souffle lent + murmures + craquements | 3–6 s loop | Audible à 8 blocs, spatialisé |
| `gaze_snatcher_spawn` | Pop humide + inspiration | ~1 s | One-shot |
| `gaze_snatcher_steal` | Rire étouffé / aspiration | ~0.6 s | One-shot |
| `gaze_snatcher_death` | Cri long → éclatement en petits cris | ~1.5 s | Enchaîner avec spawn bébés |
| `baby_*` | Pleurs/gémissements aigus déformés | loops courts | Pitch 1.2–1.5 |

Les fichiers `.ogg` dans `assets/gazesnatchers/sounds/` sont des **placeholders** (mix bruit/sine léger) : remplacez-les sans changer les noms.

## Rendu yeux (in-game)

- Couche `EyesLayer` + `RenderTypes.eyes()` sur les renderers adulte/bébé
- Textures : `gaze_snatcher_emissive.png`, `baby_gaze_snatcher_emissive.png` (blanc sur transparent)

## Items volés

- Nom custom : `item.gazesnatchers.stolen_item_label`
- Un bébé aléatoire hérite du loot à la mort de l'adulte ; drop rare (`rareStolenItemDropChance` dans la config)
