# Gaze Snatchers

Mod Minecraft **dual-loader** (Fabric + NeoForge) pour **Minecraft 1.21.11** (NeoForge **21.11.x**, compatible avec la ligne **26.3.x**).

Un mob inquiétant qui apparaît lorsque vous vous couchez la nuit, vous fixe, et peut voler un item si vous ne prêtez pas attention. En le tuant, il se divise en 2 à 4 bébés agressifs.

## Structure du projet

```text
common/     Logique partagée (entités, IA, items, config, assets)
fabric/     Entrée Fabric + hooks sommeil + client
neoforge/   Entrée NeoForge + hooks sommeil + client
docs/       Spécifications art/audio
```

## Build

```bash
./gradlew build
```

JARs :
- `fabric/build/libs/gazesnatchers-fabric-1.0.0.jar`
- `neoforge/build/libs/gazesnatchers-neoforge-1.0.0.jar`

Client de dev :
```bash
./gradlew :fabric:runClient
./gradlew :neoforge:runClient
```

## Configuration

Fichier `config/gazesnatchers.json` (généré au premier lancement) :

| Clé | Défaut | Description |
|-----|--------|-------------|
| `spawnChance` | 0.72 | Chance d'apparition au coucher |
| `ticksBeforeSteal` | 160 | Ticks sans attention avant vol |
| `minBabiesOnSplit` / `maxBabiesOnSplit` | 2 / 4 | Division à la mort |
| `amuletStealReduction` | 0.55 | Ralentit le vol si amulette en inventaire |
| `despawnAfterSteal` | true | Disparition après vol réussi |

## Gameplay

- **Adulte** : passif le jour ; la nuit, spawn devant le joueur endormi, fixe, vole un item, brûle au soleil.
- **Bébés** : toujours hostiles, rapides, drop 1–3 Éclats de Regard.
- **Amulette de Veille** : craft 4 éclats + amethyst shard (réduit le vol).

Test rapide : œufs d'apparition dans l'onglet créatif, ou `/summon gazesnatchers:gaze_snatcher`.

## Multi-joueur

Toute la logique (spawn, vol, division) est **serveur-side** ; compatible multiplayer dédié.

## Crédits & licence

MIT — voir `LICENSE`. Textures/sons placeholders : remplacez avant publication Modrinth (voir `docs/ART_AND_AUDIO.md`).
