package vectorwing.farmersdelight.legacy;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public final class TatamiSolid extends Block {

    public TatamiSolid() {
        super(LegacyIds.block("tatami"),Material.cloth); setHardness(0.8F); setCreativeTab(Content.TAB);
        setUnlocalizedName("farmersdelight.tatami"); setTextureName("farmersdelight:tatami_mat_half");
    }
    public int getRenderType() { return Foundation.proxy.modelRenderId(); }
    public int onBlockPlaced(World world,int x,int y,int z,int face,float hx,float hy,float hz,int meta) { return face^1; }
    public void onBlockPlacedBy(World world,int x,int y,int z,EntityLivingBase placer,ItemStack stack) {
        if(world.isRemote || placer.isSneaking()) return;
        int face=world.getBlockMetadata(x,y,z)&7; net.minecraftforge.common.ForgeDirection direction=net.minecraftforge.common.ForgeDirection.getOrientation(face);
        int nx=x+direction.offsetX,ny=y+direction.offsetY,nz=z+direction.offsetZ;
        if(NativeRegistry.at(world,nx,ny,nz)==this && (world.getBlockMetadata(nx,ny,nz)&8)==0) {
            world.setBlockMetadataWithNotify(nx,ny,nz,(face^1)|8,3); world.setBlockMetadataWithNotify(x,y,z,face|8,3);
        }
    }
    public void onNeighborBlockChange(World world,int x,int y,int z,int block) {
        int meta=world.getBlockMetadata(x,y,z);
        net.minecraftforge.common.ForgeDirection direction=net.minecraftforge.common.ForgeDirection.getOrientation(meta&7);
        if(!world.isRemote && (meta&8)!=0 && NativeRegistry.at(world,x+direction.offsetX,y+direction.offsetY,z+direction.offsetZ)!=this) world.setBlockMetadataWithNotify(x,y,z,meta&7,3);
    }
}

