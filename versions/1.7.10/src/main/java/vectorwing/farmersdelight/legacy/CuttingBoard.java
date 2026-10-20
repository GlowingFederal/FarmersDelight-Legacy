package vectorwing.farmersdelight.legacy;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public final class CuttingBoard extends BlockContainer {
    public CuttingBoard() {
        super(Material.wood);
        setBlockName("farmersdelight.cutting_board"); setBlockTextureName("farmersdelight:cutting_board");
        setCreativeTab(Content.TAB); setHardness(2F); setStepSound(soundTypeWood);
        setBlockBounds(1F / 16, 0, 1F / 16, 15F / 16, 1F / 16, 15F / 16);
    }
    public TileEntity createNewTileEntity(World world, int metadata) { return new CuttingBoardTile(); }
    public boolean isOpaqueCube() { return false; }
    public boolean renderAsNormalBlock() { return false; }
    public int getRenderType() { return Foundation.proxy.modelRenderId(); }
    @Override public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        return super.canPlaceBlockAt(world, x, y, z) && world.isSideSolid(x, y - 1, z, net.minecraftforge.common.util.ForgeDirection.UP);
    }
    @Override public void onNeighborBlockChange(World world, int x, int y, int z, net.minecraft.block.Block block) {
        if (!world.isRemote && !world.isSideSolid(x, y - 1, z, net.minecraftforge.common.util.ForgeDirection.UP)) {
            dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0); world.setBlockToAir(x, y, z);
        }
    }
    @Override public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
        world.setBlockMetadataWithNotify(x, y, z, MathHelper.floor_double(placer.rotationYaw * 4 / 360 + 0.5) & 3, 2);
    }
    @Override public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hx, float hy, float hz) {
        if (!(world.getTileEntity(x, y, z) instanceof CuttingBoardTile)) return false;
        CuttingBoardTile tile = (CuttingBoardTile) world.getTileEntity(x, y, z);
        ItemStack held = player.getHeldItem();
        if (world.isRemote) return true;
        if (Stacks.empty(tile.getStackInSlot(0))) {
            if (Stacks.empty(held)) return false;
            tile.setInventorySlotContents(0, Stacks.copy(held, 1));
            tile.carved = player.isSneaking() && (held.getItem().isDamageable() || held.getItem() instanceof net.minecraft.item.ItemShears);
            if (!player.capabilities.isCreativeMode) { Stacks.shrink(held, 1); if (Stacks.empty(held)) player.inventory.setInventorySlotContents(player.inventory.currentItem, null); }
            tile.changed();
        } else if (Stacks.empty(held)) {
            ItemStack removed = tile.decrStackSize(0, Stacks.size(tile.getStackInSlot(0)));
            tile.carved = false;
            if (!player.capabilities.isCreativeMode && !player.inventory.addItemStackToInventory(removed)) tile.drop(removed);
            tile.changed();
        } else tile.process(held, player);
        return true;
    }
    @Override public void breakBlock(World world, int x, int y, int z, net.minecraft.block.Block block, int metadata) {
        if (!world.isRemote && world.getTileEntity(x, y, z) instanceof CuttingBoardTile) {
            CuttingBoardTile tile = (CuttingBoardTile) world.getTileEntity(x, y, z);
            tile.drop(tile.decrStackSize(0, Stacks.size(tile.getStackInSlot(0))));
        }
        super.breakBlock(world, x, y, z, block, metadata);
    }
    public boolean hasComparatorInputOverride() { return true; }
    public int getComparatorInputOverride(World world, int x, int y, int z, int side) {
        TileEntity tile = world.getTileEntity(x, y, z);
        return tile instanceof CuttingBoardTile && !Stacks.empty(((CuttingBoardTile) tile).getStackInSlot(0)) ? 15 : 0;
    }
}
