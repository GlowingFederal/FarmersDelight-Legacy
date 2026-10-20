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
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.NonNullList;

public final class RiceUpper extends BlockCrops implements IShearable {
    private final boolean wild;
    public static final PropertyInteger RICE_AGE=PropertyInteger.create("age",0,3);
    public RiceUpper(boolean wild) { super(); this.wild=wild; setTranslationKey("farmersdelight."+(wild ? "wild_rice_upper" : "rice_upper_crop")); }
    public int getMaxAge() { return 3; }
    public PropertyInteger getAgeProperty() { return RICE_AGE; }
    public int getAge(IBlockState state) { return state.getValue(RICE_AGE); }
    protected BlockStateContainer createBlockState() { return new BlockStateContainer(this,RICE_AGE); }
    protected Item getSeed() { return Content.ITEMS.get("rice"); }
    protected Item getCrop() { return Content.ITEMS.get("rice_panicle"); }
    protected boolean canSustainBush(IBlockState state) { return state.getBlock()==(wild ? Content.wildRice : Content.riceCrop); }
    public boolean canBlockStay(World world,BlockPos pos,IBlockState state) { return world.getBlockState(pos.down()).getBlock()==(wild ? Content.wildRice : Content.riceCrop) && (wild || world.getLightFromNeighbors(pos)>=8 || world.canSeeSky(pos)); }
    public void updateTick(World world,BlockPos pos,IBlockState state,Random random) {
        if(!canBlockStay(world,pos,state)) { dropBlockAsItem(world,pos,state,0); world.setBlockToAir(pos); return; }
        if(!wild && getAge(state)<3 && world.getLightFromNeighbors(pos.up())>=9
                && net.minecraftforge.common.ForgeHooks.onCropsGrowPre(world,pos,state,random.nextInt(26)==0)) {
            world.setBlockState(pos,withAge(getAge(state)+1),2); net.minecraftforge.common.ForgeHooks.onCropsGrowPost(world,pos,state,world.getBlockState(pos));
        }
    }
    public void boost(World world,BlockPos pos) {
        if(wild) spawnAsEntity(world,pos,new ItemStack(Content.ITEMS.get("wild_rice")));
        else world.setBlockState(pos,withAge(Math.min(3,getAge(world.getBlockState(pos))+(2+world.rand.nextInt(4))/3)),2);
    }
    public boolean canGrow(World world,BlockPos pos,IBlockState state,boolean client) { return wild || getAge(state)<3; }
    public void grow(World world,Random random,BlockPos pos,IBlockState state) { boost(world,pos); }
    public void getDrops(NonNullList<ItemStack> drops,IBlockAccess world,BlockPos pos,IBlockState state,int fortune) {
        if(!wild && getAge(state)>=3) drops.add(new ItemStack(Content.ITEMS.get("rice_panicle")));
    }
    public boolean isShearable(ItemStack item,IBlockAccess world,BlockPos pos) { return wild && world.getBlockState(pos.down()).getBlock()==Content.wildRice; }
    public java.util.List<ItemStack> onSheared(ItemStack item,IBlockAccess world,BlockPos pos,int fortune) {
        if(world instanceof World) ((World)world).setBlockState(pos.down(),net.minecraft.init.Blocks.WATER.getDefaultState(),3);
        return java.util.Collections.singletonList(new ItemStack(Content.ITEMS.get("wild_rice")));
    }
}
