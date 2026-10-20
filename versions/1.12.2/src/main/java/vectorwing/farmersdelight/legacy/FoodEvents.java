package vectorwing.farmersdelight.legacy;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.init.Items;
import net.minecraft.init.Blocks;
import net.minecraftforge.event.entity.player.PlayerInteractEvent; import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent; import net.minecraft.block.BlockCake; import net.minecraft.util.EnumActionResult;
public final class FoodEvents {
    @SubscribeEvent public void horseFeed(PlayerInteractEvent.EntityInteract event) {
        EntityPlayer player=event.getEntityPlayer();
        ItemStack held=event.getItemStack();
        net.minecraft.entity.Entity raw=event.getTarget();
        if(!(raw instanceof net.minecraft.entity.passive.AbstractHorse) || Stacks.empty(held) || held.getItem()!=Content.ITEMS.get("horse_feed")) return;
        net.minecraft.entity.passive.AbstractHorse horse=(net.minecraft.entity.passive.AbstractHorse)raw;
        if(!horse.isEntityAlive() || !horse.isTame()) return;
        event.setCanceled(true);
        event.setCancellationResult(EnumActionResult.SUCCESS);
        if(player.world.isRemote) return;
        horse.setHealth(horse.getMaxHealth());
        horse.addPotionEffect(new PotionEffect(net.minecraft.init.MobEffects.SPEED,6000,1));
        horse.addPotionEffect(new PotionEffect(net.minecraft.init.MobEffects.JUMP_BOOST,6000,0));
        if(!player.capabilities.isCreativeMode) Stacks.shrink(held,1);
        horse.world.playSound(null,horse.posX,horse.posY,horse.posZ,net.minecraft.init.SoundEvents.ENTITY_HORSE_EAT,net.minecraft.util.SoundCategory.PLAYERS,0.8F,0.8F);
    }
    @SubscribeEvent public void foodFinished(LivingEntityUseItemEvent.Finish event) {
        if (event.getEntityLiving().world.isRemote) return; ItemStack food = event.getItem();
        if (Settings.comfortSoups && food != null && food.getItem() == Items.MUSHROOM_STEW)
            event.getEntityLiving().addPotionEffect(new PotionEffect(Content.comfort, 3600, 0));
        if (Settings.comfortSoups && (food.getItem() == Items.BEETROOT_SOUP || food.getItem() == Items.RABBIT_STEW)) event.getEntityLiving().addPotionEffect(new PotionEffect(Content.comfort, food.getItem() == Items.RABBIT_STEW ? 6000 : 3600, 0));
        if (Settings.rabbitJump && food.getItem() == Items.RABBIT_STEW) event.getEntityLiving().addPotionEffect(new PotionEffect(net.minecraft.init.MobEffects.JUMP_BOOST, 200, 1));
    }
    @SubscribeEvent public void cake(PlayerInteractEvent.RightClickBlock event) {
        EntityPlayer player = event.getEntityPlayer(); net.minecraft.util.math.BlockPos pos = event.getPos(); net.minecraft.world.World world = player.world; ItemStack held = player.getHeldItem(event.getHand());
        if (!Ingredients.isKnife(held) || world.getBlockState(pos).getBlock() != Blocks.CAKE) return;
        event.setCanceled(true);
        event.setCancellationResult(EnumActionResult.SUCCESS);
        if (world.isRemote) return;
        int bites = world.getBlockState(pos).getValue(BlockCake.BITES);
        if (bites < 6) world.setBlockState(pos, world.getBlockState(pos).withProperty(BlockCake.BITES, bites + 1), 3); else world.setBlockToAir(pos);
        world.spawnEntity(new net.minecraft.entity.item.EntityItem(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, new ItemStack(Content.ITEMS.get("cake_slice"))));
    }
    @SubscribeEvent public void dogFood(PlayerInteractEvent.EntityInteract event) {
        EntityPlayer player = event.getEntityPlayer();
        ItemStack held = event.getItemStack();
        if (!( event.getTarget() instanceof EntityWolf) || held == null || held.getItem() != Content.ITEMS.get("dog_food")) return;
        EntityWolf wolf = (EntityWolf) event.getTarget();
        if (!wolf.isEntityAlive() || !wolf.isTamed()) return;
        event.setCanceled(true);
        event.setCancellationResult(EnumActionResult.SUCCESS);
        if (player.world.isRemote) return;
        wolf.setHealth(wolf.getMaxHealth());
        for (net.minecraft.potion.Potion effect : new net.minecraft.potion.Potion[] {net.minecraft.init.MobEffects.SPEED, net.minecraft.init.MobEffects.STRENGTH, net.minecraft.init.MobEffects.RESISTANCE})
            wolf.addPotionEffect(new PotionEffect(effect, 6000, 0));
        if (!player.capabilities.isCreativeMode) {
            held.shrink(1);
            if (held.isEmpty()) { player.setHeldItem(event.getHand(), new ItemStack(Items.BOWL)); return; }
            ItemStack bowl = new ItemStack(Items.BOWL);
            if (!player.inventory.addItemStackToInventory(bowl)) player.dropItem(bowl, false);
        }
    }

}
