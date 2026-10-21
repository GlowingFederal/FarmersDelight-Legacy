package vectorwing.farmersdelight.legacy;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
public final class SafetyNet extends Block {
    public SafetyNet() {
        super(Material.cloth); setHardness(0.2F); setCreativeTab(Content.TAB);
        setUnlocalizedName("farmersdelight.safety_net");
    }
    public boolean isOpaqueCube() { return false; }
    public boolean isFullCube() { return false; }
    public net.minecraft.util.EnumWorldBlockLayer getBlockLayer() { return net.minecraft.util.EnumWorldBlockLayer.CUTOUT; }
    public net.minecraft.util.AxisAlignedBB shape(net.minecraft.block.state.IBlockState state,net.minecraft.world.IBlockAccess world,net.minecraft.util.BlockPos pos) { return new net.minecraft.util.AxisAlignedBB(0,0.4375,0,1,0.5625,1); }
    public void onFallenUpon(net.minecraft.world.World world,net.minecraft.util.BlockPos pos,net.minecraft.entity.Entity entity,float distance) { entity.fall(distance,entity.isSneaking() ? 1 : 0); }
    public void onLanded(net.minecraft.world.World world,net.minecraft.entity.Entity entity) {
        if(entity.isSneaking()) { super.onLanded(world,entity); return; }
        if(entity.motionY<0) entity.motionY=-entity.motionY*(entity instanceof net.minecraft.entity.EntityLivingBase ? 0.6 : 0.8);
    }
    @Override public void setBlockBoundsBasedOnState(net.minecraft.world.IBlockAccess world,net.minecraft.util.BlockPos pos) {
        net.minecraft.util.AxisAlignedBB b=shape(world.getBlockState(pos),world,pos);
        setBlockBounds((float)b.minX,(float)b.minY,(float)b.minZ,(float)b.maxX,(float)b.maxY,(float)b.maxZ);
    }
    @Override public net.minecraft.util.AxisAlignedBB getSelectedBoundingBox(net.minecraft.world.World world,net.minecraft.util.BlockPos pos) {
        setBlockBoundsBasedOnState(world,pos); return super.getSelectedBoundingBox(world,pos);
    }
    @Override public net.minecraft.util.AxisAlignedBB getCollisionBoundingBox(net.minecraft.world.World world,net.minecraft.util.BlockPos pos,net.minecraft.block.state.IBlockState state) {
        return shape(state,world,pos).offset(pos.getX(),pos.getY(),pos.getZ());
    }
}

