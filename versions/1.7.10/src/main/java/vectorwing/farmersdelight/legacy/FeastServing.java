package vectorwing.farmersdelight.legacy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/** Shared server-side hand-to-inventory transaction for placed meals. */
public final class FeastServing {
    private FeastServing() {}

    /** The caller has validated the hand, serving, and authoritative portion state. */
    public static void give(World world,int x,int y,int z,EntityPlayer player,ItemStack portion) {
        if (world.isRemote || Stacks.empty(portion)) return;
        ItemStack held = player.getHeldItem();
        if(!player.capabilities.isCreativeMode) {
            Stacks.shrink(held,1);
            if(Stacks.empty(held)) player.inventory.setInventorySlotContents(player.inventory.currentItem,null);
        }
        // Creative inventory insertion silently discards a portion when there is no room.
        if((player.capabilities.isCreativeMode && !hasRoomForPortion(player,portion))
                || !player.inventory.addItemStackToInventory(portion)) player.dropPlayerItemWithRandomChoice(portion,false);
        player.inventory.markDirty();
        // Send changed slots before vanilla's interaction handler can cache them.
        player.openContainer.detectAndSendChanges();
        world.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, "random.pop", 1.0F, 1.0F);
    }

    private static boolean hasRoomForPortion(EntityPlayer player, ItemStack portion) {
        if(player.inventory.getFirstEmptyStack()>=0) return true;
        for(ItemStack stack : player.inventory.mainInventory) {
            if(Stacks.same(stack,portion) && stack.isStackable()
                    && stack.stackSize<Math.min(stack.getMaxStackSize(),player.inventory.getInventoryStackLimit())) return true;
        }
        return false;
    }
}
