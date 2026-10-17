package vectorwing.farmersdelight.legacy.integration.jei;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import vectorwing.farmersdelight.legacy.Content;
import vectorwing.farmersdelight.legacy.Ingredients;
import vectorwing.farmersdelight.legacy.Recipes;
import vectorwing.farmersdelight.legacy.api.FoundationRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

@JEIPlugin
public final class FoundationJEI implements IModPlugin {
    private static final String UID = "farmersdelight.foundation";
    @Override public void registerCategories(IRecipeCategoryRegistration registration) {
        final IDrawable background = registration.getJeiHelpers().getGuiHelper().createDrawable(
                new ResourceLocation("minecraft", "textures/gui/container/crafting_table.png"), 5, 11, 166, 65);
        registration.addRecipeCategories(new IRecipeCategory<Wrapper>() {
            public String getUid() { return UID; }
            public String getTitle() { return "Farmer's Delight Crafting"; }
            public String getModName() { return "Farmer's Delight Legacy"; }
            public IDrawable getBackground() { return background; }
            public void setRecipe(IRecipeLayout layout, Wrapper wrapper, IIngredients ingredients) {
                for (int i = 0; i < 9; i++) layout.getItemStacks().init(i, true, 24 + i % 3 * 18, 5 + i / 3 * 18);
                layout.getItemStacks().init(9, false, 118, 23);
                layout.getItemStacks().set(ingredients);
            }
        });
    }
    @Override public void register(IModRegistry registry) {
        List<Wrapper> recipes = new ArrayList<Wrapper>();
        for (FoundationRecipe recipe : Recipes.REGISTRY.entries().values()) recipes.add(new Wrapper(recipe));
        registry.addRecipes(recipes, UID);
    }
    public static final class Wrapper implements IRecipeWrapper {
        private final FoundationRecipe recipe;
        Wrapper(FoundationRecipe recipe) { this.recipe = recipe; }
        @Override public void getIngredients(IIngredients ingredients) {
            List<List<ItemStack>> inputs = new ArrayList<List<ItemStack>>();
            for (String input : recipe.grid()) inputs.add(input == null ? Collections.<ItemStack>emptyList() : Ingredients.candidates(input));
            ingredients.setInputLists(VanillaTypes.ITEM, inputs);
            ingredients.setOutput(VanillaTypes.ITEM, new ItemStack(Content.item(recipe.output), recipe.count));
        }
    }
}
