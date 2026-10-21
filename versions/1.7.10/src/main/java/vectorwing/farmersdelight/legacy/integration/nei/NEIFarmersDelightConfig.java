package vectorwing.farmersdelight.legacy.integration.nei;
import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import vectorwing.farmersdelight.legacy.BuildVersion;
public final class NEIFarmersDelightConfig implements IConfigureNEI {
    /** Send during pre-init: NEI loads plugin configs after its IMC registration window. */
    public static void registerHandlerInfo() {
        handler("CookingHandler", CookingHandler.UID, "farmersdelight:cooking_pot");
        handler("CuttingHandler", CuttingHandler.UID, "farmersdelight:cutting_board");
        handler("GrillingHandler", GrillingHandler.UID, "farmersdelight:skillet");
        handler("FoundationHandler", FoundationHandler.UID, "minecraft:crafting_table");
        catalyst(GrillingHandler.UID, "farmersdelight:stove");
    }
    private static void handler(String name, String uid, String icon) {
        net.minecraft.nbt.NBTTagCompound tag = new net.minecraft.nbt.NBTTagCompound();
        tag.setString("handler", "vectorwing.farmersdelight.legacy.integration.nei." + name);
        tag.setString("modName", "Farmer's Delight Legacy"); tag.setString("modId", "farmersdelight");
        tag.setBoolean("modRequired", true); tag.setString("itemName", icon);
        tag.setInteger("handlerWidth", FDRecipeHandler.WIDTH); tag.setInteger("handlerHeight", FDRecipeHandler.HEIGHT);
        tag.setInteger("maxRecipesPerPage", Integer.MAX_VALUE);
        tag.setBoolean("multipleWidgetsAllowed", true);
        cpw.mods.fml.common.event.FMLInterModComms.sendMessage("NotEnoughItems", "registerHandlerInfo", tag);
        catalyst(uid, icon);
    }
    private static void catalyst(String uid, String item) {
        net.minecraft.nbt.NBTTagCompound tag = new net.minecraft.nbt.NBTTagCompound();
        tag.setString("handlerID", uid); tag.setString("itemName", item);
        cpw.mods.fml.common.event.FMLInterModComms.sendMessage("NotEnoughItems", "registerCatalystInfo", tag);
    }
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
