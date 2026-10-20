package vectorwing.farmersdelight.legacy;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.IBlockAccess;


public final class StorageBlock extends BlockContainer {
    private static final double[][] WALLS={{0,0,0,1,0.125,1},{0,0.875,0,1,1,1},{0,0,0,1,1,0.125},{0,0,0.875,1,1,1},{0,0,0,0.125,1,1},{0.875,0,0,1,1,1}};
    public void addCollisionBoxesToList(World world,int x,int y,int z,net.minecraft.util.AxisAlignedBB mask,java.util.List boxes,net.minecraft.entity.Entity entity) {
        if(!basket) { super.addCollisionBoxesToList(world,x,y,z,mask,boxes,entity); return; }
        int opening=world.getBlockMetadata(x,y,z)&7;
        for(int face=0;face<6;face++) if(face!=opening) {
            double[] box=WALLS[face]; setBlockBounds((float)box[0],(float)box[1],(float)box[2],(float)box[3],(float)box[4],(float)box[5]);
            super.addCollisionBoxesToList(world,x,y,z,mask,boxes,entity);
        }
        setBlockBounds(0,0,0,1,1,1);
    }
    public final String id;
    public final boolean basket;
    @cpw.mods.fml.relauncher.SideOnly(cpw.mods.fml.relauncher.Side.CLIENT) private net.minecraft.util.IIcon front,open,top,side;
    public StorageBlock(String id,boolean basket) {
        super(Material.wood); this.id=id; this.basket=basket;
        setBlockName("farmersdelight."+id); setCreativeTab(Content.TAB); setHardness(1.5F);

    }
    public TileEntity createNewTileEntity(World world,int meta) { return basket ? new BasketTile() : new StorageTile(); }
    public boolean isOpaqueCube() { return !basket; }
    public boolean renderAsNormalBlock() { return !basket; }
    public int getRenderType() { return basket ? Foundation.proxy.modelRenderId() : 0; }
    public void onBlockPlacedBy(World world,int x,int y,int z,EntityLivingBase placer,ItemStack stack) {
        int horizontal=new int[]{2,5,3,4}[net.minecraft.util.MathHelper.floor_double(placer.rotationYaw*4/360+0.5)&3];
        int face=basket && Math.abs(placer.rotationPitch)>60 ? placer.rotationPitch>0 ? 1 : 0 : horizontal;
        world.setBlockMetadataWithNotify(x,y,z,face,3);
        if(stack.hasDisplayName() && world.getTileEntity(x,y,z) instanceof StorageTile) ((StorageTile)world.getTileEntity(x,y,z)).name(stack.getDisplayName());
    }

    public boolean onBlockActivated(World world,int x,int y,int z,EntityPlayer player,int side,float hx,float hy,float hz) {
        if(!world.isRemote) player.openGui(Foundation.instance,1,world,x,y,z);
        return true;
    }
    public void breakBlock(World world,int x,int y,int z,Block old,int meta) {
        TileEntity raw=world.getTileEntity(x,y,z);
        if(!world.isRemote && raw instanceof StorageTile) {
            StorageTile tile=(StorageTile)raw;
            for(int slot=0;slot<tile.getSizeInventory();slot++) {
                ItemStack stack=tile.decrStackSize(slot,Stacks.size(tile.getStackInSlot(slot)));
                if(!Stacks.empty(stack)) dropBlockAsItem(world,x,y,z,stack);
            }
        }
        super.breakBlock(world,x,y,z,old,meta);
    }
    public boolean hasComparatorInputOverride() { return true; }
    public int getComparatorInputOverride(World world,int x,int y,int z,int side) {
        TileEntity tile=world.getTileEntity(x,y,z);
        return tile instanceof StorageTile ? net.minecraft.inventory.Container.calcRedstoneFromInventory((StorageTile)tile) : 0;
    }
    @cpw.mods.fml.relauncher.SideOnly(cpw.mods.fml.relauncher.Side.CLIENT)
    public void registerBlockIcons(net.minecraft.client.renderer.texture.IIconRegister register) {
        front=register.registerIcon("farmersdelight:"+id+(basket ? "_side" : "_front"));
        open=basket ? front : register.registerIcon("farmersdelight:"+id+"_front_open");
        top=register.registerIcon("farmersdelight:"+id+"_top");
        side=register.registerIcon("farmersdelight:"+id+(basket ? "_bottom" : "_side"));
    }
    @cpw.mods.fml.relauncher.SideOnly(cpw.mods.fml.relauncher.Side.CLIENT)
    public net.minecraft.util.IIcon getIcon(int face,int meta) { return face==1 ? top : face==meta ? front : side; }
    @cpw.mods.fml.relauncher.SideOnly(cpw.mods.fml.relauncher.Side.CLIENT)
    public net.minecraft.util.IIcon getIcon(IBlockAccess world,int x,int y,int z,int face) {
        TileEntity tile=world.getTileEntity(x,y,z);
        if(!basket && face==world.getBlockMetadata(x,y,z) && tile instanceof StorageTile) return ((StorageTile)tile).users>0 ? open : front;
        return getIcon(face,world.getBlockMetadata(x,y,z));
    }
}

