# UAB recipe compatibility matrix

Forge 1.7.10 and 1.12.2; original catalogs from Farmer's Delight Classic Edition and all four addons, synchronized against UAB 1.1.2. The UAB feature matrix and native registration/availability implementations are the reference. [Configuration and implementation](UAB-COMPATIBILITY.md) · [Scripting syntax and examples](SCRIPTING.md).

The statuses below describe a fresh installation with all five mods, default feature settings and UAB enabled, without custom recipe scripts or unrelated content mods. They come from production dedicated-server catalogs. A supported recipe becomes deferred if a required feature, update master, ingredient, output, liquid-item or serving container is disabled or unavailable. Eight native vanilla grilling entries are listed separately from the original mod catalogs.

## Coverage by repository

| Catalog | 1.7.10 supported / deferred | 1.12.2 supported / deferred |
| --- | --- | --- |
| farmersdelight | 256 / 37 | 251 / 42 |
| brewinandchewin | 30 / 1 | 30 / 1 |
| corn_delight | 28 / 11 | 28 / 11 |
| ends_delight | 58 / 3 | 58 / 3 |
| oceansdelight | 26 / 0 | 26 / 0 |
| minecraft | 8 / 0 | 8 / 0 |

## Restored original routes

| Recipe family | 1.7.10 | 1.12.2 | Required real content / correction |
| --- | --- | --- | --- |
| Berry cookies, cheesecake and slices; honey cookies and glazed ham; Nether salad | Restored | Restored | Register the omitted original FD outputs; retain original food values, containers and servings. Require enabled berries/honey or both fungi as appropriate. Reassembly cannot bypass disabled ingredient families. |
| Beetroot crate and wild-beetroot cutting | Restored | Existing native target | Native 1.7.10 FD identities and UAB beetroot/seeds; new wild patches require enabled seed acquisition. |
| Crimson and warped pantries | Restored | Restored | Original pantry identities/assets, real planks and exact Nether slab variants. Each fungus/wood family is gated independently. |
| FD stove, End stove, bamboo basket, canvas scaffolding, cornflower cutting | Restored | Restored | UAB now supplies campfires, bamboo, scaffolding and cornflowers. No substitute furnace, stick or flower is added. |
| Native wood door/sign recovery | Restored exact UAB names | Doors supported; five wood signs unavailable | 1.7.10 uses door_SPECIES and item_sign_SPECIES. Species-specific trapdoors use their actual UAB identity instead of becoming oak. |
| Generic plank ingredients | Expanded | Expanded | Exact enabled UAB wood variants join native plank ingredients alongside plankWood. Wildcard declarations use known concrete aliases; disabled identities remain excluded. Includes crimson, warped, mangrove, cherry and bamboo. |
| Netherite knife upgrade | Restored in UAB smithing | Restored in UAB smithing | Real netherite and smithing table, native template policy, base NBT/durability and original tool tier/fire resistance. No crafting substitute. |
| Kelp, dried-kelp and sea-pickle recipes | Existing 1.7.10 routes retained | Restored | Per-feature UAB availability; native noodle soup, fermentation and Ocean food chains. |
| Ocean stuffed cod and its cooking variants | Existing route retained | Restored | Remove the unconditional 1.12.2 deferral; resolve enabled kelp and the real original ingredient chain. |
| Corn stuffing, pumpkin and meat recipes | Existing routes retained | UAB ingredients restored | Shared core resolvers, enabled original ingredients and native 1.12.2 meats/beetroot where supplied by vanilla. |
| Brewin' and Chewin' mead, berry/glow drinks, kimchi, pickles and dependent drinks | Existing routes retained | UAB ingredients restored | Real bottles/liquid items, serving tankards, glow ingredients and prerequisite foods; do not ignore missing liquid/container inputs. Native sugar cane resolves independently of UAB. |
| End foods, knives, crate and stove chains | Existing native routes plus campfire correction | UAB Nether/modern inputs restored | Original chorus/breath/shells and native End drops; preserve the native 1.12.2 provider and effective 1.7.10 UAB acquisition. |

## Remaining limitations

- UAB supplies no leather horse armor on these targets, so its cutting recipe remains unavailable.
- Native ink sac and black dye share one legacy item/metadata identity; registering the modern conversion would duplicate that ingredient, so it remains disabled.
- UAB 1.12.2 does not register the five vanilla wood-specific signs used by the original sign-cutting recipes. They are not substituted with oak signs.
- Farmer's Respite tea ingredients/processor are absent: Kombucha and the three external brewing variants stay unavailable. End's native cooking-pot tea routes are listed separately and remain supported where their real inputs exist.
- Create, Botany Pots, Silent Gear and Immersive Engineering integrations need their actual target-compatible content/serializers; installing UAB does not implement those processors. The per-recipe rows retain specific missing inputs where the native compiler identifies them.
- The original processors consume item ingredients. Kegs use liquid-bearing items, not fluid tanks. UAB's native water/lava handling does not create missing milk, honey or tea fluids for external serializers. No fluid substitutes or extra dependencies are introduced.

## Complete recipe catalog

“Supported” names the actual processor. “Unavailable” records its missing prerequisite or unsupported external serializer. Vanilla furnace processing intentionally covers the existing baking/smoking/blasting adaptations. UAB smithing is its separate native manager, including its configured template requirement.

