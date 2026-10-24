package vectorwing.farmersdelight.legacy;

import java.util.ArrayList;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.IShearable;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.BlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumWorldBlockLayer;


public final class RicePlant extends BlockContainer implements IGrowable,IShearable {
    public final boolean wild;
    public static final PropertyInteger AGE=PropertyInteger.create("age",0,3);
    public static final net.minecraft.block.properties.PropertyBool SUPPORTING=net.minecraft.block.properties.PropertyBool.create("supporting");
    public RicePlant(boolean wild) {
        super(Material.water); this.wild=wild; setTickRandomly(true); setHardness(0);
        setUnlocalizedName("farmersdelight."+(wild ? "wild_rice" : "rice_crop"));
        setDefaultState(blockState.getBaseState().withProperty(AGE,0).withProperty(SUPPORTING,false).withProperty(net.minecraft.block.BlockLiquid.LEVEL,0));
    }
    public TileEntity createNewTileEntity(World world,int meta) { return new RiceTile(); }
    private static boolean ground(Block block) { return block==Blocks.dirt || block==Blocks.grass || block==Blocks.farmland || block==Content.richSoil || block==Content.richFarmland; }
    public boolean canPlant(World world,BlockPos pos) {
        return pos.getY()>0 && pos.getY()<254 && world.getBlockState(pos).getBlock().getMaterial()==Material.water && world.getBlockState(pos).getBlock() instanceof net.minecraft.block.BlockLiquid && world.getBlockState(pos).getValue(net.minecraft.block.BlockLiquid.LEVEL)==0 && ground(world.getBlockState(pos.down()).getBlock()) && (!wild || world.isAirBlock(pos.up()));
    }
    public void place(World world,BlockPos pos) {
        world.setBlockState(pos,getDefaultState(),3);
        if(wild) world.setBlockState(pos.up(),Content.wildRiceUpper.getDefaultState(),3);
    }
    public boolean isOpaqueCube() { return false; }
    public boolean isFullCube() { return false; }
    public int getRenderType() { return 3; }
    public EnumWorldBlockLayer getBlockLayer() { return EnumWorldBlockLayer.TRANSLUCENT; }
    public AxisAlignedBB getCollisionBoundingBox(World world,BlockPos pos,IBlockState state) { return null; }
    public AxisAlignedBB shape(IBlockState state,IBlockAccess world,BlockPos pos) { return new AxisAlignedBB(0.0625,0,0.0625,0.9375,1,0.9375); }
    public boolean canCollideCheck(IBlockState state,boolean liquid) { return true; }
    public boolean isReplaceable(World world,BlockPos pos) { return false; }
    private RiceTile tile(IBlockAccess world,BlockPos pos) { TileEntity tile=world.getTileEntity(pos); return tile instanceof RiceTile ? (RiceTile)tile : null; }
    protected BlockState createBlockState() { return new BlockState(this,AGE,SUPPORTING,net.minecraft.block.BlockLiquid.LEVEL); }
    public IBlockState getStateFromMeta(int meta) { return getDefaultState(); }
    public int getMetaFromState(IBlockState state) { return 0; }
    public IBlockState getActualState(IBlockState state,IBlockAccess world,BlockPos pos) { RiceTile tile=tile(world,pos); return state.withProperty(AGE,tile==null ? 0 : tile.age).withProperty(SUPPORTING,world.getBlockState(pos.up()).getBlock()==Content.riceUpper); }
    public void onNeighborBlockChange(World world,BlockPos pos,IBlockState state,Block changed) {
        if(world.isRemote || ground(world.getBlockState(pos.down()).getBlock()) && (!wild || world.getBlockState(pos.up()).getBlock()==Content.wildRiceUpper)) return;
        dropBlockAsItem(world,pos,state,0); world.setBlockToAir(pos);
    }
    @Override public boolean removedByPlayer(World world,BlockPos pos,net.minecraft.entity.player.EntityPlayer player,boolean harvest) {
        boolean removed=super.removedByPlayer(world,pos,player,harvest);
        Block replacement=world.getBlockState(pos).getBlock();
        // The water-restoration callback replaces the requested air. Preserve the
        // successful harvest result so the native player path still emits its drop.
        return removed || !world.isRemote && (replacement==Blocks.water || replacement==Blocks.flowing_water);
    }
    public void breakBlock(World world,BlockPos pos,IBlockState state) {
        RiceTile tile=tile(world,pos); boolean restore=tile==null || !tile.draining;
        super.breakBlock(world,pos,state);
        if(!world.isRemote && restore && world.isAirBlock(pos))
            world.setBlockState(pos,Blocks.water.getDefaultState(),3);
    }
    public Item getItemDropped(IBlockState state,Random random,int fortune) { return Content.ITEMS.get("rice"); }
    public void updateTick(World world,BlockPos pos,IBlockState state,Random random) {
        if(world.isRemote || wild || !world.isAreaLoaded(pos,1) || world.getLightFromNeighbors(pos.up())<6) return;
        if(world.getBlockState(pos.up()).getBlock()!=Content.riceUpper && random.nextInt(3)==0) advance(world,pos,1);
    }
    private void advance(World world,BlockPos pos,int amount) {
        RiceTile tile=tile(world,pos); if(tile==null) return;
        int next=tile.age+amount;
        if(next<=3) { tile.age(next); return; }
        Block upper=wild ? Content.wildRiceUpper : Content.riceUpper;
        if(world.getBlockState(pos.up()).getBlock()==upper) {
            if(upper instanceof RiceUpper) ((RiceUpper)upper).boost(world,pos.up());
        } else if(world.isAirBlock(pos.up())) {
            tile.age(3);
            world.setBlockState(pos.up(),((RiceUpper)upper).withAge(Math.min(3,next-4)),3);
        }
    }
    public boolean canGrow(World world,BlockPos pos,IBlockState state,boolean client) {
        return wild || world.getBlockState(pos.up()).getBlock()!=Content.riceUpper || ((RiceUpper)Content.riceUpper).getAge(world.getBlockState(pos.up()))<3;
    }
    public boolean canUseBonemeal(World world,Random random,BlockPos pos,IBlockState state) { return true; }
    public void grow(World world,Random random,BlockPos pos,IBlockState state) {
        if(wild) spawnAsEntity(world,pos,new ItemStack(Content.ITEMS.get("wild_rice"))); else advance(world,pos,1+random.nextInt(4));
    }
    public boolean isShearable(ItemStack item,IBlockAccess world,BlockPos pos) { return wild; }
    public java.util.List<ItemStack> onSheared(ItemStack item,IBlockAccess world,BlockPos pos,int fortune) { return java.util.Collections.singletonList(new ItemStack(Content.ITEMS.get("wild_rice"))); }
    @Override public void setBlockBoundsBasedOnState(net.minecraft.world.IBlockAccess world,net.minecraft.util.BlockPos pos) {
        net.minecraft.util.AxisAlignedBB b=shape(world.getBlockState(pos),world,pos);
        setBlockBounds((float)b.minX,(float)b.minY,(float)b.minZ,(float)b.maxX,(float)b.maxY,(float)b.maxZ);
    }
    @Override public net.minecraft.util.AxisAlignedBB getSelectedBoundingBox(net.minecraft.world.World world,net.minecraft.util.BlockPos pos) {
        setBlockBoundsBasedOnState(world,pos); return super.getSelectedBoundingBox(world,pos);
    }
    @Override public int colorMultiplier(IBlockAccess world,BlockPos pos,int tint) { return tint==0 ? Blocks.water.colorMultiplier(world,pos,tint) : 0xFFFFFF; }
}
