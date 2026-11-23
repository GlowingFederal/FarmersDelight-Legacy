package vectorwing.farmersdelight.legacy.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import java.util.Random;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemBlock;
import vectorwing.farmersdelight.legacy.GrillingTile;
import vectorwing.farmersdelight.legacy.StoveTile;
import vectorwing.farmersdelight.legacy.Stacks;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;

public final class GrillingRenderer extends TileEntitySpecialRenderer<GrillingTile> {
    private final Random skilletOffsets = new Random();

    public void render(GrillingTile tile,double x,double y,double z,float partial,int stage,float alpha) {
        if (!(tile instanceof StoveTile)) {
            renderSkillet(tile, x, y, z);
            return;
        }
        for(int i=0;i<tile.getSizeInventory();i++) {
            ItemStack stack=tile.getStackInSlot(i); if(Stacks.empty(stack)) continue;
            double px=0.3+(i%3)*0.2, pz=0.35+(i/3)*0.3;
            GlStateManager.pushMatrix(); GlStateManager.translate(x+px,y+1.03,z+pz);
            if(!(stack.getItem() instanceof ItemBlock)) GlStateManager.rotate(90,1,0,0);
            GlStateManager.scale(0.25F,0.25F,0.25F);
            Minecraft.getMinecraft().getRenderItem().renderItem(stack,ItemCameraTransforms.TransformType.FIXED); GlStateManager.popMatrix();
        }
    }

    private void renderSkillet(GrillingTile tile, double x, double y, double z) {
        ItemStack stack = tile.getStackInSlot(0);
        if (Stacks.empty(stack)) return;
        int count = Stacks.size(stack);
        int layers = count == 1 ? 1 : 2 + (count - 1) / 16;
        skilletOffsets.setSeed(Item.getIdFromItem(stack.getItem()) + stack.getItemDamage());
        for (int layer = 0; layer < layers; layer++) {
            double dx = (skilletOffsets.nextFloat() * 2 - 1) * 0.075;
            double dz = (skilletOffsets.nextFloat() * 2 - 1) * 0.075;
            GlStateManager.pushMatrix();
            GlStateManager.translate(x + 0.5 + dx, y + 0.1 + 0.03 * (layer + 1), z + 0.5 + dz);
            GlStateManager.rotate(-(tile.getBlockMetadata() & 3) * 90, 0, 1, 0);
            GlStateManager.rotate(90, 1, 0, 0);
            GlStateManager.scale(0.5F, 0.5F, 0.5F);
            Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
            GlStateManager.popMatrix();
        }
    }
}
