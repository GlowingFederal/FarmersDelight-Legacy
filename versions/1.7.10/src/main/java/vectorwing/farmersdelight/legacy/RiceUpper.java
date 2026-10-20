package vectorwing.farmersdelight.legacy;

import java.util.ArrayList;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.IShearable;
import net.minecraft.util.IIcon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public final class RiceUpper extends BlockCrops implements IShearable {
    public int getRenderType() { return 1; }
    private final boolean wild;
    @SideOnly(Side.CLIENT) private IIcon[] stages;
    public RiceUpper(boolean wild) { super(); this.wild=wild; setBlockName("farmersdelight."+(wild ? "wild_rice_upper" : "rice_upper_crop")); }
    protected Item func_149866_i() { return Content.ITEMS.get("rice"); }
    protected Item func_149865_P() { return Content.ITEMS.get("rice_panicle"); }
    protected boolean canPlaceBlockOn(Block block) { return block==(wild ? Content.wildRice : Content.riceCrop); }
    public boolean canBlockStay(World world,int x,int y,int z) { return world.getBlock(x,y-1,z)==(wild ? Content.wildRice : Content.riceCrop) && (wild || world.getBlockLightValue(x,y,z)>=8 || world.canBlockSeeTheSky(x,y,z)); }
    public void updateTick(World world,int x,int y,int z,Random random) {
        if(!canBlockStay(world,x,y,z)) { dropBlockAsItem(world,x,y,z,world.getBlockMetadata(x,y,z),0); world.setBlockToAir(x,y,z); return; }
        int age=world.getBlockMetadata(x,y,z);
        if(!wild && age<3 && world.getBlockLightValue(x,y+1,z)>=9 && random.nextInt(26)==0) world.setBlockMetadataWithNotify(x,y,z,age+1,2);
    }
    public void boost(World world,int x,int y,int z) {
        if(wild) dropBlockAsItem(world,x,y,z,new ItemStack(Content.ITEMS.get("wild_rice")));
        else world.setBlockMetadataWithNotify(x,y,z,Math.min(3,world.getBlockMetadata(x,y,z)+(2+world.rand.nextInt(4))/3),2);
    }
    public boolean func_149851_a(World world,int x,int y,int z,boolean client) { return wild || world.getBlockMetadata(x,y,z)<3; }
    public void func_149853_b(World world,Random random,int x,int y,int z) { boost(world,x,y,z); }
    public ArrayList<ItemStack> getDrops(World world,int x,int y,int z,int meta,int fortune) {
        ArrayList<ItemStack> result=new ArrayList<ItemStack>();
        if(!wild && meta>=3) result.add(new ItemStack(Content.ITEMS.get("rice_panicle"))); return result;
    }
    public boolean isShearable(ItemStack item,IBlockAccess world,int x,int y,int z) { return wild && world.getBlock(x,y-1,z)==Content.wildRice; }
    public ArrayList<ItemStack> onSheared(ItemStack item,IBlockAccess world,int x,int y,int z,int fortune) {
        if(world instanceof World) ((World)world).setBlock(x,y-1,z,net.minecraft.init.Blocks.water,0,3);
        ArrayList<ItemStack> result=new ArrayList<ItemStack>(); result.add(new ItemStack(Content.ITEMS.get("wild_rice"))); return result;
    }
    @SideOnly(Side.CLIENT) public void registerBlockIcons(net.minecraft.client.renderer.texture.IIconRegister register) {
        stages=new IIcon[wild ? 1 : 4];
        for(int i=0;i<stages.length;i++) stages[i]=register.registerIcon("farmersdelight:"+(wild ? "wild_rice_top" : "rice_upper_crop_stage"+i));
    }
    @SideOnly(Side.CLIENT) public IIcon getIcon(int side,int meta) { return stages[wild ? 0 : Math.max(0,Math.min(3,meta))]; }
}
