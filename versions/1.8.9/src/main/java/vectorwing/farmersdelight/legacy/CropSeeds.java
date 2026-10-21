package vectorwing.farmersdelight.legacy;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.EnumPlantType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.IBlockAccess;
import net.minecraft.util.EnumFacing;

import net.minecraft.util.BlockPos;

public final class CropSeeds extends Item implements IPlantable {
    private final String kind;
    public CropSeeds(String kind) { this.kind=kind; }
    public Block crop() {
        return kind.equals("rice") ? Content.riceCrop : kind.equals("wild_rice") ? Content.wildRice
                : kind.equals("cabbage") ? Content.cabbages : Content.tomatoes;
    }
    public EnumPlantType getPlantType(IBlockAccess world,BlockPos pos) { return EnumPlantType.Crop; }
    public IBlockState getPlant(IBlockAccess world,BlockPos pos) { return crop().getDefaultState(); }

    public static boolean plant(Block crop,ItemStack seed,EntityPlayer player,World world,BlockPos ground,EnumFacing side) {
        BlockPos pos=ground.up();
        if(side!=EnumFacing.UP || !player.canPlayerEdit(pos,side,seed)) return false;
        boolean allowed=crop instanceof RicePlant ? ((RicePlant)crop).canPlant(world,pos)
                : world.isAirBlock(pos) && crop.canPlaceBlockAt(world,pos);
        if(!allowed) return false;
        if(!world.isRemote) {
            if(crop instanceof RicePlant) ((RicePlant)crop).place(world,pos); else world.setBlockState(pos,crop.getDefaultState(),3);
            if(!player.capabilities.isCreativeMode) Stacks.shrink(seed,1);
        }
        return true;
    }
    @Override public boolean onItemUse(ItemStack stack,EntityPlayer player,World world,BlockPos pos,EnumFacing side,float hx,float hy,float hz) {
        return plant(crop(),stack,player,world,pos,side);
    }
}
