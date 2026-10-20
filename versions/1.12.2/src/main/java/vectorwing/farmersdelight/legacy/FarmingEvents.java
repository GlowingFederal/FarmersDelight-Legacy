package vectorwing.farmersdelight.legacy;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.event.entity.player.UseHoeEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;

public final class FarmingEvents {
    @SubscribeEvent public void fuel(net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent event) {
        ItemStack stack=event.getItemStack();
        int ticks=vectorwing.farmersdelight.legacy.api.FuelRules.ticks(Stacks.empty(stack) ? null : stack.getItem().getRegistryName().toString());
        if(ticks>=0) event.setBurnTime(ticks);
    }
    @SubscribeEvent public void hoe(UseHoeEvent event) {
        World world=event.getWorld();
        BlockPos pos=event.getPos();
        if(world.getBlockState(pos).getBlock()==Content.richSoil && world.isAirBlock(pos.up())) {
            if(!world.isRemote) world.setBlockState(pos,Content.richFarmland.getDefaultState(),3);
            event.setResult(Result.ALLOW);
        }
    }
    @SubscribeEvent public void bucket(FillBucketEvent event) {
        World world=event.getWorld();
        ItemStack bucket=event.getEmptyBucket();
        if(Stacks.empty(bucket) || bucket.getItem()!=net.minecraft.init.Items.BUCKET
                || event.getTarget()==null || event.getTarget().typeOfHit!=net.minecraft.util.math.RayTraceResult.Type.BLOCK) return;
        BlockPos pos=event.getTarget().getBlockPos();
        Block block=world.getBlockState(pos).getBlock();
        if(!(block instanceof RicePlant)) return;
        net.minecraft.entity.player.EntityPlayer player=event.getEntityPlayer();
        if(!world.isBlockModifiable(player,pos) || !player.canPlayerEdit(pos,event.getTarget().sideHit,bucket)) return;
        if(!world.isRemote) {
            net.minecraft.tileentity.TileEntity tile=world.getTileEntity(pos);
            if(tile instanceof RiceTile) ((RiceTile)tile).draining=true;
            block.dropBlockAsItem(world,pos,world.getBlockState(pos),0); world.setBlockToAir(pos);
        }
        event.setFilledBucket(new ItemStack(net.minecraft.init.Items.WATER_BUCKET));
        event.setResult(Result.ALLOW);
    }
}

