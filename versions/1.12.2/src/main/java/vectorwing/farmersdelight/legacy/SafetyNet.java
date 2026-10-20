package vectorwing.farmersdelight.legacy;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
public final class SafetyNet extends Block {
    public SafetyNet() {
        super(Material.CLOTH); setHardness(0.2F); setCreativeTab(Content.TAB);
        setTranslationKey("farmersdelight.safety_net");
    }
    public boolean isOpaqueCube(net.minecraft.block.state.IBlockState state) { return false; }
    public boolean isFullCube(net.minecraft.block.state.IBlockState state) { return false; }
    public net.minecraft.util.BlockRenderLayer getBlockLayer() { return net.minecraft.util.BlockRenderLayer.CUTOUT; }
    public net.minecraft.util.math.AxisAlignedBB getBoundingBox(net.minecraft.block.state.IBlockState state,net.minecraft.world.IBlockAccess world,net.minecraft.util.math.BlockPos pos) { return new net.minecraft.util.math.AxisAlignedBB(0,0.4375,0,1,0.5625,1); }
    public void onFallenUpon(net.minecraft.world.World world,net.minecraft.util.math.BlockPos pos,net.minecraft.entity.Entity entity,float distance) { entity.fall(distance,entity.isSneaking() ? 1 : 0); }
    public void onLanded(net.minecraft.world.World world,net.minecraft.entity.Entity entity) {
        if(entity.isSneaking()) { super.onLanded(world,entity); return; }
        if(entity.motionY<0) entity.motionY=-entity.motionY*(entity instanceof net.minecraft.entity.EntityLivingBase ? 0.6 : 0.8);
    }
}

