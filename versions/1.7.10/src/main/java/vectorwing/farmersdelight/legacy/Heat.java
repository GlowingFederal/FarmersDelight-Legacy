package vectorwing.farmersdelight.legacy;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;


/** Source-equivalent native heat sources, optionally through one hopper. */
public final class Heat {
    public static boolean heated(World world, int x, int y, int z, boolean direct) {
        if (source(world, x, y - 1, z)) return true;
        return !direct && world.getBlock(x, y - 1, z) == Blocks.hopper && source(world, x, y - 2, z);
    }
    private static boolean source(World world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        String id = Block.blockRegistry.getNameForObject(block);
        if (block instanceof GrillBlock && ((GrillBlock)block).isStove()) return (world.getBlockMetadata(x, y, z) & 4) != 0;
        return block == Blocks.fire || block == Blocks.lava || block == Blocks.flowing_lava
                ;
    }
    private Heat() {}
}
