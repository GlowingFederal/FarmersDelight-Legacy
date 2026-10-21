package vectorwing.farmersdelight.legacy;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet; import net.minecraft.network.packet.Packet132TileEntityData;

/** Growth is separate from fluid metadata, so the planted base remains exactly one water source. */
public final class RiceTile extends TileEntity {
    public int age;
    public boolean draining;
    public boolean canUpdate() { return false; }
    public void age(int value) {
        age=Math.max(0,Math.min(3,value)); onInventoryChanged();
        if(worldObj!=null && !worldObj.isRemote)
            worldObj.markBlockForUpdate(xCoord,yCoord,zCoord);
    }
    public void readFromNBT(NBTTagCompound nbt) { super.readFromNBT(nbt); age=Math.max(0,Math.min(3,nbt.getInteger("Age"))); draining=false; }
    public void writeToNBT(NBTTagCompound nbt) { super.writeToNBT(nbt); nbt.setInteger("Age",age);  }
    public Packet getDescriptionPacket() { NBTTagCompound nbt=new NBTTagCompound(); writeToNBT(nbt); return new Packet132TileEntityData(xCoord,yCoord,zCoord,0,nbt); }
    public void onDataPacket(INetworkManager manager,Packet132TileEntityData packet) { readFromNBT(packet.data); worldObj.markBlockForUpdate(xCoord,yCoord,zCoord); }
}
