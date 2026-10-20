package vectorwing.farmersdelight.legacy;

import java.util.ArrayList;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.IGrowable;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.IShearable;
import cpw.mods.fml.relauncher.Side; import cpw.mods.fml.relauncher.SideOnly;

public final class MushroomColony extends BlockBush implements IGrowable,IShearable {
    public boolean canPlaceBlockAt(World world,int x,int y,int z) { return world.isAirBlock(x,y,z) && canBlockStay(world,x,y,z); }
    private final boolean red;
    @SideOnly(Side.CLIENT) private net.minecraft.util.IIcon[] stages;
    public MushroomColony(boolean red) {
        super(); this.red=red; setTickRandomly(true); setCreativeTab(Content.TAB);
        setBlockName("farmersdelight."+(red ? "red" : "brown")+"_mushroom_colony");

    }
    private Item mushroom() { return Item.getItemFromBlock(red ? Blocks.red_mushroom : Blocks.brown_mushroom); }
    public boolean canBlockStay(World world,int x,int y,int z) {
        Block floor=world.getBlock(x,y-1,z);
        return floor==Content.richSoil || floor==Content.compost || floor==Blocks.mycelium || floor==Blocks.dirt && world.getBlockMetadata(x,y-1,z)==2
                || world.getBlockLightValue(x,y,z)<13 && world.isSideSolid(x,y-1,z,net.minecraftforge.common.util.ForgeDirection.UP);
    }
    public void updateTick(World world,int x,int y,int z,Random random) {
        super.updateTick(world,x,y,z,random);
        if(!world.isRemote && world.getBlock(x,y,z)==this && world.getBlock(x,y-1,z)==Content.richSoil
                && world.getBlockLightValue(x,y+1,z)<=12 && world.getBlockMetadata(x,y,z)<3 && random.nextInt(5)==0)
            world.setBlockMetadataWithNotify(x,y,z,world.getBlockMetadata(x,y,z)+1,2);
    }
    public ArrayList<ItemStack> getDrops(World world,int x,int y,int z,int meta,int fortune) {
        ArrayList<ItemStack> result=new ArrayList<ItemStack>(); result.add(new ItemStack(mushroom(),2+(meta&3))); return result;
    }
    public boolean onBlockActivated(World world,int x,int y,int z,EntityPlayer player,int side,float hx,float hy,float hz) {
        int age=world.getBlockMetadata(x,y,z); ItemStack held=player.getHeldItem();
        if(age<=0 || !CuttingRecipes.tool(held,"shears")) return false;
        if(!world.isRemote) { world.setBlockMetadataWithNotify(x,y,z,age-1,2); dropBlockAsItem(world,x,y,z,new ItemStack(mushroom())); if(!player.capabilities.isCreativeMode) held.damageItem(1,player); }
        return true;
    }
    public boolean isShearable(ItemStack item,IBlockAccess world,int x,int y,int z) { return (world.getBlockMetadata(x,y,z)&3)==3; }
    public ArrayList<ItemStack> onSheared(ItemStack item,IBlockAccess world,int x,int y,int z,int fortune) { ArrayList<ItemStack> result=new ArrayList<ItemStack>(); result.add(new ItemStack(this)); return result; }
    public boolean func_149851_a(World world,int x,int y,int z,boolean client) { return false; }
    public boolean func_149852_a(World world,Random random,int x,int y,int z) { return false; }
    public void func_149853_b(World world,Random random,int x,int y,int z) { world.setBlockMetadataWithNotify(x,y,z,Math.min(3,world.getBlockMetadata(x,y,z)+1),2); }
    @SideOnly(Side.CLIENT) public void registerBlockIcons(net.minecraft.client.renderer.texture.IIconRegister register) {
        stages=new net.minecraft.util.IIcon[4]; for(int i=0;i<4;i++) stages[i]=register.registerIcon("farmersdelight:"+(red ? "red" : "brown")+"_mushroom_colony_stage"+i);
    }
    @SideOnly(Side.CLIENT) public net.minecraft.util.IIcon getIcon(int side,int meta) { return stages[meta&3]; }
}
