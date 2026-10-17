package vectorwing.farmersdelight.legacy;
import net.minecraft.block.BlockHay;
public final class StrawBale extends BlockHay {
    public StrawBale() {
        super();
        setBlockName("farmersdelight.straw_bale");
        setCreativeTab(Content.TAB); setHardness(0.5F);
        setBlockTextureName("farmersdelight:straw_bale"); setStepSound(soundTypeGrass);
    }
    @Override public int getFlammability(net.minecraft.world.IBlockAccess world, int x, int y, int z, net.minecraftforge.common.util.ForgeDirection face) { return 20; }
    @Override public int getFireSpreadSpeed(net.minecraft.world.IBlockAccess world, int x, int y, int z, net.minecraftforge.common.util.ForgeDirection face) { return 60; }
}
