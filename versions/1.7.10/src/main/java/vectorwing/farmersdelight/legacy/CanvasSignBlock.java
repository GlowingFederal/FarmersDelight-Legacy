package vectorwing.farmersdelight.legacy;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSign;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;


public final class CanvasSignBlock extends BlockSign {
    public final boolean wall;
    public final String background,id;
    private static final int[] DYE_TEXT={0x1D1D21,0xB02E26,0x5E7C16,0x835432,0x3C44AA,0x8932B8,0x169C9C,0x9D9D97,0x474F52,0xF38BAA,0x80C71F,0xFED83D,0x3AB3DA,0xC74EBD,0xF9801D,0xF9FFFE};

    public CanvasSignBlock(String background,boolean wall) {
        super(CanvasSignTile.class,!wall); this.background=background; this.wall=wall;
        id=background.isEmpty() ? "canvas_sign" : background+"_canvas_sign";
        setBlockName("farmersdelight."+id); setHardness(1F);

    }
    public TileEntity createNewTileEntity(World world,int meta) { return new CanvasSignTile(); }
    public Item getItemDropped(int meta,java.util.Random random,int fortune) { return Content.ITEMS.get(id); }
    public Item getItem(World world,int x,int y,int z) { return Content.ITEMS.get(id); }
    public boolean onBlockActivated(World world,int x,int y,int z,EntityPlayer player,int side,float hx,float hy,float hz) {
        ItemStack held=player.getHeldItem();
        TileEntity raw=world.getTileEntity(x,y,z);
        if(!(raw instanceof CanvasSignTile) || Stacks.empty(held) || held.getItem()!=net.minecraft.init.Items.dye
                || !player.canPlayerEdit(x,y,z,side,held)) return false;
        CanvasSignTile tile=(CanvasSignTile)raw;
        int color=DYE_TEXT[held.getItemDamage()&15];
        if(tile.textColor==color) return false;
        if(!world.isRemote) {
            tile.textColor=color; tile.markDirty();
            world.markBlockForUpdate(x,y,z);
            if(!player.capabilities.isCreativeMode) Stacks.shrink(held,1);
        }
        return true;
    }
}

