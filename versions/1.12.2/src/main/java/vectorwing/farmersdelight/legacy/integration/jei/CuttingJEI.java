package vectorwing.farmersdelight.legacy.integration.jei;

import java.util.ArrayList;
import java.util.List;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import vectorwing.farmersdelight.legacy.Content;
import vectorwing.farmersdelight.legacy.CuttingRecipes;

final class CuttingJEI {
    private static final String UID = "farmersdelight.cutting";
    static void categories(IRecipeCategoryRegistration registration) {
        final IDrawable background = registration.getJeiHelpers().getGuiHelper().createBlankDrawable(154, 62);
        registration.addRecipeCategories(new IRecipeCategory<Wrapper>() {
            public String getUid() { return UID; }
            public String getTitle() { return "Cutting Board"; }
            public String getModName() { return "Farmer's Delight Legacy"; }
            public IDrawable getBackground() { return background; }
            public void setRecipe(IRecipeLayout layout, Wrapper wrapper, IIngredients ingredients) {
                layout.getItemStacks().init(0, true, 2, 16);
                layout.getItemStacks().init(1, true, 30, 16);
                for (int i = 0; i < wrapper.recipe.outputs.size(); i++) layout.getItemStacks().init(2 + i, false, 68 + i * 21, 16);
                layout.getItemStacks().set(ingredients);
            }
        });
    }
    static void register(IModRegistry registry) {
        List<Wrapper> recipes = new ArrayList<Wrapper>();
        for (CuttingRecipes.Recipe recipe : CuttingRecipes.ALL) recipes.add(new Wrapper(recipe));
        registry.addRecipes(recipes, UID);
        registry.addRecipeCatalyst(new ItemStack(Content.cuttingBoard), UID);
    }
    static final class Wrapper implements IRecipeWrapper {
        final CuttingRecipes.Recipe recipe;
        Wrapper(CuttingRecipes.Recipe recipe) { this.recipe = recipe; }
        public void getIngredients(IIngredients ingredients) {
            List<List<ItemStack>> inputs = new ArrayList<List<ItemStack>>(); inputs.add(recipe.inputs); inputs.add(recipe.tools());
            ingredients.setInputLists(VanillaTypes.ITEM, inputs); ingredients.setOutputs(VanillaTypes.ITEM, recipe.outputs);
        }
        public void drawInfo(Minecraft minecraft, int width, int height, int mouseX, int mouseY) {
            minecraft.fontRenderer.drawString("+     >", 22, 22, 0x555555);
            for (int i = 0; i < recipe.chances.size(); i++) if (recipe.chances.get(i) < 1)
                minecraft.fontRenderer.drawString(Math.round(recipe.chances.get(i) * 100) + "%", 67 + i * 21, 39, 0x555555);
        }
    }
}
