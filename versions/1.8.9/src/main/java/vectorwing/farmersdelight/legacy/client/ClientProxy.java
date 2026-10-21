package vectorwing.farmersdelight.legacy.client;
import vectorwing.farmersdelight.legacy.*;
public final class ClientProxy extends CommonProxy {
    @Override public void recipeSettings(final boolean crates,final double fortune) {
        net.minecraft.client.Minecraft.getMinecraft().addScheduledTask(new Runnable() { public void run() {
            Settings.vanillaCrates=crates; Settings.cuttingFortuneBonus=fortune;
        }});
    }
    @Override public void preInit() {
        models();
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(new ClientFeedback());
        net.minecraftforge.fml.client.registry.ClientRegistry.bindTileEntitySpecialRenderer(CanvasSignTile.class,new CanvasSignRenderer());
        net.minecraftforge.fml.client.registry.RenderingRegistry.registerEntityRenderingHandler(RottenTomato.class,
            new net.minecraft.client.renderer.entity.RenderSnowball<RottenTomato>(net.minecraft.client.Minecraft.getMinecraft().getRenderManager(),Content.ITEMS.get("rotten_tomato"),net.minecraft.client.Minecraft.getMinecraft().getRenderItem()));
        net.minecraftforge.fml.client.registry.ClientRegistry.bindTileEntitySpecialRenderer(GrillingTile.class,new GrillingRenderer());
        net.minecraftforge.fml.client.registry.ClientRegistry.bindTileEntitySpecialRenderer(CuttingBoardTile.class,new CuttingBoardRenderer());
    }
    private void models() {
        for(net.minecraft.item.Item item:Content.ITEMS.values())
            net.minecraftforge.client.model.ModelLoader.setCustomModelResourceLocation(item,0,new net.minecraft.client.resources.model.ModelResourceLocation(item.getRegistryName(),"inventory"));
    }
    @Override public GuiHandler guiHandler() { return new ClientGuiHandler(); }
}
