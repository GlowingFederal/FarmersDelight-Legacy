# Farmer's Delight Legacy

<a href="https://www.curseforge.com/minecraft/mc-mods/farmers-delight">
  <img src="http://cf.way2muchnoise.eu/full_farmers-delight_downloads.svg" alt="Curseforge Downloads">
</a>
<a href="https://discord.gg/eFsz5SK">
  <img alt="Discord" src="https://img.shields.io/discord/734511833947439156?color=brightgreen&label=Discord">
</a>
<br>
<img src="https://i.imgur.com/aDELzhM.png" width="50%">

### Overview

Farmer's Delight is a mod that gently expands upon farming and cooking in Minecraft.

Using a collection of rustic tools and workstations, you can improve upon the foundation of your farm plots, prepare an assortment of healthy meals and snacks for you and your pets, and even equip yourself a bit better for adventuring across the land.

This mod focuses on giving more purpose to items often forgotten or overlooked by players (such as milk, mushrooms, beetroots etc). Hopefully this will encourage you to farm a little bit of everything!

### Legacy development targets

Forge 1.12.2 (14.23.5.2859) and 1.7.10 (10.13.4.1614) are the co-primary ports. Both include native Cutting Boards, Cooking Pots, placed and handheld Skillets, Stoves, farming and wild crops, rich soils and compost, shared meals, pantries, collection baskets, ropes, Safety Nets, tatami and editable canvas signs. Native loot, farmer trades, village compost heaps and progression accompany the recipe catalogs. These remain development ports; the [ecosystem audit](ECOSYSTEM-PORT-AUDIT.md#n-paired-complete-port-implementation-checklist) distinguishes implementation from gameplay acceptance. The later order is 1.8.9, then the deliberately reduced 1.6.4 target.

Use Java 8 to run either game. Install MixinBooter 11.18+ on 1.12.2 or UniMixins 0.3.1+ on 1.7.10; keep UniMixins' leading `+` in its filename. JEI (1.12.2) and NEI (1.7.10) are optional recipe viewers. Modern Regen is optional on 1.7.10: FD leaves its hunger/regeneration implementation and healing costs in control. FD also operates without Modern Regen. No 1.12.2 Modern Regen integration is assumed.

Both targets use `farmersdelight.cfg`. `effects.enableComfort` and `effects.enableNourishment` default to true. On 1.7.10, `effects.comfortId=30` and `effects.nourishmentId=31` must be unused potion slots and must match between clients and server; startup reports collisions rather than overwriting another effect. Comfort supplies slow healing when saturation and eligible natural regeneration are unavailable. Nourishment reduces activity exhaustion by up to 0.1 per tick while preserving regeneration charges.

### Farming and cooking

Find wild vegetables in suitable new surface chunks, including unexplored chunks in existing worlds. Plant cabbage and tomato seeds, onions, or rice in shallow source water over suitable soil. Mature tomatoes can be picked without uprooting the plant. Organic Compost converts into Rich Soil; till Rich Soil for farmland that retains its soil and resists trampling.

Right-click a Cutting Board to place one ingredient, use the appropriate tool to process it, or use an empty hand to retrieve it. Cooking Pots need heat below them, accept six ingredient positions, and hold cooked portions separately from their serving containers. Supply bowls or other required containers to collect meals. A harvested pot carries its unserved meal; other inventory contents drop separately. Sneak-use an empty hand to toggle its handle.

Skillets cook over heat when placed or held. Sneak-use to place one. Handheld cooking uses food in the other hand on 1.12.2 and the next hotbar slot on 1.7.10. Stoves cook six single ingredients and provide reusable heat; light with flint and steel or a fire charge, and extinguish with water or a shovel.

JEI and NEI show available crafting, cutting, pot and grilling recipes. Native wood and flower equivalents are used where appropriate. Recipes needing absent content are deferred for future backport compatibility, including the Stove recipe's campfire ingredient and log stripping's stripped-log outputs. Bark-based recipes are deferred with that acquisition path; rotten flesh remains an alternative for Organic Compost. Honey, sweet berries, Nether vegetation and netherite are absent on both targets; 1.7.10 also lacks beetroot, rabbits and mutton.

### Configuration and compatibility

Wild crop generation and patch rarity are configurable per crop under `wildCrops`; there is no automatic retrogen. `world` controls added chest loot, farmer trades and new village compost heaps. Existing compost-heap structures remain loadable when new generation is disabled. `farming.growthBoost` controls Rich Soil's growth assistance.

`cuttingBoard` controls dispenser tools and the Fortune chance bonus. `recipes.enableVanillaCropCrates` controls vanilla-crop compression. `overrides` controls native soup stacking, its item allow/deny list, soup Comfort effects and rabbit-stew Jump Boost. `client` controls food tooltips and the Comfort/Nourishment overlays. `canvasSigns.darkBackgrounds` chooses backgrounds that initially use white text. On 1.7.10, `enchantments.backstabbingId=80` must also be an unused enchantment slot.

Pantries use native chest controls. Baskets collect nearby dropped items from their facing side and stop while powered. Rope extends downward when used on an existing rope. Canvas signs use the native sign editor and accept dyes for text color. Modern bell attachments and general waterlogging are deferred; rice has its own native water-preserving cultivation.

### Building the paired ports

The build follows Examplemod-Multiversion's isolated native wrappers. Install JDK 21 for the root/1.12.2 builds, JDK 25 for the 1.7.10 build, JDK 17 for its Jabel compiler, and JDK 8 for development game runs. Set `JAVA8_HOME`, `JAVA17_HOME`, `JAVA21_HOME` and `JAVA25_HOME` when automatic JDK discovery cannot locate them. Build JVMs are separate from the Java 8 game runtime.

```powershell
.\gradlew.bat buildAll
# Or one isolated native target:
.\versions\1.12.2\gradlew.bat build
.\versions\1.7.10\gradlew.bat build
```

The root coordinator builds and checks both artifacts; a single-target build certifies only that target. On Linux/macOS use `./gradlew buildAll` or a target’s `./gradlew build`. Production JARs are in `versions/<minecraft>/build/libs/`, named `farmersdelight-<version>+mc<minecraft>-forge.jar`. Dev/source archives are not distributables. `version.properties` owns the development version; builds do not publish or advance release counters.

`common/` contains small Java-7-compatible semantic sources and food/recipe data, without Minecraft/Forge imports. `versions/` owns native registration, items, hooks, rendering and integrations. The original `src/` tree remains the 1.16.5 behavioral and asset reference; its old build configuration is in `reference/`. It is not compiled into the legacy artifacts. The [ecosystem audit](ECOSYSTEM-PORT-AUDIT.md) records the gameplay contracts, content gates and acceptance requirements. Compilation and packaging do not establish in-game parity.

### Contributing

Thank you for visiting the repository! If you'd like to contribute with the mod, feel free to check the wiki for more details, or take a look at the issues page!

I am open to constructive feedback about the mod's code: if you spot any glaring mistakes in my code, and/or you know a better way to accomplish something, feel free to open an issue/PR about it. Any help is appreciated!
