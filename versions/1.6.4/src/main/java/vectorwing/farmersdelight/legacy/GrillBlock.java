package vectorwing.farmersdelight.legacy;
import net.minecraft.item.Item;

import java.util.ArrayList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public final class GrillBlock extends BlockContainer {
    private final boolean stove;
    @SideOnly(Side.CLIENT) private net.minecraft.util.Icon top, bottom, front, lit, litTop;
    private static final int[] FACES = {2,5,3,4};
    public GrillBlock(boolean stove) {
        super(LegacyIds.block(stove ? "stove" : "skillet"),stove ? Material.rock : Material.iron); this.stove = stove;
        setUnlocalizedName("farmersdelight." + (stove ? "stove" : "skillet")); setCreativeTab(Content.TAB);
        setHardness(stove ? 2F : 0.5F); setResistance(6F); setStepSound(soundMetalFootstep);
        if (!stove) setBlockBounds(1F/16, 0, 1F/16, 15F/16, 0.25F, 15F/16);
    }
    public TileEntity createNewTileEntity(World world) { return stove ? new StoveTile() : new SkilletTile(); }
    public boolean isOpaqueCube() { return stove; }
    public boolean renderAsNormalBlock() { return stove; }
    public int getRenderType() { return stove ? 0 : Foundation.proxy.modelRenderId(); }
    public int getLightValue(IBlockAccess world, int x, int y, int z) { return stove && (world.getBlockMetadata(x,y,z) & 4) != 0 ? 13 : 0; }
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
        world.setBlockMetadataWithNotify(x,y,z, (MathHelper.floor_double(placer.rotationYaw * 4 / 360 + 0.5) & 3) | (stove ? 4 : 0), 3);
        if (!stove && world.getBlockTileEntity(x,y,z) instanceof SkilletTile) ((SkilletTile) world.getBlockTileEntity(x,y,z)).identity(stack);
    }
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hx, float hy, float hz) {
        if (!(world.getBlockTileEntity(x,y,z) instanceof GrillingTile)) return false;
        if (world.isRemote) return true;
        ItemStack held = player.getHeldItem(); GrillingTile tile = (GrillingTile) world.getBlockTileEntity(x,y,z);
        int meta = world.getBlockMetadata(x,y,z); boolean hot = (meta & 4) != 0;
        if (stove && !Stacks.empty(held)) {
            boolean shovel = NativeTools.matches(held,"shovel"), water = held.getItem() == Item.bucketWater;
            boolean lighter = held.getItem() == Item.flintAndSteel, charge = held.getItem() == Item.fireballCharge;
            if (hot && (shovel || water) || !hot && (lighter || charge)) {
                world.setBlockMetadataWithNotify(x,y,z, hot ? meta & 3 : meta | 4, 3);
                world.playSoundEffect(x+0.5,y+0.5,z+0.5,hot ? "random.fizz" : "fire.ignite",0.8F,1F);
                if (!player.capabilities.isCreativeMode) {
                    if (water) player.inventory.setInventorySlotContents(player.inventory.currentItem, new ItemStack(Item.bucketEmpty));
                    else if (charge) Stacks.shrink(held,1); else held.damageItem(1,player);
                }
                return true;
            }
        }
        if (Stacks.empty(held) && !stove) {
            ItemStack removed = tile.decrStackSize(0, Stacks.size(tile.getStackInSlot(0)));
            if (!Stacks.empty(removed) && !player.capabilities.isCreativeMode && !player.inventory.addItemStackToInventory(removed)) tile.drop(removed);
            return true;
        }
        return tile.add(held, player.capabilities.isCreativeMode);
    }
    public void onEntityWalking(World world, int x, int y, int z, Entity entity) {
        if (!world.isRemote && stove && (world.getBlockMetadata(x,y,z) & 4) != 0 && entity instanceof EntityLivingBase && !entity.isImmuneToFire())
            entity.attackEntityFrom(net.minecraft.util.DamageSource.inFire,1F);
    }
    public boolean removeBlockByPlayer(World world,EntityPlayer player,int x,int y,int z) {
        if(!world.isRemote && !player.capabilities.isCreativeMode && canHarvestBlock(player,world.getBlockMetadata(x,y,z))) {
            harvestBlock(world,player,x,y,z,world.getBlockMetadata(x,y,z)); return world.isAirBlock(x,y,z);
        }
        return super.removeBlockByPlayer(world,player,x,y,z);
    }
    public void harvestBlock(World world, EntityPlayer player, int x, int y, int z, int meta) {
        if(world.getBlockTileEntity(x,y,z)==null) return; super.harvestBlock(world,player,x,y,z,meta); world.setBlockToAir(x,y,z); }
    public ArrayList<ItemStack> getBlockDropped(World world, int x, int y, int z, int metadata, int fortune) {
        ArrayList<ItemStack> result = new ArrayList<ItemStack>(); TileEntity tile = world.getBlockTileEntity(x,y,z);
        result.add(tile instanceof SkilletTile ? ((SkilletTile) tile).identity() : new ItemStack(this)); return result;
    }
    public void breakBlock(World world, int x, int y, int z, int block, int meta) {
        if (!world.isRemote && world.getBlockTileEntity(x,y,z) instanceof GrillingTile) {
            GrillingTile tile = (GrillingTile) world.getBlockTileEntity(x,y,z);
            for (int i=0;i<tile.getSizeInventory();i++) tile.drop(tile.decrStackSize(i,Stacks.size(tile.getStackInSlot(i))));
        }
        super.breakBlock(world,x,y,z,block,meta);
    }
    @SideOnly(Side.CLIENT) public void registerIcons(net.minecraft.client.renderer.texture.IconRegister register) {
        String name = stove ? "stove" : "skillet";
        blockIcon=register.registerIcon("farmersdelight:"+name+"_side"); top=register.registerIcon("farmersdelight:"+name+"_top");
        bottom=register.registerIcon("farmersdelight:"+name+"_bottom");
        if(stove) { front=register.registerIcon("farmersdelight:stove_front"); lit=register.registerIcon("farmersdelight:stove_front_on"); litTop=register.registerIcon("farmersdelight:stove_top_on"); }
    }
    @SideOnly(Side.CLIENT) public net.minecraft.util.Icon getIcon(int side,int meta) {
        if(side==1) return stove && (meta & 4)!=0 ? litTop : top; if(side==0) return bottom;
        int face = FACES[meta & 3];
        return stove && side==face ? (meta & 4)!=0 ? lit : front : blockIcon;
    }
}
