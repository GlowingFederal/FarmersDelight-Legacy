# Recipe scripting on Forge 1.7.10 and 1.12.2

Install MineTweaker 3 on 1.7.10 or CraftTweaker 2 on 1.12.2. Neither is required for ordinary gameplay. Put `.zs` files in the instance's `scripts/` directory and install the same scripts on the server and clients. These integrations belong to Farmer's Delight Classic Edition; the alternative 1.12.2 Farmer's Delight implementation has its own scripting APIs.

Working examples are in [`examples/scripts/1.7.10`](examples/scripts/1.7.10) and [`examples/scripts/1.12.2`](examples/scripts/1.12.2). They are opt-in files, never automatically installed. The core example replaces rice, cod cutting and egg grilling recipes. The separate mead example requires Brewin' and Chewin' and enabled UAB berries/honey.

## Methods

Import the classes used by your script:

```zenscript
import mods.farmersdelight.CookingPot;
import mods.farmersdelight.CuttingBoard;
import mods.farmersdelight.Skillet;
import mods.brewinandchewin.Fermenting; // Only with Brewin' and Chewin' installed.
```

| Class | Addition |
| --- | --- |
| `CookingPot` | `addRecipe(String id, IItemStack output, IIngredient[] inputs, IItemStack container, int time, float experience)` |
| `CuttingBoard` | `addRecipe(String id, IIngredient input, IIngredient tool, IItemStack[] outputs, double[] chances, String sound)` |
| `Skillet` | `addRecipe(String id, IItemStack output, IIngredient input, int time)` |
| `Fermenting` | `addRecipe(String id, IItemStack output, IIngredient[] inputs, IIngredient liquid, IItemStack container, int time, int temperature, float experience)` |

Every class also provides `removeRecipe(String id)` and `remove(IIngredient output)`. Output removal examines every cutting result, including secondary results. IDs are namespaced, lowercase names, such as `pack:cooked_rice`. Existing recipe IDs are shown in the compatibility matrix and the original recipe catalogs. Duplicate IDs are rejected; remove a recipe before replacing its ID or overlapping inputs. Additions append in script order; an earlier matching recipe takes precedence.

```zenscript
CookingPot.removeRecipe("farmersdelight:cooking/cooked_rice");
CookingPot.addRecipe("pack:cooked_rice", <farmersdelight:cooked_rice>,
    [<ore:cropRice>], <minecraft:bowl>, 100, 0.1);
```

## Native processing rules

- Ingredients represent one item in one slot. Repeat an ingredient to consume items from additional slots. The pot has six ingredient slots and the keg has four plus its liquid-item slot. A keg may have no solid ingredients.
- Pot/keg output count is the number of servings. Supply one serving container per serving; `null` explicitly means containerless. Original input container items, such as milk/water buckets and honey bottles, return through the native processor.
- Metadata, output/container NBT, Ore Dictionary choices and ingredient predicates are retained. Use `.anyDamage()` for a cutting tool that should accept existing durability damage. Cutting still applies native tool wear, Fortune and the configured chance bonus. Each output needs a probability from `0.0` to `1.0`.
- In MineTweaker 3, `.withTag(...)` sets a stack's NBT but does not by itself constrain ingredient matching. Use `.onlyWithTag(...)` to require input NBT; combine both to show that tag on the ingredient's recipe-viewer example. CraftTweaker 2 also supports `.onlyWithTag(...)` and matches the NBT on tagged item ingredients.
- Times are ticks. Pot/grilling times accept `1..32767`; keg times accept positive integers, retaining its existing full-width progress synchronization. XP must be finite and nonnegative. Output counts must fit the item's native maximum stack size.
- Pot and grilling recipes retain their normal heat rules. `Skillet` edits the shared Skillet/Stove catalog: `time` is stove cooking time, and the held/heated skillet applies its existing acceleration and Fire Aspect rules.
- Keg temperature categories are `1` Frigid, `2` Cold, `3` Normal, `4` Warm and `5` Hot. `liquid` is an item ingredient, such as a water bucket or honey bottle; these kegs do not have fluid tanks.
- An empty cutting sound uses the existing native default. Other sound strings use the target's native sound names. The integration rejects ingredient transformers rather than silently discarding them; these playerless machines use their existing container-return rules.

## Addons, lifecycle and recipe viewers

Corn Delight, Ocean's Delight and End's Delight use these same pot/cutting/grilling managers. Their item namespaces work directly in the methods above. Brewin' and Chewin' additionally exposes its actual keg manager through `Fermenting`. Its cheese aging and feast servings remain native block behavior rather than configurable recipe processors. Ordinary crafting and furnace recipes use the scripting engine's standard `recipes` and `furnace` APIs. External Create, Immersive Engineering and Farmer's Respite serializers are not replaced with invented processing routes.

Edits made before a processor finishes initialization are queued and applied in order after its original catalog. Later edits operate directly on that catalog and rebuild cutting/pot indexes. Recipe changes invalidate loaded machine caches; stored meals keep their saved servings, containers and NBT. Script objects are not written into saves.

On 1.7.10, edits are MineTweaker undoable actions: `/mt reload` restores the recipes affected by each action before applying the script again, preserving recipes added later by other integrations. NEI reads the current processor catalogs. On 1.12.2, edits are CraftTweaker actions and run before JEI registers its startup recipes; restart the instance after changing recipe scripts, following CraftTweaker 2's normal recipe lifecycle. Both client and server need the same recipe edits. No scripting classes load when the optional engine is absent.
