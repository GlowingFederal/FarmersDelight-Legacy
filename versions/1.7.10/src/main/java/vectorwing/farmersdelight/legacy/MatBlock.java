package vectorwing.farmersdelight.legacy;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.world.IBlockAccess;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public final class MatBlock extends Block {
    public final String id;
    public final boolean full,rug;

    public MatBlock(String id) {
        super(Material.cloth); this.id=id; full=id.equals("full_tatami_mat"); rug=id.equals("canvas_rug");
        setCreativeTab(Content.TAB); setHardness(rug ? 0.2F : 0.3F);
        setBlockName("farmersdelight."+id); setBlockTextureName("farmersdelight:"+(rug ? id : "tatami_mat_half")); setBlockBounds(0,0,0,1,rug ? 0.0625F : 0.125F,1);
    }
    public boolean isOpaqueCube() { return false; }
    public boolean renderAsNormalBlock() { return false; }
    public boolean canPlaceBlockAt(World world,int x,int y,int z) {
        return super.canPlaceBlockAt(world,x,y,z) && (rug || !world.isAirBlock(x,y-1,z));
    }
    public int getRenderType() { return Foundation.proxy.modelRenderId(); }
    public int getMobilityFlag() { return 1; }
    public void onBlockPlacedBy(World world,int x,int y,int z,net.minecraft.entity.EntityLivingBase placer,ItemStack stack) {
        if(!full) world.setBlockMetadataWithNotify(x,y,z,(net.minecraft.util.MathHelper.floor_double(placer.rotationYaw*4/360+0.5)+2)&3,3);
    }
    public Item getItemDropped(int meta,java.util.Random random,int fortune) { return full && (meta&4)==0 ? null : Item.getItemFromBlock(this); }
    public void onNeighborBlockChange(World world,int x,int y,int z,Block changed) {
        if(world.isRemote) return;
        int meta=world.getBlockMetadata(x,y,z); boolean valid=rug || !world.isAirBlock(x,y-1,z);
        if(full) {
            int face=meta&3,sign=(meta&4)==0 ? 1 : -1;
            int nx=x+DX[face]*sign,nz=z+DZ[face]*sign;
            valid &= world.getBlock(nx,y,nz)==this && (world.getBlockMetadata(nx,y,nz)&4)!=(meta&4) && (world.getBlockMetadata(nx,y,nz)&3)==face;
        }
        if(!valid) { dropBlockAsItem(world,x,y,z,meta,0); world.setBlockToAir(x,y,z); }
    }
    public void onBlockHarvested(World world,int x,int y,int z,int meta,EntityPlayer player) {
        if(full && player.capabilities.isCreativeMode) {
            int face=meta&3,sign=(meta&4)==0 ? 1 : -1,nx=x+DX[face]*sign,nz=z+DZ[face]*sign;
            if(world.getBlock(nx,y,nz)==this) world.setBlock(nx,y,nz,net.minecraft.init.Blocks.air,0,2);
        }
        super.onBlockHarvested(world,x,y,z,meta,player);
    }
    public static final int[] DX={0,-1,0,1},DZ={1,0,-1,0};
}

