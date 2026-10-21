package vectorwing.farmersdelight.legacy.integration.nei;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import codechicken.nei.PositionedStack;
import net.minecraft.item.ItemStack;
import vectorwing.farmersdelight.legacy.Content;
import vectorwing.farmersdelight.legacy.Ingredients;
import vectorwing.farmersdelight.legacy.Recipes;
import vectorwing.farmersdelight.legacy.api.FoundationRecipe;
public final class FoundationHandler extends FDRecipeHandler {
    static final String UID = "farmersdelight.foundation";
    public String getOverlayIdentifier() { return UID; }
    public String getRecipeName() { return text("crafting"); }
    public String getGuiTexture() { return "minecraft:textures/gui/container/crafting_table.png"; }
    @Override public void loadTransferRects() { transferRects.add(new RecipeTransferRect(new Rectangle(84, 23, 24, 18), UID)); }
    @Override public void drawExtras(int index) {
        label(text(((Cached) arecipes.get(index)).shapeless ? "shapeless" : "shaped"), 84, 3);
    }
    @Override public void loadCraftingRecipes(String id, Object... results) {
        if (id.equals(UID) || id.equals("all")) {
            for (FoundationRecipe recipe : Recipes.REGISTRY.entries().values()) arecipes.add(new Cached(recipe));
            for (vectorwing.farmersdelight.legacy.SourceCrafting recipe : vectorwing.farmersdelight.legacy.SourceCrafting.ALL) if(recipe.enabled()) arecipes.add(new Cached(recipe));
        }
        else super.loadCraftingRecipes(id, results);
    }
    @Override public void loadCraftingRecipes(ItemStack result) {
        for (vectorwing.farmersdelight.legacy.SourceCrafting recipe : vectorwing.farmersdelight.legacy.SourceCrafting.ALL)
            if (recipe.enabled() && vectorwing.farmersdelight.legacy.Stacks.same(recipe.output, result)) arecipes.add(new Cached(recipe));
        for (FoundationRecipe recipe : Recipes.REGISTRY.entries().values())
            if (result.getItem() == Content.item(recipe.output) && result.getItemDamage() == 0) arecipes.add(new Cached(recipe));
    }
    @Override public void loadUsageRecipes(ItemStack used) {
        if(workstation(used, net.minecraft.init.Blocks.crafting_table)) { loadCraftingRecipes(UID); return; }
        for (vectorwing.farmersdelight.legacy.SourceCrafting recipe : vectorwing.farmersdelight.legacy.SourceCrafting.ALL)
            if(recipe.enabled()) for (List<ItemStack> input : recipe.inputs) if (input != null && vectorwing.farmersdelight.legacy.Stacks.matches(input, used)) { arecipes.add(new Cached(recipe)); break; }
        for (FoundationRecipe recipe : Recipes.REGISTRY.entries().values()) {
            for (String input : recipe.inputs) if (Ingredients.matches(input, used)) { arecipes.add(new Cached(recipe)); break; }
        }
    }
    public final class Cached extends CachedRecipe {
        final boolean shapeless;
        private final List<PositionedStack> inputs = new ArrayList<PositionedStack>();
        private final PositionedStack output;
        Cached(vectorwing.farmersdelight.legacy.SourceCrafting recipe) {
            shapeless = vectorwing.farmersdelight.legacy.api.SourceRecipes.all().get(recipe.id)
                    .get("type").getAsString().equals("minecraft:crafting_shapeless");
            List<List<ItemStack>> grid = recipe.grid();
            for (int i = 0; i < grid.size(); i++) if (grid.get(i) != null)
                inputs.add(new PositionedStack(grid.get(i), 25 + i % 3 * 18, 6 + i / 3 * 18));
            output = new PositionedStack(recipe.output, 119, 24);
        }
        Cached(FoundationRecipe recipe) {
            shapeless = recipe.pattern == null;
            String[] grid = recipe.grid();
            for (int i = 0; i < grid.length; i++) if (grid[i] != null)
                inputs.add(new PositionedStack(Ingredients.candidates(grid[i]), 25 + i % 3 * 18, 6 + i / 3 * 18));
            output = new PositionedStack(new ItemStack(Content.item(recipe.output), recipe.count), 119, 24);
        }
        public List<PositionedStack> getIngredients() { return getCycledIngredients(cycleticks / 20, inputs); }
        public PositionedStack getResult() { return output; }
    }
}
