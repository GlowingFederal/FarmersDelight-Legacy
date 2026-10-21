package vectorwing.farmersdelight.legacy;

import java.util.ArrayList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.IBlockAccess;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.BlockPos;

/** The remaining portions belong to the placed meal and never regenerate on pickup. */
public final class ServingBlock extends BlockContainer {
    public final String id;
    public final boolean pie, leftovers;
    private final String serving;
    public static final net.minecraft.block.properties.PropertyDirection FACING=net.minecraft.block.BlockChest.FACING;
    public static final PropertyInteger SERVINGS=PropertyInteger.create("servings",0,4);
    public ServingBlock(String id,boolean pie,String serving) {
        super(Material.cloth); this.id=id; this.pie=pie; this.serving=serving;
        leftovers=!pie && !id.equals("stuffed_pumpkin_block");
        setUnlocalizedName("farmersdelight."+id); setCreativeTab(Content.TAB); setHardness(0.5F);
        setDefaultState(blockState.getBaseState().withProperty(FACING,EnumFacing.NORTH).withProperty(SERVINGS,4));
    }
    public TileEntity createNewTileEntity(World world,int meta) { return new ServingTile(); }
    public static int portions(TileEntity tile) { return tile instanceof ServingTile ? ((ServingTile)tile).remaining : 0; }
    public boolean isOpaqueCube() { return false; }
    public boolean isFullCube() { return false; }
    public int getRenderType() { return 3; }
    protected BlockState createBlockState() { return new BlockState(this,FACING,SERVINGS); }
    public IBlockState getStateFromMeta(int meta) { return getDefaultState().withProperty(FACING,EnumFacing.getHorizontal(meta&3)); }
    public int getMetaFromState(IBlockState state) { return state.getValue(FACING).getHorizontalIndex(); }
    public IBlockState getActualState(IBlockState state,IBlockAccess world,BlockPos pos) { return state.withProperty(SERVINGS,portions(world.getTileEntity(pos))); }
    public IBlockState onBlockPlaced(World world,BlockPos pos,EnumFacing side,float x,float y,float z,int meta,EntityLivingBase placer) {
        return getDefaultState().withProperty(FACING,pie ? placer.getHorizontalFacing() : placer.getHorizontalFacing().getOpposite());
    }
    public net.minecraft.util.AxisAlignedBB shape(IBlockState state,IBlockAccess world,BlockPos pos) {
        return new net.minecraft.util.AxisAlignedBB(0.0625,0,0.0625,0.9375,pie ? 0.25 : portions(world.getTileEntity(pos))==0 ? 0.125 : 0.625,0.9375);
    }
    public boolean canPlaceBlockAt(World world,BlockPos pos) { return super.canPlaceBlockAt(world,pos) && world.getBlockState(pos.down()).getBlock().getMaterial().isSolid(); }
    public void onNeighborBlockChange(World world,BlockPos pos,IBlockState state,Block changed) {
        if(!world.isRemote && !world.getBlockState(pos.down()).getBlock().getMaterial().isSolid()) { dropBlockAsItem(world,pos,state,0); world.setBlockToAir(pos); }
    }
    public boolean onBlockActivated(World world,BlockPos pos,IBlockState state,EntityPlayer player,EnumFacing side,float x,float y,float z) {
        TileEntity raw=world.getTileEntity(pos);
        if(!(raw instanceof ServingTile)) return false;
        ServingTile tile=(ServingTile)raw;
        if(tile.remaining==0) {
            if(!world.isRemote) { dropBlockAsItem(world,pos,state,0); world.setBlockToAir(pos); }
            return true;
        }
        ItemStack held=player.getHeldItem();
        ItemStack portion=new ItemStack(Content.ITEMS.get(serving));
        boolean knife=Ingredients.isKnife(held);
        if(pie && !knife && !player.canEat(false)) return false;
        if(!pie && (Stacks.empty(held) || held.getItem()!=net.minecraft.init.Items.bowl)) return false;
        if(world.isRemote) return true;
        tile.take();
        if(pie && !knife) ((LegacyFood)portion.getItem()).eatPlaced(player,world);
        else {
            if(!pie && !player.capabilities.isCreativeMode) Stacks.shrink(held,1);
            if(pie) spawnAsEntity(world,pos,portion);
            else if(!player.inventory.addItemStackToInventory(portion)) player.dropPlayerItemWithRandomChoice(portion,false);
        }
        if(tile.remaining==0 && !leftovers) world.setBlockToAir(pos);
        return true;
    }
    private void drops(java.util.List<ItemStack> drops,int portions) {
        if(portions==4) { drops.add(new ItemStack(this)); return; }
        if(!leftovers) return;
        drops.add(new ItemStack(net.minecraft.init.Items.bowl));
        if(id.equals("honey_glazed_ham_block")) drops.add(new ItemStack(net.minecraft.init.Items.bone));
        if(id.equals("roast_chicken_block")) drops.add(Stacks.resolve("minecraft:bone_meal"));
    }
    public java.util.List<ItemStack> getDrops(IBlockAccess world,BlockPos pos,IBlockState state,int fortune) { java.util.List<ItemStack> result=new java.util.ArrayList<ItemStack>(); drops(result,portions(world.getTileEntity(pos))); return result; }
    public boolean removedByPlayer(World world,BlockPos pos,EntityPlayer player,boolean harvest) { return harvest || super.removedByPlayer(world,pos,player,false); }
    public void harvestBlock(World world,EntityPlayer player,BlockPos pos,IBlockState state,TileEntity tile) {
        super.harvestBlock(world,player,pos,state,tile); world.setBlockToAir(pos);
    }
    public boolean hasComparatorInputOverride() { return true; }
    public int getComparatorInputOverride(World world,BlockPos pos) { return portions(world.getTileEntity(pos)); }
    @Override public void setBlockBoundsBasedOnState(net.minecraft.world.IBlockAccess world,net.minecraft.util.BlockPos pos) {
        net.minecraft.util.AxisAlignedBB b=shape(world.getBlockState(pos),world,pos);
        setBlockBounds((float)b.minX,(float)b.minY,(float)b.minZ,(float)b.maxX,(float)b.maxY,(float)b.maxZ);
    }
    @Override public net.minecraft.util.AxisAlignedBB getSelectedBoundingBox(net.minecraft.world.World world,net.minecraft.util.BlockPos pos) {
        setBlockBoundsBasedOnState(world,pos); return super.getSelectedBoundingBox(world,pos);
    }
    @Override public net.minecraft.util.AxisAlignedBB getCollisionBoundingBox(net.minecraft.world.World world,net.minecraft.util.BlockPos pos,net.minecraft.block.state.IBlockState state) {
        return shape(state,world,pos).offset(pos.getX(),pos.getY(),pos.getZ());
    }
}

