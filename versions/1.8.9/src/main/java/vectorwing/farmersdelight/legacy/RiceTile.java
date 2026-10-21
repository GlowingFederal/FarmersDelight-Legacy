package vectorwing.farmersdelight.legacy;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;

/** Growth is separate from fluid metadata, so the planted base remains exactly one water source. */
public final class RiceTile extends TileEntity {
    public int age;
    public boolean draining;

    public void age(int value) {
        age=Math.max(0,Math.min(3,value)); markDirty();
        if(worldObj!=null && !worldObj.isRemote)
            worldObj.markBlockForUpdate(pos);
    }
    public void readFromNBT(NBTTagCompound nbt) { super.readFromNBT(nbt); age=Math.max(0,Math.min(3,nbt.getInteger("Age"))); draining=false; }
    public void writeToNBT(NBTTagCompound nbt) { super.writeToNBT(nbt); nbt.setInteger("Age",age);  }
    public NBTTagCompound getUpdateTag() { NBTTagCompound data=new NBTTagCompound();writeToNBT(data);return data; }
    public net.minecraft.network.Packet getDescriptionPacket() { return new S35PacketUpdateTileEntity(pos,0,getUpdateTag()); }
    public void onDataPacket(NetworkManager manager,S35PacketUpdateTileEntity packet) { readFromNBT(packet.getNbtCompound()); worldObj.markBlockRangeForRenderUpdate(pos,pos); }
}
