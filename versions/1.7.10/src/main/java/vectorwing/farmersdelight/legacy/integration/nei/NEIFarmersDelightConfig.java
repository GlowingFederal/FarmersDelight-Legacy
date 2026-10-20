package vectorwing.farmersdelight.legacy.integration.nei;
import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import vectorwing.farmersdelight.legacy.BuildVersion;
public final class NEIFarmersDelightConfig implements IConfigureNEI {
    public void loadConfig() {
        API.registerGuiOverlay(vectorwing.farmersdelight.legacy.client.CookingPotGui.class,"farmersdelight.cooking",24,15);
        API.registerGuiOverlayHandler(vectorwing.farmersdelight.legacy.client.CookingPotGui.class,new codechicken.nei.recipe.DefaultOverlayHandler(24,15),"farmersdelight.cooking");
        API.registerGuiOverlay(net.minecraft.client.gui.inventory.GuiCrafting.class,"farmersdelight.foundation",5,11);
        API.registerGuiOverlayHandler(net.minecraft.client.gui.inventory.GuiCrafting.class,new codechicken.nei.recipe.DefaultOverlayHandler(5,11),"farmersdelight.foundation");
        API.registerRecipeHandler(new CuttingHandler());
        API.registerUsageHandler(new CuttingHandler());
        API.registerRecipeHandler(new CookingHandler());
        API.registerUsageHandler(new CookingHandler());
        API.registerRecipeHandler(new GrillingHandler());
        API.registerUsageHandler(new GrillingHandler());
        API.registerRecipeHandler(new FoundationHandler());
        API.registerUsageHandler(new FoundationHandler());
    }
    public String getName() { return "Farmer's Delight Legacy"; }
    public String getVersion() { return BuildVersion.VERSION; }
}
