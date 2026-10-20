package vectorwing.farmersdelight.legacy;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;

public final class ServingTile extends TileEntity {
    public int remaining=4;

    public void take() {
        remaining=Math.max(0,remaining-1); markDirty();
        world.notifyBlockUpdate(pos,world.getBlockState(pos),world.getBlockState(pos),3);
        world.updateComparatorOutputLevel(pos,getBlockType());
    }
    public void readFromNBT(NBTTagCompound nbt) { super.readFromNBT(nbt); remaining=nbt.hasKey("Servings") ? Math.max(0,Math.min(4,nbt.getInteger("Servings"))) : 4; }
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) { super.writeToNBT(nbt); nbt.setInteger("Servings",remaining); return nbt; }
    public NBTTagCompound getUpdateTag() { return writeToNBT(new NBTTagCompound()); }
    public SPacketUpdateTileEntity getUpdatePacket() { return new SPacketUpdateTileEntity(pos,0,getUpdateTag()); }
    public void onDataPacket(NetworkManager manager,SPacketUpdateTileEntity packet) { readFromNBT(packet.getNbtCompound()); world.markBlockRangeForRenderUpdate(pos,pos); }
}

