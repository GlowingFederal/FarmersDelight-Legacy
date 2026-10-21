package vectorwing.farmersdelight.legacy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = Foundation.MODID, name = "Farmer's Delight Legacy", version = BuildVersion.VERSION,
     acceptedMinecraftVersions = "[1.8.9]", dependencies = "")
public final class Foundation {
    public static final String MODID = "farmersdelight";
    @Mod.Instance(MODID) public static Foundation instance;
    @SidedProxy(clientSide = "vectorwing.farmersdelight.legacy.client.ClientProxy", serverSide = "vectorwing.farmersdelight.legacy.CommonProxy")
    public static CommonProxy proxy;
    @Mod.EventHandler public void preInit(FMLPreInitializationEvent event) {
        Settings.load(event.getSuggestedConfigurationFile());
        Content.register();
        MinecraftForge.EVENT_BUS.register(new FoodEvents());
        MinecraftForge.EVENT_BUS.register(new WorkstationEvents());
        MinecraftForge.EVENT_BUS.register(new FarmingEvents());
        MinecraftForge.EVENT_BUS.register(new ToolCombat());
        net.minecraftforge.fml.common.registry.EntityRegistry.registerModEntity(RottenTomato.class,"rotten_tomato",0,instance,64,10,true);
        net.minecraftforge.fml.common.registry.GameRegistry.registerWorldGenerator(new WildGeneration(),0);
        proxy.preInit();
    }
    @Mod.EventHandler public void init(FMLInitializationEvent event) {
        Recipes.register();
        RecipeSettingsSync.register();
        Acquisition.register();
        VillageCompost.register();

        net.minecraftforge.fml.common.network.NetworkRegistry.INSTANCE.registerGuiHandler(instance, proxy.guiHandler());
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
        org.apache.logging.log4j.LogManager.getLogger(MODID).info("Cooking Pot: {} active recipes; {} deferred. Additional crafting: {} active; {} deferred.",
                CookingRecipes.ALL.size(), CookingRecipes.DEFERRED.size(), SourceCrafting.activeCount(), SourceCrafting.DEFERRED.size());
        CuttingDispenser.register();
        org.apache.logging.log4j.LogManager.getLogger(MODID).info("Cutting Board: {} active recipes; {} deferred for unavailable prerequisites",
                CuttingRecipes.ALL.size(), CuttingRecipes.DEFERRED.size());
    }
}
