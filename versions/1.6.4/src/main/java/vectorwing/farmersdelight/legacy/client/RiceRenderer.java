package vectorwing.farmersdelight.legacy.client;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;

public final class RiceRenderer implements ISimpleBlockRenderingHandler {
    public final int id=RenderingRegistry.getNextAvailableRenderId();
    public void renderInventoryBlock(Block block,int meta,int model,RenderBlocks renderer) {}
    public boolean renderWorldBlock(IBlockAccess world,int x,int y,int z,Block block,int model,RenderBlocks renderer) {
        renderer.renderBlockFluids(Block.waterStill,x,y,z);
        renderer.setOverrideBlockTexture(((vectorwing.farmersdelight.legacy.RicePlant)block).getBlockTexture(world,x,y,z,2));
        renderer.renderCrossedSquares(block,x,y,z);
        renderer.clearOverrideBlockTexture();
        return true;
    }
    public boolean shouldRender3DInInventory() { return false; }
    public int getRenderId() { return id; }
}

