package vectorwing.farmersdelight.legacy.client;
public final class ClientProxy extends vectorwing.farmersdelight.legacy.CommonProxy {
    @Override public void recipeSettings(final boolean crates,final double fortune) {
        net.minecraft.client.Minecraft.getMinecraft().func_152344_a(new Runnable() { public void run() {
            vectorwing.farmersdelight.legacy.Settings.vanillaCrates=crates;
            vectorwing.farmersdelight.legacy.Settings.cuttingFortuneBonus=fortune;

        } });
    }
    @Override public void preInit() {
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(new ClientFeedback());
        cpw.mods.fml.client.registry.ClientRegistry.bindTileEntitySpecialRenderer(vectorwing.farmersdelight.legacy.CanvasSignTile.class,new CanvasSignRenderer());
        cpw.mods.fml.client.registry.RenderingRegistry.registerEntityRenderingHandler(vectorwing.farmersdelight.legacy.RottenTomato.class,
                new net.minecraft.client.renderer.entity.RenderSnowball(vectorwing.farmersdelight.legacy.Content.ITEMS.get("rotten_tomato")));
        rice = new RiceRenderer();
        cpw.mods.fml.client.registry.RenderingRegistry.registerBlockHandler(rice);
        cpw.mods.fml.client.registry.ClientRegistry.bindTileEntitySpecialRenderer(vectorwing.farmersdelight.legacy.GrillingTile.class, new GrillingRenderer());
        models = new NativeModels();
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(models);
        cpw.mods.fml.client.registry.RenderingRegistry.registerBlockHandler(models);
        cpw.mods.fml.client.registry.ClientRegistry.bindTileEntitySpecialRenderer(vectorwing.farmersdelight.legacy.CuttingBoardTile.class, new CuttingBoardRenderer());
    }
    @Override public vectorwing.farmersdelight.legacy.GuiHandler guiHandler() { return new ClientGuiHandler(); }
    private NativeModels models;
    private RiceRenderer rice;
    @Override public int riceRenderId() { return rice == null ? 0 : rice.id; }
    @Override public int modelRenderId() { return models == null ? 0 : models.id; }
}
