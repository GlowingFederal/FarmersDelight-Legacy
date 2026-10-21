package vectorwing.farmersdelight.legacy;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.tileentity.TileEntity;
public final class CanvasSignItem extends Item {
    private final CanvasSignBlock standing,wall;
    public CanvasSignItem(CanvasSignBlock standing,CanvasSignBlock wall) { this.standing=standing; this.wall=wall; setMaxStackSize(16); }
    public boolean onItemUse(ItemStack stack,EntityPlayer player,World world,net.minecraft.util.BlockPos clicked,net.minecraft.util.EnumFacing face,float hx,float hy,float hz) {
        
        if(face==net.minecraft.util.EnumFacing.DOWN || !world.getBlockState(clicked).getBlock().getMaterial().isSolid()) return false;
        net.minecraft.util.BlockPos pos=clicked.offset(face);
        if(!player.canPlayerEdit(pos,face,stack) || !standing.canPlaceBlockAt(world,pos)) return false;
        if(world.isRemote) return true;
        CanvasSignBlock block=face==net.minecraft.util.EnumFacing.UP ? standing : wall;
        int meta=face==net.minecraft.util.EnumFacing.UP ? net.minecraft.util.MathHelper.floor_double((player.rotationYaw+180)*16/360+0.5)&15 : face.getIndex();
        if(!world.setBlockState(pos,block.getStateFromMeta(meta),3)) return false;
        TileEntity raw=world.getTileEntity(pos);
        if(raw instanceof CanvasSignTile) {
            CanvasSignTile tile=(CanvasSignTile)raw;
            tile.textColor=Settings.darkSignBackgrounds.contains(standing.background) ? 0xFFFFFF : 0;
            if(!net.minecraft.item.ItemBlock.setTileEntityNBT(world,player,pos,stack)) player.openEditSign(tile);
            tile.markDirty();
            world.markBlockForUpdate(pos);
        }
        if(!player.capabilities.isCreativeMode) Stacks.shrink(stack,1);
        return true;
    }
}

