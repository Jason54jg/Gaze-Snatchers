# Fiche Modrinth — Gaze Snatchers

Texte prêt à coller sur Modrinth. Les images de `docs/modrinth/` sont des rendus du modèle Blockbench importé (texture et yeux émissifs inclus), pas des captures en jeu.

## Résumé

Un mob qui vous regarde dormir, et qui repart parfois avec quelque chose à vous.

## Description

Gaze Snatchers ajoute le **Voleur de Regard** à Minecraft **1.21.11** (Fabric et NeoForge).

La nuit, quand vous vous couchez, il peut apparaître devant vous. Il reste immobile, vous fixe, puis vole un objet si vous ne le regardez pas. Le tuer le fait éclater en plusieurs bébés agressifs. Le jour, l’adulte brûle au soleil.

### Contenu

- Adulte : silhouette très haute, bras trop longs, mâchoire et griffes. Les yeux sont émissifs.
- Bébés : toujours hostiles, rapides, lâchent 1 à 3 éclats de regard.
- Amulette de veille : 4 éclats + éclat d’améthyste, ralentit le vol.
- Infusion de clarté.
- Œufs d’apparition dans l’onglet créatif.
- Config `config/gazesnatchers.json` (chance d’apparition, délai avant le vol, nombre de bébés).

### Commandes (opérateur)

- `/gazesnatchers spawn adult`
- `/gazesnatchers spawn baby`
- `/gazesnatchers trigger_sleep`
- `/gazesnatchers reload_config`

La logique de spawn, de vol et de division est côté serveur.

## English

A mob that watches you sleep, and sometimes leaves with something of yours.

Gaze Snatchers adds the Gaze Snatcher to Minecraft 1.21.11 on Fabric and NeoForge. At night it can appear in front of a sleeping player, stare, and steal an item if you look away. Killing the adult splits it into hostile babies. It burns in sunlight. Craft a Gaze Ward Amulet from gaze shards to slow the theft.

## Installation

- Minecraft 1.21.11
- Fabric Loader 0.20+ et Fabric API, ou NeoForge 21.11.x
- Java 21

## Galerie

| Fichier | Légende |
| --- | --- |
| `docs/modrinth/gaze-snatcher.png` | Voleur de Regard, trois-quarts |
| `docs/modrinth/gaze-snatcher-eyes.png` | Visage et yeux émissifs |
| `docs/modrinth/baby-gaze-snatcher.png` | Bébé |
| `docs/modrinth/gaze-snatchers.png` | Adulte et bébé |

Icône du mod : `common/src/main/resources/assets/gazesnatchers/textures/icon.png`.

## Licence

MIT.
