package vectorwing.farmersdelight.legacy.integration.nei;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;
import net.minecraft.item.ItemStack;
import vectorwing.farmersdelight.legacy.Content;
import vectorwing.farmersdelight.legacy.Ingredients;
import vectorwing.farmersdelight.legacy.Recipes;
import vectorwing.farmersdelight.legacy.api.FoundationRecipe;
public final class FoundationHandler extends TemplateRecipeHandler {
    private static final String UID = "farmersdelight.foundation";
    public String getRecipeName() { return "Farmer's Delight Crafting"; }
    public String getGuiTexture() { return "minecraft:textures/gui/container/crafting_table.png"; }
    @Override public void loadTransferRects() { transferRects.add(new RecipeTransferRect(new Rectangle(84, 23, 24, 18), UID)); }
    @Override public void loadCraftingRecipes(String id, Object... results) {
        if (id.equals(UID)) for (FoundationRecipe recipe : Recipes.REGISTRY.entries().values()) arecipes.add(new Cached(recipe));
        else super.loadCraftingRecipes(id, results);
    }
    @Override public void loadCraftingRecipes(ItemStack result) {
        for (FoundationRecipe recipe : Recipes.REGISTRY.entries().values())
            if (result.getItem() == Content.item(recipe.output) && result.getItemDamage() == 0) arecipes.add(new Cached(recipe));
    }
    @Override public void loadUsageRecipes(ItemStack used) {
        for (FoundationRecipe recipe : Recipes.REGISTRY.entries().values()) {
            for (String input : recipe.inputs) if (Ingredients.matches(input, used)) { arecipes.add(new Cached(recipe)); break; }
        }
    }
    public final class Cached extends CachedRecipe {
        private final List<PositionedStack> inputs = new ArrayList<PositionedStack>();
        private final PositionedStack output;
        Cached(FoundationRecipe recipe) {
            String[] grid = recipe.grid();
            for (int i = 0; i < grid.length; i++) if (grid[i] != null)
                inputs.add(new PositionedStack(Ingredients.candidates(grid[i]), 25 + i % 3 * 18, 6 + i / 3 * 18));
            output = new PositionedStack(new ItemStack(Content.item(recipe.output), recipe.count), 119, 24);
        }
        public List<PositionedStack> getIngredients() { return getCycledIngredients(cycleticks / 20, inputs); }
        public PositionedStack getResult() { return output; }
    }
}
