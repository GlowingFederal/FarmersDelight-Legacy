package vectorwing.farmersdelight.legacy;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.EnumPlantType;
import net.minecraft.world.IBlockAccess;

public final class CropSeeds extends Item implements IPlantable {
    private final String kind;
    public CropSeeds(String kind) { this.kind=kind; }
    public Block crop() {
        return kind.equals("rice") ? Content.riceCrop : kind.equals("wild_rice") ? Content.wildRice
                : kind.equals("cabbage") ? Content.cabbages : Content.tomatoes;
    }
    public EnumPlantType getPlantType(IBlockAccess world,int x,int y,int z) { return EnumPlantType.Crop; }
    public Block getPlant(IBlockAccess world,int x,int y,int z) { return crop(); }
    public int getPlantMetadata(IBlockAccess world,int x,int y,int z) { return 0; }
    public static boolean plant(Block crop,ItemStack seed,EntityPlayer player,World world,int x,int y,int z,int side) {
        if(side!=1 || !player.canPlayerEdit(x,y+1,z,side,seed)) return false;
        int py=y+1;
        boolean allowed=crop instanceof RicePlant ? ((RicePlant)crop).canPlant(world,x,py,z)
                : world.isAirBlock(x,py,z) && crop.canPlaceBlockAt(world,x,py,z);
        if(!allowed) return false;
        if(!world.isRemote) {
            if(crop instanceof RicePlant) ((RicePlant)crop).place(world,x,py,z); else world.setBlock(x,py,z,crop,0,3);
            if(!player.capabilities.isCreativeMode) Stacks.shrink(seed,1);
        }
        return true;
    }
    @Override public boolean onItemUse(ItemStack stack,EntityPlayer player,World world,int x,int y,int z,int side,float hx,float hy,float hz) {
        return plant(crop(),stack,player,world,x,y,z,side);
    }
}
