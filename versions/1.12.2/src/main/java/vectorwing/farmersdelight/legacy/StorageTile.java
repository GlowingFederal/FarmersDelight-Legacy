package vectorwing.farmersdelight.legacy;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.entity.player.EntityPlayer;

public class StorageTile extends InventoryTile {
    private String customName="";
    public int users;
    public StorageTile() { super(27); }

    public String getName() { return customName.isEmpty() ? "container.farmersdelight."+(getBlockType()==Content.basket ? "basket" : "pantry") : customName; }
    public boolean hasCustomName() { return !customName.isEmpty(); }
    public net.minecraft.util.text.ITextComponent getDisplayName() { return hasCustomName() ? new net.minecraft.util.text.TextComponentString(customName) : super.getDisplayName(); }
    public void name(String value) { customName=value; markDirty(); }
    public void openInventory(EntityPlayer player) { if(player.isSpectator()) return; viewer(1); }
    public void closeInventory(EntityPlayer player) { if(player.isSpectator()) return; viewer(-1); }
    private void viewer(int change) {
        if(world==null || world.isRemote) return;
        boolean was=users>0; users=Math.max(0,users+change);
        if(was!=(users>0)) {
            changed();
            world.playSound(null,pos,users>0 ? net.minecraft.init.SoundEvents.BLOCK_CHEST_OPEN : net.minecraft.init.SoundEvents.BLOCK_CHEST_CLOSE,net.minecraft.util.SoundCategory.BLOCKS,0.5F,1F);
        }
    }
    public void readFromNBT(NBTTagCompound nbt) { super.readFromNBT(nbt); customName=nbt.getString("CustomName"); users=0; }
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) { super.writeToNBT(nbt); if(!customName.isEmpty()) nbt.setString("CustomName",customName); return nbt; }
    public net.minecraft.network.play.server.SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound nbt=getUpdateTag(); nbt.setBoolean("Open",users>0);
        return new net.minecraft.network.play.server.SPacketUpdateTileEntity(pos,0,nbt);
    }
    public void onDataPacket(net.minecraft.network.NetworkManager manager,net.minecraft.network.play.server.SPacketUpdateTileEntity packet) {
        super.onDataPacket(manager,packet); users=packet.getNbtCompound().getBoolean("Open") ? 1 : 0; world.markBlockRangeForRenderUpdate(pos,pos);
    }
}

