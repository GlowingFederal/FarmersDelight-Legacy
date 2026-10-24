package vectorwing.farmersdelight.legacy;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
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
    @SubscribeEvent public void carve(PlayerInteractEvent event) {
        if (event.action != PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) return;
        EntityPlayer player=event.entityPlayer;
        ItemStack held=player.getHeldItem();
        if (!player.isSneaking() || Stacks.empty(held) || !held.getItem().isDamageable()) return;
        net.minecraft.world.World world=event.world;
        net.minecraft.tileentity.TileEntity tile=world.getTileEntity(event.x,event.y,event.z);
        if (!(tile instanceof CuttingBoardTile) || !Stacks.empty(((CuttingBoardTile)tile).getStackInSlot(0))) return;
        event.setCanceled(true);
        Content.cuttingBoard.onBlockActivated(world,event.x,event.y,event.z,player,event.face,0.5F,0.5F,0.5F);
    }
    @SubscribeEvent public void harvest(HarvestDropsEvent event) {
        EntityPlayer player=event.harvester;
        if(player==null || player.worldObj.isRemote || !Ingredients.isKnife(player.getHeldItem())) return;
        net.minecraft.block.Block block=event.block;
        int meta=event.blockMetadata;
        boolean mature=block==Blocks.wheat && meta==7;
        boolean grass=block==Blocks.tallgrass && meta==1
                || block==Blocks.double_plant && meta==2;
        if(mature || grass && player.worldObj.rand.nextFloat()<0.2F)
            event.drops.add(new ItemStack(Content.ITEMS.get("straw")));
        if(block==Content.riceUpper && meta>=3) {
            event.drops.clear();
            event.drops.add(new ItemStack(Content.ITEMS.get("rice")));
            event.drops.add(new ItemStack(Content.ITEMS.get("straw")));
        }
        if(block instanceof ServingBlock && ((ServingBlock)block).pie) {
            event.drops.clear();
            int portions=ServingBlock.portions(event.world.getTileEntity(event.x,event.y,event.z));
            if(portions>0) event.drops.add(new ItemStack(Content.ITEMS.get(((ServingBlock)block).id+"_slice"),portions));
        }
        if(block==Blocks.cake) event.drops.add(new ItemStack(Content.ITEMS.get("cake_slice"),Math.max(0,7-meta)));
    }
    @SubscribeEvent public void scavenge(LivingDropsEvent event) {
        Entity attacker=event.source.getEntity();
        EntityLivingBase target=event.entityLiving;
        if(target.worldObj.isRemote || !(attacker instanceof EntityLivingBase)) return;
        ItemStack held=((EntityLivingBase)attacker).getHeldItem();
        if(!Ingredients.isKnife(held)) return;
        ItemStack extra=Stacks.none();
        if(target instanceof net.minecraft.entity.passive.EntityPig && target.worldObj.rand.nextFloat()<0.5F+0.1F*event.lootingLevel)
            extra=new ItemStack(Content.ITEMS.get(target.isBurning() ? "smoked_ham" : "ham"));
        else if(target instanceof net.minecraft.entity.passive.EntityChicken) extra=new ItemStack(Items.feather);
        else if(target instanceof net.minecraft.entity.monster.EntitySpider) extra=new ItemStack(Items.string);
        else if(target instanceof net.minecraft.entity.passive.EntityCow
                || target instanceof net.minecraft.entity.passive.EntityHorse && ((net.minecraft.entity.passive.EntityHorse)target).getHorseType()<3)
            extra=new ItemStack(Items.leather);

        else extra=UABCompatibility.scavenge(target);
        if(!Stacks.empty(extra)) event.drops.add(new EntityItem(target.worldObj,target.posX,target.posY,target.posZ,extra));
    }
}
