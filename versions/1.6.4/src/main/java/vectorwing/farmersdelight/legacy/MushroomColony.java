package vectorwing.farmersdelight.legacy;

import java.util.ArrayList;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower;


import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.IShearable;
import cpw.mods.fml.relauncher.Side; import cpw.mods.fml.relauncher.SideOnly;

public final class MushroomColony extends BlockFlower implements IShearable {
    public boolean canPlaceBlockAt(World world,int x,int y,int z) { return world.isAirBlock(x,y,z) && canBlockStay(world,x,y,z); }
    private final boolean red;
    @SideOnly(Side.CLIENT) private net.minecraft.util.Icon[] stages;
    public MushroomColony(boolean red) {
        super(LegacyIds.block((red ? "red" : "brown")+"_mushroom_colony")); this.red=red; setTickRandomly(true); setCreativeTab(Content.TAB);
        setUnlocalizedName("farmersdelight."+(red ? "red" : "brown")+"_mushroom_colony");

    }
    private Item mushroom() { return NativeRegistry.item(red ? Block.mushroomRed : Block.mushroomBrown); }
    public boolean canBlockStay(World world,int x,int y,int z) {
        Block floor=NativeRegistry.at(world,x,y-1,z);
        return floor==Content.richSoil || floor==Content.compost || floor==Block.mycelium || floor==Block.dirt && world.getBlockMetadata(x,y-1,z)==2
                || world.getBlockLightValue(x,y,z)<13 && world.isBlockSolidOnSide(x,y-1,z,net.minecraftforge.common.ForgeDirection.UP);
    }
    public void updateTick(World world,int x,int y,int z,Random random) {
        super.updateTick(world,x,y,z,random);
        if(!world.isRemote && NativeRegistry.at(world,x,y,z)==this && NativeRegistry.at(world,x,y-1,z)==Content.richSoil
                && world.getBlockLightValue(x,y+1,z)<=12 && world.getBlockMetadata(x,y,z)<3 && random.nextInt(5)==0)
            world.setBlockMetadataWithNotify(x,y,z,world.getBlockMetadata(x,y,z)+1,2);
    }
    public ArrayList<ItemStack> getBlockDropped(World world,int x,int y,int z,int meta,int fortune) {
        ArrayList<ItemStack> result=new ArrayList<ItemStack>(); result.add(new ItemStack(mushroom(),2+(meta&3))); return result;
    }
    public boolean onBlockActivated(World world,int x,int y,int z,EntityPlayer player,int side,float hx,float hy,float hz) {
        int age=world.getBlockMetadata(x,y,z); ItemStack held=player.getHeldItem();
        if(age<=0 || !CuttingRecipes.tool(held,"shears")) return false;
        if(!world.isRemote) { world.setBlockMetadataWithNotify(x,y,z,age-1,2); dropBlockAsItem_do(world,x,y,z,new ItemStack(mushroom())); if(!player.capabilities.isCreativeMode) held.damageItem(1,player); }
        return true;
    }
    public boolean isShearable(ItemStack item,World world,int x,int y,int z) { return (world.getBlockMetadata(x,y,z)&3)==3; }
    public ArrayList<ItemStack> onSheared(ItemStack item,World world,int x,int y,int z,int fortune) { ArrayList<ItemStack> result=new ArrayList<ItemStack>(); result.add(new ItemStack(this)); return result; }
    public boolean func_149851_a(World world,int x,int y,int z,boolean client) { return false; }
    public boolean func_149852_a(World world,Random random,int x,int y,int z) { return false; }
    public void func_149853_b(World world,Random random,int x,int y,int z) { world.setBlockMetadataWithNotify(x,y,z,Math.min(3,world.getBlockMetadata(x,y,z)+1),2); }
    @SideOnly(Side.CLIENT) public void registerIcons(net.minecraft.client.renderer.texture.IconRegister register) {
        stages=new net.minecraft.util.Icon[4]; for(int i=0;i<4;i++) stages[i]=register.registerIcon("farmersdelight:"+(red ? "red" : "brown")+"_mushroom_colony_stage"+i);
    }
    @SideOnly(Side.CLIENT) public net.minecraft.util.Icon getIcon(int side,int meta) { return stages[meta&3]; }
}
