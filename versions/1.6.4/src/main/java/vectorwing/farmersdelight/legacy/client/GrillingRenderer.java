package vectorwing.farmersdelight.legacy.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemBlock;
import vectorwing.farmersdelight.legacy.GrillingTile;
import vectorwing.farmersdelight.legacy.StoveTile;
import vectorwing.farmersdelight.legacy.Stacks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityItem;
import org.lwjgl.opengl.GL11;

public final class GrillingRenderer extends TileEntitySpecialRenderer {
    private final EntityItem display=new EntityItem(null);
    public void renderTileEntityAt(TileEntity raw,double x,double y,double z,float partial) {
        GrillingTile tile=(GrillingTile)raw;
        boolean stove=tile instanceof StoveTile;
        for(int i=0;i<tile.getSizeInventory();i++) {
            ItemStack stack=tile.getStackInSlot(i); if(Stacks.empty(stack)) continue;
            double px=stove ? 0.3+(i%3)*0.2 : 0.5, pz=stove ? 0.35+(i/3)*0.3 : 0.5;
            GL11.glPushMatrix(); GL11.glTranslated(x+px,y+(stove ? 1.03 : 0.13),z+pz);
            if(!(stack.getItem() instanceof ItemBlock)) GL11.glRotatef(90,1,0,0);
            GL11.glScalef(stove ? 0.5F : 0.9F,stove ? 0.5F : 0.9F,stove ? 0.5F : 0.9F);
            display.worldObj=tile.getWorldObj(); display.setEntityItemStack(stack); display.hoverStart=0;
            RenderManager.instance.renderEntityWithPosYaw(display,0,0,0,0,0); GL11.glPopMatrix();
        }
    }
}
