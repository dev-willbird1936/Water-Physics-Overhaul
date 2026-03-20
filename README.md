# Water Physics Overhaul 1.20.1

Forge 1.20.1 port of Water Physics Overhaul.

## What It Is

Water Physics Overhaul replaces the normal lightweight vanilla water behavior with a heavier finite-water simulation layer.

At a high level, the mod adds:

- level-based surface water storage
- water equalization across nearby spaces
- downhill sliding behavior to reach lower terrain
- custom interaction reach for bucket pickup logic
- an advanced bucket item
- integration hooks used by the two add-on mods in this workspace

This repository is the main gameplay mod in the workspace. The other two WPO add-ons extend it rather than replacing it.

## Core Simulation Direction

The 1.20.1 port keeps the original WPO idea of treating water as a more physical finite system instead of letting it behave like ordinary vanilla source-spread water.

The current port includes systems and code paths for:

- chunk-level fluid data storage
- finite water amounts with a max fluid level of `8`
- equalization distance controls
- sliding distance controls
- custom fluid interaction helpers used by the add-ons
- block-state injection for representative waterloggable blocks

In practice, this means the mod tries to move and settle water based on nearby state and configured simulation limits instead of relying only on vanilla fluid updates.

## Configuration

The common config is stored at:

```text
config/wpo/common.toml
```

The in-game config screen exposes the main tuning values:

- `performancePreset`
- `setMaxEqualizeDistance`
- `setMaxSlidingDistance`
- `setMaxBucketDistance`

The performance preset controls how aggressive the simulation can be, while `CUSTOM` unlocks manual equalize and slide distances.

## Included Content

The base mod currently includes:

- the `Advanced Bucket`
- the water simulation itself
- entity, block-state, and packet hooks needed by the simulation

Most additional placeable utility blocks live in the add-on mods rather than this base repository.

## Add-ons

Two original add-on mods are available for the 1.20.1 port:

- `WPO Environmental Expansion` adds rain collection, puddles, evaporation, absorption, drought, seasonal effects, and biome-aware environmental behavior.
- `WPO Hydraulic Utilities` adds pumps, drains, nozzles, valves, grates, watertight doors, watertight trapdoors, and creative fluid sources.

For external fluid transport with the add-ons, Pipez is the recommended companion pipe mod.

## Dependencies And Layout

For local source builds, clone `SKDS-Core-1.20.1` next to this repository so the folder layout is:

```text
../SKDS-Core-1.20.1
../Water-Physics-Overhaul-1.20.1
```

This repository is intended to stay fork-linked to the original WPO repository while the 1.20.1 port lives here.

## Credits

- Original Water Physics Overhaul: `Sasai_Kudasai_BM`
- 1.18.2 work used in the porting path: `Felicis`
- 1.20.1 port and repository maintenance: [`dev-willbird1936`](https://github.com/dev-willbird1936)

## Related Repositories

- [`SKDS-Core-1.20.1`](https://github.com/dev-willbird1936/SKDS-Core-1.20.1)
- [`WPO-Environmental-Expansion-1.20.1`](https://github.com/dev-willbird1936/WPO-Environmental-Expansion-1.20.1)
- [`WPO-Hydraulic-Utilities-1.20.1`](https://github.com/dev-willbird1936/WPO-Hydraulic-Utilities-1.20.1)

## Build

Typical local build:

```powershell
.\gradlew.bat build
```
