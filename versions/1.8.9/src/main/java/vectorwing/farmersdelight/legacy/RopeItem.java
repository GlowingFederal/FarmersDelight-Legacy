package vectorwing.farmersdelight.legacy;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
public final class RopeItem extends ItemBlock {
    public RopeItem(Block block) { super(block); }
    public boolean onItemUse(ItemStack stack,EntityPlayer player,World world,net.minecraft.util.BlockPos pos,net.minecraft.util.EnumFacing face,float hx,float hy,float hz) {
        if(world.getBlockState(pos).getBlock()!=Content.rope) return super.onItemUse(stack,player,world,pos,face,hx,hy,hz);
        net.minecraft.util.EnumFacing direction=player.isSneaking() ? face : net.minecraft.util.EnumFacing.DOWN;
        net.minecraft.util.BlockPos target=pos.offset(direction);
        int steps=0;
        while(target.getY()>0 && target.getY()<256 && world.isBlockLoaded(target) && world.getBlockState(target).getBlock()==Content.rope && direction==net.minecraft.util.EnumFacing.DOWN && steps++<256) target=target.down();
        
        if(target.getY()<=0 || target.getY()>=256 || !world.isBlockLoaded(target) || !world.getBlockState(target).getBlock().isReplaceable(world,target) || !player.canPlayerEdit(target,direction,stack)) return false;
        if(!world.isRemote) { world.setBlockState(target,Content.rope.getDefaultState(),3); if(!player.capabilities.isCreativeMode) Stacks.shrink(stack,1); }
        return true;
    }
}

