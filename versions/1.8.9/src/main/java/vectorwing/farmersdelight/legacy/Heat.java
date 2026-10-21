package vectorwing.farmersdelight.legacy;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.util.BlockPos;

/** Source-equivalent native heat sources, optionally through one hopper. */
public final class Heat {
    public static boolean heated(World world, BlockPos pos, boolean direct) {
        if (source(world, pos.down())) return true;
        return !direct && world.getBlockState(pos.down()).getBlock() == Blocks.hopper && source(world, pos.down(2));
    }
    private static boolean source(World world, BlockPos pos) {
        Block block = world.getBlockState(pos).getBlock();
        if (block == Content.stove) return (block.getMetaFromState(world.getBlockState(pos)) & 4) != 0;
        return block == Blocks.fire || block == Blocks.lava || block == Blocks.flowing_lava
                ;
    }
    private Heat() {}
}
