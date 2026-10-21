package vectorwing.farmersdelight.legacy;

import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

public final class CuttingDispenser extends BehaviorDefaultDispenseItem {
    private final IBehaviorDispenseItem previous;
    private CuttingDispenser(IBehaviorDispenseItem previous) { this.previous = previous; }
    public static void register() {
        if(!Settings.dispenserTools) return;
        for (Item item : Item.itemsList) {
            if(item==null) continue;
            ItemStack stack = new ItemStack(item);
            boolean accepted = false;
            for (CuttingRecipes.Recipe recipe : CuttingRecipes.ALL) if (recipe.matchesTool(stack)) { accepted = true; break; }
            if (accepted) BlockDispenser.dispenseBehaviorRegistry.putObject(item,
                    new CuttingDispenser((IBehaviorDispenseItem) BlockDispenser.dispenseBehaviorRegistry.getObject(item)));
        }
    }
    @Override protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
        EnumFacing facing = BlockDispenser.getFacing(source.getBlockMetadata());
        TileEntity tile = source.getWorld().getBlockTileEntity(source.getXInt() + facing.getFrontOffsetX(), source.getYInt() + facing.getFrontOffsetY(), source.getZInt() + facing.getFrontOffsetZ());
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
