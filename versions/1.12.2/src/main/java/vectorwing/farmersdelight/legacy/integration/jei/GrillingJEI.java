package vectorwing.farmersdelight.legacy.integration.jei;

import java.util.ArrayList;
import java.util.Collections;
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
import vectorwing.farmersdelight.legacy.GrillingRecipes;

final class GrillingJEI {
    private static final String UID="farmersdelight.grilling";
    static void categories(IRecipeCategoryRegistration registration) {
        final IDrawable background=registration.getJeiHelpers().getGuiHelper().createBlankDrawable(154,62);
        registration.addRecipeCategories(new IRecipeCategory<Wrapper>() {
            public String getUid() { return UID; }
            public String getTitle() { return "Skillet / Stove"; }
            public String getModName() { return "Farmer's Delight Legacy"; }
            public IDrawable getBackground() { return background; }
            public void setRecipe(IRecipeLayout layout,Wrapper wrapper,IIngredients ingredients) {
                layout.getItemStacks().init(0,true,26,4); layout.getItemStacks().init(1,false,106,4); layout.getItemStacks().set(ingredients);
            }
        });
    }
    static void register(IModRegistry registry) {
        List<Wrapper> recipes=new ArrayList<Wrapper>();
        for(GrillingRecipes.Recipe recipe:GrillingRecipes.ALL) recipes.add(new Wrapper(recipe));
        registry.addRecipes(recipes,UID); registry.addRecipeCatalyst(new ItemStack(Content.stove),UID); registry.addRecipeCatalyst(new ItemStack(Content.skillet),UID);
    }
    static final class Wrapper implements IRecipeWrapper {
        final GrillingRecipes.Recipe recipe;
        Wrapper(GrillingRecipes.Recipe recipe) { this.recipe=recipe; }
        public void getIngredients(IIngredients ingredients) {
            ingredients.setInputLists(VanillaTypes.ITEM,Collections.singletonList(recipe.inputs)); ingredients.setOutput(VanillaTypes.ITEM,recipe.output);
        }
        public void drawInfo(Minecraft minecraft,int width,int height,int mx,int my) {
            minecraft.fontRenderer.drawString(">",74,10,0x555555);
            minecraft.fontRenderer.drawString("Stove: "+recipe.time/20F+" s",4,30,0x555555);
            minecraft.fontRenderer.drawString("Heated skillet: "+GrillingRecipes.skilletTime(recipe.time,0)/20F+" s",4,44,0x555555);
        }
    }
}
