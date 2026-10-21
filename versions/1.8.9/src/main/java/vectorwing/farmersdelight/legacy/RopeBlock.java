package vectorwing.farmersdelight.legacy;

import net.minecraft.block.BlockPane;
import net.minecraft.block.material.Material;
public final class RopeBlock extends BlockPane {
    public RopeBlock() {
        super(Material.cloth,false);
        setUnlocalizedName("farmersdelight.rope"); setCreativeTab(Content.TAB); setHardness(0.2F);
    }
    public boolean isLadder(net.minecraft.world.IBlockAccess world,net.minecraft.util.BlockPos pos,net.minecraft.entity.EntityLivingBase entity) { return true; }
    public net.minecraft.util.AxisAlignedBB getCollisionBoundingBox(net.minecraft.world.World world,net.minecraft.util.BlockPos pos,net.minecraft.block.state.IBlockState state) { return null; }
    public void addCollisionBoxesToList(net.minecraft.world.World world,net.minecraft.util.BlockPos pos,net.minecraft.block.state.IBlockState state,net.minecraft.util.AxisAlignedBB mask,java.util.List<net.minecraft.util.AxisAlignedBB> boxes,net.minecraft.entity.Entity entity) {}
}

