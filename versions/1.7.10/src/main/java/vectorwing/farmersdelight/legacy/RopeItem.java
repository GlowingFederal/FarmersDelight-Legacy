package vectorwing.farmersdelight.legacy;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
public final class RopeItem extends ItemBlock {
    public RopeItem(Block block) { super(block); }
    public boolean onItemUse(ItemStack stack,EntityPlayer player,World world,int x,int y,int z,int face,float hx,float hy,float hz) {
        if(world.getBlock(x,y,z)!=Content.rope) return super.onItemUse(stack,player,world,x,y,z,face,hx,hy,hz);
        net.minecraftforge.common.util.ForgeDirection direction=net.minecraftforge.common.util.ForgeDirection.getOrientation(player.isSneaking() ? face : 0);
        x+=direction.offsetX; y+=direction.offsetY; z+=direction.offsetZ;
        int steps=0;
        while(y>0 && y<256 && world.blockExists(x,y,z) && world.getBlock(x,y,z)==Content.rope && direction==net.minecraftforge.common.util.ForgeDirection.DOWN && steps++<256) y--;
        if(y<=0 || y>=256 || !world.blockExists(x,y,z) || !world.getBlock(x,y,z).isReplaceable(world,x,y,z) || !player.canPlayerEdit(x,y,z,face,stack)) return false;
        if(!world.isRemote) { world.setBlock(x,y,z,Content.rope,0,3); if(!player.capabilities.isCreativeMode) Stacks.shrink(stack,1); }
        return true;
    }
}

