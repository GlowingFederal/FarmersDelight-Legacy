package vectorwing.farmersdelight.legacy.mixin;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public abstract class SkilletEnchantmentsMixin {
    @Inject(method="canApply(Lnet/minecraft/item/ItemStack;)Z",at=@At("HEAD"),cancellable=true,require=1)
    private void farmersdelight$weapon(ItemStack stack,CallbackInfoReturnable<Boolean> result) {
        if(!vectorwing.farmersdelight.legacy.Stacks.empty(stack) && stack.getItem() instanceof vectorwing.farmersdelight.legacy.SkilletItem
                && ((Enchantment)(Object)this).type==EnumEnchantmentType.weapon) result.setReturnValue(true);
    }
}

