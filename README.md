# Gaze Snatchers

Mod Minecraft **dual-loader** (Fabric + NeoForge) pour **Minecraft 1.21.11** et **26.1 / 26.1.1 / 26.1.2 / 26.2 / 26.3**.

Un mob inquiétant qui apparaît lorsque vous vous couchez la nuit, vous fixe, et peut voler un item si vous ne prêtez pas attention. En le tuant, il se divise en 2 à 4 bébés agressifs.

## Structure du projet

```text
common/     Logique partagée (java + javaMojmap26 pour MC 26.x)
fabric/     Entrée Fabric + hooks sommeil + client
neoforge/   Entrée NeoForge + hooks sommeil + client
gradle/versions/  Profils par version Minecraft
docs/       Spécifications art/audio + MINECRAFT-26.md
```

## Build

Profil courant (défaut **1.21.11**, JDK 21) :

```bash
./gradlew build
```

Toutes les versions (1.21.11 + matrice 26.x avec JDK 25) :

```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-25.0.3"   # requis pour 26.x
$env:BUILD_26 = "true"
.\scripts\build-all-versions.ps1
```

JARs sous `release/<minecraft_version>/`. Détails 26.x : [docs/MINECRAFT-26.md](docs/MINECRAFT-26.md).

Client de dev (profil courant) :
```bash
./gradlew :fabric:runClient
./gradlew :neoforge:runClient
```

## Commandes (OP / niveau 2)

| Commande | Description |
|----------|-------------|
| `/gazesnatchers spawn adult` | Spawn un Voleur de Regard devant vous |
| `/gazesnatchers spawn baby` | Spawn un bébé |
| `/gazesnatchers trigger_sleep` | Simule l'apparition au coucher (sans lit) |
| `/gazesnatchers reload_config` | Recharge `config/gazesnatchers.json` |

## Configuration

Fichier `config/gazesnatchers.json` (généré au premier lancement) :

| Clé | Défaut | Description |
|-----|--------|-------------|
| `spawnChance` | 0.72 | Chance d'apparition au coucher |
| `ticksBeforeSteal` | 160 | Ticks sans attention avant vol |
| `minBabiesOnSplit` / `maxBabiesOnSplit` | 2 / 4 | Division à la mort |
| `amuletStealReduction` | 0.55 | Ralentit le vol si amulette en inventaire |
| `despawnAfterSteal` | true | Disparition après vol réussi |
| `alwaysDropStolenFromCarrier` | false | Le bébé porteur droppe toujours l'item volé |

## Gameplay

- **Adulte** : passif le jour ; la nuit, spawn devant le joueur endormi, fixe, vole un item, brûle au soleil.
- **Bébés** : toujours hostiles, rapides, drop 1–3 Éclats de Regard.
- **Amulette de Veille** : craft 4 éclats + amethyst shard (réduit le vol).

Test rapide : œufs d'apparition dans l'onglet créatif, ou `/summon gazesnatchers:gaze_snatcher`.

## Multi-joueur

Toute la logique (spawn, vol, division) est **serveur-side** ; compatible multiplayer dédié.

## Wiki

Site joueur (GitHub Pages, déployé depuis `wiki/` via Actions) :
https://jason54jg.github.io/Gaze-Snatchers/

## Crédits & licence

MIT — voir `LICENSE`. Textures/sons placeholders : remplacez avant publication Modrinth (voir `docs/ART_AND_AUDIO.md`).
