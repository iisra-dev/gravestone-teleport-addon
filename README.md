# GraveStone Teleport Addon

An independent NeoForge 1.21.1 **addon** for [GraveStone](https://github.com/henkelmax/gravestone) by
**Max Henkel (henkelmax)**. This is not a fork: it contains none of GraveStone's code or assets. It is a
separate mod that requires GraveStone to be installed and extends its obituary item purely through
NeoForge's public APIs (events and data components).

- Required dependency: [GraveStone](https://modrinth.com/mod/gravestone-mod) by henkelmax
- This repository's code: MIT licensed (see `LICENSE`) — GraveStone itself keeps its own license

## What it does

- **Charging recipe:** combine a GraveStone obituary with an Ender Pearl in a crafting table (shapeless,
  any arrangement) to charge it.
- **Glowing indicator:** a charged obituary gets the enchanted glint (via the vanilla
  `minecraft:enchantment_glint_override` data component — no rendering code needed), and its tooltip
  says whether it's charged and ready, or needs an Ender Pearl.
- **Right-click teleport:** right-clicking a charged obituary teleports you to the grave's stored
  coordinates and dimension, with the chorus fruit teleport sound and particles, then consumes the
  charge.
  - **Shift + right-click** is left untouched and still opens GraveStone's own death-info GUI / admin
    restore menu.
  - A plain right-click on an **uncharged** obituary also falls through to GraveStone's own GUI.

## How it works (non-intrusively)

This addon never edits, patches, or bundles any GraveStone class or asset:

- It registers its own data component, `gravestone_teleport_addon:charged`, instead of touching
  GraveStone's item data.
- It listens to NeoForge's `PlayerInteractEvent.RightClickItem` and `ItemTooltipEvent` — it only
  **cancels** the interaction event when a charged obituary is right-clicked without sneaking, so
  GraveStone's own `use()` method never runs for that click. Every other interaction reaches
  GraveStone completely unmodified.
- To find where a grave is, it reads GraveStone's own public `gravestone:death` data component
  (via `Main.DEATH_DATA_COMPONENT` and `DeathInfo`, both public classes GraveStone already exposes)
  and re-reads the matching death record from disk using
  [corelib](https://github.com/henkelmax/corelib)'s `DeathManager` — the same on-disk file GraveStone
  itself wrote. corelib is a separate, independently published utility library (also used internally
  by GraveStone); this addon embeds its own copy via NeoForge's jar-in-jar mechanism, so it needs
  nothing installed beyond GraveStone itself.

## Building From Source

You need the official GraveStone mod jar to compile against (see `libs/README.md`):

1. Download the GraveStone NeoForge 1.21.1 jar from
   [Modrinth](https://modrinth.com/mod/gravestone-mod) or
   [CurseForge](https://www.curseforge.com/minecraft/mc-mods/gravestone-mod).
2. Place it in `libs/` (e.g. `libs/gravestone-neoforge-1.21.1-1.0.40.jar`).
3. Run:

```
./gradlew build
```

The compiled jar will be in `build/libs/`. At runtime, install both this addon's jar and the
official GraveStone jar in your `mods/` folder.
