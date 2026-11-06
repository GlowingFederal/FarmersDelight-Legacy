# Universal Adaptive Backporter compatibility (Forge 1.7.10 and 1.12.2)

Install the matching Farmer's Delight Legacy release with any combination of its four addons. UAB is optional. Its technical mod ID and registry namespace remain `etfuturum`; the current integration supports UAB 1.1.2's native availability APIs. Forge 1.7.10 uses `ContentAvailability.available(Object)` plus effective metadata-family configuration; Forge 1.12.2 uses `Content.available(String)`. Older Et Futurum builds without the matching acquisition API leave the affected integration inactive with a startup diagnostic. UAB is ordered before FD, and recipes compile after all addons contribute their catalogs.

The original matrix below primarily describes the 1.7.10 gameplay adapters. The [recipe synchronization matrix](UAB-RECIPE-MATRIX.md) records both targets separately, including restored recipes and remaining prerequisites. [Recipe scripting](SCRIPTING.md) covers optional MineTweaker/CraftTweaker support for the core and all four addons.

## Compatibility matrix

| Verified UAB feature | Farmer's Delight | Brewin' and Chewin' | Corn Delight | Ocean's Delight | End's Delight |
| --- | --- | --- | --- | --- | --- |
| Sweet berries, honey | Original cookies, cheesecake, salads, ham and pumpkin recipes | Original mead and dependent drinks | Original cornbread stuffing and pumpkin recipes | Original honey inputs where all prerequisites exist | Not applicable |
| Glow berries, glow ink | Ingredient resolver | Original glittering grenadine | Not applicable | Not applicable | Not applicable |
| Kelp, dried kelp, sea pickles (UAB revisions supplying these registrations) | Original noodle soup | Original kimchi, kippers, pickled pickles, salty folly and dependent drinks | Not applicable | Original stuffed cod, honey-fried kelp and braised sea pickle | Not applicable |
| Mutton, rabbit, beetroot | Original cutting, pot, crafting and skillet routes; exact meat/vegetable tags | Original strongroot ale and meat inputs | Original taco mutton and beetroot vegetable inputs | Not applicable | Not applicable |
| Crimson/warped fungi, shroomlight | Original Nether salad, distinct fungus metadata | Original scarlet cheese, steel toe stout and red rum where prerequisite chains exist | Not applicable | Not applicable | Original roasted dragon steak where all prerequisites exist |
| Honeycomb, iron nuggets, turtle eggs, flowers | Resolve original ingredients | Original tankard, cheese containers, pale jane and dread nog where prerequisites exist | Not applicable | Not applicable | Not applicable |
| Stripped logs, bark wood and Nether wood | Original axe cutting, tree bark and plank recovery with exact species/variant metadata | Supplies original pale jane tree-bark ingredient; native reeds naming also enables the original saccharine rum chain | Not applicable | Not applicable | Not applicable |
| Cod, salmon, tropical fish, pufferfish mobs | Existing vanilla fish metadata and cutting/cooking; no duplicate fish items or drops | Existing fish tag acceptance | Not applicable | Existing pufferfish processing; cod stuffing requires enabled kelp | Not applicable |
| Guardian and elder guardian | Not applicable | Not applicable | Not applicable | Normal guardian ingredient; knife-qualified elder slab; original cutting and downstream recipes where all ingredients exist | Not applicable |
| Glow squid | Not applicable | UAB glow ink ingredient | Not applicable | One knife-qualified tentacle drop, through the existing squid handler | Not applicable |
| Rabbit, shulker, endermite | Original extra rabbit hide/shulker shell on knife kills | Not applicable | Not applicable | Not applicable | Original knife-qualified shulker/endermite meat drops, retaining 90% chance |
| Beetroot soup and rabbit stew | Original stack-selection rules, Comfort and rabbit Jump Boost settings | Not applicable | Not applicable | Not applicable | Not applicable |
| Cherry Grove, Meadow, Grove, Bamboo Jungle, Mangrove Swamp | Existing temperature and Forge biome tags already select suitable wild crops; source-water rice can also grow above enabled UAB mud | Not applicable | Existing temperate/grass checks already support suitable land biomes | No addon generation to adapt | Not applicable |
| Warm/cold/deep ocean variants | No new wild-crop placement rules | Not applicable | No new placement rules | UAB owns aquatic ecology and monument spawning; no extra generation pass | Not applicable |
| Chorus fruit/flower, popped chorus, purpur, End bricks, dragon breath, shells | Shared exact ingredient resolution | Not applicable | Not applicable | Not applicable | Original recipes and crate conversion where all prerequisites exist |
| Outer End islands | Not applicable | Not applicable | Not applicable | Not applicable | Original succulent patches on exposed End stone beyond 1024 blocks, only with UAB's enabled End provider |
| End Cities, monuments, bastions, outposts, Ancient Cities, Trial Chambers | Existing dungeon/mineshaft loot remains unchanged | No applicable chest loot | No applicable chest loot | Guardian acquisition works with UAB monument mobs | End City shulkers use the same meat handler; no chest loot injection |

