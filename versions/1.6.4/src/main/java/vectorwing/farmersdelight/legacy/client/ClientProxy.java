package vectorwing.farmersdelight.legacy.client;
public final class ClientProxy extends vectorwing.farmersdelight.legacy.CommonProxy {
    @Override public void recipeSettings(final boolean crates,final double fortune) {
        pending.set(new Snapshot(crates,fortune));
    }
    private static final java.util.concurrent.atomic.AtomicReference<Snapshot> pending=new java.util.concurrent.atomic.AtomicReference<Snapshot>();
    private static final class Snapshot {
        final boolean crates;final double fortune;
        Snapshot(boolean crates,double fortune) { this.crates=crates;this.fortune=fortune; }
    }
    @Override public void preInit() {
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(new ClientSounds());
        cpw.mods.fml.common.registry.TickRegistry.registerTickHandler(new cpw.mods.fml.common.ITickHandler() {
            public java.util.EnumSet<cpw.mods.fml.common.TickType> ticks() { return java.util.EnumSet.of(cpw.mods.fml.common.TickType.CLIENT); }
            public String getLabel() { return "Farmer's Delight settings"; }
            public void tickStart(java.util.EnumSet<cpw.mods.fml.common.TickType> types,Object... data) {
                Snapshot snapshot=pending.getAndSet(null);
                if(snapshot!=null) { vectorwing.farmersdelight.legacy.Settings.vanillaCrates=snapshot.crates;vectorwing.farmersdelight.legacy.Settings.cuttingFortuneBonus=snapshot.fortune; }
            }
            public void tickEnd(java.util.EnumSet<cpw.mods.fml.common.TickType> types,Object... data) {}
        },cpw.mods.fml.relauncher.Side.CLIENT);
        if(cpw.mods.fml.common.Loader.isModLoaded("NotEnoughItems"))
            vectorwing.farmersdelight.legacy.integration.nei.NEIFarmersDelightConfig.registerHandlerInfo();
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
