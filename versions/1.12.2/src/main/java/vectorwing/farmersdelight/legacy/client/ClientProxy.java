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
    @Override public void recipeSettings(final boolean crates,final double fortune) {
        net.minecraft.client.Minecraft.getMinecraft().addScheduledTask(new Runnable() { public void run() {
            vectorwing.farmersdelight.legacy.Settings.vanillaCrates=crates;
            vectorwing.farmersdelight.legacy.Settings.cuttingFortuneBonus=fortune;
            if(net.minecraftforge.fml.common.Loader.isModLoaded("jei")) vectorwing.farmersdelight.legacy.integration.jei.FoundationJEI.refreshAvailability();
        } });
    }
    @Override public void preInit() {
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(new ClientFeedback());
        net.minecraftforge.fml.client.registry.ClientRegistry.bindTileEntitySpecialRenderer(vectorwing.farmersdelight.legacy.CanvasSignTile.class,new CanvasSignRenderer());
        net.minecraftforge.fml.client.registry.RenderingRegistry.registerEntityRenderingHandler(vectorwing.farmersdelight.legacy.RottenTomato.class,
                manager -> new net.minecraft.client.renderer.entity.RenderSnowball<>(manager,Content.ITEMS.get("rotten_tomato"),net.minecraft.client.Minecraft.getMinecraft().getRenderItem()));
        net.minecraftforge.fml.client.registry.ClientRegistry.bindTileEntitySpecialRenderer(vectorwing.farmersdelight.legacy.GrillingTile.class, new GrillingRenderer());

        MinecraftForge.EVENT_BUS.register(this);
        net.minecraftforge.fml.client.registry.ClientRegistry.bindTileEntitySpecialRenderer(vectorwing.farmersdelight.legacy.CuttingBoardTile.class, new CuttingBoardRenderer());
    }
    @Override public vectorwing.farmersdelight.legacy.GuiHandler guiHandler() { return new ClientGuiHandler(); }

    @SubscribeEvent public void models(ModelRegistryEvent event) {
        for (Item item : Content.ITEMS.values()) ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
    }
    @SubscribeEvent public void colors(net.minecraftforge.client.event.ColorHandlerEvent.Block event) {
        event.getBlockColors().registerBlockColorHandler((state,world,pos,tint) ->
                tint==0 && world!=null && pos!=null ? net.minecraft.world.biome.BiomeColorHelper.getWaterColorAtPos(world,pos) : 0xFFFFFF,Content.riceCrop,Content.wildRice);
    }
}
