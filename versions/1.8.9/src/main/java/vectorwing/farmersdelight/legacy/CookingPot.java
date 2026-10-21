package vectorwing.farmersdelight.legacy;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;


import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public final class CookingPot extends BlockContainer {
    public static final PropertyDirection FACING = net.minecraft.block.BlockChest.FACING;
    public static final net.minecraft.block.properties.PropertyInteger SUPPORT=net.minecraft.block.properties.PropertyInteger.create("support",0,2);
    private static final AxisAlignedBB SHAPE = new AxisAlignedBB(0.125, 0, 0.125, 0.875, 0.625, 0.875);
    public CookingPot() {
        super(Material.iron); setUnlocalizedName("farmersdelight.cooking_pot"); setCreativeTab(Content.TAB);
        setHardness(0.5F); setResistance(6F); setStepSound(soundTypeMetal);
        setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(SUPPORT,0));
    }
    public TileEntity createNewTileEntity(World world, int metadata) { return new CookingPotTile(); }
    @Override public boolean removedByPlayer(World world, BlockPos pos, EntityPlayer player, boolean harvest) {
        return harvest || super.removedByPlayer(world, pos, player, false);
    }
    @Override public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity tile) {
        super.harvestBlock(world, player, pos, state, tile); world.setBlockToAir(pos);
    }
    public boolean isOpaqueCube() { return false; }
    public boolean isFullCube() { return false; }
    public int getRenderType() { return 3; }
    public AxisAlignedBB shape(IBlockState state, IBlockAccess world, BlockPos pos) { return SHAPE; }
    public void addCollisionBoxesToList(net.minecraft.world.World world,net.minecraft.util.BlockPos pos,net.minecraft.block.state.IBlockState state,AxisAlignedBB entityBox,java.util.List<AxisAlignedBB> boxes,net.minecraft.entity.Entity entity) {
        addCollisionBoxToList(pos,entityBox,boxes,SHAPE);
        if(state.getValue(SUPPORT)==1) addCollisionBoxToList(pos,entityBox,boxes,new AxisAlignedBB(0,-0.0625,0,1,0,1));
    }
    protected BlockState createBlockState() { return new BlockState(this, FACING,SUPPORT); }
    public IBlockState getStateFromMeta(int meta) { return getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta & 3)).withProperty(SUPPORT,Math.min(2,(meta>>2)&3)); }
    public int getMetaFromState(IBlockState state) { return state.getValue(FACING).getHorizontalIndex()|(state.getValue(SUPPORT)<<2); }
    private int tray(World world,BlockPos pos) {
        String id=world.getBlockState(pos.down()).getBlock().getRegistryName().toString();
        return id.equals("minecraft:campfire") || id.equals("minecraft:soul_campfire") ? 1 : 0;
    }
    public void onNeighborBlockChange(World world,BlockPos pos,IBlockState state,Block block) {
        if(!world.isRemote && state.getValue(SUPPORT)!=2 && state.getValue(SUPPORT)!=tray(world,pos))
            world.setBlockState(pos,state.withProperty(SUPPORT,tray(world,pos)),3);
    }
    public IBlockState onBlockPlaced(World world, BlockPos pos, EnumFacing side, float x, float y, float z, int meta, EntityLivingBase placer) {
        return getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite()).withProperty(SUPPORT,side==EnumFacing.DOWN ? 2 : tray(world,pos));
    }
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        if (world.getTileEntity(pos) instanceof CookingPotTile) {
            CookingPotTile pot = (CookingPotTile) world.getTileEntity(pos); pot.restoreMeal(stack);
            if (stack.hasDisplayName()) pot.name(stack.getDisplayName());
        }
    }
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float x, float y, float z) {
        if(player.isSneaking() && Stacks.empty(player.getHeldItem())) {
            if(!world.isRemote) {
                world.setBlockState(pos,state.withProperty(SUPPORT,state.getValue(SUPPORT)==2 ? tray(world,pos) : 2),3);
                world.playSoundEffect(pos.getX()+0.5,pos.getY()+0.5,pos.getZ()+0.5,"dig.stone",0.7F,1F);
            }
            return true;
        }
        if (!(world.getTileEntity(pos) instanceof CookingPotTile)) return false;
        if (!world.isRemote) {
            CookingPotTile tile = (CookingPotTile) world.getTileEntity(pos);
            ItemStack served = tile.serve(player.getHeldItem(), player.capabilities.isCreativeMode);
            if (!Stacks.empty(served)) { if (!player.inventory.addItemStackToInventory(served)) tile.drop(served); }
            else player.openGui(Foundation.instance, 0, world, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }
    public java.util.List<ItemStack> getDrops(IBlockAccess world,BlockPos pos,IBlockState state,int fortune) { java.util.List<ItemStack> drops=new java.util.ArrayList<ItemStack>();
        TileEntity tile = world.getTileEntity(pos);
        drops.add(tile instanceof CookingPotTile ? ((CookingPotTile) tile).portable() : new ItemStack(this));
    return drops; }
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        if (!world.isRemote && world.getTileEntity(pos) instanceof CookingPotTile) {
            CookingPotTile pot = (CookingPotTile) world.getTileEntity(pos);
            for (int i = 0; i < 9; i++) if (i != 6) pot.drop(pot.decrStackSize(i, Stacks.size(pot.getStackInSlot(i))));
            pot.payExperience();
        }
        super.breakBlock(world, pos, state);
    }
    public boolean hasComparatorInputOverride() { return true; }
    public int getComparatorInputOverride(World world, BlockPos pos) {
        return Math.min(15,net.minecraft.inventory.Container.calcRedstoneFromInventory((net.minecraft.inventory.IInventory) world.getTileEntity(pos)));
    }
    @Override public void setBlockBoundsBasedOnState(net.minecraft.world.IBlockAccess world,net.minecraft.util.BlockPos pos) {
        net.minecraft.util.AxisAlignedBB b=shape(world.getBlockState(pos),world,pos);
        setBlockBounds((float)b.minX,(float)b.minY,(float)b.minZ,(float)b.maxX,(float)b.maxY,(float)b.maxZ);
    }
    @Override public net.minecraft.util.AxisAlignedBB getSelectedBoundingBox(net.minecraft.world.World world,net.minecraft.util.BlockPos pos) {
        setBlockBoundsBasedOnState(world,pos); return super.getSelectedBoundingBox(world,pos);
    }
    @Override public net.minecraft.util.AxisAlignedBB getCollisionBoundingBox(net.minecraft.world.World world,net.minecraft.util.BlockPos pos,net.minecraft.block.state.IBlockState state) {
        return shape(state,world,pos).offset(pos.getX(),pos.getY(),pos.getZ());
    }
    private static void addCollisionBoxToList(net.minecraft.util.BlockPos pos,net.minecraft.util.AxisAlignedBB mask,java.util.List<net.minecraft.util.AxisAlignedBB> boxes,net.minecraft.util.AxisAlignedBB local) {
        net.minecraft.util.AxisAlignedBB box=local.offset(pos.getX(),pos.getY(),pos.getZ()); if(mask.intersectsWith(box)) boxes.add(box);
    }
}
