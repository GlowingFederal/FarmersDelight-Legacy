package vectorwing.farmersdelight.legacy.integration.nei;

import java.util.ArrayList;
import java.util.List;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.item.ItemStack;
import vectorwing.farmersdelight.legacy.CuttingRecipes;
import vectorwing.farmersdelight.legacy.Stacks;

public final class CuttingHandler extends TemplateRecipeHandler {
    private static final String UID = "farmersdelight.cutting";
    public String getRecipeName() { return "Cutting Board"; }
    public String getGuiTexture() { return "minecraft:textures/gui/container/crafting_table.png"; }
    public void drawBackground(int recipe) { Gui.drawRect(0, 0, 166, 65, 0xffc6c6c6); }
    public void drawExtras(int index) {
        Cached cached = (Cached) arecipes.get(index);
        Minecraft.getMinecraft().fontRenderer.drawString("+     >", 26, 22, 0x555555);
        for (int i = 0; i < cached.recipe.chances.size(); i++) if (cached.recipe.chances.get(i) < 1)
            Minecraft.getMinecraft().fontRenderer.drawString(Math.round(cached.recipe.chances.get(i) * 100) + "%", 71 + i * 21, 39, 0x555555);
    }
    public void loadCraftingRecipes(String id, Object... results) {
        if (id.equals(UID)) for (CuttingRecipes.Recipe recipe : CuttingRecipes.ALL) arecipes.add(new Cached(recipe));
        else super.loadCraftingRecipes(id, results);
    }
    public void loadCraftingRecipes(ItemStack result) {
        for (CuttingRecipes.Recipe recipe : CuttingRecipes.ALL) for (ItemStack output : recipe.outputs)
            if (Stacks.same(output, result)) { arecipes.add(new Cached(recipe)); break; }
    }
    public void loadUsageRecipes(ItemStack used) {
        for (CuttingRecipes.Recipe recipe : CuttingRecipes.ALL) if (recipe.matchesInput(used) || recipe.matchesTool(used)) arecipes.add(new Cached(recipe));
    }
    public final class Cached extends CachedRecipe {
        final CuttingRecipes.Recipe recipe;
        private final List<PositionedStack> inputs = new ArrayList<PositionedStack>(), secondary = new ArrayList<PositionedStack>();
        private final PositionedStack output;
        Cached(CuttingRecipes.Recipe recipe) {
            this.recipe = recipe;
            inputs.add(new PositionedStack(recipe.inputs, 6, 16)); inputs.add(new PositionedStack(recipe.tools(), 34, 16));
            output = new PositionedStack(recipe.outputs.get(0), 72, 16);
            for (int i = 1; i < recipe.outputs.size(); i++) secondary.add(new PositionedStack(recipe.outputs.get(i), 72 + i * 21, 16));
        }
        public List<PositionedStack> getIngredients() { return getCycledIngredients(cycleticks / 20, inputs); }
        public PositionedStack getResult() { return output; }
        public List<PositionedStack> getOtherStacks() { return secondary; }
    }
}
