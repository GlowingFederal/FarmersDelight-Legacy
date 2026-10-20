package vectorwing.farmersdelight.legacy;

import java.util.ArrayList;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.IGrowable;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.IShearable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.util.EnumFacing; import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos; import net.minecraft.util.NonNullList;

public final class MushroomColony extends BlockBush implements IGrowable,IShearable {
    public boolean canPlaceBlockAt(World world,BlockPos pos) { return world.isAirBlock(pos) && canBlockStay(world,pos,getDefaultState()); }
    private final boolean red;
    public static final PropertyInteger AGE=PropertyInteger.create("age",0,3);
    public MushroomColony(boolean red) {
        super(); this.red=red; setTickRandomly(true); setCreativeTab(Content.TAB);
        setTranslationKey("farmersdelight."+(red ? "red" : "brown")+"_mushroom_colony");
        setDefaultState(blockState.getBaseState().withProperty(AGE,0));
    }
    private Item mushroom() { return Item.getItemFromBlock(red ? Blocks.RED_MUSHROOM : Blocks.BROWN_MUSHROOM); }
    protected BlockStateContainer createBlockState() { return new BlockStateContainer(this,AGE); }
    public IBlockState getStateFromMeta(int meta) { return getDefaultState().withProperty(AGE,meta&3); }
    public int getMetaFromState(IBlockState state) { return state.getValue(AGE); }
    public boolean canBlockStay(World world,BlockPos pos,IBlockState state) {
        IBlockState below=world.getBlockState(pos.down()); Block floor=below.getBlock();
        return floor==Content.richSoil || floor==Content.compost || floor==Blocks.MYCELIUM
                || floor==Blocks.DIRT && below.getValue(net.minecraft.block.BlockDirt.VARIANT)==net.minecraft.block.BlockDirt.DirtType.PODZOL
                || world.getLightFromNeighbors(pos)<13 && below.isSideSolid(world,pos.down(),EnumFacing.UP);
    }
    public void updateTick(World world,BlockPos pos,IBlockState state,Random random) {
        super.updateTick(world,pos,state,random);
        if(!world.isRemote && world.getBlockState(pos).getBlock()==this && world.getBlockState(pos.down()).getBlock()==Content.richSoil
                && world.getLightFromNeighbors(pos.up())<=12 && state.getValue(AGE)<3
                && net.minecraftforge.common.ForgeHooks.onCropsGrowPre(world,pos,state,random.nextInt(5)==0)) {
            world.setBlockState(pos,state.withProperty(AGE,state.getValue(AGE)+1),2);
            net.minecraftforge.common.ForgeHooks.onCropsGrowPost(world,pos,state,world.getBlockState(pos));
        }
    }
    public void getDrops(NonNullList<ItemStack> drops,IBlockAccess world,BlockPos pos,IBlockState state,int fortune) { drops.add(new ItemStack(mushroom(),2+state.getValue(AGE))); }
    public boolean onBlockActivated(World world,BlockPos pos,IBlockState state,EntityPlayer player,EnumHand hand,EnumFacing side,float hx,float hy,float hz) {
        int age=state.getValue(AGE); ItemStack held=player.getHeldItem(hand);
        if(age<=0 || !CuttingRecipes.tool(held,"shears")) return false;
        if(!world.isRemote) { world.setBlockState(pos,state.withProperty(AGE,age-1),2); spawnAsEntity(world,pos,new ItemStack(mushroom())); if(!player.capabilities.isCreativeMode) held.damageItem(1,player); }
        return true;
    }
    public boolean isShearable(ItemStack item,IBlockAccess world,BlockPos pos) { return world.getBlockState(pos).getValue(AGE)==3; }
    public java.util.List<ItemStack> onSheared(ItemStack item,IBlockAccess world,BlockPos pos,int fortune) { return java.util.Collections.singletonList(new ItemStack(this)); }
    public boolean canGrow(World world,BlockPos pos,IBlockState state,boolean client) { return false; }
    public boolean canUseBonemeal(World world,Random random,BlockPos pos,IBlockState state) { return false; }
    public void grow(World world,Random random,BlockPos pos,IBlockState state) { world.setBlockState(pos,state.withProperty(AGE,Math.min(3,state.getValue(AGE)+1)),2); }
}
