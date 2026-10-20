package vectorwing.farmersdelight.legacy;

import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing; import net.minecraft.util.math.BlockPos;

public final class CuttingDispenser extends BehaviorDefaultDispenseItem {
    private final IBehaviorDispenseItem previous;
    private CuttingDispenser(IBehaviorDispenseItem previous) { this.previous = previous; }
    public static void register() {
        if(!Settings.dispenserTools) return;
        for (Item value : Item.REGISTRY) {
            Item item = (Item) value;
            ItemStack stack = new ItemStack(item);
            boolean accepted = false;
            for (CuttingRecipes.Recipe recipe : CuttingRecipes.ALL) if (recipe.matchesTool(stack)) { accepted = true; break; }
            if (accepted) BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(item,
                    new CuttingDispenser((IBehaviorDispenseItem) BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.getObject(item)));
        }
    }
    @Override protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
        EnumFacing facing = source.getBlockState().getValue(BlockDispenser.FACING);
        TileEntity tile = source.getWorld().getTileEntity(source.getBlockPos().offset(facing));
        if (tile instanceof CuttingBoardTile) {
            ((CuttingBoardTile) tile).process(stack, null);
            return stack;
        }
        return previous.dispense(source, stack);
    }
    // The delegated behavior owns its own sound/particles. Boards supply their processing feedback.
    @Override protected void playDispenseSound(IBlockSource source) {}
    @Override protected void spawnDispenseParticles(IBlockSource source, EnumFacing facing) {}
}
