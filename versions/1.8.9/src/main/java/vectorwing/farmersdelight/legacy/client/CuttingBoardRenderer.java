package vectorwing.farmersdelight.legacy.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import vectorwing.farmersdelight.legacy.CuttingBoardTile;
import vectorwing.farmersdelight.legacy.Stacks;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;

public final class CuttingBoardRenderer extends TileEntitySpecialRenderer<CuttingBoardTile> {
    @Override public void renderTileEntityAt(CuttingBoardTile tile, double x, double y, double z, float partial, int stage) {
        ItemStack stack = tile.getStackInSlot(0);
        if (Stacks.empty(stack)) return;
        boolean block = stack.getItem() instanceof ItemBlock;
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5, y + (tile.carved ? 0.25 : block ? 0.27 : 0.09), z + 0.5);
        GlStateManager.rotate(tile.getBlockMetadata() * 90, 0, 1, 0);
        if (!tile.carved && !block) GlStateManager.rotate(90, 1, 0, 0);
        if (tile.carved) GlStateManager.rotate(180, 0, 0, 1);
        GlStateManager.scale(0.6F, 0.6F, 0.6F);
        Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
        GlStateManager.popMatrix();
    }
}
