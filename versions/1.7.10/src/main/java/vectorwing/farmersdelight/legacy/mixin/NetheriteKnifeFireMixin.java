package vectorwing.farmersdelight.legacy.mixin;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vectorwing.farmersdelight.legacy.NetheriteKnife;
import vectorwing.farmersdelight.legacy.Stacks;
/** Retain the original netherite knife's fire resistance without a new saved entity identity. */
@Mixin(EntityItem.class)
public abstract class NetheriteKnifeFireMixin {
    @Inject(method = "attackEntityFrom", at = @At("HEAD"), cancellable = true)
    private void farmersdelight$fireproof(DamageSource source, float amount, CallbackInfoReturnable<Boolean> result) {
        net.minecraft.item.ItemStack stack = ((EntityItem) (Object) this).getEntityItem();
        if (source.isFireDamage() && !Stacks.empty(stack) && stack.getItem() instanceof NetheriteKnife) result.setReturnValue(false);
    }
}
