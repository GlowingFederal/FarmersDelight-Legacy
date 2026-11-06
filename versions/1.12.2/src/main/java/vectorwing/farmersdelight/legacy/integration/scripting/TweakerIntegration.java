package vectorwing.farmersdelight.legacy.integration.scripting;

import java.util.*;
import net.minecraft.item.ItemStack;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import vectorwing.farmersdelight.legacy.ScriptRecipes;
import vectorwing.farmersdelight.legacy.Stacks;

/** Loaded only when the matching optional scripting engine is present. */
public final class TweakerIntegration {
    public static void register() {
        CraftTweakerAPI.registerClass(CookingPot.class);
        CraftTweakerAPI.registerClass(CuttingBoard.class);
        CraftTweakerAPI.registerClass(Skillet.class);
        if (net.minecraftforge.fml.common.Loader.isModLoaded("brewinandchewin")) CraftTweakerAPI.registerClass(Fermenting.class);
    }
    private static ItemStack stack(IItemStack value, boolean optional) {
        ItemStack nativeStack = value == null ? Stacks.none() : CraftTweakerMC.getItemStack(value);
        if (Stacks.empty(nativeStack)) {
            if (optional) return Stacks.none();
            throw new IllegalArgumentException("Recipe output must be a real nonempty item stack");
        }
        return nativeStack.copy();
    }
    private static List<ItemStack> ingredient(IIngredient value) {
        if (value == null || value.getAmount() != 1 || value.hasTransformers() || value.hasNewTransformers())
            throw new IllegalArgumentException("Use unit ingredients without transformers; native processors return real item containers automatically");
        List<ItemStack> examples = new ArrayList<ItemStack>();
        for (IItemStack example : value.getItems()) {
            ItemStack nativeStack = stack(example, false); Stacks.count(nativeStack, 1); examples.add(nativeStack);
        }
        if (examples.isEmpty()) throw new IllegalArgumentException("Ingredient has no displayable real items");
        return new ScriptRecipes.Ingredient(examples, actual -> value.matches(CraftTweakerMC.getIItemStack(Stacks.copy(actual, 1))));
    }
    private static List<List<ItemStack>> inputs(IIngredient[] values) {
        if (values == null) throw new IllegalArgumentException("Missing ingredient array");
        List<List<ItemStack>> result = new ArrayList<List<ItemStack>>();
        for (IIngredient value : values) result.add(ingredient(value));
        return result;
    }
    private static void apply(ScriptRecipes.Edit edit) {
        CraftTweakerAPI.apply(new crafttweaker.IAction() {
            public void apply() { edit.apply(); }
            public String describe() { return edit.describe(); }
            
        });
    }
    private static void remove(String kind, IIngredient output) {
        if (output == null) throw new IllegalArgumentException("Missing output filter");
        apply(ScriptRecipes.removeOutput(kind, actual -> output.matches(CraftTweakerMC.getIItemStack(actual))));
    }
    private static ScriptRecipes.Spec spec(String id, IItemStack output, IIngredient[] values, IItemStack container, int time, float experience) {
        ScriptRecipes.Spec spec = new ScriptRecipes.Spec(); spec.id = id;
        spec.inputs = inputs(values); spec.outputs.add(stack(output, false));
        spec.container = stack(container, true); spec.time = time; spec.experience = experience;
        return spec;
    }
    @ZenClass("mods.farmersdelight.CookingPot")
    public static final class CookingPot {
        @ZenMethod public static void addRecipe(String id, IItemStack output, IIngredient[] inputs, IItemStack container, int time, float experience) {
            apply(ScriptRecipes.add("cooking", spec(id, output, inputs, container, time, experience)));
        }
        @ZenMethod public static void removeRecipe(String id) { apply(ScriptRecipes.remove("cooking", id)); }
        @ZenMethod public static void remove(IIngredient output) { TweakerIntegration.remove("cooking", output); }
    }
    @ZenClass("mods.farmersdelight.CuttingBoard")
    public static final class CuttingBoard {
        @ZenMethod public static void addRecipe(String id, IIngredient input, IIngredient tool, IItemStack[] outputs, double[] chances, String sound) {
            ScriptRecipes.Spec spec = new ScriptRecipes.Spec(); spec.id = id; spec.inputs.add(ingredient(input)); spec.tools = ingredient(tool); spec.sound = sound;
            if (outputs == null || chances == null) throw new IllegalArgumentException("Missing cutting results/chances");
            for (IItemStack output : outputs) spec.outputs.add(stack(output, false));
            for (double chance : chances) spec.chances.add(chance);
            apply(ScriptRecipes.add("cutting", spec));
        }
        @ZenMethod public static void removeRecipe(String id) { apply(ScriptRecipes.remove("cutting", id)); }
        @ZenMethod public static void remove(IIngredient output) { TweakerIntegration.remove("cutting", output); }
    }
    @ZenClass("mods.farmersdelight.Skillet")
    public static final class Skillet {
        @ZenMethod public static void addRecipe(String id, IItemStack output, IIngredient input, int time) {
            apply(ScriptRecipes.add("grilling", spec(id, output, new IIngredient[]{input}, null, time, 0)));
        }
        @ZenMethod public static void removeRecipe(String id) { apply(ScriptRecipes.remove("grilling", id)); }
        @ZenMethod public static void remove(IIngredient output) { TweakerIntegration.remove("grilling", output); }
    }
    @ZenClass("mods.brewinandchewin.Fermenting")
    public static final class Fermenting {
        @ZenMethod public static void addRecipe(String id, IItemStack output, IIngredient[] inputs, IIngredient liquid, IItemStack container, int time, int temperature, float experience) {
            ScriptRecipes.Spec spec = spec(id, output, inputs, container, time, experience);
            spec.liquid = liquid == null ? Collections.<ItemStack>emptyList() : ingredient(liquid); spec.temperature = temperature;
            apply(ScriptRecipes.add("fermenting", spec));
        }
        @ZenMethod public static void removeRecipe(String id) { apply(ScriptRecipes.remove("fermenting", id)); }
        @ZenMethod public static void remove(IIngredient output) { TweakerIntegration.remove("fermenting", output); }
    }
    private TweakerIntegration() {}
}
