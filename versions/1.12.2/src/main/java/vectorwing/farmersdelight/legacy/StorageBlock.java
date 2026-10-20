package vectorwing.farmersdelight.legacy;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.IBlockAccess;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.BlockStateContainer;

public final class StorageBlock extends BlockContainer {
    private static final double[][] WALLS={{0,0,0,1,0.125,1},{0,0.875,0,1,1,1},{0,0,0,1,1,0.125},{0,0,0.875,1,1,1},{0,0,0,0.125,1,1},{0.875,0,0,1,1,1}};
    public void addCollisionBoxToList(IBlockState state,World world,BlockPos pos,net.minecraft.util.math.AxisAlignedBB mask,java.util.List<net.minecraft.util.math.AxisAlignedBB> boxes,net.minecraft.entity.Entity entity,boolean actual) {
        if(!basket) { super.addCollisionBoxToList(state,world,pos,mask,boxes,entity,actual); return; }
        int opening=state.getValue(FACING).getIndex();
        for(int face=0;face<6;face++) if(face!=opening) {
            double[] box=WALLS[face];
            addCollisionBoxToList(pos,mask,boxes,new net.minecraft.util.math.AxisAlignedBB(box[0],box[1],box[2],box[3],box[4],box[5]));
        }
    }
    public final String id;
    public final boolean basket;
    public static final net.minecraft.block.properties.PropertyDirection FACING=net.minecraft.block.properties.PropertyDirection.create("facing");
    public static final net.minecraft.block.properties.PropertyBool OPEN=net.minecraft.block.properties.PropertyBool.create("open");
    public StorageBlock(String id,boolean basket) {
        super(Material.WOOD); this.id=id; this.basket=basket;
        setTranslationKey("farmersdelight."+id); setCreativeTab(Content.TAB); setHardness(1.5F);
        setDefaultState(blockState.getBaseState().withProperty(FACING,basket ? EnumFacing.UP : EnumFacing.NORTH).withProperty(OPEN,false));
    }
    public TileEntity createNewTileEntity(World world,int meta) { return basket ? new BasketTile() : new StorageTile(); }
    protected BlockStateContainer createBlockState() { return new BlockStateContainer(this,FACING,OPEN); }
    public int getMetaFromState(IBlockState state) { return state.getValue(FACING).getIndex(); }
    public IBlockState getStateFromMeta(int meta) { EnumFacing face=EnumFacing.byIndex(meta&7); return getDefaultState().withProperty(FACING,basket || face.getAxis()!=EnumFacing.Axis.Y ? face : EnumFacing.NORTH); }
    public IBlockState getActualState(IBlockState state,IBlockAccess world,BlockPos pos) { TileEntity tile=world.getTileEntity(pos); return state.withProperty(OPEN,tile instanceof StorageTile && ((StorageTile)tile).users>0); }
    public IBlockState getStateForPlacement(World world,BlockPos pos,EnumFacing face,float x,float y,float z,int meta,EntityLivingBase placer) {
        return getDefaultState().withProperty(FACING,basket ? EnumFacing.getDirectionFromEntityLiving(pos,placer).getOpposite() : placer.getHorizontalFacing().getOpposite());
    }
    public net.minecraft.util.EnumBlockRenderType getRenderType(IBlockState state) { return net.minecraft.util.EnumBlockRenderType.MODEL; }
    public boolean isOpaqueCube(IBlockState state) { return !basket; }
    public boolean isFullCube(IBlockState state) { return !basket; }
    public void onBlockPlacedBy(World world,BlockPos pos,IBlockState state,EntityLivingBase placer,ItemStack stack) {
        if(stack.hasDisplayName() && world.getTileEntity(pos) instanceof StorageTile) ((StorageTile)world.getTileEntity(pos)).name(stack.getDisplayName());
    }
    public boolean onBlockActivated(World world,BlockPos pos,IBlockState state,EntityPlayer player,EnumHand hand,EnumFacing side,float x,float y,float z) {
        if(!world.isRemote) player.openGui(Foundation.instance,1,world,pos.getX(),pos.getY(),pos.getZ());
        return true;
    }
    public void breakBlock(World world,BlockPos pos,IBlockState state) {
        TileEntity raw=world.getTileEntity(pos);
        if(!world.isRemote && raw instanceof StorageTile) {
            StorageTile tile=(StorageTile)raw;
            for(int slot=0;slot<tile.getSizeInventory();slot++) {
                ItemStack stack=tile.decrStackSize(slot,Stacks.size(tile.getStackInSlot(slot)));
                if(!Stacks.empty(stack)) spawnAsEntity(world,pos,stack);
            }
        }
        super.breakBlock(world,pos,state);
    }
    public boolean hasComparatorInputOverride(IBlockState state) { return true; }
    public int getComparatorInputOverride(World world,BlockPos pos) {
        TileEntity tile=world.getTileEntity(pos);
        return tile instanceof StorageTile ? net.minecraft.inventory.Container.calcRedstoneFromInventory((StorageTile)tile) : 0;
    }
    public int getComparatorInputOverride(IBlockState state,World world,BlockPos pos) { return getComparatorInputOverride(world,pos); }
}

