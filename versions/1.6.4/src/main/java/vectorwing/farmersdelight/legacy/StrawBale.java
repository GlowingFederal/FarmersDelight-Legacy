package vectorwing.farmersdelight.legacy;
import net.minecraft.block.BlockHay;
public final class StrawBale extends BlockHay {
    public StrawBale() {
        super(LegacyIds.block("straw_bale"));
        setUnlocalizedName("farmersdelight.straw_bale");
        setCreativeTab(Content.TAB); setHardness(0.5F);
        setTextureName("farmersdelight:straw_bale"); setStepSound(soundGrassFootstep);
    }
    @cpw.mods.fml.relauncher.SideOnly(cpw.mods.fml.relauncher.Side.CLIENT)
    @Override public void registerIcons(net.minecraft.client.renderer.texture.IconRegister register) {
        field_111051_a = register.registerIcon("farmersdelight:straw_bale_end");
        blockIcon = register.registerIcon("farmersdelight:straw_bale_side");
    }
    @Override public int getFlammability(net.minecraft.world.IBlockAccess world, int x, int y, int z, int metadata, net.minecraftforge.common.ForgeDirection face) { return 20; }
    @Override public int getFireSpreadSpeed(net.minecraft.world.World world, int x, int y, int z, int metadata, net.minecraftforge.common.ForgeDirection face) { return 60; }
}
