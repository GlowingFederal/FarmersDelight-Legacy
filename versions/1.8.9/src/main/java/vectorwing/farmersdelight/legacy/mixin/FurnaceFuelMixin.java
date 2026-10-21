package vectorwing.farmersdelight.legacy.mixin;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(TileEntityFurnace.class)
public abstract class FurnaceFuelMixin {
    @Inject(method="getItemBurnTime(Lnet/minecraft/item/ItemStack;)I",at=@At("HEAD"),cancellable=true,require=1)
    private static void farmersdelight$fuel(ItemStack stack,CallbackInfoReturnable<Integer> result) {
        int ticks=vectorwing.farmersdelight.legacy.api.FuelRules.ticks(stack==null ? null : Item.itemRegistry.getNameForObject(stack.getItem()).toString());
        if(ticks>=0) result.setReturnValue(ticks);
    }
}

