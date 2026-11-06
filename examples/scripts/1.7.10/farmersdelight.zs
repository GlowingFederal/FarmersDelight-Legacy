// Copy into scripts/ to opt in. Uses only items present without UAB.
import mods.farmersdelight.CookingPot;
import mods.farmersdelight.CuttingBoard;
import mods.farmersdelight.Skillet;

CookingPot.removeRecipe("farmersdelight:cooking/cooked_rice");
CookingPot.addRecipe("pack:cooked_rice", <farmersdelight:cooked_rice>, [<ore:cropRice>], <minecraft:bowl>, 100, 0.1);

CuttingBoard.removeRecipe("farmersdelight:cutting/cod");
CuttingBoard.addRecipe("pack:cod", <minecraft:fish:0>, <farmersdelight:iron_knife>.anyDamage(),
    [<farmersdelight:cod_slice> * 2, <minecraft:dye:15>], [1.0, 1.0], "");

Skillet.removeRecipe("farmersdelight:fried_egg_from_campfire_cooking");
Skillet.addRecipe("pack:fried_egg", <farmersdelight:fried_egg>, <minecraft:egg>, 600);
