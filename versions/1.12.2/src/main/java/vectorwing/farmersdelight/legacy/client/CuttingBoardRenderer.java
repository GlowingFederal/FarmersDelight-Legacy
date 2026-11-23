package vectorwing.farmersdelight.legacy.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import vectorwing.farmersdelight.legacy.CuttingBoardTile;
import vectorwing.farmersdelight.legacy.Stacks;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;

public final class CuttingBoardRenderer extends TileEntitySpecialRenderer<CuttingBoardTile> {
    @Override public void render(CuttingBoardTile tile, double x, double y, double z, float partial, int stage, float alpha) {
        ItemStack stack = tile.getStackInSlot(0);
        if (Stacks.empty(stack)) return;
        boolean block = Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(stack, tile.getWorld(), null).isGui3d();
        int facing = (tile.getBlockMetadata() + 2) & 3;
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5, y + (tile.carved ? 0.23 : block ? 0.27 : 0.08), z + 0.5);
        GlStateManager.rotate(-facing * 90 + (tile.carved ? 180 : 0), 0, 1, 0);
        if (!tile.carved && !block) GlStateManager.rotate(90, 1, 0, 0);
        if (tile.carved) {
            boolean angleTool = stack.getItem() instanceof ItemPickaxe || stack.getItem() instanceof ItemHoe;
            GlStateManager.rotate(angleTool ? 225 : 180, 0, 0, 1);
        }
        float scale = block && !tile.carved ? 0.8F : 0.6F;
        GlStateManager.scale(scale, scale, scale);
        Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
        GlStateManager.popMatrix();
    }
}
