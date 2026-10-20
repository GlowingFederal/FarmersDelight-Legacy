package vectorwing.farmersdelight.legacy;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
public final class FullMatItem extends ItemBlock {
    public FullMatItem(Block block) { super(block); }
    public net.minecraft.util.EnumActionResult onItemUse(EntityPlayer player,World world,net.minecraft.util.math.BlockPos pos,net.minecraft.util.EnumHand hand,net.minecraft.util.EnumFacing side,float hx,float hy,float hz) {
        ItemStack stack=player.getHeldItem(hand);
        if(!world.getBlockState(pos).getBlock().isReplaceable(world,pos)) pos=pos.offset(side);
        net.minecraft.util.EnumFacing facing=player.getHorizontalFacing(); net.minecraft.util.math.BlockPos pair=pos.offset(facing);
        if(!world.isBlockLoaded(pair) || !world.getBlockState(pos).getBlock().isReplaceable(world,pos) || !world.getBlockState(pair).getBlock().isReplaceable(world,pair)
                || !player.canPlayerEdit(pos,side,stack) || !player.canPlayerEdit(pair,side,stack) || world.isAirBlock(pos.down()) || world.isAirBlock(pair.down()) || pos.getY()>=256) return net.minecraft.util.EnumActionResult.FAIL;
        if(!world.isRemote) {
            net.minecraft.block.state.IBlockState state=Content.fullMat.getDefaultState().withProperty(MatBlock.FACING,facing).withProperty(MatBlock.HEAD,false);
            world.setBlockState(pos,state,2); world.setBlockState(pair,state.withProperty(MatBlock.HEAD,true),2);
            world.notifyNeighborsOfStateChange(pos,Content.fullMat,false); world.notifyNeighborsOfStateChange(pair,Content.fullMat,false);
            if(!player.capabilities.isCreativeMode) Stacks.shrink(stack,1);
        }
        return net.minecraft.util.EnumActionResult.SUCCESS;
    }
}

