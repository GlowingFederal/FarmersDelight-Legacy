package vectorwing.farmersdelight.legacy;
public class CommonProxy {
    public void recipeSettings(boolean crates,double fortune) {}
    public int riceRenderId() { return 0; }
    public void preInit() {}
    public GuiHandler guiHandler() { return new GuiHandler(); }
    public void registerMetadataModels(net.minecraft.block.Block block,String namespace,String[] names,int[] metadata) {}
    /** Packed model choice: high bits select the model, low two bits select clockwise rotation. */
    public interface ModelState {
        int select(net.minecraft.world.IBlockAccess world, int x, int y, int z);
    }
    public void registerStateModels(net.minecraft.block.Block block, String namespace, String[] names,
                                    int inventoryModel, ModelState state) {}
    public void registerModels(net.minecraft.block.Block block,String namespace,String[] stages) {}
    public int modelRenderId() { return 0; }
}
