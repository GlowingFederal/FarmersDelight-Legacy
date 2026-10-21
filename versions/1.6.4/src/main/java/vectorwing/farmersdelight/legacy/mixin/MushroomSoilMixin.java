package vectorwing.farmersdelight.legacy.mixin;
import vectorwing.farmersdelight.legacy.NativeRegistry;

import net.minecraft.block.Block;
import net.minecraft.block.BlockMushroom;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vectorwing.farmersdelight.legacy.Content;

@Mixin(BlockMushroom.class)
public abstract class MushroomSoilMixin {
    @Inject(method="canBlockStay(Lnet/minecraft/world/World;III)Z",at=@At("HEAD"),cancellable=true,require=1)
    private void farmersdelight$support(World world,int x,int y,int z,CallbackInfoReturnable<Boolean> result) {
        Block soil=NativeRegistry.at(world,x,y-1,z);
        if(soil==Content.richSoil || soil==Content.compost) result.setReturnValue(true);
    }
}

