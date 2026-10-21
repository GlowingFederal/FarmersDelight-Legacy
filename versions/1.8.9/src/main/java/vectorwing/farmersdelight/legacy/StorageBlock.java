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
import net.minecraft.util.BlockPos;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.BlockState;

public final class StorageBlock extends BlockContainer {
    private static final double[][] WALLS={{0,0,0,1,0.125,1},{0,0.875,0,1,1,1},{0,0,0,1,1,0.125},{0,0,0.875,1,1,1},{0,0,0,0.125,1,1},{0.875,0,0,1,1,1}};
    public void addCollisionBoxesToList(net.minecraft.world.World world,net.minecraft.util.BlockPos pos,net.minecraft.block.state.IBlockState state,net.minecraft.util.AxisAlignedBB mask,java.util.List<net.minecraft.util.AxisAlignedBB> boxes,net.minecraft.entity.Entity entity) {
        if(!basket) { super.addCollisionBoxesToList(world,pos,state,mask,boxes,entity); return; }
        int opening=state.getValue(FACING).getIndex();
        for(int face=0;face<6;face++) if(face!=opening) {
            double[] box=WALLS[face];
            addCollisionBoxToList(pos,mask,boxes,new net.minecraft.util.AxisAlignedBB(box[0],box[1],box[2],box[3],box[4],box[5]));
        }
    }
    public final String id;
    public final boolean basket;
    public static final net.minecraft.block.properties.PropertyDirection FACING=net.minecraft.block.properties.PropertyDirection.create("facing");
    public static final net.minecraft.block.properties.PropertyBool OPEN=net.minecraft.block.properties.PropertyBool.create("open");
    public StorageBlock(String id,boolean basket) {
        super(Material.wood); this.id=id; this.basket=basket;
        setUnlocalizedName("farmersdelight."+id); setCreativeTab(Content.TAB); setHardness(1.5F);
        setDefaultState(blockState.getBaseState().withProperty(FACING,basket ? EnumFacing.UP : EnumFacing.NORTH).withProperty(OPEN,false));
    }
    public TileEntity createNewTileEntity(World world,int meta) { return basket ? new BasketTile() : new StorageTile(); }
    protected BlockState createBlockState() { return new BlockState(this,FACING,OPEN); }
    public int getMetaFromState(IBlockState state) { return state.getValue(FACING).getIndex(); }
    public IBlockState getStateFromMeta(int meta) { EnumFacing face=EnumFacing.getFront(meta&7); return getDefaultState().withProperty(FACING,basket || face.getAxis()!=EnumFacing.Axis.Y ? face : EnumFacing.NORTH); }
    public IBlockState getActualState(IBlockState state,IBlockAccess world,BlockPos pos) { TileEntity tile=world.getTileEntity(pos); return state.withProperty(OPEN,tile instanceof StorageTile && ((StorageTile)tile).users>0); }
    public IBlockState onBlockPlaced(World world,BlockPos pos,EnumFacing face,float x,float y,float z,int meta,EntityLivingBase placer) {
        return getDefaultState().withProperty(FACING,basket ? net.minecraft.block.BlockPistonBase.getFacingFromEntity(world,pos,placer).getOpposite() : placer.getHorizontalFacing().getOpposite());
    }
    public int getRenderType() { return 3; }
    public boolean isOpaqueCube() { return !basket; }
    public boolean isFullCube() { return !basket; }
    public void onBlockPlacedBy(World world,BlockPos pos,IBlockState state,EntityLivingBase placer,ItemStack stack) {
        if(stack.hasDisplayName() && world.getTileEntity(pos) instanceof StorageTile) ((StorageTile)world.getTileEntity(pos)).name(stack.getDisplayName());
    }
    public boolean onBlockActivated(World world,BlockPos pos,IBlockState state,EntityPlayer player,EnumFacing side,float x,float y,float z) {
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
    public boolean hasComparatorInputOverride() { return true; }
    public int getComparatorInputOverride(World world,BlockPos pos) {
        TileEntity tile=world.getTileEntity(pos);
        return tile instanceof StorageTile ? net.minecraft.inventory.Container.calcRedstoneFromInventory((StorageTile)tile) : 0;
    }
    private static void addCollisionBoxToList(net.minecraft.util.BlockPos pos,net.minecraft.util.AxisAlignedBB mask,java.util.List<net.minecraft.util.AxisAlignedBB> boxes,net.minecraft.util.AxisAlignedBB local) {
        net.minecraft.util.AxisAlignedBB box=local.offset(pos.getX(),pos.getY(),pos.getZ()); if(mask.intersectsWith(box)) boxes.add(box);
    }
}

