package vectorwing.farmersdelight.legacy;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import net.minecraft.world.IBlockAccess;
import net.minecraft.entity.Entity;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
public final class PackedBlock extends Block {
    public final String id;
    private final boolean bale;
    public static final net.minecraft.block.properties.PropertyDirection FACING=net.minecraft.block.properties.PropertyDirection.create("facing");
    public PackedBlock(String id) {
        super(id.endsWith("_crate") ? Material.WOOD : Material.CLOTH);
        this.id=id; bale=id.equals("rice_bale"); setHardness(id.endsWith("_crate") ? 2 : 0.5F); setCreativeTab(Content.TAB);
        setTranslationKey("farmersdelight."+id);
        setDefaultState(blockState.getBaseState().withProperty(FACING,EnumFacing.UP));
    }
    protected BlockStateContainer createBlockState() { return new BlockStateContainer(this,FACING); }
    public IBlockState getStateFromMeta(int meta) { return getDefaultState().withProperty(FACING,bale ? EnumFacing.byIndex(meta&7) : EnumFacing.UP); }
    public int getMetaFromState(IBlockState state) { return bale ? state.getValue(FACING).getIndex() : 0; }
    public IBlockState getStateForPlacement(World world,BlockPos pos,EnumFacing side,float x,float y,float z,int meta,net.minecraft.entity.EntityLivingBase placer) { return getDefaultState().withProperty(FACING,bale ? side : EnumFacing.UP); }
    public void onFallenUpon(World world,BlockPos pos,Entity entity,float distance) { entity.fall(distance,bale ? 0.2F : 1); }
    public int getFlammability(IBlockAccess world,BlockPos pos,EnumFacing face) { return bale ? 20 : super.getFlammability(world,pos,face); }
    public int getFireSpreadSpeed(IBlockAccess world,BlockPos pos,EnumFacing face) { return bale ? 60 : super.getFireSpreadSpeed(world,pos,face); }
}

