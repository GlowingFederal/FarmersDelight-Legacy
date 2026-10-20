package vectorwing.farmersdelight.legacy;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
public final class TatamiSolid extends Block {
    public static final net.minecraft.block.properties.PropertyDirection FACING=net.minecraft.block.properties.PropertyDirection.create("facing");
    public static final net.minecraft.block.properties.PropertyBool PAIRED=net.minecraft.block.properties.PropertyBool.create("paired");
    public TatamiSolid() {
        super(Material.CLOTH); setHardness(0.8F); setCreativeTab(Content.TAB);
        setTranslationKey("farmersdelight.tatami"); setDefaultState(blockState.getBaseState().withProperty(FACING,EnumFacing.DOWN).withProperty(PAIRED,false));
    }
    protected BlockStateContainer createBlockState() { return new BlockStateContainer(this,FACING,PAIRED); }
    public int getMetaFromState(IBlockState state) { return state.getValue(FACING).getIndex()|(state.getValue(PAIRED) ? 8 : 0); }
    public IBlockState getStateFromMeta(int meta) { return getDefaultState().withProperty(FACING,EnumFacing.byIndex(meta&7)).withProperty(PAIRED,(meta&8)!=0); }
    public IBlockState getStateForPlacement(World world,BlockPos pos,EnumFacing side,float x,float y,float z,int meta,EntityLivingBase placer) {
        EnumFacing facing=side.getOpposite(); IBlockState other=world.getBlockState(pos.offset(facing));
        return getDefaultState().withProperty(FACING,facing).withProperty(PAIRED,!placer.isSneaking() && other.getBlock()==this && !other.getValue(PAIRED));
    }
    public void onBlockPlacedBy(World world,BlockPos pos,IBlockState state,EntityLivingBase placer,ItemStack stack) {
        if(world.isRemote || placer.isSneaking()) return;
        EnumFacing facing=state.getValue(FACING); BlockPos target=pos.offset(facing); IBlockState other=world.getBlockState(target);
        if(other.getBlock()==this && !other.getValue(PAIRED)) {
            world.setBlockState(target,state.withProperty(FACING,facing.getOpposite()).withProperty(PAIRED,true),3);
            world.setBlockState(pos,state.withProperty(PAIRED,true),3);
        }
    }
    public void neighborChanged(IBlockState state,World world,BlockPos pos,Block block,BlockPos from) {
        if(!world.isRemote && state.getValue(PAIRED) && world.getBlockState(pos.offset(state.getValue(FACING))).getBlock()!=this) world.setBlockState(pos,state.withProperty(PAIRED,false),3);
    }
}

