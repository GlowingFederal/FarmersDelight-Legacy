package vectorwing.farmersdelight.legacy;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.material.Material;

import net.minecraft.item.Item;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public final class RichFarmland extends BlockFarmland {
    @SideOnly(Side.CLIENT) private net.minecraft.util.Icon dry,moist,side,moistSide;
    public RichFarmland() { super(LegacyIds.block("rich_soil_farmland")); setHardness(0.6F); setUnlocalizedName("farmersdelight.rich_soil_farmland"); }
    private boolean covered(World world,int x,int y,int z) {
        Block block=NativeRegistry.at(world,x,y+1,z);
        return block.blockMaterial.isSolid() && block!=Block.melon && block!=Block.pumpkinLantern;
    }
    private void revert(World world,int x,int y,int z) { NativeRegistry.set(world,x,y,z,Content.richSoil,0,3); }
    public void onNeighborBlockChange(World world,int x,int y,int z,int changed) { if(!world.isRemote && covered(world,x,y,z)) revert(world,x,y,z); }
    public void updateTick(World world,int x,int y,int z,Random random) {
        if(world.isRemote || !world.checkChunksExist(x-4,y,z-4,x+4,y+1,z+4)) return;
        if(covered(world,x,y,z)) { revert(world,x,y,z); return; }
        boolean water=world.canLightningStrikeAt(x,y+1,z);
        for(int dx=-4;dx<=4 && !water;dx++) for(int dz=-4;dz<=4 && !water;dz++) for(int dy=0;dy<=1;dy++)
            if(world.getBlockMaterial(x+dx,y+dy,z+dz)==Material.water) water=true;
        int moisture=world.getBlockMetadata(x,y,z)&7;
        int next=water ? 7 : Math.max(0,moisture-1);
        if(next!=moisture) world.setBlockMetadataWithNotify(x,y,z,next,2);
        else if(moisture==7) SoilBlock.boost(world,x,y+1,z,random);
    }
    public void onFallenUpon(World world,int x,int y,int z,Entity entity,float distance) {}
    public int idDropped(int meta,Random random,int fortune) { return NativeRegistry.idOrZero(NativeRegistry.item(Content.richSoil)); }
    public boolean canSustainPlant(World world,int x,int y,int z,ForgeDirection face,IPlantable plant) {
        EnumPlantType type=plant.getPlantType(world,x+face.offsetX,y+face.offsetY,z+face.offsetZ); return type==EnumPlantType.Crop || type==EnumPlantType.Plains;
    }
    public boolean isFertile(World world,int x,int y,int z) { return (world.getBlockMetadata(x,y,z)&7)>0; }
    @SideOnly(Side.CLIENT) public void registerIcons(net.minecraft.client.renderer.texture.IconRegister register) {
        dry=register.registerIcon("farmersdelight:rich_soil_farmland"); moist=register.registerIcon("farmersdelight:rich_soil_farmland_moist");
        side=register.registerIcon("farmersdelight:rich_soil"); moistSide=register.registerIcon("farmersdelight:rich_soil_farmland_moist_side");
    }
    @SideOnly(Side.CLIENT) public net.minecraft.util.Icon getIcon(int face,int meta) { return face==1 ? meta>0 ? moist : dry : meta>0 ? moistSide : side; }
}
