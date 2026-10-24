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
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

/** The remaining portions belong to the placed meal and never regenerate on pickup. */
public class ServingBlock extends BlockContainer {
    public final String id;
    public final boolean pie, leftovers;
    private final String serving;
    public static final net.minecraft.block.properties.PropertyDirection FACING=net.minecraft.block.BlockHorizontal.FACING;
    public static final PropertyInteger SERVINGS=PropertyInteger.create("servings",0,4);
    public ServingBlock(String id,boolean pie,String serving) {
        this("farmersdelight",id,pie,"farmersdelight:"+serving,!pie && !id.equals("stuffed_pumpkin_block"));
    }
    /** Native addon feast using the same serving, persistence and duplication protections. */
    public ServingBlock(String namespace,String id,boolean pie,String serving,boolean leftovers) {
        super(Material.CLOTH); this.id=id; this.pie=pie; this.serving=serving; this.leftovers=leftovers;
        setTranslationKey(namespace+"."+id); setCreativeTab(Content.TAB); setHardness(0.5F);
        setDefaultState(blockState.getBaseState().withProperty(FACING,EnumFacing.NORTH).withProperty(SERVINGS,4));
    }
    public TileEntity createNewTileEntity(World world,int meta) { return new ServingTile(); }
    public static int portions(TileEntity tile) { return tile instanceof ServingTile ? ((ServingTile)tile).remaining : 0; }
    public boolean isOpaqueCube(IBlockState state) { return false; }
    public boolean isFullCube(IBlockState state) { return false; }
    public net.minecraft.util.EnumBlockRenderType getRenderType(IBlockState state) { return net.minecraft.util.EnumBlockRenderType.MODEL; }
    protected BlockStateContainer createBlockState() { return new BlockStateContainer(this,FACING,SERVINGS); }
    public IBlockState getStateFromMeta(int meta) { return getDefaultState().withProperty(FACING,EnumFacing.byHorizontalIndex(meta&3)); }
    public int getMetaFromState(IBlockState state) { return state.getValue(FACING).getHorizontalIndex(); }
    public IBlockState getActualState(IBlockState state,IBlockAccess world,BlockPos pos) { return state.withProperty(SERVINGS,portions(world.getTileEntity(pos))); }
    public IBlockState getStateForPlacement(World world,BlockPos pos,EnumFacing side,float x,float y,float z,int meta,EntityLivingBase placer) {
        return getDefaultState().withProperty(FACING,pie ? placer.getHorizontalFacing() : placer.getHorizontalFacing().getOpposite());
    }
    public net.minecraft.util.math.AxisAlignedBB getBoundingBox(IBlockState state,IBlockAccess world,BlockPos pos) {
        return new net.minecraft.util.math.AxisAlignedBB(0.0625,0,0.0625,0.9375,pie ? 0.25 : portions(world.getTileEntity(pos))==0 ? 0.125 : 0.625,0.9375);
    }
    public boolean canPlaceBlockAt(World world,BlockPos pos) { return super.canPlaceBlockAt(world,pos) && world.getBlockState(pos.down()).getMaterial().isSolid(); }
    public void neighborChanged(IBlockState state,World world,BlockPos pos,Block changed,BlockPos from) {
        if(!world.isRemote && !world.getBlockState(pos.down()).getMaterial().isSolid()) { dropBlockAsItem(world,pos,state,0); world.setBlockToAir(pos); }
    }
    public boolean onBlockActivated(World world,BlockPos pos,IBlockState state,EntityPlayer player,EnumHand hand,EnumFacing side,float x,float y,float z) {
        TileEntity raw=world.getTileEntity(pos);
        if(!(raw instanceof ServingTile)) return false;
        ServingTile tile=(ServingTile)raw;
        if(tile.remaining==0) {
            if(!world.isRemote) { dropBlockAsItem(world,pos,state,0); world.setBlockToAir(pos); }
            return true;
        }
        ItemStack held=player.getHeldItem(hand);
        ItemStack portion=Stacks.resolve(serving);
        boolean knife=Ingredients.isKnife(held);
        if(pie && !knife && !player.canEat(false)) return false;
        if(!pie && (Stacks.empty(held) || held.getItem()!=net.minecraft.init.Items.BOWL)) return false;
        if(world.isRemote) return true;
        tile.take();
        if(pie && !knife) ((LegacyFood)portion.getItem()).eatPlaced(player,world);
        else {
            if(!pie && !player.capabilities.isCreativeMode) Stacks.shrink(held,1);
            if(pie) spawnAsEntity(world,pos,portion);
            else if(!player.inventory.addItemStackToInventory(portion)) player.dropItem(portion,false);
        }
        if(tile.remaining==0 && !leftovers) world.setBlockToAir(pos);
        return true;
    }
    private void drops(java.util.List<ItemStack> drops,int portions) {
        if(portions==4) { drops.add(new ItemStack(this)); return; }
        if(!leftovers) return;
        drops.add(new ItemStack(net.minecraft.init.Items.BOWL));
        if(id.equals("honey_glazed_ham_block")) drops.add(new ItemStack(net.minecraft.init.Items.BONE));
        if(id.equals("roast_chicken_block")) drops.add(Stacks.resolve("minecraft:bone_meal"));
    }
    public void getDrops(net.minecraft.util.NonNullList<ItemStack> drops,IBlockAccess world,BlockPos pos,IBlockState state,int fortune) { drops(drops,portions(world.getTileEntity(pos))); }
    public boolean removedByPlayer(IBlockState state,World world,BlockPos pos,EntityPlayer player,boolean harvest) { return harvest || super.removedByPlayer(state,world,pos,player,false); }
    public void harvestBlock(World world,EntityPlayer player,BlockPos pos,IBlockState state,TileEntity tile,ItemStack tool) {
        super.harvestBlock(world,player,pos,state,tile,tool); world.setBlockToAir(pos);
    }
    public boolean hasComparatorInputOverride(IBlockState state) { return true; }
    public int getComparatorInputOverride(IBlockState state,World world,BlockPos pos) { return portions(world.getTileEntity(pos)); }
}

