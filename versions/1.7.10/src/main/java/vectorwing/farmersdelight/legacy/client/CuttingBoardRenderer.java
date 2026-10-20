package vectorwing.farmersdelight.legacy.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import vectorwing.farmersdelight.legacy.CuttingBoardTile;
import vectorwing.farmersdelight.legacy.Stacks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityItem;
import org.lwjgl.opengl.GL11;

public final class CuttingBoardRenderer extends TileEntitySpecialRenderer {
    private final EntityItem display = new EntityItem(null);
    @Override public void renderTileEntityAt(TileEntity raw, double x, double y, double z, float partial) {
        CuttingBoardTile tile = (CuttingBoardTile) raw;
        ItemStack stack = tile.getStackInSlot(0);
        if (Stacks.empty(stack)) return;
        boolean block = stack.getItem() instanceof ItemBlock;
        GL11.glPushMatrix();
        GL11.glTranslated(x + 0.5, y + (tile.carved ? 0.25 : block ? 0.27 : 0.09), z + 0.5);
        GL11.glRotatef(tile.getBlockMetadata() * 90, 0, 1, 0);
        if (!tile.carved && !block) GL11.glRotatef(90, 1, 0, 0);
        if (tile.carved) GL11.glRotatef(180, 0, 0, 1);
        GL11.glScalef(1.2F, 1.2F, 1.2F);
        display.worldObj = tile.getWorldObj(); display.setEntityItemStack(stack); display.hoverStart = 0;
        RenderManager.instance.renderEntityWithPosYaw(display, 0, 0, 0, 0, 0);
        GL11.glPopMatrix();
    }
}
