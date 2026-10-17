package vectorwing.farmersdelight.legacy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = Foundation.MODID, name = "Farmer's Delight Legacy", version = BuildVersion.VERSION,
     acceptedMinecraftVersions = "[1.12.2]", dependencies = "required-after:mixinbooter@[11.18,)")
public final class Foundation {
    public static final String MODID = "farmersdelight";
    @SidedProxy(clientSide = "vectorwing.farmersdelight.legacy.client.ClientProxy", serverSide = "vectorwing.farmersdelight.legacy.CommonProxy")
    public static CommonProxy proxy;
    @Mod.EventHandler public void preInit(FMLPreInitializationEvent event) {
        Settings.load(event.getSuggestedConfigurationFile());
        Content.register();
        MinecraftForge.EVENT_BUS.register(new FoodEvents());
        proxy.preInit();
    }
    @Mod.EventHandler public void init(FMLInitializationEvent event) { Recipes.register(); }
    @Mod.EventHandler public void postInit(FMLPostInitializationEvent event) {
        Ingredients.REGISTRY.freeze();
        Recipes.REGISTRY.freeze();
    }
}
