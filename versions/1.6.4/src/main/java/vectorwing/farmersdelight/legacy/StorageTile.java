package vectorwing.farmersdelight.legacy;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.entity.player.EntityPlayer;

public class StorageTile extends InventoryTile {
    private String customName="";
    public int users;
    public StorageTile() { super(27); }
    public boolean canUpdate() { return false; }
    public String getInvName() { return customName.isEmpty() ? "container.farmersdelight."+(getBlockType()==Content.basket ? "basket" : "pantry") : customName; }
    public boolean isInvNameLocalized() { return !customName.isEmpty(); }

    public void name(String value) { customName=value; onInventoryChanged(); }
    public void openChest() { viewer(1); }
    public void closeChest() { viewer(-1); }
    private void viewer(int change) {
        if(worldObj==null || worldObj.isRemote) return;
        boolean was=users>0; users=Math.max(0,users+change);
        if(was!=(users>0)) {
            changed();
            worldObj.playSoundEffect(xCoord+0.5,yCoord+0.5,zCoord+0.5,users>0 ? "random.chestopen" : "random.chestclosed",0.5F,1F);
        }
    }
    public void readFromNBT(NBTTagCompound nbt) { super.readFromNBT(nbt); customName=nbt.getString("CustomName"); users=0; }
    public void writeToNBT(NBTTagCompound nbt) { super.writeToNBT(nbt); if(!customName.isEmpty()) nbt.setString("CustomName",customName);  }
    public net.minecraft.network.packet.Packet getDescriptionPacket() {
        NBTTagCompound nbt=new NBTTagCompound(); writeToNBT(nbt); nbt.setBoolean("Open",users>0);
        return new net.minecraft.network.packet.Packet132TileEntityData(xCoord,yCoord,zCoord,0,nbt);
    }
    public void onDataPacket(net.minecraft.network.INetworkManager manager,net.minecraft.network.packet.Packet132TileEntityData packet) {
        super.onDataPacket(manager,packet); users=packet.data.getBoolean("Open") ? 1 : 0; worldObj.markBlockForUpdate(xCoord,yCoord,zCoord);
    }
}

