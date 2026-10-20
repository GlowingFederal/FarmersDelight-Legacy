package vectorwing.farmersdelight.legacy;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSign;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

public final class CanvasSignBlock extends BlockSign {
    public final boolean wall;
    public final String background,id;
    private static final int[] DYE_TEXT={0x1D1D21,0xB02E26,0x5E7C16,0x835432,0x3C44AA,0x8932B8,0x169C9C,0x9D9D97,0x474F52,0xF38BAA,0x80C71F,0xFED83D,0x3AB3DA,0xC74EBD,0xF9801D,0xF9FFFE};
    public static final net.minecraft.block.properties.PropertyInteger ROTATION=net.minecraft.block.properties.PropertyInteger.create("rotation",0,15);
    public static final net.minecraft.block.properties.PropertyDirection FACING=net.minecraft.block.BlockHorizontal.FACING;
    public CanvasSignBlock(String background,boolean wall) {
        super(); this.background=background; this.wall=wall;
        id=background.isEmpty() ? "canvas_sign" : background+"_canvas_sign";
        setTranslationKey("farmersdelight."+id); setHardness(1F);
        setDefaultState(blockState.getBaseState().withProperty(ROTATION,0).withProperty(FACING,EnumFacing.NORTH));
    }
    public TileEntity createNewTileEntity(World world,int meta) { return new CanvasSignTile(); }
    public Item getItemDropped(IBlockState state,java.util.Random random,int fortune) { return Content.ITEMS.get(id); }
    public net.minecraft.util.EnumBlockRenderType getRenderType(IBlockState state) { return net.minecraft.util.EnumBlockRenderType.ENTITYBLOCK_ANIMATED; }
    protected BlockStateContainer createBlockState() { return new BlockStateContainer(this,ROTATION,FACING); }
    public int getMetaFromState(IBlockState state) { return wall ? state.getValue(FACING).getIndex() : state.getValue(ROTATION); }
    public IBlockState getStateFromMeta(int meta) { EnumFacing face=EnumFacing.byIndex(meta); return getDefaultState().withProperty(ROTATION,meta&15).withProperty(FACING,face.getAxis()==EnumFacing.Axis.Y ? EnumFacing.NORTH : face); }
    public ItemStack getItem(World world,BlockPos pos,IBlockState state) { return new ItemStack(Content.ITEMS.get(id)); }
    public net.minecraft.util.math.AxisAlignedBB getBoundingBox(IBlockState state,net.minecraft.world.IBlockAccess world,BlockPos pos) {
        if(!wall) return super.getBoundingBox(state,world,pos);
        EnumFacing face=state.getValue(FACING);
        return face==EnumFacing.NORTH ? new net.minecraft.util.math.AxisAlignedBB(0,0.28125,0.875,1,0.78125,1)
                : face==EnumFacing.SOUTH ? new net.minecraft.util.math.AxisAlignedBB(0,0.28125,0,1,0.78125,0.125)
                : face==EnumFacing.WEST ? new net.minecraft.util.math.AxisAlignedBB(0.875,0.28125,0,1,0.78125,1)
                : new net.minecraft.util.math.AxisAlignedBB(0,0.28125,0,0.125,0.78125,1);
    }
    public void neighborChanged(IBlockState state,World world,BlockPos pos,Block changed,BlockPos from) {
        BlockPos support=wall ? pos.offset(state.getValue(FACING).getOpposite()) : pos.down();
        if(!world.isRemote && !world.getBlockState(support).getMaterial().isSolid()) { dropBlockAsItem(world,pos,state,0); world.setBlockToAir(pos); }
    }
    public boolean onBlockActivated(World world,BlockPos pos,IBlockState state,EntityPlayer player,EnumHand hand,EnumFacing side,float hx,float hy,float hz) {
        ItemStack held=player.getHeldItem(hand);
        TileEntity raw=world.getTileEntity(pos);
        if(!(raw instanceof CanvasSignTile) || Stacks.empty(held) || held.getItem()!=net.minecraft.init.Items.DYE
                || !player.canPlayerEdit(pos,side,held)) return false;
        CanvasSignTile tile=(CanvasSignTile)raw;
        int color=DYE_TEXT[held.getMetadata()&15];
        if(tile.textColor==color) return false;
        if(!world.isRemote) {
            tile.textColor=color; tile.markDirty();
            world.notifyBlockUpdate(pos,state,state,3);
            if(!player.capabilities.isCreativeMode) Stacks.shrink(held,1);
        }
        return true;
    }
}

