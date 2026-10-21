package vectorwing.farmersdelight.legacy.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Constant;

@Mixin(EntityLivingBase.class)
public abstract class ToolKnockbackMixin {
    @ModifyConstant(method="knockBack(Lnet/minecraft/entity/Entity;FDD)V",constant=@Constant(floatValue=0.4F),require=1)
    private float farmersdelight$toolStrength(float value,Entity attacker,float strength,double x,double z) {
        return vectorwing.farmersdelight.legacy.ToolCombat.knockback(value,attacker);
    }
}

