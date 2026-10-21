package vectorwing.farmersdelight.legacy;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.EnumPlantType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public final class RichFarmland extends BlockFarmland {

    public RichFarmland() { super(); setHardness(0.6F); setUnlocalizedName("farmersdelight.rich_soil_farmland"); }
    private boolean covered(World world,BlockPos pos) {
        Block block=world.getBlockState(pos.up()).getBlock();
        return world.getBlockState(pos.up()).getBlock().getMaterial().isSolid() && block!=Blocks.melon_block && block!=Blocks.pumpkin;
    }
    private void revert(World world,BlockPos pos) { world.setBlockState(pos,Content.richSoil.getDefaultState(),3); }
    public void onNeighborBlockChange(World world,BlockPos pos,IBlockState state,Block changed) { if(!world.isRemote && covered(world,pos)) revert(world,pos); }
    public void updateTick(World world,BlockPos pos,IBlockState state,Random random) {
        if(world.isRemote || !world.isAreaLoaded(pos,4)) return;
        if(covered(world,pos)) { revert(world,pos); return; }
        boolean water=world.isRainingAt(pos.up());
        for(int dx=-4;dx<=4 && !water;dx++) for(int dz=-4;dz<=4 && !water;dz++) for(int dy=0;dy<=1;dy++)
            if(world.getBlockState(pos.add(dx,dy,dz)).getBlock().getMaterial()==Material.water) water=true;
        int moisture=state.getValue(MOISTURE);
        int next=water ? 7 : Math.max(0,moisture-1);
        if(next!=moisture) world.setBlockState(pos,state.withProperty(MOISTURE,next),2);
        else if(moisture==7) SoilBlock.boost(world,pos.up(),random);
    }
    public void onFallenUpon(World world,BlockPos pos,Entity entity,float distance) { entity.fall(distance,1F); }
    public Item getItemDropped(IBlockState state,Random random,int fortune) { return Item.getItemFromBlock(Content.richSoil); }
    public boolean canSustainPlant(IBlockAccess world,BlockPos pos,EnumFacing face,IPlantable plant) {
        EnumPlantType type=plant.getPlantType(world,pos.offset(face)); return type==EnumPlantType.Crop || type==EnumPlantType.Plains;
    }
    public boolean isFertile(World world,BlockPos pos) { return world.getBlockState(pos).getValue(MOISTURE)>0; }
}
