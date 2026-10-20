package vectorwing.farmersdelight.legacy;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public final class BasketTile extends StorageTile  {
    private int cooldown;
    public boolean canUpdate() { return true; }
    public void updateEntity() {
        if(worldObj==null || worldObj.isRemote) return;
        if(cooldown>0) { cooldown--; return; }
        if(worldObj.isBlockIndirectlyGettingPowered(xCoord,yCoord,zCoord)) return;
        int face=worldObj.getBlockMetadata(xCoord,yCoord,zCoord);
        net.minecraftforge.common.util.ForgeDirection direction=net.minecraftforge.common.util.ForgeDirection.getOrientation(face);
        int dx=direction.offsetX,dy=direction.offsetY,dz=direction.offsetZ;
        double x=xCoord,y=yCoord,z=zCoord;
        net.minecraft.util.AxisAlignedBB box=net.minecraft.util.AxisAlignedBB.getBoundingBox(x+Math.min(0,dx),y+Math.min(0,dy),z+Math.min(0,dz),x+1+Math.max(0,dx),y+1+Math.max(0,dy),z+1+Math.max(0,dz));
        for(Object object:worldObj.getEntitiesWithinAABB(EntityItem.class,box)) {
            EntityItem entity=(EntityItem)object; if(entity.isDead) continue;
            ItemStack stack=entity.getEntityItem();
            int before=Stacks.size(stack);
            for(int slot=0;slot<inventory.length && !Stacks.empty(stack);slot++) {
                ItemStack stored=inventory[slot];
                if(!Stacks.empty(stored) && !(stored.isItemEqual(stack) && ItemStack.areItemStackTagsEqual(stored,stack))) continue;
                int room=Math.max(0,Math.min(getInventoryStackLimit(),stack.getMaxStackSize())-Stacks.size(stored));
                int moved=Math.min(room,Stacks.size(stack)); if(moved==0) continue;
                if(Stacks.empty(stored)) inventory[slot]=Stacks.copy(stack,moved); else Stacks.count(stored,Stacks.size(stored)+moved);
                Stacks.shrink(stack,moved);
            }
            if(Stacks.size(stack)<before) {
                if(Stacks.empty(stack)) entity.setDead(); else entity.setEntityItemStack(stack);
                cooldown=8; changed(); break;
            }
        }
    }
    public void readFromNBT(NBTTagCompound nbt) { super.readFromNBT(nbt); cooldown=Math.max(0,nbt.getInteger("TransferCooldown")); }
    public void writeToNBT(NBTTagCompound nbt) { super.writeToNBT(nbt); nbt.setInteger("TransferCooldown",cooldown);  }
}

