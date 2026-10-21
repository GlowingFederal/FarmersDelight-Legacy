package vectorwing.farmersdelight.legacy.integration.nei;
import vectorwing.farmersdelight.legacy.NativeRegistry;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.GuiRecipe;
import codechicken.nei.recipe.TemplateRecipeHandler;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
import vectorwing.farmersdelight.legacy.CookingRecipes;
import vectorwing.farmersdelight.legacy.Stacks;

/** Drawing and tooltips using the NEI API shared by legacy and GTNH releases. */
public abstract class FDRecipeHandler extends TemplateRecipeHandler {
    static final int WIDTH = 166, HEIGHT = 65;
    protected static String text(String key, Object... values) {
        return I18n.getStringParams("farmersdelight.nei." + key, values);
    }
    protected static String number(double value) {
        return new DecimalFormat("0.##", DecimalFormatSymbols.getInstance(Locale.ROOT)).format(value);
    }
    protected static String seconds(int ticks) { return text("seconds", number(ticks / 20D)); }
    protected static boolean workstation(ItemStack stack, Block block) {
        return !Stacks.empty(stack) && stack.getItem() == NativeRegistry.item(block);
    }
    protected static void panel() { Gui.drawRect(0, 0, WIDTH, HEIGHT, 0xffc6c6c6); }
    protected static void slot(int x, int y) {
        Gui.drawRect(x - 1, y - 1, x + 17, y + 17, 0xff373737);
        Gui.drawRect(x, y, x + 17, y + 17, 0xffffffff);
        Gui.drawRect(x, y, x + 16, y + 16, 0xff8b8b8b);
        GL11.glColor4f(1, 1, 1, 1);
    }
    protected static void label(String value, int x, int y) {
        Minecraft.getMinecraft().fontRenderer.drawString(value, x, y, 0x404040);
    }
    protected static void arrow(int x, int y, int width) {
        Gui.drawRect(x, y + 5, x + width - 6, y + 8, 0xff8b8b8b);
        for (int i = 0; i < 7; i++)
            Gui.drawRect(x + width - 7 + i, y + i, x + width - 6 + i, y + 13 - i, 0xff8b8b8b);
        GL11.glColor4f(1, 1, 1, 1);
    }
    protected static void note(List<String> tooltip, String value) { tooltip.add("\u00a77" + value); }
    @Override public void loadUsageRecipes(String id, Object... ingredients) {
        if (id.equals(getOverlayIdentifier())) loadCraftingRecipes(id);
        else super.loadUsageRecipes(id, ingredients);
    }
    @Override public List<String> handleItemTooltip(GuiRecipe gui, ItemStack stack, List<String> tooltip, int index) {
        super.handleItemTooltip(gui, stack, tooltip, index);
        for (PositionedStack input : getIngredientStacks(index)) if (gui.isMouseOver(input, index)) {
            ItemStack remainder = CookingRecipes.remainder(stack);
            if (!Stacks.empty(remainder)) note(tooltip, text("remainder", remainder.getDisplayName()));
            break;
        }
        return tooltip;
    }
    protected static void changeTexture(String resource) { Minecraft.getMinecraft().getTextureManager().bindTexture(new net.minecraft.util.ResourceLocation(resource)); }
    protected static void drawTexturedModalRect(int x,int y,int u,int v,int width,int height) { new Gui().drawTexturedModalRect(x,y,u,v,width,height); }
}
