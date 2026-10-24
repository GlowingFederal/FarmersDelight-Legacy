package vectorwing.farmersdelight.legacy.mixin;

import java.io.File;
import net.minecraft.world.storage.SaveHandler;
import net.minecraft.world.storage.WorldInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vectorwing.farmersdelight.legacy.PotionIdRegistry;

/** Migrate offline data before even the integrated player's level.dat is deserialized. */
@Mixin(SaveHandler.class)
public abstract class PotionSaveMigrationMixin {
    @Shadow public abstract File getWorldDirectory();

    @Inject(method = "loadWorldInfo", at = @At("HEAD"))
    private void farmersdelight$migratePotions(CallbackInfoReturnable<WorldInfo> result) {
        PotionIdRegistry.prepareWorld(getWorldDirectory());
    }
}
