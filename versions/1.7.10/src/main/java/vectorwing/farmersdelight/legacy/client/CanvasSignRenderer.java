package vectorwing.farmersdelight.legacy.client;

import org.lwjgl.opengl.GL11;
import net.minecraft.client.model.ModelSign;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import vectorwing.farmersdelight.legacy.CanvasSignBlock;
import vectorwing.farmersdelight.legacy.CanvasSignTile;

public final class CanvasSignRenderer extends TileEntitySpecialRenderer {
    private final ModelSign model=new ModelSign();
    private final java.util.Map<String,ResourceLocation> materials=new java.util.HashMap<String,ResourceLocation>();
    public void renderTileEntityAt(net.minecraft.tileentity.TileEntity tile,double x,double y,double z,float ticks) { draw((CanvasSignTile)tile,x,y,z); }
    private void draw(CanvasSignTile tile,double x,double y,double z) {
        if(!(tile.getBlockType() instanceof CanvasSignBlock)) return;
        CanvasSignBlock block=(CanvasSignBlock)tile.getBlockType();
        int metadata=tile.getBlockMetadata();
        float angle=block.wall ? metadata==2 ? 180 : metadata==4 ? 90 : metadata==5 ? -90 : 0 : metadata*360F/16F;
        GL11.glPushMatrix();
        // The native editor treats custom standing signs as wall signs; cancel only its preview rotation.
        if(!block.wall && net.minecraft.client.Minecraft.getMinecraft().currentScreen instanceof net.minecraft.client.gui.inventory.GuiEditSign) {
            float editorAngle=metadata==2 ? 180 : metadata==4 ? 90 : metadata==5 ? -90 : 0;
            GL11.glRotatef(angle-editorAngle,0,1,0);
        }
        GL11.glTranslated(x+0.5,y+0.5,z+0.5); GL11.glRotatef(-angle,0,1,0);
        if(block.wall) GL11.glTranslatef(0,-0.3125F,-0.4375F);
        model.signStick.showModel=!block.wall;
        ResourceLocation material=materials.get(block.background);
        if(material==null) { material=new ResourceLocation("farmersdelight","textures/entity/signs/canvas"+(block.background.isEmpty() ? "" : "_"+block.background)+".png"); materials.put(block.background,material); }
        bindTexture(material);
        GL11.glPushMatrix(); GL11.glScalef(0.6666667F,-0.6666667F,-0.6666667F); model.renderSign(); GL11.glPopMatrix();
        FontRenderer font=func_147498_b();
        GL11.glTranslatef(0,0.33333334F,0.046666667F); GL11.glScalef(0.010416667F,-0.010416667F,0.010416667F);
        GL11.glNormal3f(0,0,-1); GL11.glDepthMask(false); GL11.glDisable(GL11.GL_LIGHTING);
        int rgb=tile.textColor;
        int color=((int)(((rgb>>16)&255)*0.6)<<16)|((int)(((rgb>>8)&255)*0.6)<<8)|(int)((rgb&255)*0.6);
        for(int line=0;line<4;line++) {
            String text=tile.signText[line];
            text=font.trimStringToWidth(text,90);
            if(line==tile.lineBeingEdited) text="> "+text+" <";
            font.drawString(text,-font.getStringWidth(text)/2,line*10-19,color);
        }
        GL11.glEnable(GL11.GL_LIGHTING); GL11.glDepthMask(true); GL11.glColor4f(1,1,1,1); GL11.glPopMatrix();
    }
}

