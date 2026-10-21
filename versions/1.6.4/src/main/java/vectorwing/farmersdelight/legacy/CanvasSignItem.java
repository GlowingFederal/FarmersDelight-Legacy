package vectorwing.farmersdelight.legacy;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.tileentity.TileEntity;
public final class CanvasSignItem extends Item {
    private final CanvasSignBlock standing,wall;
    public CanvasSignItem(CanvasSignBlock standing,CanvasSignBlock wall) { super(LegacyIds.item(standing.id)); this.standing=standing; this.wall=wall; setMaxStackSize(16); }
    public boolean onItemUse(ItemStack stack,EntityPlayer player,World world,int x,int y,int z,int face,float hx,float hy,float hz) {
        if(face==0 || !world.getBlockMaterial(x,y,z).isSolid()) return false;
        net.minecraftforge.common.ForgeDirection direction=net.minecraftforge.common.ForgeDirection.getOrientation(face);
        x+=direction.offsetX; y+=direction.offsetY; z+=direction.offsetZ;
        if(!player.canPlayerEdit(x,y,z,face,stack) || !standing.canPlaceBlockAt(world,x,y,z)) return false;
        if(world.isRemote) return true;
        CanvasSignBlock block=face==1 ? standing : wall;
        int meta=face==1 ? net.minecraft.util.MathHelper.floor_double((player.rotationYaw+180)*16/360+0.5)&15 : face;
        if(!NativeRegistry.set(world,x,y,z,block,meta,3)) return false;
        TileEntity raw=world.getBlockTileEntity(x,y,z);
        if(!player.capabilities.isCreativeMode) Stacks.shrink(stack,1);
        if(raw instanceof CanvasSignTile) {
            CanvasSignTile tile=(CanvasSignTile)raw;
            tile.textColor=Settings.darkSignBackgrounds.contains(standing.background) ? 0xFFFFFF : 0;
            player.displayGUIEditSign(tile);
            tile.onInventoryChanged();
            world.markBlockForUpdate(x,y,z);
        }
        return true;
    }
}

