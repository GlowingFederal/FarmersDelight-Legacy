package vectorwing.farmersdelight.legacy;
import net.minecraft.item.Item;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.EnumAction;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import net.minecraft.world.World;
import vectorwing.farmersdelight.legacy.api.FoodDefinition;


public final class LegacyFood extends ItemFood {
    public final FoodDefinition definition;
    public LegacyFood(FoodDefinition definition) {
        super(LegacyIds.item(definition.id),definition.nutrition, definition.saturation, definition.meat);
        this.definition = definition;
        setMaxStackSize(definition.stackSize);
        if (definition.alwaysEat) setAlwaysEdible();
        if (definition.container != null) setContainerItem(Content.item(definition.container));
    }
    @Override public int getMaxItemUseDuration(ItemStack stack) { return definition.fast ? 16 : 32; }
    @Override public EnumAction getItemUseAction(ItemStack stack) { return definition.action == null ? EnumAction.eat : EnumAction.drink; }
    @Override public ItemStack onEaten(ItemStack stack, World world, EntityPlayer player) {

        if (!world.isRemote) {
            affect(player, world);
            player.getFoodStats().addStats(definition.nutrition, definition.saturation);
            player.addStat(net.minecraft.stats.StatList.objectUseStats[NativeRegistry.id(this)], 1);
        }
        world.playSoundAtEntity(player, "random.burp", 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);
        if (player.capabilities.isCreativeMode) return stack;
        --stack.stackSize;
        ItemStack remainder = definition.container == null ? null : new ItemStack(Content.item(definition.container));
        if (stack.stackSize <= 0) return remainder;
        if (!world.isRemote && remainder != null && !player.inventory.addItemStackToInventory(remainder))
            player.dropPlayerItemWithRandomChoice(remainder, false);
        return stack;
    }
    @Override public boolean onItemUse(ItemStack stack,EntityPlayer player,World world,int x,int y,int z,int side,float hx,float hy,float hz) {
        return definition.id.equals("onion") && CropSeeds.plant(Content.onions,stack,player,world,x,y,z,side);
    }
    public void eatPlaced(EntityPlayer player, World world) {
        if(!world.isRemote) { affect(player,world); player.getFoodStats().addStats(definition.nutrition,definition.saturation); Progression.consumed(player,new ItemStack(this)); }
    }
    private void affect(EntityPlayer player, World world) {
        if (definition.id.equals("melon_popsicle")) player.extinguish();
        if ("heal".equals(definition.action)) player.heal(2.0F);
        if ("milk".equals(definition.action) || "cocoa".equals(definition.action)) {
            List<PotionEffect> choices = new ArrayList<PotionEffect>();
            for (Object object : player.getActivePotionEffects()) {
                PotionEffect effect = (PotionEffect) object;
                Potion potion = Potion.potionTypes[effect.getPotionID()];
                if (effect.isCurativeItem(new ItemStack(Item.bucketMilk  ))
                        && (!"cocoa".equals(definition.action) || potion.isBadEffect())) choices.add(effect);
            }
            if (!choices.isEmpty()) player.removePotionEffect(choices.get(world.rand.nextInt(choices.size())).getPotionID());
        }
        Potion potion = effect(definition.effect);
        if (potion != null && world.rand.nextFloat() < definition.chance)
            player.addPotionEffect(new PotionEffect(potion.id, definition.duration, definition.amplifier));
    }
    public static Potion effect(String name) {
        if (name == null) return null;
        if (name.equals("comfort")) return Content.comfort;
        if (name.equals("nourishment")) return Content.nourishment;
        if (name.equals("speed")) return Potion.moveSpeed;
        if (name.equals("hunger")) return Potion.hunger;
        if (name.equals("regeneration")) return Potion.regeneration;
        if (name.equals("absorption")) return Potion.field_76444_x;
        if (name.equals("nausea")) return Potion.confusion;
        throw new IllegalArgumentException("Unknown effect: " + name);
    }
}
