package vectorwing.farmersdelight.legacy;
import net.minecraft.potion.Potion;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import vectorwing.farmersdelight.legacy.api.EffectRules;
public final class LegacyEffect extends Potion {
    private final boolean healing;
    public LegacyEffect(String name) {
        super(false, 0);
        healing = name.equals("comfort");
        setPotionName("potion.farmersdelight." + name);
        setRegistryName("farmersdelight", name);
    }
    
    @Override public boolean isReady(int duration, int amplifier) { return healing && duration % 80 == 0; }
    @Override public void performEffect(EntityLivingBase entity, int amplifier) {
        if (entity.world.isRemote || !Settings.comfort) return;
        EntityPlayer player = entity instanceof EntityPlayer ? (EntityPlayer) entity : null;
        if (EffectRules.comfort(entity.isPotionActive(net.minecraft.init.MobEffects.REGENERATION), player != null,
                player == null ? 0 : player.getFoodStats().getSaturationLevel(),
                player != null && player.world.getGameRules().getBoolean("naturalRegeneration"),
                player == null ? 0 : player.getFoodStats().getFoodLevel(), entity.getHealth() < entity.getMaxHealth())) entity.heal(1.0F);
    }
}
