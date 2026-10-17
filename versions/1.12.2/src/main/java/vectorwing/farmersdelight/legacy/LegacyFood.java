package vectorwing.farmersdelight.legacy;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.EnumAction;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.init.Items;
import net.minecraft.world.World;
import vectorwing.farmersdelight.legacy.api.FoodDefinition;
import net.minecraft.entity.EntityLivingBase; import net.minecraft.util.ResourceLocation; import net.minecraft.util.SoundCategory; import net.minecraft.init.SoundEvents;

public final class LegacyFood extends ItemFood {
    public final FoodDefinition definition;
    public LegacyFood(FoodDefinition definition) {
        super(definition.nutrition, definition.saturation, definition.meat);
        this.definition = definition;
        setMaxStackSize(definition.stackSize);
        if (definition.alwaysEat) setAlwaysEdible();
        if (definition.container != null) setContainerItem(Content.item(definition.container));
    }
    @Override public int getMaxItemUseDuration(ItemStack stack) { return definition.fast ? 16 : 32; }
    @Override public EnumAction getItemUseAction(ItemStack stack) { return definition.action == null ? EnumAction.EAT : EnumAction.DRINK; }
    @Override public ItemStack onItemUseFinish(ItemStack stack, World world, EntityLivingBase consumer) {
        if (!(consumer instanceof EntityPlayer)) return stack; EntityPlayer player = (EntityPlayer) consumer;
        if (!world.isRemote) {
            if (player instanceof net.minecraft.entity.player.EntityPlayerMP) net.minecraft.advancements.CriteriaTriggers.CONSUME_ITEM.trigger((net.minecraft.entity.player.EntityPlayerMP) player, stack);
            affect(player, world);
            player.getFoodStats().addStats(definition.nutrition, definition.saturation);
            player.addStat(net.minecraft.stats.StatList.getObjectUseStats(this));
        }
        world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);
        if (player.capabilities.isCreativeMode) return stack;
        stack.shrink(1);
        ItemStack remainder = definition.container == null ? ItemStack.EMPTY : new ItemStack(Content.item(definition.container));
        if (stack.isEmpty()) return remainder;
        if (!world.isRemote && !remainder.isEmpty() && !player.inventory.addItemStackToInventory(remainder))
            player.dropItem(remainder, false);
        return stack;
    }
    private void affect(EntityPlayer player, World world) {
        if (definition.id.equals("melon_popsicle")) player.extinguish();
        if ("heal".equals(definition.action)) player.heal(2.0F);
        if ("milk".equals(definition.action) || "cocoa".equals(definition.action)) {
            List<PotionEffect> choices = new ArrayList<PotionEffect>();
            for (Object object : player.getActivePotionEffects()) {
                PotionEffect effect = (PotionEffect) object;
                Potion potion = effect.getPotion();
                if (effect.isCurativeItem(new ItemStack(Items.MILK_BUCKET ))
                        && (!"cocoa".equals(definition.action) || potion.isBadEffect())) choices.add(effect);
            }
            if (!choices.isEmpty()) player.removePotionEffect(choices.get(world.rand.nextInt(choices.size())).getPotion());
        }
        Potion potion = effect(definition.effect);
        if (potion != null && world.rand.nextFloat() < definition.chance)
            player.addPotionEffect(new PotionEffect(potion, definition.duration, definition.amplifier));
    }
    public static Potion effect(String name) {
        if (name == null) return null;
        if (name.equals("comfort")) return Content.comfort;
        if (name.equals("nourishment")) return Content.nourishment;
        return Potion.getPotionFromResourceLocation("minecraft:" + name);
    }
}
