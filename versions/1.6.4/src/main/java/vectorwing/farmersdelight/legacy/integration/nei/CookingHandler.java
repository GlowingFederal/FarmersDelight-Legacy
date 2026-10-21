package vectorwing.farmersdelight.legacy.integration.nei;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.GuiRecipe;
import net.minecraft.item.ItemStack;
import vectorwing.farmersdelight.legacy.Content;
import vectorwing.farmersdelight.legacy.CookingRecipes;
import vectorwing.farmersdelight.legacy.Stacks;

public final class CookingHandler extends FDRecipeHandler {
    static final String UID = "farmersdelight.cooking";
    public String getOverlayIdentifier() { return UID; }
    public String getRecipeName() { return text("cooking"); }
    public String getGuiTexture() { return "farmersdelight:textures/gui/cooking_pot.png"; }
    @Override public Class<? extends net.minecraft.client.gui.inventory.GuiContainer> getGuiClass() {
        return vectorwing.farmersdelight.legacy.client.CookingPotGui.class;
    }
    @Override public void loadTransferRects() {
        transferRects.add(new RecipeTransferRect(new Rectangle(65, 10, 25, 17), UID));
    }
    @Override public void drawBackground(int index) {
        panel();
        for (int i = 0; i < 6; i++) slot(6 + i % 3 * 18, 2 + i / 3 * 18);
        slot(68, 40); slot(100, 40);
        changeTexture(getGuiTexture());
        drawTexturedModalRect(95, 2, 119, 17, 28, 30);
        drawTexturedModalRect(23, 40, 176, 0, 17, 15);
        drawTexturedModalRect(65, 10, 89, 25, 25, 17);
        drawTexturedModalRect(88, 38, 112, 53, 11, 19);
    }
    @Override public void drawExtras(int index) {
        Cached cached = (Cached) arecipes.get(index);
        drawProgressBar(65, 10, 176, 15, 25, 17, cached.recipe.time, 0);
        label(cached.time, 123, 12); label(cached.xp, 123, 25);
        label(text("heated_batch"), 6, 56);
    }
    public void loadCraftingRecipes(String id, Object... results) {
        if (id.equals(UID) || id.equals("all"))
            for (CookingRecipes.Recipe recipe : CookingRecipes.ALL) arecipes.add(new Cached(recipe));
        else super.loadCraftingRecipes(id, results);
    }
    public void loadCraftingRecipes(ItemStack result) {
        for (CookingRecipes.Recipe recipe : CookingRecipes.ALL)
            if (Stacks.same(recipe.output, result)) arecipes.add(new Cached(recipe));
    }
    public void loadUsageRecipes(ItemStack used) {
        if (workstation(used, Content.cookingPot)) { loadCraftingRecipes(UID); return; }
        for (CookingRecipes.Recipe recipe : CookingRecipes.ALL) {
            boolean found = Stacks.same(recipe.container, used);
            for (List<ItemStack> input : recipe.inputs) if (Stacks.matches(input, used)) found = true;
            if (found) arecipes.add(new Cached(recipe));
        }
    }
    @Override public List<String> handleItemTooltip(GuiRecipe gui, ItemStack stack, List<String> tooltip, int index) {
        super.handleItemTooltip(gui, stack, tooltip, index);
        Cached cached = (Cached) arecipes.get(index);
        if (gui.isMouseOver(cached.output, index)) {
            note(tooltip, text("batch_output", Stacks.size(cached.recipe.output)));
            note(tooltip, text("cook_time", cached.time));
            note(tooltip, text("experience", number(cached.recipe.experience)));
            note(tooltip, text("meal_buffer"));
            note(tooltip, text("heat_sources"));
            note(tooltip, text("heat_conductor"));
            note(tooltip, text(cached.container == null ? "no_container" : "serving_containers"));
        }
        if (cached.container != null && gui.isMouseOver(cached.container, index))
            note(tooltip, text("serving_cost", cached.recipe.container.getDisplayName()));
        return tooltip;
    }
    public final class Cached extends CachedRecipe {
        final CookingRecipes.Recipe recipe;
        final String time, xp;
        final PositionedStack output, container;
        private final List<PositionedStack> inputs = new ArrayList<PositionedStack>();
        Cached(CookingRecipes.Recipe recipe) {
            this.recipe = recipe; time = seconds(recipe.time); xp = text("xp", number(recipe.experience));
            // +24,+15 maps only ingredients and containers, never the locked meal buffer.
            for (int i = 0; i < recipe.inputs.size(); i++)
                inputs.add(new PositionedStack(recipe.inputs.get(i), 6 + i % 3 * 18, 2 + i / 3 * 18));
            container = Stacks.empty(recipe.container) ? null : new PositionedStack(recipe.container, 68, 40);
            if (container != null) inputs.add(container);
            output = new PositionedStack(recipe.output.copy(), 100, 40);
        }
        public List<PositionedStack> getIngredients() { return getCycledIngredients(cycleticks / 20, inputs); }
        public PositionedStack getResult() { return output; }
    }
}
