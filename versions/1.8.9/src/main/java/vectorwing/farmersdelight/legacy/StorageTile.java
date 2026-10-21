package vectorwing.farmersdelight.legacy;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.entity.player.EntityPlayer;

public class StorageTile extends InventoryTile {
    private String customName="";
    public int users;
    public StorageTile() { super(27); }

    public String getName() { return customName.isEmpty() ? "container.farmersdelight."+(getBlockType()==Content.basket ? "basket" : "pantry") : customName; }
    public boolean hasCustomName() { return !customName.isEmpty(); }
    public net.minecraft.util.IChatComponent getDisplayName() { return hasCustomName() ? new net.minecraft.util.ChatComponentText(customName) : super.getDisplayName(); }
    public void name(String value) { customName=value; markDirty(); }
    public void openInventory(EntityPlayer player) { if(player.isSpectator()) return; viewer(1); }
    public void closeInventory(EntityPlayer player) { if(player.isSpectator()) return; viewer(-1); }
    private void viewer(int change) {
        if(worldObj==null || worldObj.isRemote) return;
        boolean was=users>0; users=Math.max(0,users+change);
        if(was!=(users>0)) {
            changed();
            worldObj.playSoundEffect(pos.getX()+0.5,pos.getY()+0.5,pos.getZ()+0.5,users>0 ? "random.chestopen" : "random.chestclosed",0.5F,1F);
        }
    }
    public void readFromNBT(NBTTagCompound nbt) { super.readFromNBT(nbt); customName=nbt.getString("CustomName"); users=0; }
    public void writeToNBT(NBTTagCompound nbt) { super.writeToNBT(nbt); if(!customName.isEmpty()) nbt.setString("CustomName",customName);  }
    public net.minecraft.network.play.server.S35PacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound nbt=getUpdateTag(); nbt.setBoolean("Open",users>0);
        return new net.minecraft.network.play.server.S35PacketUpdateTileEntity(pos,0,nbt);
    }
    public void onDataPacket(net.minecraft.network.NetworkManager manager,net.minecraft.network.play.server.S35PacketUpdateTileEntity packet) {
        super.onDataPacket(manager,packet); users=packet.getNbtCompound().getBoolean("Open") ? 1 : 0; worldObj.markBlockRangeForRenderUpdate(pos,pos);
    }
}

