package vectorwing.farmersdelight.legacy;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.init.Items;
import net.minecraft.init.Blocks;
import net.minecraftforge.event.entity.player.PlayerInteractEvent; import net.minecraftforge.event.entity.player.EntityInteractEvent; import net.minecraftforge.event.entity.player.PlayerUseItemEvent; import net.minecraftforge.event.entity.living.LivingHurtEvent; import net.minecraft.util.MathHelper; import net.minecraft.util.DamageSource;
public final class FoodEvents {
    @SubscribeEvent public void horseFeed(EntityInteractEvent event) {
        EntityPlayer player=event.entityPlayer;
        ItemStack held=player.getHeldItem();
        net.minecraft.entity.Entity raw=event.target;
        if(!(raw instanceof net.minecraft.entity.passive.EntityHorse) || Stacks.empty(held) || held.getItem()!=Content.ITEMS.get("horse_feed")) return;
        net.minecraft.entity.passive.EntityHorse horse=(net.minecraft.entity.passive.EntityHorse)raw;
        if(!horse.isEntityAlive() || !horse.isTame()) return;
        event.setCanceled(true);

        if(player.worldObj.isRemote) return;
        horse.setHealth(horse.getMaxHealth());
        horse.addPotionEffect(new PotionEffect(net.minecraft.potion.Potion.moveSpeed.id,6000,1));
        horse.addPotionEffect(new PotionEffect(net.minecraft.potion.Potion.jump.id,6000,0));
        if(!player.capabilities.isCreativeMode) Stacks.shrink(held,1);
        horse.worldObj.playSoundAtEntity(horse,"mob.horse.eat",0.8F,0.8F);
    }
    @SubscribeEvent public void foodFinished(PlayerUseItemEvent.Finish event) {
        if (event.entityPlayer.worldObj.isRemote) return; ItemStack food = event.item;
        Progression.consumed(event.entityPlayer,food);
        if(food!=null && food.getItem()==Items.rabbit_stew) {
            if(Settings.comfortSoups) event.entityPlayer.addPotionEffect(new PotionEffect(Content.comfort.id,6000,0));
            if(Settings.rabbitJump) event.entityPlayer.addPotionEffect(new PotionEffect(net.minecraft.potion.Potion.jump.id,200,1));
        }
        if (Settings.comfortSoups && food != null && food.getItem() == Items.mushroom_stew)
            event.entityPlayer.addPotionEffect(new PotionEffect(Content.comfort.id, 3600, 0));


    }
    @SubscribeEvent public void cake(PlayerInteractEvent event) {
        if (event.action != PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) return; EntityPlayer player = event.entityPlayer; net.minecraft.world.World world = event.world; ItemStack held = player.getCurrentEquippedItem();
        if (!Ingredients.isKnife(held) || world.getBlockState(event.pos).getBlock() != Blocks.cake) return;
        event.setCanceled(true);

        if (world.isRemote) return;
        int bites = world.getBlockState(event.pos).getBlock().getMetaFromState(world.getBlockState(event.pos));
        if (bites < 6) world.setBlockState(event.pos,Blocks.cake.getStateFromMeta(bites+1),3); else world.setBlockToAir(event.pos);
        world.spawnEntityInWorld(new net.minecraft.entity.item.EntityItem(world, event.pos.getX() + 0.5, event.pos.getY() + 0.5, event.pos.getZ() + 0.5, new ItemStack(Content.ITEMS.get("cake_slice"))));
    }
    @SubscribeEvent public void dogFood(EntityInteractEvent event) {
        EntityPlayer player = event.entityPlayer;
        ItemStack held = player.getCurrentEquippedItem();
        if (!( event.target instanceof EntityWolf) || held == null || held.getItem() != Content.ITEMS.get("dog_food")) return;
        EntityWolf wolf = (EntityWolf) event.target;
        if (!wolf.isEntityAlive() || !wolf.isTamed()) return;
        event.setCanceled(true);

        if (player.worldObj.isRemote) return;
        wolf.setHealth(wolf.getMaxHealth());
        for (int effect : new int[] {net.minecraft.potion.Potion.moveSpeed.id, net.minecraft.potion.Potion.damageBoost.id, net.minecraft.potion.Potion.resistance.id})
            wolf.addPotionEffect(new PotionEffect(effect, 6000, 0));
        if (!player.capabilities.isCreativeMode) {
            --held.stackSize;
            if (held.stackSize <= 0) { player.inventory.setInventorySlotContents(player.inventory.currentItem, new ItemStack(Items.bowl)); return; }
            ItemStack bowl = new ItemStack(Items.bowl);
            if (!player.inventory.addItemStackToInventory(bowl)) player.dropPlayerItemWithRandomChoice(bowl, false);
        }
    }
    // Pre-1.8 hay lacks fall cushioning. Apply the source's damage multiplier at its native damage hook.
    @SubscribeEvent public void strawFall(LivingHurtEvent event) {
        if (event.source != DamageSource.fall || event.entityLiving.worldObj.isRemote) return;
        net.minecraft.entity.EntityLivingBase entity = event.entityLiving;
        net.minecraft.block.Block ground=entity.worldObj.getBlockState(new net.minecraft.util.BlockPos(entity.posX,entity.posY-0.2,entity.posZ)).getBlock();
        if (ground == Content.strawBale || ground == Content.PACKED.get("rice_bale")) event.ammount *= 0.2F;
    }
}
