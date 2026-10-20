package vectorwing.farmersdelight.legacy;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public final class BasketTile extends StorageTile implements net.minecraft.util.ITickable {
    private int cooldown;

    public void update() {
        if(world==null || world.isRemote) return;
        if(cooldown>0) { cooldown--; return; }
        if(world.isBlockPowered(pos)) return;
        int face=world.getBlockState(pos).getValue(StorageBlock.FACING).getIndex();
        net.minecraft.util.EnumFacing direction=net.minecraft.util.EnumFacing.byIndex(face);
        int dx=direction.getXOffset(),dy=direction.getYOffset(),dz=direction.getZOffset();
        double x=pos.getX(),y=pos.getY(),z=pos.getZ();
        net.minecraft.util.math.AxisAlignedBB box=new net.minecraft.util.math.AxisAlignedBB(x+Math.min(0,dx),y+Math.min(0,dy),z+Math.min(0,dz),x+1+Math.max(0,dx),y+1+Math.max(0,dy),z+1+Math.max(0,dz));
        for(Object object:world.getEntitiesWithinAABB(EntityItem.class,box)) {
            EntityItem entity=(EntityItem)object; if(entity.isDead) continue;
            ItemStack stack=entity.getItem();
            int before=Stacks.size(stack);
            for(int slot=0;slot<inventory.length && !Stacks.empty(stack);slot++) {
                ItemStack stored=inventory[slot];
                if(!Stacks.empty(stored) && !(ItemStack.areItemsEqual(stored,stack) && ItemStack.areItemStackTagsEqual(stored,stack))) continue;
                int room=Math.max(0,Math.min(getInventoryStackLimit(),stack.getMaxStackSize())-Stacks.size(stored));
                int moved=Math.min(room,Stacks.size(stack)); if(moved==0) continue;
                if(Stacks.empty(stored)) inventory[slot]=Stacks.copy(stack,moved); else Stacks.count(stored,Stacks.size(stored)+moved);
                Stacks.shrink(stack,moved);
            }
            if(Stacks.size(stack)<before) {
                if(Stacks.empty(stack)) entity.setDead(); else entity.setItem(stack);
                cooldown=8; changed(); break;
            }
        }
    }
    public void readFromNBT(NBTTagCompound nbt) { super.readFromNBT(nbt); cooldown=Math.max(0,nbt.getInteger("TransferCooldown")); }
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) { super.writeToNBT(nbt); nbt.setInteger("TransferCooldown",cooldown); return nbt; }
}

