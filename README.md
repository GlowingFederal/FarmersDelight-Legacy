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

Forge 1.12.2 (14.23.5.2859) and 1.7.10 (10.13.4.1614) are the co-primary ports. Each major system is implemented and accepted on both before the next begins. The current source provides the foundation: foods/effects, four knife tiers, ingredients and initial crafting recipes. Farming, workstations and most acquisition paths follow later. This is not a complete playable Farmer's Delight port. The later order is 1.8.9, then the deliberately reduced 1.6.4 target.

Use Java 8 to run either game. Install MixinBooter 11.18+ on 1.12.2 or UniMixins 0.3.1+ on 1.7.10; keep UniMixins' leading `+` in its filename. JEI (1.12.2) and NEI (1.7.10) are optional recipe viewers. Modern Regen is optional on 1.7.10: FD leaves its hunger/regeneration implementation and healing costs in control. FD also operates without Modern Regen. No 1.12.2 Modern Regen integration is assumed.

Both targets use `farmersdelight.cfg`. `effects.enableComfort` and `effects.enableNourishment` default to true. On 1.7.10, `effects.comfortId=30` and `effects.nourishmentId=31` must be unused potion slots and must match between clients and server; startup reports collisions rather than overwriting another effect. Comfort supplies slow healing when saturation and eligible natural regeneration are unavailable. Nourishment reduces activity exhaustion by up to 0.1 per tick while preserving regeneration charges.

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
