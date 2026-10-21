package vectorwing.farmersdelight.legacy;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntitySign;

public final class CanvasSignTile extends TileEntitySign {
    public int textColor=0x000000;
    public boolean canUpdate() { return false; }
    public void readFromNBT(NBTTagCompound nbt) { super.readFromNBT(nbt); textColor=nbt.hasKey("TextColor") ? nbt.getInteger("TextColor")&0xFFFFFF : 0; }
    public void writeToNBT(NBTTagCompound nbt) { super.writeToNBT(nbt); nbt.setInteger("TextColor",textColor);  }
    public net.minecraft.network.packet.Packet getDescriptionPacket() { NBTTagCompound nbt=new NBTTagCompound(); writeToNBT(nbt); return new net.minecraft.network.packet.Packet132TileEntityData(xCoord,yCoord,zCoord,0,nbt); }
    public void onDataPacket(net.minecraft.network.INetworkManager manager,net.minecraft.network.packet.Packet132TileEntityData packet) { readFromNBT(packet.data); }
}

