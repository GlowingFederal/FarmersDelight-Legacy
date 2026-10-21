package vectorwing.farmersdelight.legacy;

import java.util.ArrayList;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.IBlockAccess;
import net.minecraft.util.Icon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class NativeCrop extends BlockCrops {
    protected final String kind;
    private static final int[] TOMATO_HEIGHT={6,6,10,10,13,13,16,16}, CABBAGE_HEIGHT={2,3,5,7,8,9,9,10};
    public int getRenderType() { return 1; }
    public void setBlockBoundsBasedOnState(IBlockAccess world,int x,int y,int z) {
        int age=Math.max(0,Math.min(7,world.getBlockMetadata(x,y,z)));
        setBlockBounds(0,0,0,1,(kind.equals("tomatoes") ? TOMATO_HEIGHT[age] : kind.equals("cabbages") ? CABBAGE_HEIGHT[age] : age+2)/16F,1);
    }
    @SideOnly(Side.CLIENT) private Icon[] stages;
    public NativeCrop(String kind) { super(LegacyIds.block(kind)); this.kind=kind; setUnlocalizedName("farmersdelight."+kind); }
    protected Item seed() { return Content.ITEMS.get(kind.equals("cabbages") ? "cabbage_seeds" : kind.equals("tomatoes") ? "tomato_seeds" : "onion"); }
    protected Item produce() { return Content.ITEMS.get(kind.equals("cabbages") ? "cabbage" : kind.equals("tomatoes") ? "tomato" : "onion"); }
    protected int getSeedItem() { return seed().itemID; }
    protected int getCropItem() { return produce().itemID; }
    protected boolean canThisPlantGrowOnThisBlockID(int nativeId) { return nativeId==Block.tilledField.blockID || nativeId==Content.richFarmland.blockID; }
    protected static int bonus(Random random,int fortune) {
        int count=1;
        for(int i=0;i<3+Math.max(0,fortune);i++) if(random.nextFloat()<4F/7F) count++;
        return count;
    }
    protected void drops(java.util.List<ItemStack> drops,int age,int fortune,Random random) {
        if(age<7) { drops.add(new ItemStack(seed())); return; }
        if(kind.equals("tomatoes")) {
            drops.add(new ItemStack(produce(),1+random.nextInt(2)+random.nextInt(Math.max(0,fortune)+1)));
            drops.add(new ItemStack(seed()));
            if(random.nextFloat()<0.05F) drops.add(new ItemStack(Content.ITEMS.get("rotten_tomato")));
        } else {
            drops.add(new ItemStack(produce()));
            drops.add(new ItemStack(seed(),bonus(random,fortune)));
        }
    }
    @Override public ArrayList<ItemStack> getBlockDropped(World world,int x,int y,int z,int age,int fortune) {
        ArrayList<ItemStack> result=new ArrayList<ItemStack>(); drops(result,age,fortune,world.rand); return result;
    }
    @Override public boolean onBlockActivated(World world,int x,int y,int z,EntityPlayer player,int side,float hx,float hy,float hz) {
        if(!kind.equals("tomatoes") || world.getBlockMetadata(x,y,z)<7) return false;
        if(!world.isRemote) {
            world.setBlockMetadataWithNotify(x,y,z,5,2);
            dropBlockAsItem_do(world,x,y,z,new ItemStack(produce(),1+world.rand.nextInt(2)));
            if(world.rand.nextFloat()<0.05F) dropBlockAsItem_do(world,x,y,z,new ItemStack(Content.ITEMS.get("rotten_tomato")));
            world.playSoundEffect(x+0.5,y+0.5,z+0.5,"dig.grass",0.8F,1F);
        }
        return true;
    }
    @SideOnly(Side.CLIENT) public void registerIcons(net.minecraft.client.renderer.texture.IconRegister register) {
        int count=kind.equals("cabbages") ? 8 : kind.equals("tomatoes") ? 5 : 4;
        stages=new Icon[count];
        for(int i=0;i<count;i++) stages[i]=register.registerIcon("farmersdelight:"+kind+"_stage"+i);
    }
    @SideOnly(Side.CLIENT) public Icon getIcon(int side,int meta) {
        int age=Math.max(0,Math.min(7,meta));
        int stage=kind.equals("cabbages") ? age : kind.equals("tomatoes") ? age<6 ? age/2 : age-3 : age/2;
        return stages[stage];
    }
}
