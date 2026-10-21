package vectorwing.farmersdelight.legacy;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import vectorwing.farmersdelight.legacy.api.SourceRecipes;

public final class CuttingBoardTile extends InventoryTile {
    public boolean carved;
    public CuttingBoardTile() { super(1); }
    @Override public int getInventoryStackLimit() { return 1; }
    @Override public void setInventorySlotContents(int slot, ItemStack stack) { carved = false; super.setInventorySlotContents(slot, stack); }
    public boolean process(ItemStack tool, EntityPlayer player) {
        if (worldObj == null || worldObj.isRemote) return false;
        CuttingRecipes.Recipe recipe = CuttingRecipes.find(inventory[0], tool);
        if (recipe == null) {
            if(player!=null) {
                boolean cuttable=false;
                for(CuttingRecipes.Recipe candidate:CuttingRecipes.ALL) if(candidate.matchesInput(inventory[0])) { cuttable=true; break; }
                player.addChatMessage(new net.minecraft.util.ChatComponentTranslation("farmersdelight.block.cutting_board."+(cuttable ? "invalid_tool" : "invalid_item")));
            }
            return false;
        }
        int fortune = EnchantmentHelper.getEnchantmentLevel(net.minecraft.enchantment.Enchantment.fortune.effectId, tool);
        // Remove exactly one input before emitting outputs. Retrieval and break paths clear their slot once.
        decrStackSize(0, 1); carved = false;
        for (int i = 0; i < recipe.outputs.size(); i++) {
            ItemStack output = recipe.outputs.get(i);
            int amount = SourceRecipes.roll(Stacks.size(output), recipe.chances.get(i), fortune, Settings.cuttingFortuneBonus, worldObj.rand);
            if (amount > 0) drop(Stacks.copy(output, amount));
        }
        if (player == null) {
            if (tool.attemptDamageItem(1, worldObj.rand)) Stacks.shrink(tool, 1);
        } else if (!player.capabilities.isCreativeMode) tool.damageItem(1, player);
        worldObj.playSoundEffect(pos.getX()+0.5,pos.getY()+0.5,pos.getZ()+0.5,recipe.tool.equals("knife") ? "farmersdelight:block.cutting_board.knife" : "dig.wood", 0.8F, 1F);
        worldObj.addBlockEvent(pos, getBlockType(), 1, 0);
        if(player!=null) Progression.grant(player,"use_cutting_board","cutting_board");
        changed(); return true;
    }
    public void drop(ItemStack stack) {
        if (Stacks.empty(stack) || worldObj.isRemote) return;
        EntityItem item = new EntityItem(worldObj, pos.getX() + 0.5, pos.getY() + 0.2, pos.getZ() + 0.5, stack);
        item.motionX = 0; item.motionY = 0.1; item.motionZ = 0;
        worldObj.spawnEntityInWorld(item);
    }
    @Override public boolean receiveClientEvent(int id, int parameter) {
        if (id == 1) {
            for (int i = 0; i < 5; i++) worldObj.spawnParticle(net.minecraft.util.EnumParticleTypes.BLOCK_CRACK,
                    pos.getX() + 0.5, pos.getY() + 0.12, pos.getZ() + 0.5, (worldObj.rand.nextDouble() - 0.5) * 0.15, 0.1, (worldObj.rand.nextDouble() - 0.5) * 0.15,
                    net.minecraft.block.Block.getStateId(net.minecraft.init.Blocks.planks.getDefaultState()));
            return true;
        }
        return super.receiveClientEvent(id, parameter);
    }
    @Override public void readFromNBT(NBTTagCompound nbt) { super.readFromNBT(nbt); carved = nbt.getBoolean("IsItemCarved"); }
    @Override public void writeToNBT(NBTTagCompound nbt) { super.writeToNBT(nbt); nbt.setBoolean("IsItemCarved", carved);  }
}
