package vectorwing.farmersdelight.legacy;

import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class GuiHandler implements IGuiHandler {
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(new net.minecraft.util.BlockPos(x, y, z));
        if(id==1 && tile instanceof StorageTile) return new net.minecraft.inventory.ContainerChest(player.inventory,(StorageTile)tile,player);
        return id == 0 && tile instanceof CookingPotTile ? new CookingPotContainer(player.inventory, (CookingPotTile) tile) : null;
    }
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) { return null; }
}
