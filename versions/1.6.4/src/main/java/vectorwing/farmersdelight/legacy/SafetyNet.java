package vectorwing.farmersdelight.legacy;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
public final class SafetyNet extends Block {
    public SafetyNet() {
        super(LegacyIds.block("safety_net"),Material.cloth); setHardness(0.2F); setCreativeTab(Content.TAB);
        setUnlocalizedName("farmersdelight.safety_net"); setTextureName("farmersdelight:safety_net"); setBlockBounds(0,0.4375F,0,1,0.5625F,1);
    }
    public boolean isOpaqueCube() { return false; }
    public boolean renderAsNormalBlock() { return false; }
    public void onFallenUpon(net.minecraft.world.World world,int x,int y,int z,net.minecraft.entity.Entity entity,float distance) {
        if(!entity.isSneaking()) entity.fallDistance=0;
    }

}

