package vectorwing.farmersdelight.legacy;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;

public final class ServingTile extends TileEntity {
    public int remaining=4;
    public boolean canUpdate() { return false; }
    public void take() {
        remaining=Math.max(0,remaining-1); markDirty();
        worldObj.markBlockForUpdate(xCoord,yCoord,zCoord);
        worldObj.func_147453_f(xCoord,yCoord,zCoord,getBlockType());
    }
    public void readFromNBT(NBTTagCompound nbt) { super.readFromNBT(nbt); remaining=nbt.hasKey("Servings") ? Math.max(0,Math.min(4,nbt.getInteger("Servings"))) : 4; }
    public void writeToNBT(NBTTagCompound nbt) { super.writeToNBT(nbt); nbt.setInteger("Servings",remaining);  }
    public net.minecraft.network.Packet getDescriptionPacket() { NBTTagCompound nbt=new NBTTagCompound(); writeToNBT(nbt); return new S35PacketUpdateTileEntity(xCoord,yCoord,zCoord,0,nbt); }
    public void onDataPacket(NetworkManager manager,S35PacketUpdateTileEntity packet) { readFromNBT(packet.func_148857_g()); worldObj.markBlockForUpdate(xCoord,yCoord,zCoord); }
}

