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
    private static mezz.jei.api.IJeiRuntime runtime;
    private static final List<Wrapper> REGISTERED=new ArrayList<Wrapper>();
    @Override public void onRuntimeAvailable(mezz.jei.api.IJeiRuntime value) { runtime=value; refreshAvailability(); }
    public static void refreshAvailability() {
        if(runtime==null) return;
        mezz.jei.api.IRecipeRegistry registry=runtime.getRecipeRegistry();
        for(Wrapper wrapper:REGISTERED) if(wrapper.source!=null) {
            if(wrapper.source.enabled()) registry.unhideRecipe(wrapper,UID); else registry.hideRecipe(wrapper,UID);
            IRecipeWrapper vanilla=registry.getRecipeWrapper(wrapper.source,"minecraft.crafting");
            if(vanilla!=null) { if(wrapper.source.enabled()) registry.unhideRecipe(vanilla,"minecraft.crafting"); else registry.hideRecipe(vanilla,"minecraft.crafting"); }
        }
    }
    private static final String UID = "farmersdelight.foundation";
    @Override public void registerCategories(IRecipeCategoryRegistration registration) {
        CuttingJEI.categories(registration);
        CookingJEI.categories(registration);
        GrillingJEI.categories(registration);
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
        CuttingJEI.register(registry);
        CookingJEI.register(registry);
        GrillingJEI.register(registry);
        List<Wrapper> recipes = new ArrayList<Wrapper>();
        for (FoundationRecipe recipe : Recipes.REGISTRY.entries().values()) recipes.add(new Wrapper(recipe));
        for (vectorwing.farmersdelight.legacy.SourceCrafting recipe : vectorwing.farmersdelight.legacy.SourceCrafting.ALL) recipes.add(new Wrapper(recipe));
        registry.addRecipes(recipes, UID);
        REGISTERED.clear(); REGISTERED.addAll(recipes);
    }
    public static final class Wrapper implements IRecipeWrapper {
        private final FoundationRecipe recipe;
        private final vectorwing.farmersdelight.legacy.SourceCrafting source;
        Wrapper(FoundationRecipe recipe) { this.recipe = recipe; this.source = null; }
        Wrapper(vectorwing.farmersdelight.legacy.SourceCrafting source) { this.recipe = null; this.source = source; }
        @Override public void getIngredients(IIngredients ingredients) {
            List<List<ItemStack>> inputs = new ArrayList<List<ItemStack>>();
            if (source != null) {
                for (List<ItemStack> input : source.grid()) inputs.add(input == null ? Collections.<ItemStack>emptyList() : input);
                ingredients.setInputLists(VanillaTypes.ITEM, inputs); ingredients.setOutput(VanillaTypes.ITEM, source.output);
                return;
            }
            for (String input : recipe.grid()) inputs.add(input == null ? Collections.<ItemStack>emptyList() : Ingredients.candidates(input));
            ingredients.setInputLists(VanillaTypes.ITEM, inputs);
            ingredients.setOutput(VanillaTypes.ITEM, new ItemStack(Content.item(recipe.output), recipe.count));
        }
    }
}