These are ingredient, recipe, mob-drop, biome/generation and gameplay integrations. There is no new chest-loot integration: the audited addon sources do not define suitable structure chest pools. Previously omitted original FD foods, storage and tool identities are restored additively. Existing saved identities remain unchanged; no dimensions or mob entities are added. UAB's End provider uses the native End biome; there are no separate modern End biome identities to match.

## Configuration

`farmersdelight.cfg`, category `compatibility.uab`, controls the whole ecosystem's recipes. Both recipe switches apply on both targets; the extra mob/world-generation switches below belong to the 1.7.10 adapters:

| Setting | Default | Effect |
| --- | --- | --- |
| `enableUABCompatibility` | `true` | Master switch for the optional integration |
| `enableUABRecipes` | `true` | Use enabled UAB content in original recipe catalogs |
| `enableUABMobDrops` | `true` | Allow original core/addon drops on enabled UAB species |
| `enableUABWorldgenCompatibility` | `true` | Rice on enabled UAB mud; succulent generation on enabled outer End islands |

Restart after changes. Existing addon drop/generation switches, generation rarity, core soup settings and all UAB effective feature/dependency/master switches continue to apply. Recipe, mob and generation switches are independent; disabling recipes does not disable core soup effects or knife drops. No loot setting is added because there is no new chest-loot integration.

## Implementation and boundaries

The source audit uses UAB's `ModItems`, `ModBlocks`, `ContentAvailability`, `UabConfiguration`, `BlockNetherFungus`, entity implementations, surface/ocean biome managers and End provider, alongside the five mods' original catalogs, loot modifiers and native processors. UAB items/blocks use lowercase enum registry names, which can differ from modern names: e.g. `mutton_raw`, `mutton_cooked`, `glow_berries_item` and `chorus_fruit_popped`. `nether_fungus` metadata 0 and 1 are crimson and warped, independently gated by their effective settings.

Farmer's Delight snapshots enabled references during initialization, before recipe compilation. Registry lookups use Forge APIs. A small startup-only reflective boundary reads UAB's effective availability API because disabled identities remain registered and no linked UAB dependency is bundled. Runtime events, soup checks and generation use cached references, without reflection or registry scans. Modern recipe IDs are resolved to exact UAB stacks; disabled UAB Ore Dictionary entries are filtered, metadata is preserved, NBT-bearing fish buckets are never treated as fish, and no broad Ore Dictionary substitutions are added. Original catalogs are compiled once rather than cloned into parallel recipes. Missing or disabled inputs/outputs are logged for each recipe family at startup; failures in optional mob/configuration checks do not disable otherwise supported ingredient families. Native sugar cane maps to reeds independently of UAB.

Missing inputs, outputs, liquids or serving containers defer the original recipe. Kelp, dried kelp and sea pickles use their verified registrations in UAB revisions that supply them; older UAB builds keep these paths deferred. Current UAB supplies campfires, bamboo, scaffolding and cornflowers, restoring the original FD/End stove, basket, canvas scaffolding and flower-cutting routes. Native wooden-door/sign names and Nether slab metadata are mapped explicitly on 1.7.10. The formerly omitted original honey/berry foods, Nether salad, beetroot storage/wild plant and Nether pantries now retain their own stable FD identities; acquisition stays gated by real enabled ingredients. The original netherite knife upgrade registers in UAB's actual smithing manager, preserving base NBT/damage and UAB's upgrade-template policy. Ocean's Delight's old unconditional stuffed-cod deferral is removed on 1.12.2. Missing third-party tea ingredients remain unavailable. No substitute ingredients are invented. Disabling one fungus or stripped-wood variant cannot enable it through another variant's shared block registry.

Existing registries, entity identities, inventories and tile serialization are preserved. No retrogen is performed. Wild crop and corn generation retain their existing rules; UAB already supplies the appropriate Forge biome tags and climates. Succulents use one bounded native population pass on already loaded terrain, require exposed End stone and leave existing structures/blocks intact. UAB continues to own terrain, biomes, structures and mob spawning.

The shared 1.7.10 potion allocator uses a UniMixins accessor to expand the native potion array to at least 128 slots during construction, before effects are allocated. This applies with or without UAB, including Brewin' and Chewin' installations. Existing entries and larger third-party arrays are retained. Automatic allocations remain within native signed-byte IDs 1..127, preserving packets and effect NBT. Capacity remains available with ingredient compatibility switched off or UAB removed, so saved effects through ID 127 stay readable without a separate ID extender.
