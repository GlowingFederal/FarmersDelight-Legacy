package vectorwing.farmersdelight.legacy.client;
import vectorwing.farmersdelight.legacy.Content;
import vectorwing.farmersdelight.legacy.CommonProxy;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
public final class ClientProxy extends CommonProxy {
    @Override public void preInit() { MinecraftForge.EVENT_BUS.register(this); }
    @SubscribeEvent public void models(ModelRegistryEvent event) {
        for (Item item : Content.ITEMS.values()) ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
    }
}
