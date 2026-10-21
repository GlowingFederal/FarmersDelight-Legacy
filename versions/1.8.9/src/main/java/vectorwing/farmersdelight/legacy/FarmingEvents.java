package vectorwing.farmersdelight.legacy;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.event.entity.player.UseHoeEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.util.BlockPos;

public final class FarmingEvents {
    @SubscribeEvent public void hoe(UseHoeEvent event) {
        World world=event.world;
        BlockPos pos=event.pos;
        if(world.getBlockState(pos).getBlock()==Content.richSoil && world.isAirBlock(pos.up())) {
            if(!world.isRemote) world.setBlockState(pos,Content.richFarmland.getDefaultState(),3);
            event.setResult(Result.ALLOW);
        }
    }
    @SubscribeEvent public void bucket(FillBucketEvent event) {
        World world=event.world;
        ItemStack bucket=event.current;
        if(Stacks.empty(bucket) || bucket.getItem()!=net.minecraft.init.Items.bucket
                || event.target==null || event.target.typeOfHit!=net.minecraft.util.MovingObjectPosition.MovingObjectType.BLOCK) return;
        BlockPos pos=event.target.getBlockPos();
        Block block=world.getBlockState(pos).getBlock();
        if(!(block instanceof RicePlant)) return;
        net.minecraft.entity.player.EntityPlayer player=event.entityPlayer;
        if(!world.isBlockModifiable(player,pos) || !player.canPlayerEdit(pos,event.target.sideHit,bucket)) return;
        if(!world.isRemote) {
            net.minecraft.tileentity.TileEntity tile=world.getTileEntity(pos);
            if(tile instanceof RiceTile) ((RiceTile)tile).draining=true;
            block.dropBlockAsItem(world,pos,world.getBlockState(pos),0); world.setBlockToAir(pos);
        }
        event.result=new ItemStack(net.minecraft.init.Items.water_bucket);
        event.setResult(Result.ALLOW);
    }
}

