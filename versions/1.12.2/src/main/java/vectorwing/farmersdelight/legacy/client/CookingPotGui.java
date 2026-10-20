package vectorwing.farmersdelight.legacy.client;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import vectorwing.farmersdelight.legacy.CookingPotContainer;
import vectorwing.farmersdelight.legacy.CookingPotTile;
import net.minecraft.client.renderer.GlStateManager;

public final class CookingPotGui extends GuiContainer {
    private static final ResourceLocation TEXTURE = new ResourceLocation("farmersdelight", "textures/gui/cooking_pot.png");
    private final CookingPotTile pot;
    public CookingPotGui(InventoryPlayer player, CookingPotTile pot) {
        super(new CookingPotContainer(player, pot)); this.pot = pot; xSize = 176; ySize = 166;
    }
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        fontRenderer.drawString(pot.hasCustomName() ? pot.getName() : net.minecraft.client.resources.I18n.format(pot.getName()), 8, 6, 0x404040);
        fontRenderer.drawString(net.minecraft.client.resources.I18n.format("container.inventory"), 8, 73, 0x404040);
    }
    protected void drawGuiContainerBackgroundLayer(float partial, int mouseX, int mouseY) {
        GlStateManager.color(1, 1, 1, 1);
        mc.getTextureManager().bindTexture(TEXTURE);
        int x = (width - xSize) / 2, y = (height - ySize) / 2;
        drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
        int progress = pot.total > 0 ? Math.min(24, pot.progress * 24 / pot.total) : 0;
        drawTexturedModalRect(x + 89, y + 25, 176, 15, progress + 1, 17);
        if (pot.heated) drawTexturedModalRect(x + 47, y + 55, 176, 0, 17, 15);
    }
    @Override public void drawScreen(int mouseX,int mouseY,float partial) {
        drawDefaultBackground(); super.drawScreen(mouseX,mouseY,partial);
        renderHoveredToolTip(mouseX,mouseY);
        if(mouseX>=guiLeft+47 && mouseX<guiLeft+64 && mouseY>=guiTop+55 && mouseY<guiTop+70)
            drawHoveringText(java.util.Collections.singletonList(net.minecraft.client.resources.I18n.format(pot.heated ? "farmersdelight.container.cooking_pot.heated" : "farmersdelight.container.cooking_pot.not_heated")),mouseX,mouseY);
    }
}
