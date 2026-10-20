package vectorwing.farmersdelight.legacy;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.tileentity.TileEntity;
public final class CanvasSignItem extends Item {
    private final CanvasSignBlock standing,wall;
    public CanvasSignItem(CanvasSignBlock standing,CanvasSignBlock wall) { this.standing=standing; this.wall=wall; setMaxStackSize(16); }
    public net.minecraft.util.EnumActionResult onItemUse(EntityPlayer player,World world,net.minecraft.util.math.BlockPos clicked,net.minecraft.util.EnumHand hand,net.minecraft.util.EnumFacing face,float hx,float hy,float hz) {
        ItemStack stack=player.getHeldItem(hand);
        if(face==net.minecraft.util.EnumFacing.DOWN || !world.getBlockState(clicked).getMaterial().isSolid()) return net.minecraft.util.EnumActionResult.FAIL;
        net.minecraft.util.math.BlockPos pos=clicked.offset(face);
        if(!player.canPlayerEdit(pos,face,stack) || !standing.canPlaceBlockAt(world,pos)) return net.minecraft.util.EnumActionResult.FAIL;
        if(world.isRemote) return net.minecraft.util.EnumActionResult.SUCCESS;
        CanvasSignBlock block=face==net.minecraft.util.EnumFacing.UP ? standing : wall;
        int meta=face==net.minecraft.util.EnumFacing.UP ? net.minecraft.util.math.MathHelper.floor((player.rotationYaw+180)*16/360+0.5)&15 : face.getIndex();
        if(!world.setBlockState(pos,block.getStateFromMeta(meta),3)) return net.minecraft.util.EnumActionResult.FAIL;
        TileEntity raw=world.getTileEntity(pos);
        if(raw instanceof CanvasSignTile) {
            CanvasSignTile tile=(CanvasSignTile)raw;
            tile.textColor=Settings.darkSignBackgrounds.contains(standing.background) ? 0xFFFFFF : 0;
            if(!net.minecraft.item.ItemBlock.setTileEntityNBT(world,player,pos,stack)) player.openEditSign(tile);
            tile.markDirty();
            world.notifyBlockUpdate(pos,world.getBlockState(pos),world.getBlockState(pos),3);
        }
        if(!player.capabilities.isCreativeMode) Stacks.shrink(stack,1);
        return net.minecraft.util.EnumActionResult.SUCCESS;
    }
}

