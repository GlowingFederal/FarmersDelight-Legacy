package vectorwing.farmersdelight.legacy;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import net.minecraft.world.IBlockAccess;
import net.minecraft.entity.Entity;

public final class PackedBlock extends Block {
    public final String id;
    private final boolean bale;
    @cpw.mods.fml.relauncher.SideOnly(cpw.mods.fml.relauncher.Side.CLIENT) private net.minecraft.util.Icon top,bottom,side,tied;
    public PackedBlock(String id) {
        super(LegacyIds.block(id),id.endsWith("_crate") ? Material.wood : Material.cloth);
        this.id=id; bale=id.equals("rice_bale"); setHardness(id.endsWith("_crate") ? 2 : 0.5F); setCreativeTab(Content.TAB);
        setUnlocalizedName("farmersdelight."+id);

    }
    public int onBlockPlaced(World world,int x,int y,int z,int face,float hx,float hy,float hz,int meta) { return bale ? face : 0; }
    public int getFlammability(IBlockAccess world,int x,int y,int z,int metadata,net.minecraftforge.common.ForgeDirection face) { return bale ? 20 : super.getFlammability(world,x,y,z,metadata,face); }
    public int getFireSpreadSpeed(World world,int x,int y,int z,int metadata,net.minecraftforge.common.ForgeDirection face) { return bale ? 60 : super.getFireSpreadSpeed(world,x,y,z,metadata,face); }
    @cpw.mods.fml.relauncher.SideOnly(cpw.mods.fml.relauncher.Side.CLIENT)
    public void registerIcons(net.minecraft.client.renderer.texture.IconRegister register) {
        top=register.registerIcon("farmersdelight:"+id+"_top");
        bottom=register.registerIcon("farmersdelight:"+(id.endsWith("_crate") ? "crate_bottom" : id+"_bottom"));
        side=register.registerIcon("farmersdelight:"+id+"_side");
        tied=id.equals("rice_bag") ? register.registerIcon("farmersdelight:rice_bag_side_tied") : side;
    }
    @cpw.mods.fml.relauncher.SideOnly(cpw.mods.fml.relauncher.Side.CLIENT)
    public net.minecraft.util.Icon getIcon(int face,int meta) {
        int topFace=bale ? meta : 1;
        return face==topFace ? top : face==(topFace^1) ? bottom : id.equals("rice_bag") && (face==2 || face==3) ? tied : side;
    }
}

