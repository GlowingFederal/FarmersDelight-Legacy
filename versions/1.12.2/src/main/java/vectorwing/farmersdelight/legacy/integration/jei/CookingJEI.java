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
import vectorwing.farmersdelight.legacy.CookingRecipes;
import vectorwing.farmersdelight.legacy.Stacks;

final class CookingJEI {
    static final String UID = "farmersdelight.cooking";
    static void categories(IRecipeCategoryRegistration registration) {
        final IDrawable background = new RecipeArtwork(0);
        registration.addRecipeCategories(new IRecipeCategory<Wrapper>() {
            private final IDrawable icon = registration.getJeiHelpers().getGuiHelper().createDrawableIngredient(new ItemStack(Content.cookingPot));
            public IDrawable getIcon() { return icon; }
            public String getUid() { return UID; }
            public String getTitle() { return "Cooking Pot"; }
            public String getModName() { return "Farmer's Delight Legacy"; }
            public IDrawable getBackground() { return background; }
            public void setRecipe(IRecipeLayout layout, Wrapper wrapper, IIngredients ingredients) {
                for (int i = 0; i < 6; i++) layout.getItemStacks().init(i, true, 5 + i % 3 * 18, 1 + i / 3 * 18);
                layout.getItemStacks().init(6, true, 67, 39);
                layout.getItemStacks().init(7, false, 99, 39);
                layout.getItemStacks().set(ingredients);
            }
        });
    }
    static void register(IModRegistry registry) {
        List<Wrapper> recipes = new ArrayList<Wrapper>();
        for (CookingRecipes.Recipe recipe : CookingRecipes.ALL) recipes.add(new Wrapper(recipe));
        registry.addRecipes(recipes, UID); registry.addRecipeCatalyst(new ItemStack(Content.cookingPot), UID);
        registry.addRecipeClickArea(vectorwing.farmersdelight.legacy.client.CookingPotGui.class, 89, 25, 24, 17, UID);
        registry.getRecipeTransferRegistry().addRecipeTransferHandler(new mezz.jei.api.recipe.transfer.IRecipeTransferInfo<vectorwing.farmersdelight.legacy.CookingPotContainer>() {
            public Class<vectorwing.farmersdelight.legacy.CookingPotContainer> getContainerClass() { return vectorwing.farmersdelight.legacy.CookingPotContainer.class; }
            public String getRecipeCategoryUid() { return UID; }
            public boolean canHandle(vectorwing.farmersdelight.legacy.CookingPotContainer container) { return true; }
            public List<net.minecraft.inventory.Slot> getRecipeSlots(vectorwing.farmersdelight.legacy.CookingPotContainer container) {
                List<net.minecraft.inventory.Slot> slots=new ArrayList<net.minecraft.inventory.Slot>();
                for(int i=0;i<6;i++) slots.add(container.getSlot(i));
                slots.add(container.getSlot(7)); return slots;
            }
            public List<net.minecraft.inventory.Slot> getInventorySlots(vectorwing.farmersdelight.legacy.CookingPotContainer container) {
                return new ArrayList<net.minecraft.inventory.Slot>(container.inventorySlots.subList(9,45));
            }
        });
    }
    static final class Wrapper implements IRecipeWrapper {
        final CookingRecipes.Recipe recipe;
        Wrapper(CookingRecipes.Recipe recipe) { this.recipe = recipe; }
        public void getIngredients(IIngredients ingredients) {
            List<List<ItemStack>> inputs = new ArrayList<List<ItemStack>>(recipe.inputs);
            while(inputs.size()<6) inputs.add(java.util.Collections.<ItemStack>emptyList());
            inputs.add(Stacks.empty(recipe.container) ? java.util.Collections.<ItemStack>emptyList() : java.util.Collections.singletonList(recipe.container));
            ingredients.setInputLists(VanillaTypes.ITEM, inputs); ingredients.setOutput(VanillaTypes.ITEM, recipe.output);
        }
        public void drawInfo(Minecraft minecraft, int width, int height, int mouseX, int mouseY) {
            RecipeArtwork.progress(minecraft, RecipeArtwork.POT, 65, 10, 176, 15, 25, 17);
            minecraft.fontRenderer.drawString(recipe.time / 20F + " s", 123, 12, 0x404040);
            minecraft.fontRenderer.drawString(recipe.experience + " XP", 123, 25, 0x404040);
            minecraft.fontRenderer.drawString("Heated batch", 6, 56, 0x404040);
            minecraft.fontRenderer.drawString(">", 106, 26, 0x555555);
        }
    }
}
