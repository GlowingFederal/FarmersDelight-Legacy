package vectorwing.farmersdelight.legacy;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet132TileEntityData;

public final class ServingTile extends TileEntity {
    public int remaining=4;
    public boolean canUpdate() { return false; }
    public void take() {
        remaining=Math.max(0,remaining-1); onInventoryChanged();
        worldObj.markBlockForUpdate(xCoord,yCoord,zCoord);
        worldObj.func_96440_m(xCoord,yCoord,zCoord,getBlockType().blockID);
    }
    public void readFromNBT(NBTTagCompound nbt) { super.readFromNBT(nbt); remaining=nbt.hasKey("Servings") ? Math.max(0,Math.min(4,nbt.getInteger("Servings"))) : 4; }
    public void writeToNBT(NBTTagCompound nbt) { super.writeToNBT(nbt); nbt.setInteger("Servings",remaining);  }
    public net.minecraft.network.packet.Packet getDescriptionPacket() { NBTTagCompound nbt=new NBTTagCompound(); writeToNBT(nbt); return new Packet132TileEntityData(xCoord,yCoord,zCoord,0,nbt); }
    public void onDataPacket(INetworkManager manager,Packet132TileEntityData packet) { readFromNBT(packet.data); worldObj.markBlockForUpdate(xCoord,yCoord,zCoord); }
}

