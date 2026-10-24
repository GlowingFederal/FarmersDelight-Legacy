package vectorwing.farmersdelight.legacy.integration.nei;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.GuiRecipe;
import net.minecraft.item.ItemStack;
import vectorwing.farmersdelight.legacy.Content;
import vectorwing.farmersdelight.legacy.CuttingRecipes;
import vectorwing.farmersdelight.legacy.Settings;
import vectorwing.farmersdelight.legacy.Stacks;

public final class CuttingHandler extends FDRecipeHandler {
    static final String UID = "farmersdelight.cutting";
    public String getOverlayIdentifier() { return UID; }
    public String getRecipeName() { return text("cutting"); }
    public String getGuiTexture() { return "minecraft:textures/gui/container/furnace.png"; }
    @Override public void loadTransferRects() {
        transferRects.add(new RecipeTransferRect(new Rectangle(53, 14, 18, 18), UID));
    }
    @Override public void drawBackground(int index) {
        panel(); slot(6, 16); slot(34, 16);
        Cached cached = (Cached) arecipes.get(index);
        for (PositionedStack output : cached.outputs) slot(output.relx, output.rely);
        arrow(53, 18, 18);
    }
    @Override public void drawExtras(int index) {
        Cached cached = (Cached) arecipes.get(index);
        label("+", 25, 20);
        label(text("tool_use", cached.tool), 6, 3);
        for (int i = 0; i < cached.recipe.chances.size(); i++) if (cached.recipe.chances.get(i) < 1)
            label(Math.round(cached.recipe.chances.get(i) * 100) + "%", 72 + i * 22, 36);
        label(text("fortune", number(Settings.cuttingFortuneBonus * 100)), 6, 53);
    }
    public void loadCraftingRecipes(String id, Object... results) {
        if (id.equals(UID) || id.equals("all"))
            for (CuttingRecipes.Recipe recipe : CuttingRecipes.ALL) arecipes.add(new Cached(recipe));
        else super.loadCraftingRecipes(id, results);
    }
    public void loadCraftingRecipes(ItemStack result) {
        for (CuttingRecipes.Recipe recipe : CuttingRecipes.ALL) for (ItemStack output : recipe.outputs)
            if (Stacks.same(output, result)) { arecipes.add(new Cached(recipe)); break; }
    }
    public void loadUsageRecipes(ItemStack used) {
        if (workstation(used, Content.cuttingBoard)) { loadCraftingRecipes(UID); return; }
        for (CuttingRecipes.Recipe recipe : CuttingRecipes.ALL)
            if (recipe.matchesInput(used) || recipe.matchesTool(used)) arecipes.add(new Cached(recipe));
    }
    @Override public List<String> handleItemTooltip(GuiRecipe gui, ItemStack stack, List<String> tooltip, int index) {
        super.handleItemTooltip(gui, stack, tooltip, index);
        Cached cached = (Cached) arecipes.get(index);
        if (gui.isMouseOver(cached.inputs.get(1), index)) note(tooltip, text("tool_retained"));
        for (int i = 0; i < cached.outputs.size(); i++) if (gui.isMouseOver(cached.outputs.get(i), index)) {
            note(tooltip, text("chance", number(cached.recipe.chances.get(i) * 100)));
            note(tooltip, text("independent_rolls"));
            note(tooltip, text("fortune", number(Settings.cuttingFortuneBonus * 100)));
        }
        return tooltip;
    }
    public final class Cached extends CachedRecipe {
        final CuttingRecipes.Recipe recipe;
        final String tool;
        final List<PositionedStack> inputs = new ArrayList<PositionedStack>(), outputs = new ArrayList<PositionedStack>();
        Cached(CuttingRecipes.Recipe recipe) {
            this.recipe = recipe; tool = recipe.tool.equals("ingredient") ? recipe.tools().get(0).getDisplayName() : text("tool." + recipe.tool);
            inputs.add(new PositionedStack(recipe.inputs, 6, 16));
            inputs.add(new PositionedStack(recipe.tools(), 34, 16));
            for (int i = 0; i < recipe.outputs.size(); i++)
                outputs.add(new PositionedStack(recipe.outputs.get(i).copy(), 72 + i * 22, 16));
        }
        public List<PositionedStack> getIngredients() { return getCycledIngredients(cycleticks / 20, inputs); }
        public PositionedStack getResult() { return outputs.get(0); }
        public List<PositionedStack> getOtherStacks() { return outputs.subList(1, outputs.size()); }
    }
}
