package vectorwing.farmersdelight.legacy;
import net.minecraft.block.BlockHay;
public final class StrawBale extends BlockHay {
    public StrawBale() {
        super();
        setBlockName("farmersdelight.straw_bale");
        setCreativeTab(Content.TAB); setHardness(0.5F);
        setBlockTextureName("farmersdelight:straw_bale"); setStepSound(soundTypeGrass);
    }
    @cpw.mods.fml.relauncher.SideOnly(cpw.mods.fml.relauncher.Side.CLIENT)
    @Override public void registerBlockIcons(net.minecraft.client.renderer.texture.IIconRegister register) {
        field_150164_N = register.registerIcon("farmersdelight:straw_bale_end");
        blockIcon = register.registerIcon("farmersdelight:straw_bale_side");
    }
    @Override public int getFlammability(net.minecraft.world.IBlockAccess world, int x, int y, int z, net.minecraftforge.common.util.ForgeDirection face) { return 20; }
    @Override public int getFireSpreadSpeed(net.minecraft.world.IBlockAccess world, int x, int y, int z, net.minecraftforge.common.util.ForgeDirection face) { return 60; }
}
