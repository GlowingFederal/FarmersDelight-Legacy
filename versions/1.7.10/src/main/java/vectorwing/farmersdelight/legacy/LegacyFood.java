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


public class LegacyFood extends ItemFood {
    public final FoodDefinition definition;
    public LegacyFood(FoodDefinition definition) {
        super(definition.nutrition, definition.saturation, definition.meat);
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
            player.addStat(net.minecraft.stats.StatList.objectUseStats[net.minecraft.item.Item.getIdFromItem(this)], 1);
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
                if (effect.isCurativeItem(new ItemStack(Items.milk_bucket  ))
                        && (!"cocoa".equals(definition.action) || potion.isBadEffect())) choices.add(effect);
            }
            if (!choices.isEmpty()) player.removePotionEffect(choices.get(world.rand.nextInt(choices.size())).getPotionID());
        }
        Potion potion = effect(definition.effect);
        if (potion != null && world.rand.nextFloat() < definition.chance)
            player.addPotionEffect(new PotionEffect(potion.id, definition.duration, definition.amplifier));
        if (definition.effects != null) for (FoodDefinition.Effect extra : definition.effects) {
            Potion additional = effect(extra.effect);
            if (additional != null && world.rand.nextFloat() < extra.chance)
                player.addPotionEffect(new PotionEffect(additional.id, extra.duration, extra.amplifier));
        }
    }
    private static final java.util.Map<String,Potion> ADDON_EFFECTS=new java.util.LinkedHashMap<String,Potion>();
    public static void registerEffect(String name,Potion potion) {
        if(name.indexOf(':')<1 || potion==null || ADDON_EFFECTS.containsKey(name)) throw new IllegalArgumentException("Invalid or duplicate effect: "+name);
        ADDON_EFFECTS.put(name,potion);
    }
    public static Potion effect(String name) {
        if (name == null) return null;
        if (ADDON_EFFECTS.containsKey(name)) return ADDON_EFFECTS.get(name);
        if (name.equals("comfort")) return Content.comfort;
        if (name.equals("nourishment")) return Content.nourishment;
        if (name.equals("resistance")) return Potion.resistance;
        if (name.equals("health_boost")) return Potion.field_76434_w;
        if (name.equals("night_vision")) return Potion.nightVision;
        if (name.equals("water_breathing")) return Potion.waterBreathing;
        if (name.equals("weakness")) return Potion.weakness;
        if (name.equals("slowness")) return Potion.moveSlowdown;
        if (name.equals("wither")) return Potion.wither;
        if (name.equals("haste")) return Potion.digSpeed;
        // These effects have no native representation here; their dependency chains are deferred by addons.
        if (name.equals("levitation") || name.equals("glowing") || name.equals("luck")) return null;
        if (name.equals("blindness")) return Potion.blindness;
        if (name.equals("mining_fatigue")) return Potion.digSlowdown;
        if (name.equals("strength")) return Potion.damageBoost;
        if (name.equals("speed")) return Potion.moveSpeed;
        if (name.equals("hunger")) return Potion.hunger;
        if (name.equals("regeneration")) return Potion.regeneration;
        if (name.equals("absorption")) return Potion.field_76444_x;
        if (name.equals("nausea")) return Potion.confusion;
        throw new IllegalArgumentException("Unknown effect: " + name);
    }
}
