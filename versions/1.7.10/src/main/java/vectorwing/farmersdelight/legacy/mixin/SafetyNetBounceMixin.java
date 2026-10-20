package vectorwing.farmersdelight.legacy.mixin;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class SafetyNetBounceMixin {
    @Unique private double farmersdelight$landingVelocity;
    @Inject(method="moveEntity(DDD)V",at=@At("HEAD"),require=1)
    private void farmersdelight$beforeMove(double x,double y,double z,CallbackInfo info) {
        farmersdelight$landingVelocity=((Entity)(Object)this).motionY;
    }
    @Inject(method="moveEntity(DDD)V",at=@At("RETURN"),require=1)
    private void farmersdelight$bounce(double x,double y,double z,CallbackInfo info) {
        Entity entity=(Entity)(Object)this;
        if(farmersdelight$landingVelocity>=0 || !entity.onGround || entity.isSneaking()) return;
        if(entity.worldObj.getBlock((int)Math.floor(entity.posX),(int)Math.floor(entity.posY-entity.yOffset-0.01),(int)Math.floor(entity.posZ))==vectorwing.farmersdelight.legacy.Content.safetyNet) {
            entity.motionY=-farmersdelight$landingVelocity*(entity instanceof net.minecraft.entity.EntityLivingBase ? 0.6 : 0.8);
            entity.fallDistance=0;
        }
    }
}

