package vectorwing.farmersdelight.legacy;
import net.minecraft.block.BlockHay;
public final class StrawBale extends BlockHay {
    public StrawBale() {
        super();
        setTranslationKey("farmersdelight.straw_bale");
        setCreativeTab(Content.TAB); setHardness(0.5F);
        setSoundType(net.minecraft.block.SoundType.PLANT);
    }
    @Override public int getFlammability(net.minecraft.world.IBlockAccess world, net.minecraft.util.math.BlockPos pos, net.minecraft.util.EnumFacing face) { return 20; }
    @Override public int getFireSpreadSpeed(net.minecraft.world.IBlockAccess world, net.minecraft.util.math.BlockPos pos, net.minecraft.util.EnumFacing face) { return 60; }
}
