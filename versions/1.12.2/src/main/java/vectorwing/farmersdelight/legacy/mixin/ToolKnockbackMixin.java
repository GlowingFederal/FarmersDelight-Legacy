package vectorwing.farmersdelight.legacy.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;


@Mixin(EntityLivingBase.class)
public abstract class ToolKnockbackMixin {
    @ModifyVariable(method="knockBack(Lnet/minecraft/entity/Entity;FDD)V",at=@At("HEAD"),argsOnly=true,ordinal=0,require=1)
    private float farmersdelight$toolStrength(float value,Entity attacker,float strength,double x,double z) {
        return vectorwing.farmersdelight.legacy.ToolCombat.knockback(value,attacker);
    }
}

