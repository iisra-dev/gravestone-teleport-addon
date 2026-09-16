# GraveStone Teleport Addon

An independent NeoForge **addon** for [GraveStone](https://github.com/henkelmax/gravestone), the mod by
**Max Henkel (henkelmax)**. This is not a fork: it contains none of GraveStone's code or assets. It's a
separate mod that requires GraveStone to be installed and extends its obituary item purely through
NeoForge's public APIs (events and data components) — GraveStone itself is never modified, patched, or
redistributed.

- Required dependency: [GraveStone](https://modrinth.com/mod/gravestone-mod) by henkelmax
- This repository's code: MIT licensed (see `LICENSE`) — GraveStone itself keeps its own license

This `main` branch is a landing page only. **Buildable code lives on a per-Minecraft-version branch** —
pick yours from the table below.

## Supported versions

| Minecraft | Loader | Branch |
|---|---|---|
| 1.21.1 | NeoForge | [`1.21.1`](https://github.com/iisra-dev/gravestone-teleport-addon/tree/1.21.1) |
| 1.21.11 | NeoForge | [`1.21.11`](https://github.com/iisra-dev/gravestone-teleport-addon/tree/1.21.11) |

Each branch tracks the matching upstream GraveStone version and is built/verified independently — the
Mojang and NeoForge APIs this addon touches (recipe serialization, teleportation, resource lookups) have
changed across that range, so a branch built for one version won't compile against another.

## What it does

- **Charging recipe:** combine a GraveStone obituary with an Ender Pearl in a crafting table (shapeless —
  any arrangement of the two items works) to charge it.
- **Glowing indicator:** a charged obituary gets the enchanted glint effect, and its tooltip tells you
  whether it's charged and ready to teleport, or still needs an Ender Pearl.
- **Right-click teleport:** right-clicking a charged obituary teleports you to the grave's stored
  coordinates and dimension, with the chorus fruit teleport sound and particles, then consumes the
  charge (you'll need another Ender Pearl to recharge it).
  - **Shift + right-click** is left untouched and still opens GraveStone's own death-info GUI / admin
    restore menu, whether or not the obituary is charged.
  - A plain right-click on an **uncharged** obituary also falls through to GraveStone's own GUI.

## How it works (non-intrusively)

This addon never edits, patches, or bundles any GraveStone class or asset:

- It registers its own data component (`gravestone_teleport_addon:charged`) instead of touching
  GraveStone's item data.
- It listens to NeoForge's player-interaction and tooltip events, only **cancelling** the interaction
  when a charged obituary is right-clicked without sneaking — so GraveStone's own obituary GUI never
  opens for that one click. Every other interaction reaches GraveStone completely unmodified.
- To find where a grave is, it reads GraveStone's own public `gravestone:death` data component off the
  obituary (by its stable registered id, not GraveStone's internal class names) and independently
  re-reads the matching death record from disk via [corelib](https://github.com/henkelmax/corelib) — the
  same on-disk file GraveStone itself wrote. corelib is a separate, independently published utility
  library also used internally by GraveStone; this addon embeds its own copy via NeoForge's jar-in-jar
  mechanism, so nothing beyond GraveStone itself needs to be installed.

## Installing (players)

1. Install [GraveStone](https://modrinth.com/mod/gravestone-mod) for your Minecraft version.
2. Install this addon's jar for the **same** version (see the table above) alongside it.
3. In-game: craft an obituary + Ender Pearl together, then right-click the charged obituary to
   teleport back to your grave.

## Building from source

Check out the branch matching the Minecraft version you want (see the table above), then:

1. Download the official GraveStone mod jar for that same version from
   [Modrinth](https://modrinth.com/mod/gravestone-mod) or
   [CurseForge](https://www.curseforge.com/minecraft/mc-mods/gravestone-mod).
2. Place it in `libs/` (see `libs/README.md` on that branch for details).
3. Run:

```
./gradlew build
```

The compiled jar will be in `build/libs/`. At runtime, install both this addon's jar and the official
GraveStone jar in your `mods/` folder.

## Porting to a new version

Each version branch is a plain fork of the closest existing one with the minimum changes needed to
compile and run against that Minecraft/NeoForge/GraveStone release — see that branch's commit history
for the exact API differences handled.