| Recipe ID | Forge 1.7.10 | Forge 1.12.2 |
| --- | --- | --- |
| `brewinandchewin:cutting/flaxen_cheese_wheel` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `brewinandchewin:cutting/pizza` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `brewinandchewin:cutting/scarlet_cheese_wheel` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `brewinandchewin:fermenting/beer` | Supported (fermenting) | Supported (fermenting) |
| `brewinandchewin:fermenting/bloody_mary` | Supported (fermenting) | Supported (fermenting) |
| `brewinandchewin:fermenting/cocoa_fudge` | Supported (fermenting) | Supported (fermenting) |
| `brewinandchewin:fermenting/dread_nog` | Supported (fermenting) | Supported (fermenting) |
| `brewinandchewin:fermenting/egg_grog` | Supported (fermenting) | Supported (fermenting) |
| `brewinandchewin:fermenting/glittering_grenadine` | Supported (fermenting) | Supported (fermenting) |
| `brewinandchewin:fermenting/jerky` | Supported (fermenting) | Supported (fermenting) |
| `brewinandchewin:fermenting/kimchi` | Supported (fermenting) | Supported (fermenting) |
| `brewinandchewin:fermenting/kippers` | Supported (fermenting) | Supported (fermenting) |
| `brewinandchewin:fermenting/mead` | Supported (fermenting) | Supported (fermenting) |
| `brewinandchewin:fermenting/pale_jane` | Supported (fermenting) | Supported (fermenting) |
| `brewinandchewin:fermenting/pickled_pickles` | Supported (fermenting) | Supported (fermenting) |
| `brewinandchewin:fermenting/red_rum` | Supported (fermenting) | Supported (fermenting) |
| `brewinandchewin:fermenting/rice_wine` | Supported (fermenting) | Supported (fermenting) |
| `brewinandchewin:fermenting/saccharine_rum` | Supported (fermenting) | Supported (fermenting) |
| `brewinandchewin:fermenting/salty_folly` | Supported (fermenting) | Supported (fermenting) |
| `brewinandchewin:fermenting/steel_toe_stout` | Supported (fermenting) | Supported (fermenting) |
| `brewinandchewin:fermenting/strongroot_ale` | Supported (fermenting) | Supported (fermenting) |
| `brewinandchewin:fermenting/unripe_flaxen_cheese_wheel` | Supported (fermenting) | Supported (fermenting) |
| `brewinandchewin:fermenting/unripe_scarlet_cheese_wheel` | Supported (fermenting) | Supported (fermenting) |
| `brewinandchewin:fermenting/vodka` | Supported (fermenting) | Supported (fermenting) |
| `brewinandchewin:fermenting/withering_dross` | Supported (fermenting) | Supported (fermenting) |
| `brewinandchewin:ham_and_cheese_sandwich` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `brewinandchewin:item_mat` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `brewinandchewin:keg` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `brewinandchewin:kombucha` | Unavailable: Unavailable original prerequisites: [{"item":"farmersrespite:black_tea_leaves"}, liquid={"item":"farmersrespite:green_tea"}] (provider absent, effective feature disabled, or dependent addon acquisition unavailable) | Unavailable: Unavailable original prerequisites: [{"item":"farmersrespite:black_tea_leaves"}, liquid={"item":"farmersrespite:green_tea"}] (provider absent, effective feature disabled, or dependent addon acquisition unavailable) |
| `brewinandchewin:pizza` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `brewinandchewin:tankard` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `corn_delight:cooking/boiled_corn` | Supported (CookingRecipes) | Supported (CookingRecipes) |
| `corn_delight:cooking/corn_soup` | Supported (CookingRecipes) | Supported (CookingRecipes) |
| `corn_delight:cooking/cornbread_stuffing` | Supported (CookingRecipes) | Supported (CookingRecipes) |
| `corn_delight:cooking/creamed_corn` | Supported (CookingRecipes) | Supported (CookingRecipes) |
| `corn_delight:cooking/creamy_corn_drink` | Supported (CookingRecipes) | Supported (CookingRecipes) |
| `corn_delight:cooking/stuffed_pumpkin_from_cornbread` | Supported (CookingRecipes) | Supported (CookingRecipes) |
| `corn_delight:corn_crate` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `corn_delight:corn_from_crate` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `corn_delight:corn_kernel_bag` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `corn_delight:corn_seeds` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `corn_delight:corn_seeds_from_bag` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `corn_delight:cornbread` | Supported (native furnace) | Supported (native furnace) |
| `corn_delight:cornbread_batter` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `corn_delight:cornbread_from_campfire_cooking` | Supported (GrillingRecipes) | Supported (GrillingRecipes) |
| `corn_delight:cornbread_from_smoking` | Supported (native furnace) | Supported (native furnace) |
| `corn_delight:cutting/corn` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `corn_delight:cutting/wild_corn` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `corn_delight:grilled_corn` | Supported (native furnace) | Supported (native furnace) |
| `corn_delight:grilled_corn_from_campfire_cooking` | Supported (GrillingRecipes) | Supported (GrillingRecipes) |
| `corn_delight:grilled_corn_from_smoking` | Supported (native furnace) | Supported (native furnace) |
| `corn_delight:integration/create/milling/corn` | Unavailable: external processor not installed/implemented on this target | Unavailable: external processor not installed/implemented on this target |
| `corn_delight:integration/create/milling/wild_corn` | Unavailable: external processor not installed/implemented on this target | Unavailable: external processor not installed/implemented on this target |
| `corn_delight:integration/create/mixing/cornbread_batter_from_mixing` | Unavailable: external processor not installed/implemented on this target | Unavailable: external processor not installed/implemented on this target |
| `corn_delight:integration/create/mixing/raw_tortilla_from_mixing` | Unavailable: external processor not installed/implemented on this target | Unavailable: external processor not installed/implemented on this target |
| `corn_delight:integration/create/mixing/raw_tortilla_wheat_from_mixing` | Unavailable: external processor not installed/implemented on this target | Unavailable: external processor not installed/implemented on this target |
| `corn_delight:integration/immersiveengineering/cloche/corn` | Unavailable: external processor not installed/implemented on this target | Unavailable: external processor not installed/implemented on this target |
| `corn_delight:integration/immersiveengineering/crusher/corn` | Unavailable: external processor not installed/implemented on this target | Unavailable: external processor not installed/implemented on this target |
| `corn_delight:integration/immersiveengineering/crusher/wild_corn` | Unavailable: external processor not installed/implemented on this target | Unavailable: external processor not installed/implemented on this target |
| `corn_delight:integration/immersiveengineering/fermenter/corn` | Unavailable: external processor not installed/implemented on this target | Unavailable: external processor not installed/implemented on this target |
| `corn_delight:integration/immersiveengineering/fermenter/corn_seeds` | Unavailable: external processor not installed/implemented on this target | Unavailable: external processor not installed/implemented on this target |
| `corn_delight:integration/immersiveengineering/squeezer/corn_seeds` | Unavailable: external processor not installed/implemented on this target | Unavailable: external processor not installed/implemented on this target |
| `corn_delight:popcorn` | Supported (native furnace) | Supported (native furnace) |
| `corn_delight:popcorn_from_campfire_cooking` | Supported (GrillingRecipes) | Supported (GrillingRecipes) |
| `corn_delight:popcorn_from_smoking` | Supported (native furnace) | Supported (native furnace) |
| `corn_delight:taco` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `corn_delight:tortilla` | Supported (native furnace) | Supported (native furnace) |
| `corn_delight:tortilla_from_campfire_cooking` | Supported (GrillingRecipes) | Supported (GrillingRecipes) |
| `corn_delight:tortilla_from_smoking` | Supported (native furnace) | Supported (native furnace) |
| `corn_delight:tortilla_raw` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `ends_delight:brewing/bubble_tea` | Unavailable: No native dependency/processor for upstream recipe type farmersrespite:brewing | Unavailable: No native dependency/processor for upstream recipe type farmersrespite:brewing |
| `ends_delight:brewing/chorus_flower_tea` | Unavailable: No native dependency/processor for upstream recipe type farmersrespite:brewing | Unavailable: No native dependency/processor for upstream recipe type farmersrespite:brewing |
| `ends_delight:brewing/chorus_fruit_milk_tea` | Unavailable: No native dependency/processor for upstream recipe type farmersrespite:brewing | Unavailable: No native dependency/processor for upstream recipe type farmersrespite:brewing |
| `ends_delight:chorus_fruit_crate` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `ends_delight:chorus_fruit_crate_to_chorus_fruit` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `ends_delight:crack_non_hatchable_dragon_egg` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `ends_delight:dragon_egg_knife` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `ends_delight:dragon_tooth_knife` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `ends_delight:dried_chorus_flower` | Supported (native furnace) | Supported (native furnace) |
| `ends_delight:end_stone_knife` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `ends_delight:end_stove` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `ends_delight:ender_pearl_grain` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `ends_delight:food/assorted_salad` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `ends_delight:food/bubble_tea` | Supported (CookingRecipes) | Supported (CookingRecipes) |
| `ends_delight:food/chorus_cookie` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `ends_delight:food/chorus_flower_pie` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `ends_delight:food/chorus_flower_tea` | Supported (CookingRecipes) | Supported (CookingRecipes) |
| `ends_delight:food/chorus_fruit_grain` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `ends_delight:food/chorus_fruit_milk_tea` | Supported (CookingRecipes) | Supported (CookingRecipes) |
| `ends_delight:food/chorus_fruit_pie` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `ends_delight:food/chorus_fruit_pie_cutting` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `ends_delight:food/chorus_fruit_popsicle` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `ends_delight:food/chorus_fruit_wine` | Supported (CookingRecipes) | Supported (CookingRecipes) |
| `ends_delight:food/dragon_breath_and_chorus_soup` | Supported (CookingRecipes) | Supported (CookingRecipes) |
| `ends_delight:food/dragon_breath_soda` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `ends_delight:food/dragon_meat_stew_block` | Supported (CookingRecipes) | Supported (CookingRecipes) |
| `ends_delight:food/end_barbecue_stick` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `ends_delight:food/end_mixed_salad` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `ends_delight:food/ender_congee` | Supported (CookingRecipes) | Supported (CookingRecipes) |
| `ends_delight:food/ender_sauce` | Supported (CookingRecipes) | Supported (CookingRecipes) |
| `ends_delight:food/grilled_shulker_block` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `ends_delight:food/leg_to_meat` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `ends_delight:food/raw_dragon_meat_cuts` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `ends_delight:food/roasted_dragon_meat_cuts` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `ends_delight:food/roasted_dragon_steak` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `ends_delight:food/roasted_shulker_meat_slice` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `ends_delight:food/shulker_meat_slice` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `ends_delight:food/steamed_dragon_egg_block` | Supported (CookingRecipes) | Supported (CookingRecipes) |
| `ends_delight:food/stir_fried_shulker_meat` | Supported (CookingRecipes) | Supported (CookingRecipes) |
| `ends_delight:food/stuffed_rice_cake` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `ends_delight:food_campfire_cooking/fried_dragon_egg_campfire` | Supported (GrillingRecipes) | Supported (GrillingRecipes) |
| `ends_delight:food_campfire_cooking/roasted_dragon_meat_campfire` | Supported (GrillingRecipes) | Supported (GrillingRecipes) |
| `ends_delight:food_campfire_cooking/roasted_dragon_meat_cuts_campfire` | Supported (GrillingRecipes) | Supported (GrillingRecipes) |
| `ends_delight:food_campfire_cooking/roasted_shulker_meat_campfire` | Supported (GrillingRecipes) | Supported (GrillingRecipes) |
| `ends_delight:food_campfire_cooking/roasted_shulker_meat_slice_campfire` | Supported (GrillingRecipes) | Supported (GrillingRecipes) |
| `ends_delight:food_smelting/fried_dragon_egg` | Supported (native furnace) | Supported (native furnace) |
| `ends_delight:food_smelting/roasted_dragon_meat` | Supported (native furnace) | Supported (native furnace) |
| `ends_delight:food_smelting/roasted_dragon_meat_cuts` | Supported (native furnace) | Supported (native furnace) |
| `ends_delight:food_smelting/roasted_shulker_meat` | Supported (native furnace) | Supported (native furnace) |
| `ends_delight:food_smelting/roasted_shulker_meat_slice` | Supported (native furnace) | Supported (native furnace) |
| `ends_delight:food_smoking/dried_endermite_meat` | Supported (native furnace) | Supported (native furnace) |
| `ends_delight:food_smoking/fried_dragon_egg_smoking` | Supported (native furnace) | Supported (native furnace) |
| `ends_delight:food_smoking/roasted_dragon_meat_cuts_smoking` | Supported (native furnace) | Supported (native furnace) |
| `ends_delight:food_smoking/roasted_dragon_meat_smoking` | Supported (native furnace) | Supported (native furnace) |
| `ends_delight:food_smoking/roasted_shulker_meat_slice_smoking` | Supported (native furnace) | Supported (native furnace) |
| `ends_delight:food_smoking/roasted_shulker_meat_smoking` | Supported (native furnace) | Supported (native furnace) |
| `ends_delight:food_smoking/smoked_dragon_leg` | Supported (native furnace) | Supported (native furnace) |
| `ends_delight:half_dragon_egg_shell` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `ends_delight:non_hatchable_dragon_egg` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `ends_delight:purpur_knife` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `ends_delight:succulent_to_dye` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:acacia_pantry` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:apple_pie` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:apple_pie_from_slices` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:bacon_and_eggs` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:bacon_sandwich` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:barbecue_stick` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:basket` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:beef_patty` | Supported (native furnace) | Supported (native furnace) |
| `farmersdelight:beef_patty_from_campfire_cooking` | Supported (GrillingRecipes) | Supported (GrillingRecipes) |
| `farmersdelight:beef_patty_from_smoking` | Supported (native furnace) | Supported (native furnace) |
| `farmersdelight:beetroot_crate` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:beetroot_from_crate` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:birch_pantry` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:black_canvas_sign` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:blue_canvas_sign` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:book_from_canvas` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:bread_from_smelting` | Supported (native furnace) | Supported (native furnace) |
| `farmersdelight:bread_from_smoking` | Supported (native furnace) | Supported (native furnace) |
| `farmersdelight:brown_canvas_sign` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:cabbage` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:cabbage_crate` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:cabbage_from_leaves` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:cake_from_milk_bottle` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:cake_from_slices` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:canvas` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:canvas_from_canvas_rug` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:canvas_rug` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:canvas_sign` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:carrot_crate` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:carrot_from_crate` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:chicken_sandwich` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:chocolate_pie` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:chocolate_pie_from_slices` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:cooked_bacon` | Supported (native furnace) | Supported (native furnace) |
| `farmersdelight:cooked_bacon_from_campfire_cooking` | Supported (GrillingRecipes) | Supported (GrillingRecipes) |
| `farmersdelight:cooked_bacon_from_smoking` | Supported (native furnace) | Supported (native furnace) |
| `farmersdelight:cooked_chicken_cuts` | Supported (native furnace) | Supported (native furnace) |
| `farmersdelight:cooked_chicken_cuts_from_campfire_cooking` | Supported (GrillingRecipes) | Supported (GrillingRecipes) |
| `farmersdelight:cooked_chicken_cuts_from_smoking` | Supported (native furnace) | Supported (native furnace) |
| `farmersdelight:cooked_cod_slice` | Supported (native furnace) | Supported (native furnace) |
| `farmersdelight:cooked_cod_slice_from_campfire_cooking` | Supported (GrillingRecipes) | Supported (GrillingRecipes) |
| `farmersdelight:cooked_cod_slice_from_smoking` | Supported (native furnace) | Supported (native furnace) |
| `farmersdelight:cooked_mutton_chops` | Supported (native furnace) | Supported (native furnace) |
| `farmersdelight:cooked_mutton_chops_from_campfire_cooking` | Supported (GrillingRecipes) | Supported (GrillingRecipes) |
| `farmersdelight:cooked_mutton_chops_from_smoking` | Supported (native furnace) | Supported (native furnace) |
| `farmersdelight:cooked_salmon_slice` | Supported (native furnace) | Supported (native furnace) |
| `farmersdelight:cooked_salmon_slice_from_campfire_cooking` | Supported (GrillingRecipes) | Supported (GrillingRecipes) |
| `farmersdelight:cooked_salmon_slice_from_smoking` | Supported (native furnace) | Supported (native furnace) |
| `farmersdelight:cooking/apple_cider` | Supported (CookingRecipes) | Supported (CookingRecipes) |
| `farmersdelight:cooking/baked_cod_stew` | Supported (CookingRecipes) | Supported (CookingRecipes) |
| `farmersdelight:cooking/beef_stew` | Supported (CookingRecipes) | Supported (CookingRecipes) |
| `farmersdelight:cooking/beetroot_soup` | Supported (CookingRecipes) | Supported (CookingRecipes) |
| `farmersdelight:cooking/cabbage_rolls` | Supported (CookingRecipes) | Supported (CookingRecipes) |
| `farmersdelight:cooking/chicken_soup` | Supported (CookingRecipes) | Supported (CookingRecipes) |
| `farmersdelight:cooking/cooked_rice` | Supported (CookingRecipes) | Supported (CookingRecipes) |
| `farmersdelight:cooking/dog_food` | Supported (CookingRecipes) | Supported (CookingRecipes) |
| `farmersdelight:cooking/dumplings` | Supported (CookingRecipes) | Supported (CookingRecipes) |
| `farmersdelight:cooking/fish_stew` | Supported (CookingRecipes) | Supported (CookingRecipes) |
| `farmersdelight:cooking/fried_rice` | Supported (CookingRecipes) | Supported (CookingRecipes) |
| `farmersdelight:cooking/hot_cocoa` | Supported (CookingRecipes) | Supported (CookingRecipes) |
| `farmersdelight:cooking/mushroom_stew` | Supported (CookingRecipes) | Supported (CookingRecipes) |
| `farmersdelight:cooking/noodle_soup` | Supported (CookingRecipes) | Supported (CookingRecipes) |
| `farmersdelight:cooking/pasta_with_meatballs` | Supported (CookingRecipes) | Supported (CookingRecipes) |
| `farmersdelight:cooking/pasta_with_mutton_chop` | Supported (CookingRecipes) | Supported (CookingRecipes) |
| `farmersdelight:cooking/pumpkin_soup` | Supported (CookingRecipes) | Supported (CookingRecipes) |
| `farmersdelight:cooking/rabbit_stew` | Supported (CookingRecipes) | Supported (CookingRecipes) |
| `farmersdelight:cooking/ratatouille` | Supported (CookingRecipes) | Supported (CookingRecipes) |
| `farmersdelight:cooking/squid_ink_pasta` | Supported (CookingRecipes) | Supported (CookingRecipes) |
| `farmersdelight:cooking/stuffed_pumpkin_block` | Supported (CookingRecipes) | Supported (CookingRecipes) |
| `farmersdelight:cooking/tomato_sauce` | Supported (CookingRecipes) | Supported (CookingRecipes) |
| `farmersdelight:cooking/vegetable_noodles` | Supported (CookingRecipes) | Supported (CookingRecipes) |
| `farmersdelight:cooking/vegetable_soup` | Supported (CookingRecipes) | Supported (CookingRecipes) |
| `farmersdelight:cooking_pot` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:crimson_pantry` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:cutting/acacia_door` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `farmersdelight:cutting/acacia_log` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `farmersdelight:cutting/acacia_sign` | Supported (CuttingRecipes) | Unavailable: Unavailable input [{"item":"minecraft:acacia_sign"}] |
| `farmersdelight:cutting/acacia_trapdoor` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `farmersdelight:cutting/acacia_wood` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `farmersdelight:cutting/allium` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `farmersdelight:cutting/apple_pie` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `farmersdelight:cutting/azure_bluet` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `farmersdelight:cutting/beef` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `farmersdelight:cutting/birch_door` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `farmersdelight:cutting/birch_log` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `farmersdelight:cutting/birch_sign` | Supported (CuttingRecipes) | Unavailable: Unavailable input [{"item":"minecraft:birch_sign"}] |
| `farmersdelight:cutting/birch_trapdoor` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `farmersdelight:cutting/birch_wood` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `farmersdelight:cutting/blue_orchid` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `farmersdelight:cutting/bricks` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `farmersdelight:cutting/brown_mushroom_colony` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `farmersdelight:cutting/cabbage` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `farmersdelight:cutting/cake` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `farmersdelight:cutting/chicken` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `farmersdelight:cutting/chocolate_pie` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `farmersdelight:cutting/clay` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `farmersdelight:cutting/cod` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `farmersdelight:cutting/cooked_chicken` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `farmersdelight:cutting/cooked_cod` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `farmersdelight:cutting/cooked_mutton` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `farmersdelight:cutting/cooked_salmon` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `farmersdelight:cutting/cornflower` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `farmersdelight:cutting/crimson_door` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `farmersdelight:cutting/crimson_hyphae` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `farmersdelight:cutting/crimson_sign` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `farmersdelight:cutting/crimson_stem` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `farmersdelight:cutting/crimson_trapdoor` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `farmersdelight:cutting/dandelion` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `farmersdelight:cutting/dark_oak_door` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `farmersdelight:cutting/dark_oak_log` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `farmersdelight:cutting/dark_oak_sign` | Supported (CuttingRecipes) | Unavailable: Unavailable input [{"item":"minecraft:dark_oak_sign"}] |
| `farmersdelight:cutting/dark_oak_trapdoor` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `farmersdelight:cutting/dark_oak_wood` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `farmersdelight:cutting/gravel` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `farmersdelight:cutting/ham` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `farmersdelight:cutting/ink_sac` | Unavailable: Equivalent input/output would create a conversion loop: "minecraft:ink_sac" -> "minecraft:black_dye" | Unavailable: Equivalent input/output would create a conversion loop: "minecraft:ink_sac" -> "minecraft:black_dye" |
| `farmersdelight:cutting/jungle_door` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `farmersdelight:cutting/jungle_log` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `farmersdelight:cutting/jungle_sign` | Supported (CuttingRecipes) | Unavailable: Unavailable input [{"item":"minecraft:jungle_sign"}] |
| `farmersdelight:cutting/jungle_trapdoor` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `farmersdelight:cutting/jungle_wood` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `farmersdelight:cutting/leather_boots` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `farmersdelight:cutting/leather_chestplate` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `farmersdelight:cutting/leather_helmet` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `farmersdelight:cutting/leather_horse_armor` | Unavailable: Unavailable input [{"item":"minecraft:leather_horse_armor"}] | Unavailable: Unavailable input [{"item":"minecraft:leather_horse_armor"}] |
| `farmersdelight:cutting/leather_leggings` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `farmersdelight:cutting/lily_of_the_valley` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `farmersdelight:cutting/melon` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `farmersdelight:cutting/mutton` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `farmersdelight:cutting/nether_bricks` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `farmersdelight:cutting/oak_door` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `farmersdelight:cutting/oak_log` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `farmersdelight:cutting/oak_sign` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `farmersdelight:cutting/oak_trapdoor` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `farmersdelight:cutting/oak_wood` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `farmersdelight:cutting/orange_tulip` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `farmersdelight:cutting/oxeye_daisy` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `farmersdelight:cutting/pink_tulip` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `farmersdelight:cutting/poppy` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `farmersdelight:cutting/porkchop` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `farmersdelight:cutting/pumpkin` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `farmersdelight:cutting/quartz_block` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `farmersdelight:cutting/red_mushroom_colony` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `farmersdelight:cutting/red_tulip` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `farmersdelight:cutting/rice_panicle` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `farmersdelight:cutting/saddle` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `farmersdelight:cutting/salmon` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `farmersdelight:cutting/smoked_ham` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `farmersdelight:cutting/spruce_door` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `farmersdelight:cutting/spruce_log` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `farmersdelight:cutting/spruce_sign` | Supported (CuttingRecipes) | Unavailable: Unavailable input [{"item":"minecraft:spruce_sign"}] |
| `farmersdelight:cutting/spruce_trapdoor` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `farmersdelight:cutting/spruce_wood` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `farmersdelight:cutting/stone` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `farmersdelight:cutting/sweet_berry_cheesecake` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `farmersdelight:cutting/warped_door` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `farmersdelight:cutting/warped_hyphae` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `farmersdelight:cutting/warped_sign` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `farmersdelight:cutting/warped_stem` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `farmersdelight:cutting/warped_trapdoor` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `farmersdelight:cutting/wheat_dough` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `farmersdelight:cutting/white_tulip` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `farmersdelight:cutting/wild_beetroots` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `farmersdelight:cutting/wild_cabbages` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `farmersdelight:cutting/wild_carrots` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `farmersdelight:cutting/wild_onions` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `farmersdelight:cutting/wild_potatoes` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `farmersdelight:cutting/wild_rice` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `farmersdelight:cutting/wild_tomatoes` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `farmersdelight:cutting/wither_rose` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `farmersdelight:cutting_board` | Supported (foundation crafting) | Supported (foundation crafting) |
| `farmersdelight:cyan_canvas_sign` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:dark_oak_pantry` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:diamond_knife` | Supported (foundation crafting) | Supported (foundation crafting) |
| `farmersdelight:egg_sandwich` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:flint_knife` | Supported (foundation crafting) | Supported (foundation crafting) |
| `farmersdelight:fried_egg` | Supported (native furnace) | Supported (native furnace) |
| `farmersdelight:fried_egg_from_campfire_cooking` | Supported (GrillingRecipes) | Supported (GrillingRecipes) |
| `farmersdelight:fried_egg_from_smoking` | Supported (native furnace) | Supported (native furnace) |
| `farmersdelight:fruit_salad` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:full_tatami_mat` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:full_tatami_mat_from_halves` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:gold_nugget_from_blasting_knife` | Supported (native furnace) | Supported (native furnace) |
| `farmersdelight:gold_nugget_from_smelting_knife` | Supported (native furnace) | Supported (native furnace) |
| `farmersdelight:golden_knife` | Supported (foundation crafting) | Supported (foundation crafting) |
| `farmersdelight:gray_canvas_sign` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:green_canvas_sign` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:grilled_salmon` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:half_tatami_mat` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:hamburger` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:honey_cookie` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:honey_glazed_ham_block` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:horse_feed` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:integration/botanypots/crops/cabbages` | Unavailable: external processor not installed/implemented on this target | Unavailable: external processor not installed/implemented on this target |
| `farmersdelight:integration/botanypots/crops/onions` | Unavailable: external processor not installed/implemented on this target | Unavailable: external processor not installed/implemented on this target |
| `farmersdelight:integration/botanypots/crops/rice_crop` | Unavailable: external processor not installed/implemented on this target | Unavailable: external processor not installed/implemented on this target |
| `farmersdelight:integration/botanypots/crops/tomatoes` | Unavailable: external processor not installed/implemented on this target | Unavailable: external processor not installed/implemented on this target |
| `farmersdelight:integration/botanypots/soil/rich_soil` | Unavailable: external processor not installed/implemented on this target | Unavailable: external processor not installed/implemented on this target |
| `farmersdelight:integration/botanypots/soil/rich_soil_farmland` | Unavailable: external processor not installed/implemented on this target | Unavailable: external processor not installed/implemented on this target |
| `farmersdelight:integration/create/filling/chocolate_pie` | Unavailable: external processor not installed/implemented on this target | Unavailable: external processor not installed/implemented on this target |
| `farmersdelight:integration/create/milling/rice_panicle` | Unavailable: external processor not installed/implemented on this target | Unavailable: external processor not installed/implemented on this target |
| `farmersdelight:integration/create/milling/wild_beetroots` | Unavailable: external processor not installed/implemented on this target | Unavailable: external processor not installed/implemented on this target |
| `farmersdelight:integration/create/milling/wild_cabbages` | Unavailable: external processor not installed/implemented on this target | Unavailable: external processor not installed/implemented on this target |
| `farmersdelight:integration/create/milling/wild_carrots` | Unavailable: external processor not installed/implemented on this target | Unavailable: external processor not installed/implemented on this target |
| `farmersdelight:integration/create/milling/wild_onions` | Unavailable: external processor not installed/implemented on this target | Unavailable: external processor not installed/implemented on this target |
| `farmersdelight:integration/create/milling/wild_potatoes` | Unavailable: external processor not installed/implemented on this target | Unavailable: external processor not installed/implemented on this target |
| `farmersdelight:integration/create/milling/wild_rice` | Unavailable: external processor not installed/implemented on this target | Unavailable: external processor not installed/implemented on this target |
| `farmersdelight:integration/create/milling/wild_tomatoes` | Unavailable: external processor not installed/implemented on this target | Unavailable: external processor not installed/implemented on this target |
| `farmersdelight:integration/create/mixing/cabbage_slice_from_mixing` | Unavailable: external processor not installed/implemented on this target | Unavailable: external processor not installed/implemented on this target |
| `farmersdelight:integration/create/mixing/pie_crust_from_mixing` | Unavailable: external processor not installed/implemented on this target | Unavailable: external processor not installed/implemented on this target |
| `farmersdelight:integration/create/mixing/tomato_sauce_from_mixing` | Unavailable: external processor not installed/implemented on this target | Unavailable: external processor not installed/implemented on this target |
| `farmersdelight:integration/immersiveengineering/cloche/cabbage` | Unavailable: external processor not installed/implemented on this target | Unavailable: external processor not installed/implemented on this target |
| `farmersdelight:integration/immersiveengineering/cloche/onion` | Unavailable: external processor not installed/implemented on this target | Unavailable: external processor not installed/implemented on this target |
| `farmersdelight:integration/immersiveengineering/cloche/rice` | Unavailable: external processor not installed/implemented on this target | Unavailable: external processor not installed/implemented on this target |
| `farmersdelight:integration/immersiveengineering/cloche/tomato` | Unavailable: external processor not installed/implemented on this target | Unavailable: external processor not installed/implemented on this target |
| `farmersdelight:integration/immersiveengineering/crusher/rice_panicle` | Unavailable: external processor not installed/implemented on this target | Unavailable: external processor not installed/implemented on this target |
| `farmersdelight:integration/immersiveengineering/crusher/wild_beetroots` | Unavailable: external processor not installed/implemented on this target | Unavailable: external processor not installed/implemented on this target |
| `farmersdelight:integration/immersiveengineering/crusher/wild_cabbages` | Unavailable: external processor not installed/implemented on this target | Unavailable: external processor not installed/implemented on this target |
| `farmersdelight:integration/immersiveengineering/crusher/wild_carrots` | Unavailable: external processor not installed/implemented on this target | Unavailable: external processor not installed/implemented on this target |
| `farmersdelight:integration/immersiveengineering/crusher/wild_onions` | Unavailable: external processor not installed/implemented on this target | Unavailable: external processor not installed/implemented on this target |
| `farmersdelight:integration/immersiveengineering/crusher/wild_potatoes` | Unavailable: external processor not installed/implemented on this target | Unavailable: external processor not installed/implemented on this target |
| `farmersdelight:integration/immersiveengineering/crusher/wild_rice` | Unavailable: external processor not installed/implemented on this target | Unavailable: external processor not installed/implemented on this target |
| `farmersdelight:integration/immersiveengineering/crusher/wild_tomatoes` | Unavailable: external processor not installed/implemented on this target | Unavailable: external processor not installed/implemented on this target |
| `farmersdelight:integration/immersiveengineering/fermenter/tomato` | Unavailable: external processor not installed/implemented on this target | Unavailable: external processor not installed/implemented on this target |
| `farmersdelight:integration/immersiveengineering/metalpress/pumpkin` | Unavailable: external processor not installed/implemented on this target | Unavailable: external processor not installed/implemented on this target |
| `farmersdelight:integration/immersiveengineering/squeezer/cabbage_seeds` | Unavailable: external processor not installed/implemented on this target | Unavailable: external processor not installed/implemented on this target |
| `farmersdelight:integration/immersiveengineering/squeezer/tomato_seeds` | Unavailable: external processor not installed/implemented on this target | Unavailable: external processor not installed/implemented on this target |
| `farmersdelight:integration/silentgear/cutting/netherwood` | Unavailable: Unavailable output silentgear:stripped_netherwood_log | Unavailable: Unavailable output silentgear:stripped_netherwood_log |
| `farmersdelight:iron_knife` | Supported (foundation crafting) | Supported (foundation crafting) |
| `farmersdelight:iron_nugget_from_blasting_knife` | Supported (native furnace) | Supported (native furnace) |
| `farmersdelight:iron_nugget_from_smelting_knife` | Supported (native furnace) | Supported (native furnace) |
| `farmersdelight:jungle_pantry` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:lead_from_rope` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:light_blue_canvas_sign` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:light_gray_canvas_sign` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:lime_canvas_sign` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:magenta_canvas_sign` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:melon_juice` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:melon_popsicle` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:milk_bottle` | Supported (foundation crafting) | Supported (foundation crafting) |
| `farmersdelight:milk_bucket_from_bottles` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:mixed_salad` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:mutton_wrap` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:nether_salad` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:netherite_knife_smithing` | Supported (UAB native smithing; template policy applies) | Supported (UAB native smithing; template policy applies) |
| `farmersdelight:oak_pantry` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:onion` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:onion_crate` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:orange_canvas_sign` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:organic_compost_from_rotten_flesh` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:organic_compost_from_tree_bark` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:painting_from_canvas` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:paper_from_tree_bark` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:pie_crust` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:pink_canvas_sign` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:potato_crate` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:potato_from_crate` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:pumpkin_from_slices` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:pumpkin_seeds_from_slice` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:purple_canvas_sign` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:red_canvas_sign` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:rice` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:rice_bag` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:rice_bale` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:rice_from_bag` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:rice_panicle` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:roast_chicken_block` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:roasted_mutton_chops` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:rope` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:safety_net` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:scaffolding_from_canvas` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:shepherds_pie_block` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:skillet` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:smoked_ham` | Supported (native furnace) | Supported (native furnace) |
| `farmersdelight:spruce_pantry` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:steak_and_potatoes` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:stove` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:straw` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:straw_bale` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:stuffed_potato` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:sweet_berry_cheesecake` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:sweet_berry_cheesecake_from_slices` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:sweet_berry_cookie` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:tatami` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:tatami_block_from_full` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:tomato` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:tomato_crate` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:tomato_seeds` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:warped_pantry` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:wheat_dough_from_eggs` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:wheat_dough_from_water` | Supported (foundation crafting) | Supported (foundation crafting) |
| `farmersdelight:white_canvas_sign` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `farmersdelight:yellow_canvas_sign` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `minecraft:baked_potato_from_campfire_cooking` | Supported (GrillingRecipes) | Supported (GrillingRecipes) |
| `minecraft:cooked_beef_from_campfire_cooking` | Supported (GrillingRecipes) | Supported (GrillingRecipes) |
| `minecraft:cooked_chicken_from_campfire_cooking` | Supported (GrillingRecipes) | Supported (GrillingRecipes) |
| `minecraft:cooked_cod_from_campfire_cooking` | Supported (GrillingRecipes) | Supported (GrillingRecipes) |
| `minecraft:cooked_mutton_from_campfire_cooking` | Supported (GrillingRecipes) | Supported (GrillingRecipes) |
| `minecraft:cooked_porkchop_from_campfire_cooking` | Supported (GrillingRecipes) | Supported (GrillingRecipes) |
| `minecraft:cooked_rabbit_from_campfire_cooking` | Supported (GrillingRecipes) | Supported (GrillingRecipes) |
| `minecraft:cooked_salmon_from_campfire_cooking` | Supported (GrillingRecipes) | Supported (GrillingRecipes) |
| `oceansdelight:baked_tentacle_on_a_stick` | Supported (native furnace) | Supported (native furnace) |
| `oceansdelight:baked_tentacle_on_a_stick_from_campfire_cooking` | Supported (GrillingRecipes) | Supported (GrillingRecipes) |
| `oceansdelight:baked_tentacle_on_a_stick_from_smoking` | Supported (native furnace) | Supported (native furnace) |
| `oceansdelight:cabbage_wrapped_elder_guardian` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `oceansdelight:cooked_elder_guardian_slice` | Supported (native furnace) | Supported (native furnace) |
| `oceansdelight:cooked_elder_guardian_slice_from_campfire_cooking` | Supported (GrillingRecipes) | Supported (GrillingRecipes) |
| `oceansdelight:cooked_elder_guardian_slice_from_smoking` | Supported (native furnace) | Supported (native furnace) |
| `oceansdelight:cooked_guardian_tail` | Supported (native furnace) | Supported (native furnace) |
| `oceansdelight:cooked_guardian_tail_from_campfire_cooking` | Supported (GrillingRecipes) | Supported (GrillingRecipes) |
| `oceansdelight:cooked_guardian_tail_from_smoking` | Supported (native furnace) | Supported (native furnace) |
| `oceansdelight:cooked_stuffed_cod` | Supported (native furnace) | Supported (native furnace) |
| `oceansdelight:cooked_stuffed_cod_from_campfire_cooking` | Supported (GrillingRecipes) | Supported (GrillingRecipes) |
| `oceansdelight:cooked_stuffed_cod_from_smoking` | Supported (native furnace) | Supported (native furnace) |
| `oceansdelight:cooking/braised_sea_pickle` | Supported (CookingRecipes) | Supported (CookingRecipes) |
| `oceansdelight:cooking/guardian_soup` | Supported (CookingRecipes) | Supported (CookingRecipes) |
| `oceansdelight:cooking/honey_fried_kelp` | Supported (CookingRecipes) | Supported (CookingRecipes) |
| `oceansdelight:cooking/squid_rings` | Supported (CookingRecipes) | Supported (CookingRecipes) |
| `oceansdelight:cutting/elder_guardian_slab` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `oceansdelight:cutting/guardian` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `oceansdelight:cutting/pufferfish` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `oceansdelight:cutting/tentacles` | Supported (CuttingRecipes) | Supported (CuttingRecipes) |
| `oceansdelight:food/elder_guardian_roll` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `oceansdelight:food/fugu_roll` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `oceansdelight:seagrass_salad` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `oceansdelight:stuffed_cod` | Supported (SourceCrafting) | Supported (SourceCrafting) |
| `oceansdelight:tentacle_on_a_stick` | Supported (SourceCrafting) | Supported (SourceCrafting) |
