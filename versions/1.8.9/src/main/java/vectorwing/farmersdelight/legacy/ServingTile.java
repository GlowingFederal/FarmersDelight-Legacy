package vectorwing.farmersdelight.legacy;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;

public final class ServingTile extends TileEntity {
    public int remaining=4;

    public void take() {
        remaining=Math.max(0,remaining-1); markDirty();
        worldObj.markBlockForUpdate(pos);
        worldObj.updateComparatorOutputLevel(pos,getBlockType());
    }
    public void readFromNBT(NBTTagCompound nbt) { super.readFromNBT(nbt); remaining=nbt.hasKey("Servings") ? Math.max(0,Math.min(4,nbt.getInteger("Servings"))) : 4; }
    public void writeToNBT(NBTTagCompound nbt) { super.writeToNBT(nbt); nbt.setInteger("Servings",remaining);  }
    public NBTTagCompound getUpdateTag() { NBTTagCompound data=new NBTTagCompound();writeToNBT(data);return data; }
    public net.minecraft.network.Packet getDescriptionPacket() { return new S35PacketUpdateTileEntity(pos,0,getUpdateTag()); }
    public void onDataPacket(NetworkManager manager,S35PacketUpdateTileEntity packet) { readFromNBT(packet.getNbtCompound()); worldObj.markBlockRangeForRenderUpdate(pos,pos); }
}

