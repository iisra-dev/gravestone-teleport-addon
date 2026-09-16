# libs/

This folder is where you place the **official GraveStone mod jar** (by Max Henkel / henkelmax)
so this addon can compile against its public classes.

Download it yourself from one of the official sources — do not ask anyone else to redistribute it:

- Modrinth: https://modrinth.com/mod/gravestone-mod
- CurseForge: https://www.curseforge.com/minecraft/mc-mods/gravestone-mod
- Source: https://github.com/henkelmax/gravestone

Drop the `.jar` file (e.g. `gravestone-neoforge-1.0.40+26.2.jar`) directly into this folder.
`build.gradle` picks up any `*.jar` in here as a `compileOnly` dependency.

This jar is **never bundled, shaded, or committed** to this repository (see `.gitignore`) —
it is only used locally to compile against GraveStone's public API. At runtime, players need
to install the real GraveStone mod jar themselves alongside this addon.
