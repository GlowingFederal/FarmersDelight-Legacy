// Requires Brewin' and Chewin' and UAB, with berries and honey enabled.
#modloaded brewinandchewin
import mods.brewinandchewin.Fermenting;

Fermenting.removeRecipe("brewinandchewin:fermenting/mead");
Fermenting.addRecipe("pack:mead", <brewinandchewin:mead>,
    [<minecraft:wheat>, <minecraft:wheat>, <etfuturum:sweet_berries>],
    <etfuturum:honey_bottle>, <brewinandchewin:tankard>, 12000, 3, 0.6);
