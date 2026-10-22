package vectorwing.farmersdelight.legacy;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = Foundation.MODID, name = "Farmer's Delight Legacy", version = BuildVersion.VERSION,
     acceptedMinecraftVersions = "[1.7.10]", dependencies = "required-after:unimixins@[0.3.1,)")
public final class Foundation {
    public static final String MODID = "farmersdelight";
    public static final PotionIdRegistry POTIONS = new PotionIdRegistry(MODID);
    @Mod.Instance(MODID) public static Foundation instance;
    @SidedProxy(clientSide = "vectorwing.farmersdelight.legacy.client.ClientProxy", serverSide = "vectorwing.farmersdelight.legacy.CommonProxy")
    public static CommonProxy proxy;
    @Mod.EventHandler public void construct(cpw.mods.fml.common.event.FMLConstructionEvent event) {
        Settings.reservePotions(new java.io.File(cpw.mods.fml.common.Loader.instance().getConfigDir(), "farmersdelight.cfg"));
    }
    @cpw.mods.fml.common.network.NetworkCheckHandler
    public boolean checkNetwork(java.util.Map<String,String> remote,cpw.mods.fml.relauncher.Side side) {
        if (!remote.containsKey(MODID)) return side == cpw.mods.fml.relauncher.Side.SERVER;
        return BuildVersion.VERSION.equals(remote.get(MODID)) && POTIONS.matches(remote);
    }
    @Mod.EventHandler public void loadComplete(cpw.mods.fml.common.event.FMLLoadCompleteEvent event) {
        POTIONS.verify();
    }
    @Mod.EventHandler public void preInit(FMLPreInitializationEvent event) {
        Settings.load(event.getSuggestedConfigurationFile());
        Content.register();
        MinecraftForge.EVENT_BUS.register(POTIONS);
        MinecraftForge.EVENT_BUS.register(new FoodEvents());
        MinecraftForge.EVENT_BUS.register(new WorkstationEvents());
        MinecraftForge.EVENT_BUS.register(new FarmingEvents());
        MinecraftForge.EVENT_BUS.register(new ToolCombat());
        cpw.mods.fml.common.registry.EntityRegistry.registerModEntity(RottenTomato.class,"rotten_tomato",0,instance,64,10,true);
        cpw.mods.fml.common.registry.GameRegistry.registerWorldGenerator(new WildGeneration(),0);
        proxy.preInit();
    }
    @Mod.EventHandler public void init(FMLInitializationEvent event) {
        Recipes.register();
        RecipeSettingsSync.register();
        Acquisition.register();
        VillageCompost.register();

        cpw.mods.fml.common.network.NetworkRegistry.INSTANCE.registerGuiHandler(instance, proxy.guiHandler());
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
