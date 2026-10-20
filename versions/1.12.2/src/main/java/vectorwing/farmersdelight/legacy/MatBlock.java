package vectorwing.farmersdelight.legacy;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.world.IBlockAccess;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
public final class MatBlock extends Block {
    public final String id;
    public final boolean full,rug;
    public static final net.minecraft.block.properties.PropertyDirection FACING=net.minecraft.block.BlockHorizontal.FACING;
    public static final net.minecraft.block.properties.PropertyBool HEAD=net.minecraft.block.properties.PropertyBool.create("head");
    public MatBlock(String id) {
        super(Material.CLOTH); this.id=id; full=id.equals("full_tatami_mat"); rug=id.equals("canvas_rug");
        setCreativeTab(Content.TAB); setHardness(rug ? 0.2F : 0.3F);
        setTranslationKey("farmersdelight."+id); setDefaultState(blockState.getBaseState().withProperty(FACING,EnumFacing.SOUTH).withProperty(HEAD,false));
    }
    public boolean isOpaqueCube(IBlockState state) { return false; }
    public boolean isFullCube(IBlockState state) { return false; }
    public boolean canPlaceBlockAt(World world,BlockPos pos) {
        return super.canPlaceBlockAt(world,pos) && (rug || !world.isAirBlock(pos.down()));
    }
    protected BlockStateContainer createBlockState() { return new BlockStateContainer(this,FACING,HEAD); }
    public int getMetaFromState(IBlockState state) { return state.getValue(FACING).getHorizontalIndex()|(state.getValue(HEAD) ? 4 : 0); }
    public IBlockState getStateFromMeta(int meta) { return getDefaultState().withProperty(FACING,EnumFacing.byHorizontalIndex(meta&3)).withProperty(HEAD,full && (meta&4)!=0); }
    public IBlockState getStateForPlacement(World world,BlockPos pos,EnumFacing side,float x,float y,float z,int meta,net.minecraft.entity.EntityLivingBase placer) { return getDefaultState().withProperty(FACING,placer.getHorizontalFacing().getOpposite()); }
    public net.minecraft.util.math.AxisAlignedBB getBoundingBox(IBlockState state,IBlockAccess world,BlockPos pos) { return new net.minecraft.util.math.AxisAlignedBB(0,0,0,1,rug ? 0.0625 : 0.125,1); }
    public net.minecraft.block.material.EnumPushReaction getPushReaction(IBlockState state) { return net.minecraft.block.material.EnumPushReaction.DESTROY; }
    public Item getItemDropped(IBlockState state,java.util.Random random,int fortune) { return full && !state.getValue(HEAD) ? net.minecraft.init.Items.AIR : Item.getItemFromBlock(this); }
    public void neighborChanged(IBlockState state,World world,BlockPos pos,Block changed,BlockPos from) {
        if(world.isRemote) return;
        boolean valid=rug || !world.isAirBlock(pos.down());
        if(full) {
            BlockPos other=pos.offset(state.getValue(HEAD) ? state.getValue(FACING).getOpposite() : state.getValue(FACING));
            IBlockState partner=world.getBlockState(other);
            valid &= partner.getBlock()==this && partner.getValue(HEAD)!=state.getValue(HEAD) && partner.getValue(FACING)==state.getValue(FACING);
        }
        if(!valid) { dropBlockAsItem(world,pos,state,0); world.setBlockToAir(pos); }
    }
    public void onBlockHarvested(World world,BlockPos pos,IBlockState state,EntityPlayer player) {
        if(full && player.capabilities.isCreativeMode) {
            BlockPos other=pos.offset(state.getValue(HEAD) ? state.getValue(FACING).getOpposite() : state.getValue(FACING));
            if(world.getBlockState(other).getBlock()==this) world.setBlockState(other,net.minecraft.init.Blocks.AIR.getDefaultState(),2);
        }
        super.onBlockHarvested(world,pos,state,player);
    }
    public static final int[] DX={0,-1,0,1},DZ={1,0,-1,0};
}

