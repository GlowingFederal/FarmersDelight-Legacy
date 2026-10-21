package vectorwing.farmersdelight.legacy;

import net.minecraft.block.Block;

import net.minecraft.world.World;


/** Source-equivalent native heat sources, optionally through one hopper. */
public final class Heat {
    public static boolean heated(World world, int x, int y, int z, boolean direct) {
        if (source(world, x, y - 1, z)) return true;
        return !direct && NativeRegistry.at(world,x, y - 1, z) == Block.hopperBlock && source(world, x, y - 2, z);
    }
    private static boolean source(World world, int x, int y, int z) {
        Block block = NativeRegistry.at(world,x, y, z);
        if (block == Content.stove) return (world.getBlockMetadata(x, y, z) & 4) != 0;
        return block == Block.fire || block == Block.lavaStill || block == Block.lavaMoving
                ;
    }
    private Heat() {}
}
