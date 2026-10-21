package vectorwing.farmersdelight.legacy;
import net.minecraft.block.BlockHay;
public final class StrawBale extends BlockHay {
    public StrawBale() {
        super();
        setUnlocalizedName("farmersdelight.straw_bale");
        setCreativeTab(Content.TAB); setHardness(0.5F);
        setStepSound(soundTypeGrass);
    }
    @Override public int getFlammability(net.minecraft.world.IBlockAccess world, net.minecraft.util.BlockPos pos, net.minecraft.util.EnumFacing face) { return 20; }
    @Override public int getFireSpreadSpeed(net.minecraft.world.IBlockAccess world, net.minecraft.util.BlockPos pos, net.minecraft.util.EnumFacing face) { return 60; }
}
