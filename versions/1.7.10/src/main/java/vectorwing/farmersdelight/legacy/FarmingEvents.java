package vectorwing.farmersdelight.legacy;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.eventhandler.Event.Result;
import net.minecraftforge.event.entity.player.UseHoeEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;


public final class FarmingEvents {
    @SubscribeEvent public void hoe(UseHoeEvent event) {
        World world=event.world;
        int x=event.x,y=event.y,z=event.z;
        if(world.getBlock(x,y,z)==Content.richSoil && world.isAirBlock(x,y+1,z)) {
            if(!world.isRemote) world.setBlock(x,y,z,Content.richFarmland,0,3);
            event.setResult(Result.ALLOW);
        }
    }
    @SubscribeEvent public void bucket(FillBucketEvent event) {
        World world=event.world;
        ItemStack bucket=event.current;
        if(Stacks.empty(bucket) || bucket.getItem()!=net.minecraft.init.Items.bucket
                || event.target==null || event.target.typeOfHit!=net.minecraft.util.MovingObjectPosition.MovingObjectType.BLOCK) return;
        int x=event.target.blockX,y=event.target.blockY,z=event.target.blockZ;
        Block block=world.getBlock(x,y,z);
        if(!(block instanceof RicePlant)) return;
        net.minecraft.entity.player.EntityPlayer player=event.entityPlayer;
        if(!world.canMineBlock(player,x,y,z) || !player.canPlayerEdit(x,y,z,event.target.sideHit,bucket)) return;
        if(!world.isRemote) {
            net.minecraft.tileentity.TileEntity tile=world.getTileEntity(x,y,z);
            if(tile instanceof RiceTile) ((RiceTile)tile).draining=true;
            block.dropBlockAsItem(world,x,y,z,0,0); world.setBlockToAir(x,y,z);
        }
        event.result=new ItemStack(net.minecraft.init.Items.water_bucket);
        event.setResult(Result.ALLOW);
    }
}

