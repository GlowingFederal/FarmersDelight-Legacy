package vectorwing.farmersdelight.legacy;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
public final class FullMatItem extends ItemBlock {
    public FullMatItem(Block block) { super(block); }
    public boolean onItemUse(ItemStack stack,EntityPlayer player,World world,int x,int y,int z,int side,float hx,float hy,float hz) {
        if(!world.getBlock(x,y,z).isReplaceable(world,x,y,z)) {
            net.minecraftforge.common.util.ForgeDirection face=net.minecraftforge.common.util.ForgeDirection.getOrientation(side); x+=face.offsetX; y+=face.offsetY; z+=face.offsetZ;
        }
        int direction=net.minecraft.util.MathHelper.floor_double(player.rotationYaw*4/360+0.5)&3,nx=x+MatBlock.DX[direction],nz=z+MatBlock.DZ[direction];
        if(y<0 || y>=256 || !world.blockExists(nx,y,nz) || !world.getBlock(x,y,z).isReplaceable(world,x,y,z) || !world.getBlock(nx,y,nz).isReplaceable(world,nx,y,nz)
                || !player.canPlayerEdit(x,y,z,side,stack) || !player.canPlayerEdit(nx,y,nz,side,stack) || world.isAirBlock(x,y-1,z) || world.isAirBlock(nx,y-1,nz)) return false;
        if(!world.isRemote) {
            world.setBlock(x,y,z,Content.fullMat,direction,2); world.setBlock(nx,y,nz,Content.fullMat,direction|4,2);
            world.notifyBlocksOfNeighborChange(x,y,z,Content.fullMat); world.notifyBlocksOfNeighborChange(nx,y,nz,Content.fullMat);
            if(!player.capabilities.isCreativeMode) Stacks.shrink(stack,1);
        }
        return true;
    }
}

