package vectorwing.farmersdelight.legacy;

import net.minecraft.block.BlockPane;
import net.minecraft.block.material.Material;
public final class RopeBlock extends BlockPane {
    public RopeBlock() {
        super(LegacyIds.block("rope"),"farmersdelight:rope","farmersdelight:rope",Material.cloth,true);
        setUnlocalizedName("farmersdelight.rope"); setCreativeTab(Content.TAB); setHardness(0.2F);
    }
    public boolean isLadder(net.minecraft.world.World world,int x,int y,int z,net.minecraft.entity.EntityLivingBase entity) { return true; }
    public net.minecraft.util.AxisAlignedBB getCollisionBoundingBoxFromPool(net.minecraft.world.World world,int x,int y,int z) { return null; }
    public void addCollisionBoxesToList(net.minecraft.world.World world,int x,int y,int z,net.minecraft.util.AxisAlignedBB mask,java.util.List boxes,net.minecraft.entity.Entity entity) {}
}

