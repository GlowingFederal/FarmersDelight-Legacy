# Changelog

## 2026-10-17 08:19 — Paired legacy foundation
- Establish isolated Forge 1.12.2 and 1.7.10 targets using the ExampleMod toolchain pins, with a root paired build coordinator. Preserve the original 1.16.5 source/assets and former build configuration as the behavioral reference.
- Add the shared food catalog, four native knife tiers, basic materials and Straw Bale registration, exact ingredient/OreDictionary memberships, duplicate-rejecting recipe registration, initial crafting recipes, and optional JEI/NEI presentation. Gate unavailable honey, sweet-berry and Nether-vegetation foods.
- Implement native consumables with survival container returns, one-effect milk/cocoa curing and server-owned effects. Nourishment reduces activity exhaustion without refunding natural regeneration costs; Comfort supplies slow healing only outside eligible natural regeneration. Modern Regen remains optional and owns its hunger state and timers.
- Adopt paired foundation → cutting → cooking → farming/serving → secondary-system milestones, then 1.8.9 and 1.6.4; addon support follows equivalent feature-gated paired acceptance.

## 2026-10-18 11:13 — Correct foundation textures and Nourishment loading
- Use the existing straw_bale_end texture for the 1.7.10 pillar end icons and the 1.12.2 block model, alongside the original side texture.
- Require both Straw Bale textures during resource packaging so a missing source texture fails the build instead of producing a missing face in-game.
- Register the 1.7.10 Nourishment Mixin once through UniMixins' static configuration, removing the competing empty configuration and redundant early loader that prevented the exhaustion hook from applying.

## 2026-10-18 16:40 — Restore legacy cooking, farming and shared meals
- Add native Cutting Boards with tool processing, secondary drops, Fortune, automation, dispenser support and ingredient rendering on both versions.
- Add the Cooking Pot inventory, heat-based cooking, serving containers, portable meal storage, experience, GUI and sided automation. Add six-position Stoves and placed/handheld Skillets with native heat and lighting interactions.
- Adapt the original cutting, cooking, crafting and grilling recipe catalogs to native item names and metadata. Use available wood and flower equivalents; defer recipes whose required items or mobs are absent.
- Add plantable cabbage, tomato, onion and rice crops, configurable wild patches, rich soils, compost conversion and mushroom colonies. Rice retains source water through harvesting and handles bucket drainage separately.
- Add placed pies and four-serving feasts with persistent remaining portions, serving-container costs, leftovers and knife slicing. Add six native-wood pantries and directional, redstone-controlled collection baskets with persistent inventories.
- Add crates, rice bags and bales, paired tatami mats, canvas rugs, climbable extending ropes, bouncing Safety Nets and all seventeen canvas-sign backgrounds with native editing and dyed text.
- Add thrown rotten tomatoes, Backstabbing, skillet combat and enchantments, native pet feed and breeding support. Restore source fuel values, safe optional soup stacking, food tooltips, effect icons and configurable status overlays.
- Add additive dungeon/mineshaft loot, native farmer purchases, biome-adapted village compost heaps and native advancements or achievements. Disabling new compost heaps preserves existing structure loading.
- Add optional NEI/JEI processing categories and safe pot ingredient transfer. Synchronize server-controlled crate availability and cutting Fortune settings; keep unavailable recipes out of active viewer lists.
- Keep wild-crop placement inside the native shifted population window. Retain the existing Modern Regen ownership and optional loading contract.

## 2026-10-20 02:06 — Improve NEI recipe pages and workstation usages
- Add recessed ingredient/result slots, processing arrows and a compact Cooking Pot layout that distinguishes stored meals, serving containers and finished output.
- Show cooking times, batch experience, container returns, cutting chances, Fortune bonuses and tool durability information. Label shaped and shapeless crafting recipes and explain Skillet heat and Fire Aspect behavior.
- Register GTNH NEI category dimensions, icons and multiple recipes per page. Viewing Cooking Pot, Cutting Board, Skillet or Stove usages now lists the corresponding processing recipes, including carried or enchanted workstations.
- Keep Cooking Pot ingredient transfer aligned with the native slots and exclude its locked meal buffer.

## 2026-10-20 14:15 — Normalize legacy release artifacts
- Use the established 0.1.0 release version without the internal foundation milestone suffix, shared by archive names, mod metadata and compiled version constants.
- Centralize public filenames as farmersdelight-<version>+mc<minecraft>.jar, independent of project names and Forge/plugin archive suffixes.
- Stage only the verified native remap/reobfuscation outputs for root buildAll, build and release tasks in build/release, clearing stale staged artifacts before building. Native build/release tasks stage one target locally; keep development and auxiliary archives separate.
- Reject incomplete archives, incorrect release metadata, missing production Mixin mappings, development-named bytecode and classes incompatible with Java 8 during release staging. Preserve the existing native toolchains and development launch setup.

## 2026-10-20 19:20 — Add native 1.6.4 and 1.8.9 beta targets
- Extend the isolated multiversion build to four independent Forge distributions, including aggregate clean/test/build tasks and verified release staging. Preserve the established 1.7.10 and 1.12.2 targets and the 0.1.0 release naming.
- Port the maintained food, crops, wild vegetation, soils, cooking and cutting, stove/skillet, serving, storage, decoration, combat, acquisition and progression systems against each target's native APIs. Keep unavailable vanilla-content recipes deferred without adding replacement mobs or ingredients.
- Add frozen, configurable 1.6.4 numeric block/item/achievement allocations, native legacy inventories and packets, metadata rendering, sound registration and save handling. Adapt village palettes to available native wood and soil.
- Add 1.8.9 block-state/model variants, native crop age properties, inventory handling and farmer-career integration. Retain the existing Cooking Pot artwork, processing rules and server authority.
- Keep Modern Regen and NEI optional, preserve regeneration ownership and exact ingredient memberships, and provide native NEI processing categories and pot transfer adapters.
- Include pinned, licensed Mixin bootstraps in the new targets while preserving stock Forge libraries and the existing maintained-target Mixin setups. Use Java 7 bytecode for the 1.6.4 distribution and Java 8 for the other targets.
- Document the four-version feature baseline, installation/build requirements, registration contracts and unavailable content gates. Parse the shared recipe tree through the native Gson tree parser for compatibility with older runtimes.
- Correct Cooking Pot support model variants on both block-state targets and use existing meal textures for legacy serving-block particles and fallback icons.
- Register 1.6.4 numeric achievements before local client statistics load, then resolve recipe-dependent criteria after integrations; validate IDs in the native achievement range.
- Remap the existing optional 1.12.2 JEI runtime for development launches so its production method names resolve against the mapped game.
- Preserve successful player harvesting when rice restores its source water on all four targets, allowing the native harvest path to emit crop drops after the replacement callback.

## 2026-10-21 22:08 — Persist automatic 1.7.10 potion allocations
- Replace Comfort/Nourishment's fixed defaults with persistent named allocation shared with updated EFR-R. Reserve legacy explicit overrides during construction, allocate against the native registry, and retain reservations across restarts, disabled effects and modpack changes.
- Validate registration ownership, saved-world allocation snapshots and early Forge handshake mappings. Reject incompatible or missing saved allocations before gameplay and client/server mapping differences before player potion packets arrive.
- Preserve native effects, existing configured potion IDs, entity registration and other Minecraft targets. Document allocation-file distribution, legacy upgrade requirements, capacity limits and explicit first-start repair for the former EFR-R collision.
