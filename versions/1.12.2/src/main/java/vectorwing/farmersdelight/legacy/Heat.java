package vectorwing.farmersdelight.legacy;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;

/** Source-equivalent native heat sources, optionally through one hopper. */
public final class Heat {
    public static boolean heated(World world, BlockPos pos, boolean direct) {
        if (source(world, pos.down())) return true;
        return !direct && world.getBlockState(pos.down()).getBlock() == Blocks.HOPPER && source(world, pos.down(2));
    }
    private static boolean source(World world, BlockPos pos) {
        Block block = world.getBlockState(pos).getBlock();
        String id = String.valueOf(block.getRegistryName());
        if (block instanceof GrillBlock && ((GrillBlock)block).isStove()) return (block.getMetaFromState(world.getBlockState(pos)) & 4) != 0;
        return block == Blocks.FIRE || block == Blocks.LAVA || block == Blocks.FLOWING_LAVA
                || block == Blocks.MAGMA;
    }
    private Heat() {}
}
