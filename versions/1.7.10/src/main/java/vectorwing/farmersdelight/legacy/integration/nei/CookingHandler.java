package vectorwing.farmersdelight.legacy.integration.nei;

import java.util.ArrayList;
import java.util.List;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.item.ItemStack;
import vectorwing.farmersdelight.legacy.CookingRecipes;
import vectorwing.farmersdelight.legacy.Stacks;

public final class CookingHandler extends TemplateRecipeHandler {
    private static final String UID = "farmersdelight.cooking";
    public String getOverlayIdentifier() { return UID; }
    public String getRecipeName() { return "Cooking Pot"; }
    public String getGuiTexture() { return "minecraft:textures/gui/container/crafting_table.png"; }
    public void drawBackground(int recipe) { Gui.drawRect(0, 0, 166, 76, 0xffc6c6c6); }
    public void drawExtras(int index) {
        CookingRecipes.Recipe recipe = ((Cached) arecipes.get(index)).recipe;
        Minecraft.getMinecraft().fontRenderer.drawString("Heated: " + recipe.time / 20F + " s", 6, 48, 0x555555);
        Minecraft.getMinecraft().fontRenderer.drawString(recipe.experience + " XP / batch", 6, 60, 0x555555);
        Minecraft.getMinecraft().fontRenderer.drawString(">", 110, 26, 0x555555);
    }
    public void loadCraftingRecipes(String id, Object... results) {
        if (id.equals(UID)) for (CookingRecipes.Recipe recipe : CookingRecipes.ALL) arecipes.add(new Cached(recipe));
        else super.loadCraftingRecipes(id, results);
    }
    public void loadCraftingRecipes(ItemStack result) {
        for (CookingRecipes.Recipe recipe : CookingRecipes.ALL) if (Stacks.same(recipe.output, result)) arecipes.add(new Cached(recipe));
    }
    public void loadUsageRecipes(ItemStack used) {
        for (CookingRecipes.Recipe recipe : CookingRecipes.ALL) {
            boolean found = Stacks.same(recipe.container, used);
            for (List<ItemStack> input : recipe.inputs) if (Stacks.matches(input, used)) found = true;
            if (found) arecipes.add(new Cached(recipe));
        }
    }
    public final class Cached extends CachedRecipe {
        final CookingRecipes.Recipe recipe;
        private final List<PositionedStack> inputs = new ArrayList<PositionedStack>();
        private final PositionedStack output;
        Cached(CookingRecipes.Recipe recipe) {
            this.recipe = recipe;
            for (int i = 0; i < recipe.inputs.size(); i++) inputs.add(new PositionedStack(recipe.inputs.get(i), 6 + i % 3 * 18, 2 + i / 3 * 18));
            if (!Stacks.empty(recipe.container)) inputs.add(new PositionedStack(recipe.container, 68, 40));
            output = new PositionedStack(recipe.output, 126, 20);
        }
        public List<PositionedStack> getIngredients() { return getCycledIngredients(cycleticks / 20, inputs); }
        public PositionedStack getResult() { return output; }
    }
}
