package vectorwing.farmersdelight.legacy;

import net.minecraft.block.Block;
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
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public final class CookingPot extends BlockContainer {
    public static final PropertyDirection FACING = net.minecraft.block.BlockHorizontal.FACING;
    public static final net.minecraft.block.properties.PropertyInteger SUPPORT=net.minecraft.block.properties.PropertyInteger.create("support",0,2);
    private static final AxisAlignedBB SHAPE = new AxisAlignedBB(0.125, 0, 0.125, 0.875, 0.625, 0.875);
    public CookingPot() {
        super(Material.IRON); setTranslationKey("farmersdelight.cooking_pot"); setCreativeTab(Content.TAB);
        setHardness(0.5F); setResistance(6F); setSoundType(SoundType.METAL);
        setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(SUPPORT,0));
    }
    public TileEntity createNewTileEntity(World world, int metadata) { return new CookingPotTile(); }
    @Override public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean harvest) {
        return harvest || super.removedByPlayer(state, world, pos, player, false);
    }
    @Override public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity tile, ItemStack tool) {
        super.harvestBlock(world, player, pos, state, tile, tool); world.setBlockToAir(pos);
    }
    public boolean isOpaqueCube(IBlockState state) { return false; }
    public boolean isFullCube(IBlockState state) { return false; }
    public EnumBlockRenderType getRenderType(IBlockState state) { return EnumBlockRenderType.MODEL; }
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) { return SHAPE; }
    public void addCollisionBoxToList(IBlockState state,World world,BlockPos pos,AxisAlignedBB entityBox,java.util.List<AxisAlignedBB> boxes,net.minecraft.entity.Entity entity,boolean actual) {
        addCollisionBoxToList(pos,entityBox,boxes,SHAPE);
        if(state.getValue(SUPPORT)==1) addCollisionBoxToList(pos,entityBox,boxes,new AxisAlignedBB(0,-0.0625,0,1,0,1));
    }
    protected BlockStateContainer createBlockState() { return new BlockStateContainer(this, FACING,SUPPORT); }
    public IBlockState getStateFromMeta(int meta) { return getDefaultState().withProperty(FACING, EnumFacing.byHorizontalIndex(meta & 3)).withProperty(SUPPORT,Math.min(2,(meta>>2)&3)); }
    public int getMetaFromState(IBlockState state) { return state.getValue(FACING).getHorizontalIndex()|(state.getValue(SUPPORT)<<2); }
    private int tray(World world,BlockPos pos) {
        String id=world.getBlockState(pos.down()).getBlock().getRegistryName().toString();
        return id.equals("minecraft:campfire") || id.equals("minecraft:soul_campfire") ? 1 : 0;
    }
    public void neighborChanged(IBlockState state,World world,BlockPos pos,net.minecraft.block.Block block,BlockPos from) {
        if(!world.isRemote && state.getValue(SUPPORT)!=2 && state.getValue(SUPPORT)!=tray(world,pos))
            world.setBlockState(pos,state.withProperty(SUPPORT,tray(world,pos)),3);
    }
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing side, float x, float y, float z, int meta, EntityLivingBase placer) {
        return getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite()).withProperty(SUPPORT,side==EnumFacing.DOWN ? 2 : tray(world,pos));
    }
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        if (world.getTileEntity(pos) instanceof CookingPotTile) {
            CookingPotTile pot = (CookingPotTile) world.getTileEntity(pos); pot.restoreMeal(stack);
            if (stack.hasDisplayName()) pot.name(stack.getDisplayName());
        }
    }
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float x, float y, float z) {
        if(player.isSneaking() && Stacks.empty(player.getHeldItem(hand))) {
            if(!world.isRemote) {
                world.setBlockState(pos,state.withProperty(SUPPORT,state.getValue(SUPPORT)==2 ? tray(world,pos) : 2),3);
                world.playSound(null,pos,net.minecraft.init.SoundEvents.BLOCK_METAL_PLACE,net.minecraft.util.SoundCategory.BLOCKS,0.7F,1F);
            }
            return true;
        }
        if (!(world.getTileEntity(pos) instanceof CookingPotTile)) return false;
        if (!world.isRemote) {
            CookingPotTile tile = (CookingPotTile) world.getTileEntity(pos);
            ItemStack served = tile.serve(player.getHeldItem(hand), player.capabilities.isCreativeMode);
            if (!Stacks.empty(served)) { if (!player.inventory.addItemStackToInventory(served)) tile.drop(served); }
            else player.openGui(Foundation.instance, 0, world, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        TileEntity tile = world.getTileEntity(pos);
        drops.add(tile instanceof CookingPotTile ? ((CookingPotTile) tile).portable() : new ItemStack(this));
    }
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        if (!world.isRemote && world.getTileEntity(pos) instanceof CookingPotTile) {
            CookingPotTile pot = (CookingPotTile) world.getTileEntity(pos);
            for (int i = 0; i < 9; i++) if (i != 6) pot.drop(pot.decrStackSize(i, Stacks.size(pot.getStackInSlot(i))));
            pot.payExperience();
        }
        super.breakBlock(world, pos, state);
    }
    public boolean hasComparatorInputOverride(IBlockState state) { return true; }
    public int getComparatorInputOverride(IBlockState state, World world, BlockPos pos) {
        return Math.min(15,net.minecraft.inventory.Container.calcRedstoneFromInventory((net.minecraft.inventory.IInventory) world.getTileEntity(pos)));
    }
}
