package vectorwing.farmersdelight.legacy;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
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

public class GrillBlock extends BlockContainer {
    public static final PropertyDirection FACING = net.minecraft.block.BlockHorizontal.FACING;
    public static final PropertyBool LIT = PropertyBool.create("lit");
    private static final AxisAlignedBB PAN = new AxisAlignedBB(1D/16,0,1D/16,15D/16,0.25,15D/16);
    private final boolean stove;
    public final boolean isStove() { return stove; }
    public GrillBlock(boolean stove) {
        super(Material.IRON); this.stove=stove;
        setTranslationKey("farmersdelight."+(stove ? "stove" : "skillet")); setCreativeTab(Content.TAB);
        setHardness(stove ? 2F : 0.5F); setResistance(6F); setSoundType(SoundType.METAL);
        setDefaultState(blockState.getBaseState().withProperty(FACING,EnumFacing.NORTH).withProperty(LIT,false));
    }
    public TileEntity createNewTileEntity(World world,int meta) { return stove ? new StoveTile() : new SkilletTile(); }
    public boolean isOpaqueCube(IBlockState state) { return stove; }
    public boolean isFullCube(IBlockState state) { return stove; }
    public AxisAlignedBB getBoundingBox(IBlockState state,IBlockAccess world,BlockPos pos) { return stove ? FULL_BLOCK_AABB : PAN; }
    public int getLightValue(IBlockState state,IBlockAccess world,BlockPos pos) { return stove && state.getValue(LIT) ? 13 : 0; }
    public EnumBlockRenderType getRenderType(IBlockState state) { return EnumBlockRenderType.MODEL; }
    protected BlockStateContainer createBlockState() { return new BlockStateContainer(this,FACING,LIT); }
    public IBlockState getStateFromMeta(int meta) { return getDefaultState().withProperty(FACING,EnumFacing.byHorizontalIndex(meta & 3)).withProperty(LIT,(meta & 4)!=0); }
    public int getMetaFromState(IBlockState state) { return state.getValue(FACING).getHorizontalIndex() | (state.getValue(LIT) ? 4 : 0); }
    public IBlockState getStateForPlacement(World world,BlockPos pos,EnumFacing side,float x,float y,float z,int meta,EntityLivingBase placer) {
        return getDefaultState().withProperty(FACING,placer.getHorizontalFacing().getOpposite()).withProperty(LIT,stove);
    }
    public void onBlockPlacedBy(World world,BlockPos pos,IBlockState state,EntityLivingBase placer,ItemStack stack) {
        if (!stove && world.getTileEntity(pos) instanceof SkilletTile) ((SkilletTile)world.getTileEntity(pos)).identity(stack);
    }
    public boolean onBlockActivated(World world,BlockPos pos,IBlockState state,EntityPlayer player,EnumHand hand,EnumFacing side,float x,float y,float z) {
        if (!(world.getTileEntity(pos) instanceof GrillingTile)) return false;
        if (world.isRemote) return true;
        ItemStack held=player.getHeldItem(hand); GrillingTile tile=(GrillingTile)world.getTileEntity(pos); boolean hot=state.getValue(LIT);
        if (stove && !Stacks.empty(held)) {
            boolean shovel=held.getItem().getToolClasses(held).contains("shovel"), water=held.getItem()==Items.WATER_BUCKET;
            boolean lighter=held.getItem()==Items.FLINT_AND_STEEL, charge=held.getItem()==Items.FIRE_CHARGE;
            if (hot && (shovel || water) || !hot && (lighter || charge)) {
                world.setBlockState(pos,state.withProperty(LIT,!hot),3);
                world.playSound(null,pos,hot ? net.minecraft.init.SoundEvents.BLOCK_FIRE_EXTINGUISH : net.minecraft.init.SoundEvents.ITEM_FLINTANDSTEEL_USE,net.minecraft.util.SoundCategory.BLOCKS,0.8F,1F);
                if (!player.capabilities.isCreativeMode) {
                    if (water) player.setHeldItem(hand,new ItemStack(Items.BUCKET));
                    else if(charge) held.shrink(1); else held.damageItem(1,player);
                }
                return true;
            }
        }
        if (Stacks.empty(held) && !stove) {
            ItemStack removed=tile.decrStackSize(0,Stacks.size(tile.getStackInSlot(0)));
            if (!Stacks.empty(removed) && !player.capabilities.isCreativeMode && !player.inventory.addItemStackToInventory(removed)) tile.drop(removed);
            return true;
        }
        return tile.add(held,player.capabilities.isCreativeMode);
    }
    public void onEntityWalk(World world,BlockPos pos,Entity entity) {
        if (!world.isRemote && stove && world.getBlockState(pos).getValue(LIT) && entity instanceof EntityLivingBase && !entity.isImmuneToFire())
            entity.attackEntityFrom(net.minecraft.util.DamageSource.IN_FIRE,1F);
    }
    public boolean removedByPlayer(IBlockState state,World world,BlockPos pos,EntityPlayer player,boolean harvest) { return harvest || super.removedByPlayer(state,world,pos,player,false); }
    public void harvestBlock(World world,EntityPlayer player,BlockPos pos,IBlockState state,TileEntity tile,ItemStack tool) { super.harvestBlock(world,player,pos,state,tile,tool); world.setBlockToAir(pos); }
    public void getDrops(NonNullList<ItemStack> drops,IBlockAccess world,BlockPos pos,IBlockState state,int fortune) {
        TileEntity tile=world.getTileEntity(pos); drops.add(tile instanceof SkilletTile ? ((SkilletTile)tile).identity() : new ItemStack(this));
    }
    public void breakBlock(World world,BlockPos pos,IBlockState state) {
        if (!world.isRemote && world.getTileEntity(pos) instanceof GrillingTile) {
            GrillingTile tile=(GrillingTile)world.getTileEntity(pos);
            for (int i=0;i<tile.getSizeInventory();i++) tile.drop(tile.decrStackSize(i,Stacks.size(tile.getStackInSlot(i))));
        }
        super.breakBlock(world,pos,state);
    }
}
