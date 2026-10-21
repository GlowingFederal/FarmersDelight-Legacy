package vectorwing.farmersdelight.legacy;

import java.util.ArrayList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public final class CookingPot extends BlockContainer {
    public CookingPot() {
        super(LegacyIds.block("cooking_pot"),Material.iron); setUnlocalizedName("farmersdelight.cooking_pot"); setCreativeTab(Content.TAB);
        setHardness(0.5F); setResistance(6F); setStepSound(soundMetalFootstep);
        setTextureName("farmersdelight:cooking_pot_side"); setBlockBounds(0.125F, 0, 0.125F, 0.875F, 0.625F, 0.875F);
    }
    public TileEntity createNewTileEntity(World world) { return new CookingPotTile(); }
    private int tray(World world,int x,int y,int z) {
        String id=NativeRegistry.name(NativeRegistry.at(world,x,y-1,z));
        return id.equals("minecraft:campfire") || id.equals("minecraft:soul_campfire") ? 4 : 0;
    }
    public int onBlockPlaced(World world,int x,int y,int z,int side,float hx,float hy,float hz,int meta) { return side==0 ? 8 : tray(world,x,y,z); }
    public void onNeighborBlockChange(World world,int x,int y,int z,int block) {
        int meta=world.getBlockMetadata(x,y,z);
        if(!world.isRemote && (meta&8)==0 && (meta&4)!=tray(world,x,y,z)) world.setBlockMetadataWithNotify(x,y,z,(meta&3)|tray(world,x,y,z),3);
    }
    @Override public boolean removeBlockByPlayer(World world,EntityPlayer player,int x,int y,int z) {
        if(!world.isRemote && !player.capabilities.isCreativeMode && canHarvestBlock(player,world.getBlockMetadata(x,y,z))) {
            harvestBlock(world,player,x,y,z,world.getBlockMetadata(x,y,z)); return world.isAirBlock(x,y,z);
        }
        return super.removeBlockByPlayer(world,player,x,y,z);
    }
    @Override public void harvestBlock(World world, EntityPlayer player, int x, int y, int z, int meta) {
        if(world.getBlockTileEntity(x,y,z)==null) return;
        super.harvestBlock(world, player, x, y, z, meta); world.setBlockToAir(x, y, z);
    }
    public boolean isOpaqueCube() { return false; }
    public boolean renderAsNormalBlock() { return false; }
    public int getRenderType() { return Foundation.proxy.modelRenderId(); }
    public void addCollisionBoxesToList(World world,int x,int y,int z,net.minecraft.util.AxisAlignedBB mask,java.util.List boxes,net.minecraft.entity.Entity entity) {
        super.addCollisionBoxesToList(world,x,y,z,mask,boxes,entity);
        if((world.getBlockMetadata(x,y,z)&12)==4) {
            net.minecraft.util.AxisAlignedBB tray=net.minecraft.util.AxisAlignedBB.getBoundingBox(x,y-0.0625,z,x+1,y,z+1);
            if(mask.intersectsWith(tray)) boxes.add(tray);
        }
    }
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
        world.setBlockMetadataWithNotify(x, y, z, (world.getBlockMetadata(x,y,z)&12)|(MathHelper.floor_double(placer.rotationYaw * 4 / 360 + 0.5) & 3), 2);
        if (world.getBlockTileEntity(x, y, z) instanceof CookingPotTile) {
            CookingPotTile pot = (CookingPotTile) world.getBlockTileEntity(x, y, z); pot.restoreMeal(stack);
            if (stack.hasDisplayName()) pot.name(stack.getDisplayName());
        }
    }
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hx, float hy, float hz) {
        if(player.isSneaking() && Stacks.empty(player.getHeldItem())) {
            if(!world.isRemote) {
                int meta=world.getBlockMetadata(x,y,z);
                world.setBlockMetadataWithNotify(x,y,z,(meta&3)|((meta&8)!=0 ? tray(world,x,y,z) : 8),3);
                world.playSoundEffect(x+0.5,y+0.5,z+0.5,"dig.stone",0.7F,1F);
            }
            return true;
        }
        if (!(world.getBlockTileEntity(x, y, z) instanceof CookingPotTile)) return false;
        if (!world.isRemote) {
            CookingPotTile tile = (CookingPotTile) world.getBlockTileEntity(x, y, z);
            ItemStack served = tile.serve(player.getHeldItem(), player.capabilities.isCreativeMode);
            if (!Stacks.empty(served)) { if (!player.inventory.addItemStackToInventory(served)) tile.drop(served); }
            else player.openGui(Foundation.instance, 0, world, x, y, z);
        }
        return true;
    }
    @Override public ArrayList<ItemStack> getBlockDropped(World world, int x, int y, int z, int metadata, int fortune) {
        ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
        TileEntity tile = world.getBlockTileEntity(x, y, z);
        drops.add(tile instanceof CookingPotTile ? ((CookingPotTile) tile).portable() : new ItemStack(this)); return drops;
    }
    public void breakBlock(World world, int x, int y, int z, int block, int meta) {
        if (!world.isRemote && world.getBlockTileEntity(x, y, z) instanceof CookingPotTile) {
            CookingPotTile pot = (CookingPotTile) world.getBlockTileEntity(x, y, z);
            for (int i = 0; i < 9; i++) if (i != 6) pot.drop(pot.decrStackSize(i, Stacks.size(pot.getStackInSlot(i))));
            pot.payExperience();
        }
        super.breakBlock(world, x, y, z, block, meta);
    }
    public boolean hasComparatorInputOverride() { return true; }
    public int getComparatorInputOverride(World world, int x, int y, int z, int side) {
        return Math.min(15,net.minecraft.inventory.Container.calcRedstoneFromInventory((net.minecraft.inventory.IInventory) world.getBlockTileEntity(x, y, z)));
    }
}
