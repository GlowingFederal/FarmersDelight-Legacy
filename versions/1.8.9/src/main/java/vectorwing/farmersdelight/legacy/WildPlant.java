package vectorwing.farmersdelight.legacy;

import java.util.ArrayList;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.IGrowable;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.IShearable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;


public final class WildPlant extends BlockBush implements IGrowable,IShearable {
    public final String kind;
    public WildPlant(String kind) {
        super(); this.kind=kind; setUnlocalizedName("farmersdelight.wild_"+kind);
        setCreativeTab(Content.TAB);
    }
    private String seed() {
        return kind.equals("cabbages") ? "farmersdelight:cabbage_seeds" : kind.equals("tomatoes") ? "farmersdelight:tomato_seeds"
                : kind.equals("onions") ? "farmersdelight:onion" : kind.equals("beetroots") ? "minecraft:beetroot_seeds"
                : kind.equals("carrots") ? "minecraft:carrot" : "minecraft:potato";
    }
    private String produce() { return kind.equals("cabbages") ? "farmersdelight:cabbage" : kind.equals("tomatoes") ? "farmersdelight:tomato" : "minecraft:beetroot"; }
    protected boolean canSustainBush(IBlockState state) { Block block=state.getBlock(); return block==Blocks.grass || block==Blocks.dirt || block==Blocks.sand || SoilBlock.rich(block); }
    public boolean isReplaceable(World world,BlockPos pos) { return false; }
    public java.util.List<ItemStack> getDrops(IBlockAccess world,BlockPos pos,IBlockState state,int fortune) { java.util.List<ItemStack> drops=new java.util.ArrayList<ItemStack>(); addDrops(drops,fortune,world instanceof World ? ((World)world).rand : RANDOM); return drops; }
    public boolean isShearable(ItemStack item,IBlockAccess world,BlockPos pos) { return true; }
    public java.util.List<ItemStack> onSheared(ItemStack item,IBlockAccess world,BlockPos pos,int fortune) { return java.util.Collections.singletonList(new ItemStack(this)); }
    public int getFlammability(IBlockAccess world,BlockPos pos,EnumFacing face) { return 100; }
    public int getFireSpreadSpeed(IBlockAccess world,BlockPos pos,EnumFacing face) { return 60; }
    public boolean canGrow(World world,BlockPos pos,IBlockState state,boolean client) { return true; }
    public boolean canUseBonemeal(World world,Random random,BlockPos pos,IBlockState state) { return random.nextFloat()<0.8F; }
    public void grow(World world,Random random,BlockPos pos,IBlockState state) {
        if(!world.isAreaLoaded(pos,4)) return;
        int count=0;
        for(BlockPos near:BlockPos.getAllInBox(pos.add(-4,-1,-4),pos.add(4,1,4))) if(world.getBlockState(near).getBlock()==this && ++count>=10) return;
        BlockPos cursor=pos;
        for(int step=0;step<5;step++) {
            BlockPos near=cursor.add(random.nextInt(3)-1,random.nextInt(2)-random.nextInt(2),random.nextInt(3)-1);
            if(near.getY()>0 && near.getY()<255 && world.isAirBlock(near) && canBlockStay(world,near,state)) {
                if(step==4) world.setBlockState(near,getDefaultState(),3); else cursor=near;
            }
        }
    }
    private void addDrops(java.util.List<ItemStack> drops,int fortune,Random random) {
        ItemStack seed=Stacks.resolve(seed());
        if(!Stacks.empty(seed)) { Stacks.count(seed,1+random.nextInt(Math.max(0,fortune)*2+1)); drops.add(seed); }
        if((kind.equals("cabbages") || kind.equals("tomatoes") || kind.equals("beetroots")) && random.nextFloat()<0.2F) {
            ItemStack produce=Stacks.resolve(produce()); if(!Stacks.empty(produce)) drops.add(produce);
        }
    }
}
