package vectorwing.farmersdelight.legacy;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.EnumPlantType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;

public final class SoilBlock extends Block {
    private final boolean compost;
    public static final PropertyInteger COMPOSTING=PropertyInteger.create("composting",0,7);
    public SoilBlock(boolean compost) {
        super(Material.GROUND); this.compost=compost; setTickRandomly(true); setHardness(0.5F);
        setTranslationKey("farmersdelight."+(compost ? "organic_compost" : "rich_soil")); setCreativeTab(Content.TAB);
        setSoundType(net.minecraft.block.SoundType.GROUND); setDefaultState(blockState.getBaseState().withProperty(COMPOSTING,0));
    }
    public static boolean rich(Block block) { return block==Content.richSoil || block==Content.richFarmland || block==Content.compost; }
    public static void boost(World world,BlockPos pos,Random random) {
        if(Settings.richSoilBoost<=0 || random.nextDouble()>Settings.richSoilBoost) return;
        Block block=world.getBlockState(pos).getBlock();
        if(block instanceof WildPlant || block instanceof MushroomColony || block instanceof net.minecraft.block.BlockTallGrass || block instanceof net.minecraft.block.BlockDoublePlant) return;
        if(block instanceof IGrowable) {
            IGrowable grow=(IGrowable)block;
            IBlockState state=world.getBlockState(pos);
            if(grow.canGrow(world,pos,state,false) && net.minecraftforge.common.ForgeHooks.onCropsGrowPre(world,pos,state,true)) {
                grow.grow(world,random,pos,state); world.playEvent(2005,pos,0);
                net.minecraftforge.common.ForgeHooks.onCropsGrowPost(world,pos,state,world.getBlockState(pos));
            }
        }
    }
    private static boolean activator(Block block) {
        return rich(block) || block instanceof MushroomColony || block==Blocks.BROWN_MUSHROOM
                || block==Blocks.RED_MUSHROOM || block==Blocks.MYCELIUM;
    }
    public void updateTick(World world,BlockPos pos,IBlockState state,Random random) {
        if(world.isRemote || !world.isAreaLoaded(pos,1)) return;
        if(!compost) {
            Block above=world.getBlockState(pos.up()).getBlock();
            if((above==Blocks.BROWN_MUSHROOM || above==Blocks.RED_MUSHROOM)
                    && world.getLightFromNeighbors(pos.up())<=12) {
                Block colony=above==Blocks.BROWN_MUSHROOM ? Content.brownColony : Content.redColony;
                world.setBlockState(pos.up(),colony.getDefaultState(),3);
            } else boost(world,pos.up(),random);
            return;
        }
        float chance=0; boolean water=false; int sky=0;
        for(int dx=-1;dx<=1;dx++) for(int dy=-1;dy<=1;dy++) for(int dz=-1;dz<=1;dz++) {
            BlockPos near=pos.add(dx,dy,dz); IBlockState nearState=world.getBlockState(near);
            if(activator(nearState.getBlock()) || nearState.getBlock()==Blocks.DIRT && nearState.getValue(net.minecraft.block.BlockDirt.VARIANT)==net.minecraft.block.BlockDirt.DirtType.PODZOL) chance+=0.02F;
            if(nearState.getMaterial()==Material.WATER) water=true;
            sky=Math.max(sky,world.getLightFor(net.minecraft.world.EnumSkyBlock.SKY,near.up()));
        }
        chance+=sky>12 ? 0.1F : 0.05F; if(water) chance+=0.1F;
        if(random.nextFloat()<=chance) {
            int age=state.getValue(COMPOSTING);
            if(age>=7) world.setBlockState(pos,Content.richSoil.getDefaultState(),3);
            else world.setBlockState(pos,state.withProperty(COMPOSTING,age+1),2);
        }
    }
    public boolean canSustainPlant(IBlockState state,IBlockAccess world,BlockPos pos,EnumFacing face,IPlantable plant) {
        EnumPlantType type=plant.getPlantType(world,pos.offset(face)); return type!=EnumPlantType.Crop && type!=EnumPlantType.Nether;
    }
    protected BlockStateContainer createBlockState() { return new BlockStateContainer(this,COMPOSTING); }
    public IBlockState getStateFromMeta(int meta) { return getDefaultState().withProperty(COMPOSTING,compost ? meta&7 : 0); }
    public int getMetaFromState(IBlockState state) { return compost ? state.getValue(COMPOSTING) : 0; }
    public boolean hasComparatorInputOverride(IBlockState state) { return compost; }
    public int getComparatorInputOverride(IBlockState state,World world,BlockPos pos) { return compost ? 8-state.getValue(COMPOSTING) : 0; }
}
