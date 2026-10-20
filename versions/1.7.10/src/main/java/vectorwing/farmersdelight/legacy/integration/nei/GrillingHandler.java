package vectorwing.farmersdelight.legacy.integration.nei;

import java.util.Collections;
import java.util.List;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.item.ItemStack;
import vectorwing.farmersdelight.legacy.GrillingRecipes;
import vectorwing.farmersdelight.legacy.Stacks;

public final class GrillingHandler extends TemplateRecipeHandler {
    private static final String UID="farmersdelight.grilling";
    public String getRecipeName() { return "Skillet / Stove"; }
    public String getGuiTexture() { return "minecraft:textures/gui/container/furnace.png"; }
    public void drawBackground(int recipe) { Gui.drawRect(0,0,166,65,0xffc6c6c6); }
    public void drawExtras(int index) {
        GrillingRecipes.Recipe recipe=((Cached)arecipes.get(index)).recipe;
        Minecraft.getMinecraft().fontRenderer.drawString(">",78,10,0x555555);
        Minecraft.getMinecraft().fontRenderer.drawString("Stove: "+recipe.time/20F+" s",8,30,0x555555);
        Minecraft.getMinecraft().fontRenderer.drawString("Heated skillet: "+GrillingRecipes.skilletTime(recipe.time,0)/20F+" s",8,44,0x555555);
    }
    public void loadCraftingRecipes(String id,Object... results) {
        if(id.equals(UID)) for(GrillingRecipes.Recipe recipe:GrillingRecipes.ALL) arecipes.add(new Cached(recipe));
        else super.loadCraftingRecipes(id,results);
    }
    public void loadCraftingRecipes(ItemStack result) {
        for(GrillingRecipes.Recipe recipe:GrillingRecipes.ALL) if(Stacks.same(recipe.output,result)) arecipes.add(new Cached(recipe));
    }
    public void loadUsageRecipes(ItemStack used) {
        for(GrillingRecipes.Recipe recipe:GrillingRecipes.ALL) if(Stacks.matches(recipe.inputs,used)) arecipes.add(new Cached(recipe));
    }
    public final class Cached extends CachedRecipe {
        final GrillingRecipes.Recipe recipe; private final PositionedStack input,output;
        Cached(GrillingRecipes.Recipe recipe) { this.recipe=recipe; input=new PositionedStack(recipe.inputs,30,4); output=new PositionedStack(recipe.output,110,4); }
        public List<PositionedStack> getIngredients() { return getCycledIngredients(cycleticks/20,Collections.singletonList(input)); }
        public PositionedStack getResult() { return output; }
    }
}
