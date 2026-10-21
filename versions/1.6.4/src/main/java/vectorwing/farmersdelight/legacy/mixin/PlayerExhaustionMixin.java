package vectorwing.farmersdelight.legacy.mixin;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import vectorwing.farmersdelight.legacy.Content;
import vectorwing.farmersdelight.legacy.Settings;
import vectorwing.farmersdelight.legacy.api.EffectRules;

@Mixin(EntityPlayer.class)
public abstract class PlayerExhaustionMixin {
    @Unique private int farmersdelight$budgetTick = Integer.MIN_VALUE;
    @Unique private float farmersdelight$remaining;
    @ModifyVariable(method = "addExhaustion(F)V", at = @At("HEAD"), argsOnly = true, require = 1)
    private float farmersdelight$reduceActivity(float amount) {
        EntityPlayer player = (EntityPlayer) (Object) this;
        if (player.worldObj.isRemote || !Settings.nourishment || Content.nourishment == null
                || !player.isPotionActive(Content.nourishment) || amount <= 0.0F) return amount;
        if (farmersdelight$budgetTick != player.ticksExisted) {
            farmersdelight$budgetTick = player.ticksExisted;
            farmersdelight$remaining = 0.1F;
        }
        float reduced = EffectRules.activityExhaustion(amount, farmersdelight$remaining);
        farmersdelight$remaining = Math.max(0.0F, farmersdelight$remaining - (amount - reduced));
        return reduced;
    }
}
