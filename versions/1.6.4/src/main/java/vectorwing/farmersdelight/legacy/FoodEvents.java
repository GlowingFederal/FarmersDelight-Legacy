package vectorwing.farmersdelight.legacy;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;


import net.minecraftforge.event.entity.player.PlayerInteractEvent; import net.minecraftforge.event.entity.player.EntityInteractEvent; import net.minecraftforge.event.entity.living.LivingHurtEvent; import net.minecraft.util.MathHelper; import net.minecraft.util.DamageSource;
public final class FoodEvents {
    @ForgeSubscribe public void horseFeed(EntityInteractEvent event) {
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
    public static void finished(EntityPlayer player,ItemStack food) {
        if(player.worldObj.isRemote || food==null) return;
        Progression.consumed(player,food);
        if(Settings.comfortSoups && food.getItem()==Item.bowlSoup) player.addPotionEffect(new PotionEffect(Content.comfort.id,3600,0));
    }
    @ForgeSubscribe public void cake(PlayerInteractEvent event) {
        if (event.action != PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) return; EntityPlayer player = event.entityPlayer; net.minecraft.world.World world = event.entityPlayer.worldObj; ItemStack held = player.getCurrentEquippedItem();
        if (!Ingredients.isKnife(held) || NativeRegistry.at(world,event.x, event.y, event.z) != Block.cake) return;
        event.setCanceled(true);

        if (world.isRemote) return;
        int bites = world.getBlockMetadata(event.x, event.y, event.z);
        if (bites < 6) world.setBlockMetadataWithNotify(event.x, event.y, event.z, bites + 1, 3); else world.setBlockToAir(event.x, event.y, event.z);
        world.spawnEntityInWorld(new net.minecraft.entity.item.EntityItem(world, event.x + 0.5, event.y + 0.5, event.z + 0.5, new ItemStack(Content.ITEMS.get("cake_slice"))));
    }
    @ForgeSubscribe public void dogFood(EntityInteractEvent event) {
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
            if (held.stackSize <= 0) { player.inventory.setInventorySlotContents(player.inventory.currentItem, new ItemStack(Item.bowlEmpty)); return; }
            ItemStack bowl = new ItemStack(Item.bowlEmpty);
            if (!player.inventory.addItemStackToInventory(bowl)) player.dropPlayerItemWithRandomChoice(bowl, false);
        }
    }
    // Pre-1.8 hay lacks fall cushioning. Apply the source's damage multiplier at its native damage hook.
    @ForgeSubscribe public void strawFall(LivingHurtEvent event) {
        if (event.source != DamageSource.fall || event.entityLiving.worldObj.isRemote) return;
        net.minecraft.entity.EntityLivingBase entity = event.entityLiving;
        net.minecraft.block.Block ground=NativeRegistry.at(entity.worldObj,MathHelper.floor_double(entity.posX), MathHelper.floor_double(entity.posY-entity.yOffset-0.2), MathHelper.floor_double(entity.posZ));
        if (ground == Content.strawBale || ground == Content.PACKED.get("rice_bale")) event.ammount *= 0.2F;
    }
}
