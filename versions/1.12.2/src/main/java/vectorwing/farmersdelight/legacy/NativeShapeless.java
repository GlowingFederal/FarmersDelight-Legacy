package vectorwing.farmersdelight.legacy;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.world.World;
import vectorwing.farmersdelight.legacy.api.FoundationRecipe;
import vectorwing.farmersdelight.legacy.api.ShapelessMatcher;
import net.minecraft.util.NonNullList; import net.minecraft.item.crafting.Ingredient;
public final class NativeShapeless extends ShapelessRecipes {
    private final FoundationRecipe recipe;
    public NativeShapeless(FoundationRecipe recipe) {
        super("farmersdelight", new ItemStack(Content.item(recipe.output), recipe.count), NonNullList.<Ingredient>create());
        this.recipe = recipe;
    }
    @Override public boolean matches(InventoryCrafting inventory, World world) {
        final List<ItemStack> inputs = new ArrayList<ItemStack>();
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (stack != null && !stack.isEmpty()) inputs.add(stack);
        }
        return ShapelessMatcher.matches(recipe.inputs.length, inputs.size(), new ShapelessMatcher.Match() {
            public boolean accepts(int ingredient, int slot) { return Ingredients.matches(recipe.inputs[ingredient], inputs.get(slot)); }
        });
    }
    @Override public ItemStack getCraftingResult(InventoryCrafting inventory) { return getRecipeOutput(); }
    @Override public ItemStack getRecipeOutput() { return new ItemStack(Content.item(recipe.output), recipe.count); }
    @Override public boolean canFit(int width, int height) { return width * height >= recipe.inputs.length; }
    @Override public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> result = NonNullList.create();
        for (String specification : recipe.inputs) {
            List<ItemStack> candidates = Ingredients.candidates(specification);
            result.add(Ingredient.fromStacks(candidates.toArray(new ItemStack[candidates.size()])));
        }
        return result;
    }
    @Override public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inventory) {
        return net.minecraftforge.common.ForgeHooks.defaultRecipeGetRemainingItems(inventory);
    }
}
