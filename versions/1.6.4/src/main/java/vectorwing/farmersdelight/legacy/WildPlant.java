package vectorwing.farmersdelight.legacy;

import java.util.ArrayList;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower;


import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.common.ForgeDirection;

public final class WildPlant extends BlockFlower implements IShearable {
    public final String kind;
    public WildPlant(String kind) {
        super(LegacyIds.block("wild_"+kind)); this.kind=kind; setUnlocalizedName("farmersdelight.wild_"+kind);
        setCreativeTab(Content.TAB); setTextureName("farmersdelight:wild_"+kind);
    }
    private String seed() {
        return kind.equals("cabbages") ? "farmersdelight:cabbage_seeds" : kind.equals("tomatoes") ? "farmersdelight:tomato_seeds"
                : kind.equals("onions") ? "farmersdelight:onion" : kind.equals("beetroots") ? "minecraft:beetroot_seeds"
                : kind.equals("carrots") ? "minecraft:carrot" : "minecraft:potato";
    }
    private String produce() { return kind.equals("cabbages") ? "farmersdelight:cabbage" : kind.equals("tomatoes") ? "farmersdelight:tomato" : "minecraft:beetroot"; }
    protected boolean canThisPlantGrowOnThisBlockID(int id) { Block ground=Block.blocksList[id];return ground==Block.grass || ground==Block.dirt || ground==Block.sand || SoilBlock.rich(ground); }
    public boolean isBlockReplaceable(World world,int x,int y,int z) { return false; }
    public ArrayList<ItemStack> getBlockDropped(World world,int x,int y,int z,int meta,int fortune) {
        ArrayList<ItemStack> result=new ArrayList<ItemStack>(); addDrops(result,fortune,world.rand); return result;
    }
    public boolean isShearable(ItemStack item,World world,int x,int y,int z) { return true; }
    public ArrayList<ItemStack> onSheared(ItemStack item,World world,int x,int y,int z,int fortune) {
        ArrayList<ItemStack> result=new ArrayList<ItemStack>(); result.add(new ItemStack(this)); return result;
    }
    public int getFlammability(IBlockAccess world,int x,int y,int z,int metadata,ForgeDirection face) { return 100; }
    public int getFireSpreadSpeed(World world,int x,int y,int z,int metadata,ForgeDirection face) { return 60; }
    public boolean func_149851_a(World world,int x,int y,int z,boolean client) { return true; }
    public boolean func_149852_a(World world,Random random,int x,int y,int z) { return random.nextFloat()<0.8F; }
    public void func_149853_b(World world,Random random,int x,int y,int z) {
        if(!world.checkChunksExist(x-4,y-1,z-4,x+4,y+1,z+4)) return;
        int count=0;
        for(int dx=-4;dx<=4;dx++) for(int dy=-1;dy<=1;dy++) for(int dz=-4;dz<=4;dz++)
            if(NativeRegistry.at(world,x+dx,y+dy,z+dz)==this && ++count>=10) return;
        int px=x,py=y,pz=z;
        for(int step=0;step<5;step++) {
            int nx=px+random.nextInt(3)-1,ny=py+random.nextInt(2)-random.nextInt(2),nz=pz+random.nextInt(3)-1;
            if(ny>0 && ny<255 && world.isAirBlock(nx,ny,nz) && canBlockStay(world,nx,ny,nz)) {
                if(step==4) NativeRegistry.set(world,nx,ny,nz,this,0,3); else { px=nx; py=ny; pz=nz; }
            }
        }
    }
    private void addDrops(java.util.List<ItemStack> drops,int fortune,Random random) {
        ItemStack seed=Stacks.resolve(seed());
        if(!Stacks.empty(seed)) { Stacks.count(seed,1+random.nextInt(Math.max(0,fortune)*2+1)); drops.add(seed); }
        if((kind.equals("cabbages") || kind.equals("tomatoes") || kind.equals("beetroots")) && random.nextFloat()<0.2F) {
            ItemStack produce=Stacks.resolve(produce()); if(!Stacks.empty(produce)) drops.add(produce);
        }
    }
}
