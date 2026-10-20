package vectorwing.farmersdelight.legacy;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;

public final class WorkstationEvents {
    @SubscribeEvent public void carve(PlayerInteractEvent.RightClickBlock event) {

        EntityPlayer player=event.getEntityPlayer();
        ItemStack held=player.getHeldItem(event.getHand());
        if (!player.isSneaking() || Stacks.empty(held) || !held.getItem().isDamageable()) return;
        net.minecraft.world.World world=event.getWorld();
        net.minecraft.tileentity.TileEntity tile=world.getTileEntity(event.getPos());
        if (!(tile instanceof CuttingBoardTile) || !Stacks.empty(((CuttingBoardTile)tile).getStackInSlot(0))) return;
        event.setCanceled(true);
        event.setCancellationResult(net.minecraft.util.EnumActionResult.SUCCESS);
        Content.cuttingBoard.onBlockActivated(world,event.getPos(),world.getBlockState(event.getPos()),player,event.getHand(),event.getFace(),0.5F,0.5F,0.5F);
    }
    @SubscribeEvent public void harvest(HarvestDropsEvent event) {
        EntityPlayer player=event.getHarvester();
        if(player==null || player.world.isRemote || !Ingredients.isKnife(player.getHeldItemMainhand())) return;
        net.minecraft.block.Block block=event.getState().getBlock();
        int meta=block.getMetaFromState(event.getState());
        boolean mature=block==Blocks.WHEAT && meta==7;
        boolean grass=block==Blocks.TALLGRASS && meta==1
                || block==Blocks.DOUBLE_PLANT && meta==2;
        if(mature || grass && player.world.rand.nextFloat()<0.2F)
            event.getDrops().add(new ItemStack(Content.ITEMS.get("straw")));
        if(block==Content.riceUpper && meta>=3) {
            event.getDrops().clear();
            event.getDrops().add(new ItemStack(Content.ITEMS.get("rice")));
            event.getDrops().add(new ItemStack(Content.ITEMS.get("straw")));
        }
        if(block instanceof ServingBlock && ((ServingBlock)block).pie) {
            event.getDrops().clear();
            int portions=ServingBlock.portions(event.getWorld().getTileEntity(event.getPos()));
            if(portions>0) event.getDrops().add(new ItemStack(Content.ITEMS.get(((ServingBlock)block).id+"_slice"),portions));
        }
        if(block==Blocks.CAKE) event.getDrops().add(new ItemStack(Content.ITEMS.get("cake_slice"),Math.max(0,7-meta)));
    }
    @SubscribeEvent public void scavenge(LivingDropsEvent event) {
        Entity attacker=event.getSource().getTrueSource();
        EntityLivingBase target=event.getEntityLiving();
        if(target.world.isRemote || !(attacker instanceof EntityLivingBase)) return;
        ItemStack held=((EntityLivingBase)attacker).getHeldItemMainhand();
        if(!Ingredients.isKnife(held)) return;
        ItemStack extra=Stacks.none();
        if(target instanceof net.minecraft.entity.passive.EntityPig && target.world.rand.nextFloat()<0.5F+0.1F*event.getLootingLevel())
            extra=new ItemStack(Content.ITEMS.get(target.isBurning() ? "smoked_ham" : "ham"));
        else if(target instanceof net.minecraft.entity.passive.EntityChicken) extra=new ItemStack(Items.FEATHER);
        else if(target instanceof net.minecraft.entity.monster.EntitySpider) extra=new ItemStack(Items.STRING);
        else if(target instanceof net.minecraft.entity.passive.EntityCow
                || target instanceof net.minecraft.entity.passive.EntityHorse || target instanceof net.minecraft.entity.passive.EntityDonkey || target instanceof net.minecraft.entity.passive.EntityMule || target instanceof net.minecraft.entity.passive.EntityLlama)
            extra=new ItemStack(Items.LEATHER);
        else if(target instanceof net.minecraft.entity.passive.EntityRabbit) extra=new ItemStack(Items.RABBIT_HIDE);
        else if(target instanceof net.minecraft.entity.monster.EntityShulker) extra=new ItemStack(Items.SHULKER_SHELL);
        if(!Stacks.empty(extra)) event.getDrops().add(new EntityItem(target.world,target.posX,target.posY,target.posZ,extra));
    }
}
