package vectorwing.farmersdelight.legacy;
import net.minecraft.item.Item;

import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.Event.Result;
import net.minecraftforge.event.entity.player.UseHoeEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;


public final class FarmingEvents {
    @ForgeSubscribe public void hoe(UseHoeEvent event) {
        World world=event.world;
        int x=event.x,y=event.y,z=event.z;
        if(NativeRegistry.at(world,x,y,z)==Content.richSoil && world.isAirBlock(x,y+1,z)) {
            if(!world.isRemote) NativeRegistry.set(world,x,y,z,Content.richFarmland,0,3);
            event.setResult(Result.ALLOW);
        }
    }
    @ForgeSubscribe public void bucket(FillBucketEvent event) {
        World world=event.world;
        ItemStack bucket=event.current;
        if(Stacks.empty(bucket) || bucket.getItem()!=Item.bucketEmpty
                || event.target==null || event.target.typeOfHit!=net.minecraft.util.EnumMovingObjectType.TILE) return;
        int x=event.target.blockX,y=event.target.blockY,z=event.target.blockZ;
        Block block=NativeRegistry.at(world,x,y,z);
        if(!(block instanceof RicePlant)) return;
        net.minecraft.entity.player.EntityPlayer player=event.entityPlayer;
        if(!world.canMineBlock(player,x,y,z) || !player.canPlayerEdit(x,y,z,event.target.sideHit,bucket)) return;
        if(!world.isRemote) {
            net.minecraft.tileentity.TileEntity tile=world.getBlockTileEntity(x,y,z);
            if(tile instanceof RiceTile) ((RiceTile)tile).draining=true;
            block.dropBlockAsItem(world,x,y,z,0,0); world.setBlockToAir(x,y,z);
        }
        event.result=new ItemStack(Item.bucketWater);
        event.setResult(Result.ALLOW);
    }
    @net.minecraftforge.event.ForgeSubscribe public void bonemeal(net.minecraftforge.event.entity.player.BonemealEvent event) {
        Block block=NativeRegistry.at(event.world,event.X,event.Y,event.Z);
        if(!(block instanceof RicePlant) && !(block instanceof RiceUpper)) return;
        if(event.world.isRemote || SoilBlock.grow(event.world,event.X,event.Y,event.Z)) event.setResult(Result.ALLOW);
        else event.setResult(Result.DENY);
    }
}

