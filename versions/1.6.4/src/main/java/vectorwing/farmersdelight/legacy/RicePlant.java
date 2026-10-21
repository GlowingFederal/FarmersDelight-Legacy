package vectorwing.farmersdelight.legacy;

import java.util.ArrayList;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;

import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.IShearable;
import net.minecraft.util.Icon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public final class RicePlant extends BlockContainer implements IShearable {
    public final boolean wild;
    @SideOnly(Side.CLIENT) private Icon[] stages; @SideOnly(Side.CLIENT) private Icon supporting;
    public RicePlant(boolean wild) {
        super(LegacyIds.block(wild ? "wild_rice" : "rice_crop"),Material.water); this.wild=wild; setTickRandomly(true); setHardness(0);
        setUnlocalizedName("farmersdelight."+(wild ? "wild_rice" : "rice_crop"));
        setBlockBounds(0.0625F,0,0.0625F,0.9375F,1,0.9375F);
    }
    public TileEntity createNewTileEntity(World world) { return new RiceTile(); }
    private static boolean ground(Block block) { return block==Block.dirt || block==Block.grass || block==Block.tilledField || block==Content.richSoil || block==Content.richFarmland; }
    public boolean canPlant(World world,int x,int y,int z) {
        return y>0 && y<254 && NativeRegistry.at(world,x,y,z) instanceof net.minecraft.block.BlockFluid && world.getBlockMaterial(x,y,z)==Material.water && world.getBlockMetadata(x,y,z)==0 && ground(NativeRegistry.at(world,x,y-1,z)) && (!wild || world.isAirBlock(x,y+1,z));
    }
    public void place(World world,int x,int y,int z) {
        NativeRegistry.set(world,x,y,z,this,0,3);
        if(wild) NativeRegistry.set(world,x,y+1,z,Content.wildRiceUpper,0,3);
    }
    public boolean isOpaqueCube() { return false; }
    public boolean renderAsNormalBlock() { return false; }
    public int getRenderType() { return Foundation.proxy.riceRenderId(); }
    public net.minecraft.util.AxisAlignedBB getCollisionBoundingBoxFromPool(World world,int x,int y,int z) { return null; }
    public boolean canCollideCheck(int meta,boolean liquid) { return true; }
    public boolean isBlockReplaceable(World world,int x,int y,int z) { return false; }
    private RiceTile tile(IBlockAccess world,int x,int y,int z) { TileEntity tile=world.getBlockTileEntity(x,y,z); return tile instanceof RiceTile ? (RiceTile)tile : null; }
    public void onNeighborBlockChange(World world,int x,int y,int z,int changed) {
        if(world.isRemote || ground(NativeRegistry.at(world,x,y-1,z)) && (!wild || NativeRegistry.at(world,x,y+1,z)==Content.wildRiceUpper)) return;
        dropBlockAsItem(world,x,y,z,0,0); world.setBlockToAir(x,y,z);
    }
    @Override public boolean removeBlockByPlayer(World world,net.minecraft.entity.player.EntityPlayer player,int x,int y,int z) {
        boolean removed=super.removeBlockByPlayer(world,player,x,y,z);
        Block replacement=NativeRegistry.at(world,x,y,z);
        // Restoring source water inside breakBlock changes the chunk's requested air
        // replacement, so vanilla reports false even though this crop was removed.
        return removed || !world.isRemote && (replacement==Block.waterStill || replacement==Block.waterMoving);
    }
    public void breakBlock(World world,int x,int y,int z,int block,int meta) {
        RiceTile tile=tile(world,x,y,z); boolean restore=tile==null || !tile.draining;
        super.breakBlock(world,x,y,z,block,meta);
        if(!world.isRemote && restore && world.isAirBlock(x,y,z))
            NativeRegistry.set(world,x,y,z,Block.waterStill,0,3);
    }
    public int idDropped(int meta,Random random,int fortune) { return NativeRegistry.idOrZero(Content.ITEMS.get("rice")); }
    public void updateTick(World world,int x,int y,int z,Random random) {
        if(world.isRemote || wild || !world.checkChunksExist(x-1,y,z-1,x+1,y+1,z+1) || world.getBlockLightValue(x,y+1,z)<6) return;
        if(NativeRegistry.at(world,x,y+1,z)!=Content.riceUpper && random.nextInt(3)==0) advance(world,x,y,z,1);
    }
    private void advance(World world,int x,int y,int z,int amount) {
        RiceTile tile=tile(world,x,y,z); if(tile==null) return;
        int next=tile.age+amount;
        if(next<=3) { tile.age(next); return; }
        Block upper=wild ? Content.wildRiceUpper : Content.riceUpper;
        if(NativeRegistry.at(world,x,y+1,z)==upper) {
            if(upper instanceof RiceUpper) ((RiceUpper)upper).boost(world,x,y+1,z);
        } else if(world.isAirBlock(x,y+1,z)) {
            tile.age(3);
            NativeRegistry.set(world,x,y+1,z,upper,Math.min(3,next-4),3);
        }
    }
    public boolean func_149851_a(World world,int x,int y,int z,boolean client) {
        return wild || NativeRegistry.at(world,x,y+1,z)!=Content.riceUpper || world.getBlockMetadata(x,y+1,z)<3;
    }
    public boolean func_149852_a(World world,Random random,int x,int y,int z) { return true; }
    public void func_149853_b(World world,Random random,int x,int y,int z) {
        if(wild) dropBlockAsItem_do(world,x,y,z,new ItemStack(Content.ITEMS.get("wild_rice")));
        else advance(world,x,y,z,1+random.nextInt(4));
    }
    public boolean isShearable(ItemStack item,World world,int x,int y,int z) { return wild; }
    public ArrayList<ItemStack> onSheared(ItemStack item,World world,int x,int y,int z,int fortune) { ArrayList<ItemStack> result=new ArrayList<ItemStack>(); result.add(new ItemStack(Content.ITEMS.get("wild_rice"))); return result; }
    @SideOnly(Side.CLIENT) public void registerIcons(net.minecraft.client.renderer.texture.IconRegister register) {
        stages=new Icon[wild ? 1 : 4];
        for(int i=0;i<stages.length;i++) stages[i]=register.registerIcon("farmersdelight:"+(wild ? "wild_rice_bottom" : "rice_stage"+i));
        supporting=register.registerIcon("farmersdelight:rice_supporting");
    }
    @SideOnly(Side.CLIENT) public Icon getIcon(int side,int meta) { return stages[0]; }
    @SideOnly(Side.CLIENT) public Icon getBlockTexture(IBlockAccess world,int x,int y,int z,int side) {
        RiceTile tile=tile(world,x,y,z);
        return wild ? stages[0] : NativeRegistry.at(world,x,y+1,z)==Content.riceUpper ? supporting : stages[tile==null ? 0 : tile.age];
    }
}
