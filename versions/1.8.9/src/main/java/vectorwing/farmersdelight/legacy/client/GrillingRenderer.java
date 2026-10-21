package vectorwing.farmersdelight.legacy.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemBlock;
import vectorwing.farmersdelight.legacy.GrillingTile;
import vectorwing.farmersdelight.legacy.StoveTile;
import vectorwing.farmersdelight.legacy.Stacks;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;

public final class GrillingRenderer extends TileEntitySpecialRenderer<GrillingTile> {
    public void renderTileEntityAt(GrillingTile tile,double x,double y,double z,float partial,int stage) {
        boolean stove=tile instanceof StoveTile;
        for(int i=0;i<tile.getSizeInventory();i++) {
            ItemStack stack=tile.getStackInSlot(i); if(Stacks.empty(stack)) continue;
            double px=stove ? 0.3+(i%3)*0.2 : 0.5, pz=stove ? 0.35+(i/3)*0.3 : 0.5;
            GlStateManager.pushMatrix(); GlStateManager.translate(x+px,y+(stove ? 1.03 : 0.13),z+pz);
            if(!(stack.getItem() instanceof ItemBlock)) GlStateManager.rotate(90,1,0,0);
            GlStateManager.scale(stove ? 0.25F : 0.5F,stove ? 0.25F : 0.5F,stove ? 0.25F : 0.5F);
            Minecraft.getMinecraft().getRenderItem().renderItem(stack,ItemCameraTransforms.TransformType.FIXED); GlStateManager.popMatrix();
        }
    }
}
