package vectorwing.farmersdelight.legacy;

import java.util.ArrayList;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.world.IBlockAccess;

public class NativeCrop extends BlockCrops {
    protected final String kind;
    private static final int[] TOMATO_HEIGHT={6,6,10,10,13,13,16,16}, CABBAGE_HEIGHT={2,3,5,7,8,9,9,10};

    public NativeCrop(String kind) { super(); this.kind=kind; setTranslationKey("farmersdelight."+kind); }
    protected Item seed() { return Content.ITEMS.get(kind.equals("cabbages") ? "cabbage_seeds" : kind.equals("tomatoes") ? "tomato_seeds" : "onion"); }
    protected Item produce() { return Content.ITEMS.get(kind.equals("cabbages") ? "cabbage" : kind.equals("tomatoes") ? "tomato" : "onion"); }
    protected Item getSeed() { return seed(); }
    protected Item getCrop() { return produce(); }
    protected boolean canSustainBush(IBlockState state) { return state.getBlock()==net.minecraft.init.Blocks.FARMLAND || state.getBlock()==Content.richFarmland; }
    protected static int bonus(Random random,int fortune) {
        int count=1;
        for(int i=0;i<3+Math.max(0,fortune);i++) if(random.nextFloat()<4F/7F) count++;
        return count;
    }
    protected void drops(java.util.List<ItemStack> drops,int age,int fortune,Random random) {
        if(age<7) { drops.add(new ItemStack(seed())); return; }
        if(kind.equals("tomatoes")) {
            drops.add(new ItemStack(produce(),1+random.nextInt(2)+random.nextInt(Math.max(0,fortune)+1)));
            drops.add(new ItemStack(seed()));
            if(random.nextFloat()<0.05F) drops.add(new ItemStack(Content.ITEMS.get("rotten_tomato")));
        } else {
            drops.add(new ItemStack(produce()));
            drops.add(new ItemStack(seed(),bonus(random,fortune)));
        }
    }
    @Override public void getDrops(NonNullList<ItemStack> drops,IBlockAccess world,BlockPos pos,IBlockState state,int fortune) {
        drops(drops,getAge(state),fortune,world instanceof World ? ((World)world).rand : RANDOM);
    }
    @Override public boolean onBlockActivated(World world,BlockPos pos,IBlockState state,EntityPlayer player,EnumHand hand,EnumFacing side,float x,float y,float z) {
        if(!kind.equals("tomatoes") || getAge(state)<7) return false;
        if(!world.isRemote) {
            world.setBlockState(pos,withAge(5),2);
            spawnAsEntity(world,pos,new ItemStack(produce(),1+world.rand.nextInt(2)));
            if(world.rand.nextFloat()<0.05F) spawnAsEntity(world,pos,new ItemStack(Content.ITEMS.get("rotten_tomato")));
            world.playSound(null,pos,net.minecraft.init.SoundEvents.BLOCK_GRASS_BREAK,net.minecraft.util.SoundCategory.BLOCKS,0.8F,1F);
        }
        return true;
    }
    @Override public AxisAlignedBB getBoundingBox(IBlockState state,IBlockAccess world,BlockPos pos) {
        int age=getAge(state);
        int height=kind.equals("tomatoes") ? TOMATO_HEIGHT[age] : kind.equals("cabbages") ? CABBAGE_HEIGHT[age] : age+2;
        return new AxisAlignedBB(0,0,0,1,height/16D,1);
    }
}
