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

Four independent Forge targets share the established legacy gameplay baseline:

| Minecraft | Forge | Recipe viewer (optional) | Mixin setup |
|---|---|---|---|
| 1.6.4 | 9.11.1.1345 | NEI 1.6.1.9 and its native dependencies | Included; stock Forge ASM is retained |
| 1.7.10 | 10.13.4.1614 | NEI and its native dependencies | Install UniMixins 0.3.1+; retain its leading `+` filename |
| 1.8.9 | 11.15.1.2318 | Native 1.8.9 NEI / NotEnoughItems Eight and its dependencies | Included |
| 1.12.2 | 14.23.5.2859 | JEI | Install MixinBooter 11.18+ |

Use Java 8 to run every target, on clients and dedicated servers. Install exactly the JAR matching your Minecraft version; the four JARs have no dependency on one another. These are beta development ports. The [feature and acceptance matrix](ECOSYSTEM-PORT-AUDIT.md#o-four-target-beta-baseline-and-registration-contract) distinguishes implemented systems, unavailable native content, and completed validation. They do not claim unrestricted 1.16.5 parity.

All four include Cutting Boards, Cooking Pots, placed and handheld Skillets, Stoves, four crops and wild vegetation, rich soils and compost, shared meals, pantries, collection baskets, ropes, Safety Nets, tatami and editable canvas signs. Native chest loot, farmer trades, village compost heaps and progression accompany the recipe catalogs. The 1.6.4 port uses its four available wood species; later targets use six. Beetroot remains specific to 1.12.2. Recipes requiring absent mobs or ingredients are deferred.

Modern Regen is optional on 1.6.4, 1.7.10 and 1.8.9. FD keeps its regeneration and hunger ownership intact; it also operates without Modern Regen. The Modern Regen project has no 1.12.2 target. Startup compatibility does not certify the complete player health/hunger/effects matrix.

Every target uses `farmersdelight.cfg`. `effects.enableComfort` and `effects.enableNourishment` default to true. On 1.6.4, 1.7.10 and 1.8.9, `effects.comfortId=30`, `effects.nourishmentId=31` and `enchantments.backstabbingId=80` must be unused slots and agree between client and server. Startup rejects occupied IDs. Comfort supplies slow healing outside eligible natural regeneration; Nourishment reduces activity exhaustion by up to 0.1 per tick while preserving regeneration charges.

The 1.6.4 target additionally reserves named numeric block, item and achievement IDs. Configure collisions before creating a world, keep the configuration with the save, and use the same IDs on clients and server. Do not change existing-world IDs without a save migration. The frozen [registry manifest](versions/1.6.4/src/main/resources/farmersdelight/registry164.json) and [metadata contract](ECOSYSTEM-PORT-AUDIT.md#registration-and-save-contract) document their defaults.

### Farming and cooking

Find wild vegetables in suitable new surface chunks, including unexplored chunks in existing worlds. Plant cabbage and tomato seeds, onions, or rice in shallow source water over suitable soil. Mature tomatoes can be picked without uprooting the plant. Organic Compost converts into Rich Soil; till Rich Soil for farmland that retains its soil and resists trampling.

Right-click a Cutting Board to place one ingredient, use the appropriate tool to process it, or use an empty hand to retrieve it. Cooking Pots need heat below them, accept six ingredient positions, and hold cooked portions separately from their serving containers. Supply bowls or other required containers to collect meals. A harvested pot carries its unserved meal; other inventory contents drop separately. Sneak-use an empty hand to toggle its handle.

Skillets cook over heat when placed or held. Sneak-use to place one. Handheld cooking uses food in the other hand on 1.12.2 and the next hotbar slot on 1.6.4, 1.7.10 and 1.8.9. Stoves cook six single ingredients and provide reusable heat; light with flint and steel or a fire charge, and extinguish with water or a shovel.

JEI and NEI show available crafting, cutting, pot and grilling recipes. Native wood and flower equivalents are used where appropriate. Recipes needing absent content are deferred, including the Stove recipe's campfire ingredient and log stripping's stripped-log outputs. Bark-based recipes are deferred with that acquisition path; rotten flesh remains an alternative for Organic Compost. Honey, sweet berries, Nether vegetation and netherite are absent on all four targets. The 1.6.4 and 1.7.10 targets also lack rabbits and mutton; 1.6.4 lacks later fish variants and two later wood species. Beetroot is absent before 1.12.2.

### Configuration and compatibility

With NEI, view a Cooking Pot, Cutting Board, Skillet or Stove's usages in NEI to browse its processing recipes. Hover ingredients, tools and results for container returns, heat requirements, cooking times and chance details.

Wild crop generation and patch rarity are configurable per crop under `wildCrops`; there is no automatic retrogen. `world` controls added chest loot, farmer trades and new village compost heaps. Existing compost-heap structures remain loadable when new generation is disabled. `farming.growthBoost` controls Rich Soil's growth assistance.

`cuttingBoard` controls dispenser tools and the Fortune chance bonus. `recipes.enableVanillaCropCrates` controls vanilla-crop compression. `overrides` controls native soup stacking, its item allow/deny list, soup Comfort effects and rabbit-stew Jump Boost. `client` controls food tooltips and the Comfort/Nourishment overlays. `canvasSigns.darkBackgrounds` chooses backgrounds that initially use white text. Before 1.12.2, `enchantments.backstabbingId=80` must also be an unused enchantment slot.

Pantries use native chest controls. Baskets collect nearby dropped items from their facing side and stop while powered. Rope extends downward when used on an existing rope. Canvas signs use the native sign editor and accept dyes for text color. Modern bell attachments and general waterlogging are deferred; rice has its own native water-preserving cultivation.

### Building the four ports

The build follows Examplemod-Multiversion's isolated native wrappers. Install JDK 21 for the root and Unimined builds (1.6.4, 1.8.9 and 1.12.2), JDK 25 for the 1.7.10 build, JDK 17 for its Jabel compiler and the 1.6.4 compiler, and JDK 8 for development game runs. The 1.6.4 distribution uses Java 7 bytecode for stock Forge's ASM 4.1 scanner; the other distributions use Java 8 bytecode. Set `JAVA8_HOME`, `JAVA17_HOME`, `JAVA21_HOME` and `JAVA25_HOME` when automatic JDK discovery cannot locate them. Build JVMs are separate from the Java 8 game runtime.

```powershell
.\gradlew.bat clean testAll buildAll
# Or one isolated native target:
.\versions\1.6.4\gradlew.bat build
.\versions\1.8.9\gradlew.bat build
.\versions\1.12.2\gradlew.bat build
.\versions\1.7.10\gradlew.bat build
```

The root coordinator builds and checks all four artifacts; a single-target build certifies only that target. On Linux/macOS use `./gradlew buildAll` or a target’s `./gradlew build`. Root `buildAll`, `build` and `release` stage exactly one reobfuscated JAR per target in `build/release/`, named `farmersdelight-<version>+mc<minecraft>.jar`. The release directory is cleared before building. A native target's `build` or `release` stages its own JAR in `versions/<minecraft>/build/release/`. Development JARs stay in the target's `build/devlibs/`, and source/other auxiliary archives in `build/auxiliary/`; neither is included in release staging. `version.properties` owns the release version used by filenames, mod metadata and compiled version constants; builds do not publish or advance release counters.

`testAll` runs each native test task; there are currently no automated test sources, so NO-SOURCE is not a gameplay test pass. Use a target's `runClient` or `runServer` task for development launches.

`common/` contains small Java-7-compatible semantic sources and food/recipe data, without Minecraft/Forge imports. `versions/` owns native registration, items, hooks, rendering and integrations. The original `src/` tree remains the 1.16.5 behavioral and asset reference; its old build configuration is in `reference/`. It is not compiled into the legacy artifacts. The [ecosystem audit](ECOSYSTEM-PORT-AUDIT.md) records the gameplay contracts, content gates and acceptance requirements. Compilation and packaging do not establish in-game parity.

### Credits and license

Original Farmer's Delight code, assets and design are by vectorwing and contributors, under the [MIT license](LICENSE). The preserved 1.16.5 source remains the attribution and behavior reference. The 1.6.4 and 1.8.9 distributions include SpongePowered Mixin 0.7.11 with its license. The 1.6.4 distribution also includes the Java-7-compatible Apache Log4j 2.12.4 runtime required by Mixin, with its Apache license and notice. Forge, Modern Regen and recipe viewers retain their own licenses and are not bundled as mods.

### Contributing

Thank you for visiting the repository! If you'd like to contribute with the mod, feel free to check the wiki for more details, or take a look at the issues page!

I am open to constructive feedback about the mod's code: if you spot any glaring mistakes in my code, and/or you know a better way to accomplish something, feel free to open an issue/PR about it. Any help is appreciated!
