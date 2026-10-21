package vectorwing.farmersdelight.legacy.integration.nei;

import java.awt.Rectangle;
import java.util.Collections;
import java.util.List;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.GuiRecipe;
import net.minecraft.item.ItemStack;
import vectorwing.farmersdelight.legacy.Content;
import vectorwing.farmersdelight.legacy.GrillingRecipes;
import vectorwing.farmersdelight.legacy.Stacks;

public final class GrillingHandler extends FDRecipeHandler {
    static final String UID = "farmersdelight.grilling";
    public String getOverlayIdentifier() { return UID; }
    public String getRecipeName() { return text("grilling"); }
    public String getGuiTexture() { return "minecraft:textures/gui/container/furnace.png"; }
    @Override public void loadTransferRects() {
        transferRects.add(new RecipeTransferRect(new Rectangle(72, 7, 24, 18), UID));
    }
    @Override public void drawBackground(int index) {
        panel(); slot(30, 7); slot(110, 7);
        changeTexture(getGuiTexture());
        drawTexturedModalRect(72, 7, 176, 14, 24, 16);
    }
    @Override public void drawExtras(int index) {
        Cached cached = (Cached) arecipes.get(index);
        drawProgressBar(72, 7, 176, 14, 24, 16, cached.recipe.time, 0);
        label(text("stove_time", cached.stoveTime), 6, 32);
        label(text("skillet_time", cached.skilletTime), 6, 44);
        label(text("fire_aspect"), 6, 56);
    }
    public void loadCraftingRecipes(String id, Object... results) {
        if (id.equals(UID) || id.equals("all"))
            for (GrillingRecipes.Recipe recipe : GrillingRecipes.ALL) arecipes.add(new Cached(recipe));
        else super.loadCraftingRecipes(id, results);
    }
    public void loadCraftingRecipes(ItemStack result) {
        for (GrillingRecipes.Recipe recipe : GrillingRecipes.ALL)
            if (Stacks.same(recipe.output, result)) arecipes.add(new Cached(recipe));
    }
    public void loadUsageRecipes(ItemStack used) {
        if (workstation(used, Content.skillet) || workstation(used, Content.stove)) { loadCraftingRecipes(UID); return; }
        for (GrillingRecipes.Recipe recipe : GrillingRecipes.ALL)
            if (Stacks.matches(recipe.inputs, used)) arecipes.add(new Cached(recipe));
    }
    @Override public List<String> handleItemTooltip(GuiRecipe gui, ItemStack stack, List<String> tooltip, int index) {
        super.handleItemTooltip(gui, stack, tooltip, index);
        Cached cached = (Cached) arecipes.get(index);
        if (gui.isMouseOver(cached.output, index)) {
            note(tooltip, text("stove_time", cached.stoveTime)); note(tooltip, text("skillet_time", cached.skilletTime));
            note(tooltip, text("heat_sources")); note(tooltip, text("heat_conductor"));
            note(tooltip, text("stove_clear")); note(tooltip, text("handheld"));
        }
        return tooltip;
    }
    public final class Cached extends CachedRecipe {
        final GrillingRecipes.Recipe recipe;
        final String stoveTime, skilletTime;
        final PositionedStack input, output;
        Cached(GrillingRecipes.Recipe recipe) {
            this.recipe = recipe; stoveTime = seconds(recipe.time); skilletTime = seconds(GrillingRecipes.skilletTime(recipe.time, 0));
            input = new PositionedStack(recipe.inputs, 30, 7); output = new PositionedStack(recipe.output.copy(), 110, 7);
        }
        public List<PositionedStack> getIngredients() { return getCycledIngredients(cycleticks / 20, Collections.singletonList(input)); }
        public PositionedStack getResult() { return output; }
    }
}
