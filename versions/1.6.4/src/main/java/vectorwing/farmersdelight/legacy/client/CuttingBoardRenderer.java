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
        GL11.glTranslated(x + 0.5, y + (tile.carved ? 0.23 : block ? 0.27 : 0.08), z + 0.5);
        GL11.glRotatef(-tile.getBlockMetadata() * 90 + (tile.carved ? 180 : 0), 0, 1, 0);
        if (!tile.carved && !block) GL11.glRotatef(90, 1, 0, 0);
        if (tile.carved) {
            boolean angleTool = stack.getItem() instanceof net.minecraft.item.ItemPickaxe || stack.getItem() instanceof net.minecraft.item.ItemHoe;
            GL11.glRotatef(angleTool ? 225 : 180, 0, 0, 1);
        }
        GL11.glScalef(1.2F, 1.2F, 1.2F);
        display.worldObj = tile.getWorldObj(); display.setEntityItemStack(stack); display.hoverStart = 0;
        boolean wasInFrame = RenderItem.renderInFrame;
        RenderItem.renderInFrame = true;
        try {
            // The dropped-item renderer adds a fixed 0.1 block bob at age zero.
            RenderManager.instance.renderEntityWithPosYaw(display, 0, -0.1, 0, 0, 0);
        } finally {
            RenderItem.renderInFrame = wasInFrame;
            GL11.glPopMatrix();
        }

    }
}
