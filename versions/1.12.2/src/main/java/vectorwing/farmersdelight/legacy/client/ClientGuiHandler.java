package vectorwing.farmersdelight.legacy.client;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import vectorwing.farmersdelight.legacy.CookingPotTile;

public final class ClientGuiHandler extends vectorwing.farmersdelight.legacy.GuiHandler {
    @Override public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(new net.minecraft.util.math.BlockPos(x, y, z));
        if(id==1 && tile instanceof vectorwing.farmersdelight.legacy.StorageTile) return new net.minecraft.client.gui.inventory.GuiChest(player.inventory,(vectorwing.farmersdelight.legacy.StorageTile)tile);
        return id == 0 && tile instanceof CookingPotTile ? new CookingPotGui(player.inventory, (CookingPotTile) tile) : null;
    }
}
