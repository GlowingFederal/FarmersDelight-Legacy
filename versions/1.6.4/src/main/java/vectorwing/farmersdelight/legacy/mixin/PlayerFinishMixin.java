package vectorwing.farmersdelight.legacy.mixin;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(net.minecraft.entity.player.EntityPlayer.class)
public abstract class PlayerFinishMixin {
    @Unique private net.minecraft.item.ItemStack farmersdelight$food;
    @Inject(method="onItemUseFinish()V",at=@At("HEAD"),require=1)
    private void farmersdelight$before(CallbackInfo info) {
        net.minecraft.item.ItemStack stack=((net.minecraft.entity.player.EntityPlayer)(Object)this).getItemInUse();
        farmersdelight$food=stack==null ? null : stack.copy();
    }
    @Inject(method="onItemUseFinish()V",at=@At("RETURN"),require=1)
    private void farmersdelight$after(CallbackInfo info) {
        vectorwing.farmersdelight.legacy.FoodEvents.finished((net.minecraft.entity.player.EntityPlayer)(Object)this,farmersdelight$food);farmersdelight$food=null;
    }
}
