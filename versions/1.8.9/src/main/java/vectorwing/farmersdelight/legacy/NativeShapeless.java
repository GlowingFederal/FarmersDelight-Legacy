package vectorwing.farmersdelight.legacy;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import vectorwing.farmersdelight.legacy.api.FoundationRecipe;
import vectorwing.farmersdelight.legacy.api.ShapelessMatcher;

public final class NativeShapeless implements IRecipe {
    private final FoundationRecipe recipe;
    public NativeShapeless(FoundationRecipe recipe) {
        
        this.recipe = recipe;
    }
    @Override public boolean matches(InventoryCrafting inventory, World world) {
        final List<ItemStack> inputs = new ArrayList<ItemStack>();
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (stack != null && stack.stackSize > 0) inputs.add(stack);
        }
        return ShapelessMatcher.matches(recipe.inputs.length, inputs.size(), new ShapelessMatcher.Match() {
            public boolean accepts(int ingredient, int slot) { return Ingredients.matches(recipe.inputs[ingredient], inputs.get(slot)); }
        });
    }
    @Override public ItemStack getCraftingResult(InventoryCrafting inventory) { return getRecipeOutput(); }
    @Override public ItemStack getRecipeOutput() { return new ItemStack(Content.item(recipe.output), recipe.count); }
    @Override public int getRecipeSize() { return recipe.inputs.length; }
    
    public ItemStack[] getRemainingItems(InventoryCrafting grid) {
        ItemStack[] returns=new ItemStack[grid.getSizeInventory()];
        for(int i=0;i<returns.length;i++) returns[i]=net.minecraftforge.common.ForgeHooks.getContainerItem(grid.getStackInSlot(i));
        return returns;
    }
}
