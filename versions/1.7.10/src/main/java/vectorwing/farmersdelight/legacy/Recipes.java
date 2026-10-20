package vectorwing.farmersdelight.legacy;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import com.google.gson.Gson;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import vectorwing.farmersdelight.legacy.api.FoundationRecipe;
import vectorwing.farmersdelight.legacy.api.RecipeRegistry;

public final class Recipes {
    public static final RecipeRegistry<FoundationRecipe> REGISTRY = new RecipeRegistry<FoundationRecipe>();
    public static void register() {
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Content.cuttingBoard), "/##", "/##",
                '/', net.minecraft.init.Items.stick, '#', "plankWood"));
        net.minecraftforge.oredict.RecipeSorter.register("farmersdelight:foundation_shapeless", NativeShapeless.class,
                net.minecraftforge.oredict.RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");
        FoundationRecipe[] recipes;
        try (InputStreamReader reader = new InputStreamReader(Recipes.class.getResourceAsStream("/farmersdelight/foundation_recipes.json"), StandardCharsets.UTF_8)) {
            recipes = new Gson().fromJson(reader, FoundationRecipe[].class);
        } catch (java.io.IOException e) { throw new IllegalStateException("Cannot read recipes", e); }
        for (FoundationRecipe recipe : recipes) {
            REGISTRY.register(recipe.id, recipe);
            IRecipe nativeRecipe;
            if (recipe.pattern != null) {
                Object material = recipe.inputs[0].startsWith("#") ? "ingotIron" : Ingredients.candidates(recipe.inputs[0]).get(0);
                nativeRecipe = new ShapedOreRecipe(new ItemStack(Content.item(recipe.output), recipe.count),
                    recipe.pattern[0], recipe.pattern[1], 'm', material, 's', Ingredients.candidates(recipe.inputs[1]).get(0));
            } else nativeRecipe = new NativeShapeless(recipe);
            GameRegistry.addRecipe(nativeRecipe);
        }
    }
}
