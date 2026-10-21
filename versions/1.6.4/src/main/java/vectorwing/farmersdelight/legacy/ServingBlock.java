package vectorwing.farmersdelight.legacy;
import net.minecraft.item.Item;

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


/** The remaining portions belong to the placed meal and never regenerate on pickup. */
public final class ServingBlock extends BlockContainer {
    public final String id;
    public final boolean pie, leftovers;
    private final String serving;

    public ServingBlock(String id,boolean pie,String serving) {
        super(LegacyIds.block(id),Material.cloth); this.id=id; this.pie=pie; this.serving=serving;
        leftovers=!pie && !id.equals("stuffed_pumpkin_block");
        setUnlocalizedName("farmersdelight."+id); setCreativeTab(Content.TAB); setHardness(0.5F);
        setTextureName("farmersdelight:"+(pie ? id+"_top" : id.equals("stuffed_pumpkin_block") ? "stuffed_pumpkin_bottom" : id.equals("shepherds_pie_block") ? "shepherds_pie_top" : id.replace("_block","")+"_body"));
    }
    public TileEntity createNewTileEntity(World world) { return new ServingTile(); }
    public static int portions(TileEntity tile) { return tile instanceof ServingTile ? ((ServingTile)tile).remaining : 0; }
    public boolean isOpaqueCube() { return false; }
    public boolean renderAsNormalBlock() { return false; }
    public int getRenderType() { return Foundation.proxy.modelRenderId(); }
    public void onBlockPlacedBy(World world,int x,int y,int z,EntityLivingBase placer,ItemStack stack) {
        int facing=net.minecraft.util.MathHelper.floor_double(placer.rotationYaw*4/360+0.5)&3;
        world.setBlockMetadataWithNotify(x,y,z,pie ? facing : (facing+2)&3,2);
    }
    public void setBlockBoundsBasedOnState(IBlockAccess world,int x,int y,int z) {
        setBlockBounds(0.0625F,0,0.0625F,0.9375F,pie ? 0.25F : portions(world.getBlockTileEntity(x,y,z))==0 ? 0.125F : 0.625F,0.9375F);
    }
    public boolean canPlaceBlockAt(World world,int x,int y,int z) { return super.canPlaceBlockAt(world,x,y,z) && world.getBlockMaterial(x,y-1,z).isSolid(); }
    public void onNeighborBlockChange(World world,int x,int y,int z,int changed) {
        if(!world.isRemote && !world.getBlockMaterial(x,y-1,z).isSolid()) { dropBlockAsItem(world,x,y,z,world.getBlockMetadata(x,y,z),0); world.setBlockToAir(x,y,z); }
    }
    public boolean onBlockActivated(World world,int x,int y,int z,EntityPlayer player,int side,float hx,float hy,float hz) {
        TileEntity raw=world.getBlockTileEntity(x,y,z);
        if(!(raw instanceof ServingTile)) return false;
        ServingTile tile=(ServingTile)raw;
        if(tile.remaining==0) {
            if(!world.isRemote) { dropBlockAsItem(world,x,y,z,world.getBlockMetadata(x,y,z),0); world.setBlockToAir(x,y,z); }
            return true;
        }
        ItemStack held=player.getHeldItem();
        ItemStack portion=new ItemStack(Content.ITEMS.get(serving));
        boolean knife=Ingredients.isKnife(held);
        if(pie && !knife && !player.canEat(false)) return false;
        if(!pie && (Stacks.empty(held) || held.getItem()!=Item.bowlEmpty)) return false;
        if(world.isRemote) return true;
        tile.take();
        if(pie && !knife) ((LegacyFood)portion.getItem()).eatPlaced(player,world);
        else {
            if(!pie && !player.capabilities.isCreativeMode) Stacks.shrink(held,1);
            if(pie) dropBlockAsItem_do(world,x,y,z,portion);
            else if(!player.inventory.addItemStackToInventory(portion)) player.dropPlayerItemWithRandomChoice(portion,false);
        }
        if(tile.remaining==0 && !leftovers) world.setBlockToAir(x,y,z);
        return true;
    }
    private void drops(java.util.List<ItemStack> drops,int portions) {
        if(portions==4) { drops.add(new ItemStack(this)); return; }
        if(!leftovers) return;
        drops.add(new ItemStack(Item.bowlEmpty));
        if(id.equals("honey_glazed_ham_block")) drops.add(new ItemStack(Item.bone));
        if(id.equals("roast_chicken_block")) drops.add(Stacks.resolve("minecraft:bone_meal"));
    }
    public ArrayList<ItemStack> getBlockDropped(World world,int x,int y,int z,int meta,int fortune) { ArrayList<ItemStack> list=new ArrayList<ItemStack>(); drops(list,portions(world.getBlockTileEntity(x,y,z))); return list; }
    public boolean removeBlockByPlayer(World world,EntityPlayer player,int x,int y,int z) {
        if(!world.isRemote && !player.capabilities.isCreativeMode && canHarvestBlock(player,world.getBlockMetadata(x,y,z))) {
            harvestBlock(world,player,x,y,z,world.getBlockMetadata(x,y,z)); return world.isAirBlock(x,y,z);
        }
        return super.removeBlockByPlayer(world,player,x,y,z);
    }
    public void harvestBlock(World world,EntityPlayer player,int x,int y,int z,int meta) {
        if(world.getBlockTileEntity(x,y,z)==null) return; super.harvestBlock(world,player,x,y,z,meta); world.setBlockToAir(x,y,z); }
    public boolean hasComparatorInputOverride() { return true; }
    public int getComparatorInputOverride(World world,int x,int y,int z,int side) { return portions(world.getBlockTileEntity(x,y,z)); }
}

