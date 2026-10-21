package vectorwing.farmersdelight.legacy;

import java.util.Random;
import net.minecraft.block.Block;

import net.minecraft.block.material.Material;

import net.minecraft.world.World;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public final class SoilBlock extends Block {
    private final boolean compost;
    @SideOnly(Side.CLIENT) private net.minecraft.util.Icon[] stages;
    public SoilBlock(boolean compost) {
        super(LegacyIds.block(compost ? "organic_compost" : "rich_soil"),Material.ground); this.compost=compost; setTickRandomly(true); setHardness(0.5F);
        setUnlocalizedName("farmersdelight."+(compost ? "organic_compost" : "rich_soil")); setCreativeTab(Content.TAB);
        setStepSound(soundGravelFootstep);
    }
    public static boolean rich(Block block) { return block==Content.richSoil || block==Content.richFarmland || block==Content.compost; }
    public static void boost(World world,int x,int y,int z,Random random) {
        if(Settings.richSoilBoost<=0 || random.nextDouble()>Settings.richSoilBoost) return;
        Block block=NativeRegistry.at(world,x,y,z);
        if(block instanceof WildPlant || block instanceof MushroomColony || block instanceof net.minecraft.block.BlockTallGrass) return;
        if(grow(world,x,y,z)) world.playAuxSFX(2005,x,y,z,0);
    }
    public static boolean grow(World world,int x,int y,int z) {
        Block block=NativeRegistry.at(world,x,y,z);
        if(block instanceof RicePlant && ((RicePlant)block).func_149851_a(world,x,y,z,false)) { ((RicePlant)block).func_149853_b(world,world.rand,x,y,z);return true; }
        if(block instanceof RiceUpper && ((RiceUpper)block).func_149851_a(world,x,y,z,false)) { ((RiceUpper)block).func_149853_b(world,world.rand,x,y,z);return true; }
        if(block instanceof net.minecraft.block.BlockCrops && world.getBlockMetadata(x,y,z)<7) { ((net.minecraft.block.BlockCrops)block).fertilize(world,x,y,z);return true; }
        return false;
    }

    private static boolean activator(Block block) {
        return rich(block) || block instanceof MushroomColony || block==Block.mushroomBrown
                || block==Block.mushroomRed || block==Block.mycelium;
    }
    public void updateTick(World world,int x,int y,int z,Random random) {
        if(world.isRemote || !world.checkChunksExist(x-1,y-1,z-1,x+1,y+2,z+1)) return;
        if(!compost) {
            Block above=NativeRegistry.at(world,x,y+1,z);
            if((above==Block.mushroomBrown || above==Block.mushroomRed)
                    && world.getBlockLightValue(x,y+1,z)<=12) {
                Block colony=above==Block.mushroomBrown ? Content.brownColony : Content.redColony;
                NativeRegistry.set(world,x,y+1,z,colony,0,3);
            } else boost(world,x,y+1,z,random);
            return;
        }
        float chance=0; boolean water=false; int sky=0;
        for(int dx=-1;dx<=1;dx++) for(int dy=-1;dy<=1;dy++) for(int dz=-1;dz<=1;dz++) {
            Block block=NativeRegistry.at(world,x+dx,y+dy,z+dz);
            if(activator(block) || block==Block.dirt && world.getBlockMetadata(x+dx,y+dy,z+dz)==2) chance+=0.02F;
            if(block!=null && block.blockMaterial==Material.water) water=true;
            sky=Math.max(sky,world.getSavedLightValue(net.minecraft.world.EnumSkyBlock.Sky,x+dx,y+dy+1,z+dz));
        }
        chance+=sky>12 ? 0.1F : 0.05F; if(water) chance+=0.1F;
        if(random.nextFloat()<=chance) {
            int age=world.getBlockMetadata(x,y,z);
            if(age>=7) NativeRegistry.set(world,x,y,z,Content.richSoil,0,3);
            else world.setBlockMetadataWithNotify(x,y,z,age+1,2);
        }
    }
    public boolean canSustainPlant(World world,int x,int y,int z,ForgeDirection face,IPlantable plant) {
        EnumPlantType type=plant.getPlantType(world,x+face.offsetX,y+face.offsetY,z+face.offsetZ);
        return type!=EnumPlantType.Crop && type!=EnumPlantType.Nether;
    }
    public boolean hasComparatorInputOverride() { return compost; }
    public int getComparatorInputOverride(World world,int x,int y,int z,int side) { return compost ? 8-(world.getBlockMetadata(x,y,z)&7) : 0; }
    @SideOnly(Side.CLIENT) public void registerIcons(net.minecraft.client.renderer.texture.IconRegister register) {
        stages=new net.minecraft.util.Icon[compost ? 4 : 1];
        for(int i=0;i<stages.length;i++) stages[i]=register.registerIcon("farmersdelight:"+(compost ? "organic_compost_stage"+i : "rich_soil"));
    }
    @SideOnly(Side.CLIENT) public net.minecraft.util.Icon getIcon(int side,int meta) { return stages[compost ? (meta&7)/2 : 0]; }
}
