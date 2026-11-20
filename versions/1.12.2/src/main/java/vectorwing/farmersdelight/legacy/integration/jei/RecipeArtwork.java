package vectorwing.farmersdelight.legacy.integration.jei;

import mezz.jei.api.gui.IDrawable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

/** Native recipe panels matching the established NEI workstation artwork. */
final class RecipeArtwork implements IDrawable {
    static final ResourceLocation POT = new ResourceLocation("farmersdelight", "textures/gui/cooking_pot.png");
    static final ResourceLocation FURNACE = new ResourceLocation("minecraft", "textures/gui/container/furnace.png");
    private final int kind;
    RecipeArtwork(int kind) { this.kind = kind; }
    public int getWidth() { return 166; }
    public int getHeight() { return 65; }
    public void draw(Minecraft minecraft) { draw(minecraft, 0, 0); }
    public void draw(Minecraft minecraft, int x, int y) {
        Gui.drawRect(x, y, x + 166, y + 65, 0xffc6c6c6);
        if (kind == 0) {
            for (int i = 0; i < 6; i++) slot(x + 6 + i % 3 * 18, y + 2 + i / 3 * 18);
            slot(x + 68, y + 40); slot(x + 100, y + 40);
            texture(minecraft, POT, x + 95, y + 2, 119, 17, 28, 30);
            texture(minecraft, POT, x + 23, y + 40, 176, 0, 17, 15);
            texture(minecraft, POT, x + 65, y + 10, 89, 25, 25, 17);
            texture(minecraft, POT, x + 88, y + 38, 112, 53, 11, 19);
        } else if (kind == 1) {
            slot(x + 6, y + 16); slot(x + 34, y + 16);
            for (int i = 0; i < 4; i++) slot(x + 72 + i * 22, y + 16);
            Gui.drawRect(x + 53, y + 23, x + 65, y + 26, 0xff8b8b8b);
            for (int i = 0; i < 7; i++) Gui.drawRect(x + 64 + i, y + 18 + i, x + 65 + i, y + 31 - i, 0xff8b8b8b);
        } else {
            slot(x + 30, y + 7); slot(x + 110, y + 7);
            texture(minecraft, FURNACE, x + 72, y + 7, 176, 14, 24, 16);
        }
    }
    private static void slot(int x, int y) {
        Gui.drawRect(x - 1, y - 1, x + 17, y + 17, 0xff373737);
        Gui.drawRect(x, y, x + 17, y + 17, 0xffffffff);
        Gui.drawRect(x, y, x + 16, y + 16, 0xff8b8b8b);
    }
    static void texture(Minecraft mc, ResourceLocation texture, int x, int y, int u, int v, int width, int height) {
        GlStateManager.color(1, 1, 1, 1);
        mc.getTextureManager().bindTexture(texture);
        Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, width, height, 256, 256);
    }
    static void progress(Minecraft mc, ResourceLocation texture, int x, int y, int u, int v, int width, int height) {
        int fill = (int) (Minecraft.getSystemTime() / 50 % 80) * width / 80;
        if (fill > 0) texture(mc, texture, x, y, u, v, fill, height);
    }
}
