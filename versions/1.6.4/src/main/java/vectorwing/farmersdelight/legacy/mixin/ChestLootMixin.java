package vectorwing.farmersdelight.legacy.mixin;
import java.util.Random;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.inventory.IInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(WeightedRandomChestContent.class)
public abstract class ChestLootMixin {
    @Inject(method="generateChestContents(Ljava/util/Random;[Lnet/minecraft/util/WeightedRandomChestContent;Lnet/minecraft/inventory/IInventory;I)V",at=@At("TAIL"),require=1)
    private static void farmersdelight$append(Random random,WeightedRandomChestContent[] items,IInventory inventory,int rolls,CallbackInfo info) {
        vectorwing.farmersdelight.legacy.Acquisition.extra(random,items,inventory);
    }
}

