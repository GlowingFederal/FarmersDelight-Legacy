package vectorwing.farmersdelight.legacy;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public final class CuttingBoard extends BlockContainer {
    public static final PropertyDirection FACING = net.minecraft.block.BlockHorizontal.FACING;
    private static final AxisAlignedBB SHAPE = new AxisAlignedBB(1D / 16, 0, 1D / 16, 15D / 16, 1D / 16, 15D / 16);
    public CuttingBoard() {
        super(Material.WOOD); setTranslationKey("farmersdelight.cutting_board"); setCreativeTab(Content.TAB);
        setHardness(2F); setSoundType(SoundType.WOOD); setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
    }
    public TileEntity createNewTileEntity(World world, int metadata) { return new CuttingBoardTile(); }
    public boolean isOpaqueCube(IBlockState state) { return false; }
    public boolean isFullCube(IBlockState state) { return false; }
    public EnumBlockRenderType getRenderType(IBlockState state) { return EnumBlockRenderType.MODEL; }
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) { return SHAPE; }
    protected BlockStateContainer createBlockState() { return new BlockStateContainer(this, FACING); }
    public IBlockState getStateFromMeta(int meta) { return getDefaultState().withProperty(FACING, EnumFacing.byHorizontalIndex(meta & 3)); }
    public int getMetaFromState(IBlockState state) { return state.getValue(FACING).getHorizontalIndex(); }
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing side, float x, float y, float z, int meta, EntityLivingBase placer) {
        return getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }
    public boolean canPlaceBlockAt(World world, BlockPos pos) { return super.canPlaceBlockAt(world, pos) && world.isSideSolid(pos.down(), EnumFacing.UP); }
    public void neighborChanged(IBlockState state, World world, BlockPos pos, net.minecraft.block.Block block, BlockPos from) {
        if (!world.isRemote && !world.isSideSolid(pos.down(), EnumFacing.UP)) { dropBlockAsItem(world, pos, state, 0); world.setBlockToAir(pos); }
    }
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float x, float y, float z) {
        if (!(world.getTileEntity(pos) instanceof CuttingBoardTile)) return false;
        CuttingBoardTile tile = (CuttingBoardTile) world.getTileEntity(pos);
        ItemStack held = player.getHeldItem(hand);
        if (world.isRemote) return true;
        if (Stacks.empty(tile.getStackInSlot(0))) {
            if (Stacks.empty(held)) return false;
            tile.setInventorySlotContents(0, Stacks.copy(held, 1));
            tile.carved = player.isSneaking() && (held.getItem().isDamageable() || held.getItem() instanceof net.minecraft.item.ItemShears);
            if (!player.capabilities.isCreativeMode) held.shrink(1);
            tile.changed();
        } else if (Stacks.empty(held)) {
            ItemStack removed = tile.decrStackSize(0, Stacks.size(tile.getStackInSlot(0)));
            tile.carved = false;
            if (!player.capabilities.isCreativeMode && !player.inventory.addItemStackToInventory(removed)) tile.drop(removed);
            tile.changed();
        } else tile.process(held, player);
        return true;
    }
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        if (!world.isRemote && world.getTileEntity(pos) instanceof CuttingBoardTile) {
            CuttingBoardTile tile = (CuttingBoardTile) world.getTileEntity(pos);
            tile.drop(tile.decrStackSize(0, Stacks.size(tile.getStackInSlot(0))));
        }
        super.breakBlock(world, pos, state);
    }
    public boolean hasComparatorInputOverride(IBlockState state) { return true; }
    public int getComparatorInputOverride(IBlockState state, World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        return tile instanceof CuttingBoardTile && !Stacks.empty(((CuttingBoardTile) tile).getStackInSlot(0)) ? 15 : 0;
    }
    public boolean eventReceived(IBlockState state, World world, BlockPos pos, int id, int parameter) {
        TileEntity tile = world.getTileEntity(pos); return tile != null && tile.receiveClientEvent(id, parameter);
    }
}
