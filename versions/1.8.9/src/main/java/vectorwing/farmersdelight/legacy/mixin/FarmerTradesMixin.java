package vectorwing.farmersdelight.legacy.mixin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(net.minecraft.entity.passive.EntityVillager.class)
public abstract class FarmerTradesMixin {
    @Inject(method="populateBuyingList()V",at=@At("RETURN"),require=1)
    private void farmersdelight$trades(CallbackInfo info) { vectorwing.farmersdelight.legacy.Acquisition.farmer((net.minecraft.entity.passive.EntityVillager)(Object)this); }
}
