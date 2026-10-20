package vectorwing.farmersdelight.legacy;

import net.minecraft.block.BlockPane;
import net.minecraft.block.material.Material;
public final class RopeBlock extends BlockPane {
    public RopeBlock() {
        super(Material.CLOTH,false);
        setTranslationKey("farmersdelight.rope"); setCreativeTab(Content.TAB); setHardness(0.2F);
    }
    public boolean isLadder(net.minecraft.block.state.IBlockState state,net.minecraft.world.IBlockAccess world,net.minecraft.util.math.BlockPos pos,net.minecraft.entity.EntityLivingBase entity) { return true; }
    public net.minecraft.util.math.AxisAlignedBB getCollisionBoundingBox(net.minecraft.block.state.IBlockState state,net.minecraft.world.IBlockAccess world,net.minecraft.util.math.BlockPos pos) { return NULL_AABB; }
    public void addCollisionBoxToList(net.minecraft.block.state.IBlockState state,net.minecraft.world.World world,net.minecraft.util.math.BlockPos pos,net.minecraft.util.math.AxisAlignedBB mask,java.util.List<net.minecraft.util.math.AxisAlignedBB> boxes,net.minecraft.entity.Entity entity,boolean actual) {}
}

