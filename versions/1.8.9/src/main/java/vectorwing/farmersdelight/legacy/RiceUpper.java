package vectorwing.farmersdelight.legacy;

import java.util.ArrayList;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.IShearable;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.BlockState;
import net.minecraft.util.BlockPos;


public final class RiceUpper extends net.minecraft.block.BlockBush implements net.minecraft.block.IGrowable,IShearable {
    private final boolean wild;
    public static final PropertyInteger RICE_AGE=PropertyInteger.create("age",0,3);
    public RiceUpper(boolean wild) { super(); this.wild=wild; setUnlocalizedName("farmersdelight."+(wild ? "wild_rice_upper" : "rice_upper_crop")); }
    public IBlockState withAge(int age) { return getDefaultState().withProperty(RICE_AGE,age); }
    public IBlockState getStateFromMeta(int meta) { return withAge(meta&3); }
    public int getMetaFromState(IBlockState state) { return getAge(state); }
    public boolean canUseBonemeal(World world,Random random,BlockPos pos,IBlockState state) { return true; }
    public int getMaxAge() { return 3; }
    public PropertyInteger getAgeProperty() { return RICE_AGE; }
    public int getAge(IBlockState state) { return state.getValue(RICE_AGE); }
    protected BlockState createBlockState() { return new BlockState(this,RICE_AGE); }
    protected Item getSeed() { return Content.ITEMS.get("rice"); }
    protected Item getCrop() { return Content.ITEMS.get("rice_panicle"); }
    protected boolean canPlaceBlockOn(Block block) { return block==(wild ? Content.wildRice : Content.riceCrop); }
    public boolean canBlockStay(World world,BlockPos pos,IBlockState state) { return world.getBlockState(pos.down()).getBlock()==(wild ? Content.wildRice : Content.riceCrop) && (wild || world.getLightFromNeighbors(pos)>=8 || world.canSeeSky(pos)); }
    public void updateTick(World world,BlockPos pos,IBlockState state,Random random) {
        if(!canBlockStay(world,pos,state)) { dropBlockAsItem(world,pos,state,0); world.setBlockToAir(pos); return; }
        if(!wild && getAge(state)<3 && world.getLightFromNeighbors(pos.up())>=9
                && random.nextInt(26)==0) {
            world.setBlockState(pos,withAge(getAge(state)+1),2); 
        }
    }
    public void boost(World world,BlockPos pos) {
        if(wild) spawnAsEntity(world,pos,new ItemStack(Content.ITEMS.get("wild_rice")));
        else world.setBlockState(pos,withAge(Math.min(3,getAge(world.getBlockState(pos))+(2+world.rand.nextInt(4))/3)),2);
    }
    public boolean canGrow(World world,BlockPos pos,IBlockState state,boolean client) { return wild || getAge(state)<3; }
    public void grow(World world,Random random,BlockPos pos,IBlockState state) { boost(world,pos); }
    public java.util.List<ItemStack> getDrops(IBlockAccess world,BlockPos pos,IBlockState state,int fortune) { java.util.List<ItemStack> drops=new java.util.ArrayList<ItemStack>();
        if(!wild && getAge(state)>=3) drops.add(new ItemStack(Content.ITEMS.get("rice_panicle")));
    return drops; }
    public boolean isShearable(ItemStack item,IBlockAccess world,BlockPos pos) { return wild && world.getBlockState(pos.down()).getBlock()==Content.wildRice; }
    public java.util.List<ItemStack> onSheared(ItemStack item,IBlockAccess world,BlockPos pos,int fortune) {
        if(world instanceof World) ((World)world).setBlockState(pos.down(),net.minecraft.init.Blocks.water.getDefaultState(),3);
        return java.util.Collections.singletonList(new ItemStack(Content.ITEMS.get("wild_rice")));
    }
}
