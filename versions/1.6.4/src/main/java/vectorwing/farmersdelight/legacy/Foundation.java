package vectorwing.farmersdelight.legacy;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = Foundation.MODID, name = "Farmer's Delight Legacy", version = BuildVersion.VERSION,
     acceptedMinecraftVersions = "[1.6.4]", dependencies = "")
@cpw.mods.fml.common.network.NetworkMod(clientSideRequired=true,serverSideRequired=true,channels={"fd_recipe"},packetHandler=RecipeSettingsSync.class)
public final class Foundation {
    public static final String MODID = "farmersdelight";
    @Mod.Instance(MODID) public static Foundation instance;
    @SidedProxy(clientSide = "vectorwing.farmersdelight.legacy.client.ClientProxy", serverSide = "vectorwing.farmersdelight.legacy.CommonProxy")
    public static CommonProxy proxy;
    @Mod.EventHandler public void preInit(FMLPreInitializationEvent event) {
        Settings.load(event.getSuggestedConfigurationFile());
        Content.register();
        Progression.prepare();
        MinecraftForge.EVENT_BUS.register(new FoodEvents());
        MinecraftForge.EVENT_BUS.register(new WorkstationEvents());
        MinecraftForge.EVENT_BUS.register(new FarmingEvents());
        MinecraftForge.EVENT_BUS.register(new ToolCombat());
        cpw.mods.fml.common.registry.EntityRegistry.registerModEntity(RottenTomato.class,"rotten_tomato",0,instance,64,10,true);
        cpw.mods.fml.common.registry.GameRegistry.registerWorldGenerator(new WildGeneration());
        proxy.preInit();
    }
    @Mod.EventHandler public void init(FMLInitializationEvent event) {
        Recipes.register();
        RecipeSettingsSync.register();
        Acquisition.register();
        VillageCompost.register();

        cpw.mods.fml.common.network.NetworkRegistry.instance().registerGuiHandler(instance, proxy.guiHandler());
    }
    @Mod.EventHandler public void postInit(FMLPostInitializationEvent event) {
        Ingredients.REGISTRY.freeze();
        Recipes.REGISTRY.freeze();
        CuttingRecipes.register();
        CookingRecipes.register();
        GrillingRecipes.register();
        SourceCrafting.register();
        Progression.register();
        org.apache.logging.log4j.LogManager.getLogger(MODID).debug("Deferred cutting recipes: {}",CuttingRecipes.DEFERRED);
        org.apache.logging.log4j.LogManager.getLogger(MODID).debug("Deferred pot recipes: {}",CookingRecipes.DEFERRED);
        org.apache.logging.log4j.LogManager.getLogger(MODID).debug("Deferred crafting recipes: {}",SourceCrafting.DEFERRED);
        org.apache.logging.log4j.LogManager.getLogger(MODID).debug("Deferred heat recipes: {}",GrillingRecipes.DEFERRED);
        java.util.logging.Logger.getLogger(MODID).info("Cooking Pot: "+CookingRecipes.ALL.size()+" active recipes; "+CookingRecipes.DEFERRED.size()+" deferred. Additional crafting: "+SourceCrafting.activeCount()+" active; "+SourceCrafting.DEFERRED.size()+" deferred.");
        CuttingDispenser.register();
        java.util.logging.Logger.getLogger(MODID).info("Cutting Board: "+CuttingRecipes.ALL.size()+" active recipes; "+CuttingRecipes.DEFERRED.size()+" deferred for unavailable prerequisites");

    }
}
