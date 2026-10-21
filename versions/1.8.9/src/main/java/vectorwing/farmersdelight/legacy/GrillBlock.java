package vectorwing.farmersdelight.legacy;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;


import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public final class GrillBlock extends BlockContainer {
    public static final PropertyDirection FACING = net.minecraft.block.BlockChest.FACING;
    public static final PropertyBool LIT = PropertyBool.create("lit");
    private static final AxisAlignedBB PAN = new AxisAlignedBB(1D/16,0,1D/16,15D/16,0.25,15D/16);
    private final boolean stove;
    public GrillBlock(boolean stove) {
        super(Material.iron); this.stove=stove;
        setUnlocalizedName("farmersdelight."+(stove ? "stove" : "skillet")); setCreativeTab(Content.TAB);
        setHardness(stove ? 2F : 0.5F); setResistance(6F); setStepSound(soundTypeMetal);
        setDefaultState(blockState.getBaseState().withProperty(FACING,EnumFacing.NORTH).withProperty(LIT,false));
    }
    public TileEntity createNewTileEntity(World world,int meta) { return stove ? new StoveTile() : new SkilletTile(); }
    public boolean isOpaqueCube() { return stove; }
    public boolean isFullCube() { return stove; }
    public AxisAlignedBB shape(IBlockState state,IBlockAccess world,BlockPos pos) { return stove ? new net.minecraft.util.AxisAlignedBB(0,0,0,1,1,1) : PAN; }
    public int getLightValue(IBlockAccess world,BlockPos pos) { return stove && world.getBlockState(pos).getValue(LIT) ? 13 : 0; }
    public int getRenderType() { return 3; }
    protected BlockState createBlockState() { return new BlockState(this,FACING,LIT); }
    public IBlockState getStateFromMeta(int meta) { return getDefaultState().withProperty(FACING,EnumFacing.getHorizontal(meta & 3)).withProperty(LIT,(meta & 4)!=0); }
    public int getMetaFromState(IBlockState state) { return state.getValue(FACING).getHorizontalIndex() | (state.getValue(LIT) ? 4 : 0); }
    public IBlockState onBlockPlaced(World world,BlockPos pos,EnumFacing side,float x,float y,float z,int meta,EntityLivingBase placer) {
        return getDefaultState().withProperty(FACING,placer.getHorizontalFacing().getOpposite()).withProperty(LIT,stove);
    }
    public void onBlockPlacedBy(World world,BlockPos pos,IBlockState state,EntityLivingBase placer,ItemStack stack) {
        if (!stove && world.getTileEntity(pos) instanceof SkilletTile) ((SkilletTile)world.getTileEntity(pos)).identity(stack);
    }
    public boolean onBlockActivated(World world,BlockPos pos,IBlockState state,EntityPlayer player,EnumFacing side,float x,float y,float z) {
        if (!(world.getTileEntity(pos) instanceof GrillingTile)) return false;
        if (world.isRemote) return true;
        ItemStack held=player.getHeldItem(); GrillingTile tile=(GrillingTile)world.getTileEntity(pos); boolean hot=state.getValue(LIT);
        if (stove && !Stacks.empty(held)) {
            boolean shovel=held.getItem().getToolClasses(held).contains("shovel"), water=held.getItem()==Items.water_bucket;
            boolean lighter=held.getItem()==Items.flint_and_steel, charge=held.getItem()==Items.fire_charge;
            if (hot && (shovel || water) || !hot && (lighter || charge)) {
                world.setBlockState(pos,state.withProperty(LIT,!hot),3);
                world.playSoundEffect(pos.getX()+0.5,pos.getY()+0.5,pos.getZ()+0.5,hot ? "random.fizz" : "fire.ignite",0.8F,1F);
                if (!player.capabilities.isCreativeMode) {
                    if (water) player.inventory.setInventorySlotContents(player.inventory.currentItem,new ItemStack(Items.bucket));
                    else if(charge) Stacks.shrink(held,1); else held.damageItem(1,player);
                }
                return true;
            }
        }
        if (Stacks.empty(held) && !stove) {
            ItemStack removed=tile.decrStackSize(0,Stacks.size(tile.getStackInSlot(0)));
            if (!Stacks.empty(removed) && !player.capabilities.isCreativeMode && !player.inventory.addItemStackToInventory(removed)) tile.drop(removed);
            return true;
        }
        return tile.add(held,player.capabilities.isCreativeMode);
    }
    public void onEntityWalk(World world,BlockPos pos,Entity entity) {
        if (!world.isRemote && stove && world.getBlockState(pos).getValue(LIT) && entity instanceof EntityLivingBase && !entity.isImmuneToFire())
            entity.attackEntityFrom(net.minecraft.util.DamageSource.inFire,1F);
    }
    public boolean removedByPlayer(World world,BlockPos pos,EntityPlayer player,boolean harvest) { return harvest || super.removedByPlayer(world,pos,player,false); }
    public void harvestBlock(World world,EntityPlayer player,BlockPos pos,IBlockState state,TileEntity tile) { super.harvestBlock(world,player,pos,state,tile); world.setBlockToAir(pos); }
    public java.util.List<ItemStack> getDrops(IBlockAccess world,BlockPos pos,IBlockState state,int fortune) { java.util.List<ItemStack> drops=new java.util.ArrayList<ItemStack>();
        TileEntity tile=world.getTileEntity(pos); drops.add(tile instanceof SkilletTile ? ((SkilletTile)tile).identity() : new ItemStack(this));
    return drops; }
    public void breakBlock(World world,BlockPos pos,IBlockState state) {
        if (!world.isRemote && world.getTileEntity(pos) instanceof GrillingTile) {
            GrillingTile tile=(GrillingTile)world.getTileEntity(pos);
            for (int i=0;i<tile.getSizeInventory();i++) tile.drop(tile.decrStackSize(i,Stacks.size(tile.getStackInSlot(i))));
        }
        super.breakBlock(world,pos,state);
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
}
