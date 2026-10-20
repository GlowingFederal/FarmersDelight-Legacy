package vectorwing.farmersdelight.legacy;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntitySign;

public final class CanvasSignTile extends TileEntitySign {
    public int textColor=0x000000;

    public void readFromNBT(NBTTagCompound nbt) { super.readFromNBT(nbt); textColor=nbt.hasKey("TextColor") ? nbt.getInteger("TextColor")&0xFFFFFF : 0; }
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) { super.writeToNBT(nbt); nbt.setInteger("TextColor",textColor); return nbt; }
    public void onDataPacket(net.minecraft.network.NetworkManager manager,net.minecraft.network.play.server.SPacketUpdateTileEntity packet) { readFromNBT(packet.getNbtCompound()); }
}

