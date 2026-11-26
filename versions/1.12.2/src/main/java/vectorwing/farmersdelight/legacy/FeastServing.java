package vectorwing.farmersdelight.legacy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/** Shared server-side hand-to-inventory transaction for placed meals. */
public final class FeastServing {
    private FeastServing() {}

    /** The caller has validated the hand, serving, and authoritative portion state. */
    public static void give(World world,BlockPos pos,EntityPlayer player,EnumHand hand,ItemStack portion) {
        if (world.isRemote || Stacks.empty(portion)) return;
        ItemStack held = player.getHeldItem(hand);
        if(!player.capabilities.isCreativeMode) {
            Stacks.shrink(held,1);
            if(held.isEmpty()) player.setHeldItem(hand,ItemStack.EMPTY);
        }
        // Creative inventory insertion silently discards a portion when there is no room.
        if((player.capabilities.isCreativeMode && (player.inventory.getFirstEmptyStack()<0 && player.inventory.storeItemStack(portion)<0))
                || !player.inventory.addItemStackToInventory(portion)) player.dropItem(portion,false);
        player.inventory.markDirty();
        // Send changed slots before vanilla's interaction handler can cache them.
        player.openContainer.detectAndSendChanges();
        world.playSound(null, pos, net.minecraft.init.SoundEvents.ITEM_ARMOR_EQUIP_GENERIC,
                net.minecraft.util.SoundCategory.BLOCKS, 1.0F, 1.0F);
    }
}
