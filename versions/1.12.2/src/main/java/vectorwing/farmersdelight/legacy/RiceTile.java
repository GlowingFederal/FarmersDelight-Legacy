package vectorwing.farmersdelight.legacy;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;

/** Growth is separate from fluid metadata, so the planted base remains exactly one water source. */
public final class RiceTile extends TileEntity {
    public int age;
    public boolean draining;

    public void age(int value) {
        age=Math.max(0,Math.min(3,value)); markDirty();
        if(world!=null && !world.isRemote)
            world.notifyBlockUpdate(pos,world.getBlockState(pos),world.getBlockState(pos),3);
    }
    public void readFromNBT(NBTTagCompound nbt) { super.readFromNBT(nbt); age=Math.max(0,Math.min(3,nbt.getInteger("Age"))); draining=false; }
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) { super.writeToNBT(nbt); nbt.setInteger("Age",age); return nbt; }
    public NBTTagCompound getUpdateTag() { return writeToNBT(new NBTTagCompound()); }
    public SPacketUpdateTileEntity getUpdatePacket() { return new SPacketUpdateTileEntity(pos,0,getUpdateTag()); }
    public void onDataPacket(NetworkManager manager,SPacketUpdateTileEntity packet) { readFromNBT(packet.getNbtCompound()); world.markBlockRangeForRenderUpdate(pos,pos); }
}
