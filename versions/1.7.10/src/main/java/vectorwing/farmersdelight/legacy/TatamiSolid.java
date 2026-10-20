package vectorwing.farmersdelight.legacy;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public final class TatamiSolid extends Block {

    public TatamiSolid() {
        super(Material.cloth); setHardness(0.8F); setCreativeTab(Content.TAB);
        setBlockName("farmersdelight.tatami"); setBlockTextureName("farmersdelight:tatami_mat_half");
    }
    public int getRenderType() { return Foundation.proxy.modelRenderId(); }
    public int onBlockPlaced(World world,int x,int y,int z,int face,float hx,float hy,float hz,int meta) { return face^1; }
    public void onBlockPlacedBy(World world,int x,int y,int z,EntityLivingBase placer,ItemStack stack) {
        if(world.isRemote || placer.isSneaking()) return;
        int face=world.getBlockMetadata(x,y,z)&7; net.minecraftforge.common.util.ForgeDirection direction=net.minecraftforge.common.util.ForgeDirection.getOrientation(face);
        int nx=x+direction.offsetX,ny=y+direction.offsetY,nz=z+direction.offsetZ;
        if(world.getBlock(nx,ny,nz)==this && (world.getBlockMetadata(nx,ny,nz)&8)==0) {
            world.setBlockMetadataWithNotify(nx,ny,nz,(face^1)|8,3); world.setBlockMetadataWithNotify(x,y,z,face|8,3);
        }
    }
    public void onNeighborBlockChange(World world,int x,int y,int z,Block block) {
        int meta=world.getBlockMetadata(x,y,z);
        net.minecraftforge.common.util.ForgeDirection direction=net.minecraftforge.common.util.ForgeDirection.getOrientation(meta&7);
        if(!world.isRemote && (meta&8)!=0 && world.getBlock(x+direction.offsetX,y+direction.offsetY,z+direction.offsetZ)!=this) world.setBlockMetadataWithNotify(x,y,z,meta&7,3);
    }
}

