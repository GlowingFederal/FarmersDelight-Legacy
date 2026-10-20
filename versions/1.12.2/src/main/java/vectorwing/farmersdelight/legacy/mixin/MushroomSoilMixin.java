package vectorwing.farmersdelight.legacy.mixin;

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
    @Inject(method="canBlockStay(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;)Z",at=@At("HEAD"),cancellable=true,require=1)
    private void farmersdelight$support(World world,net.minecraft.util.math.BlockPos pos,net.minecraft.block.state.IBlockState state,CallbackInfoReturnable<Boolean> result) {
        Block soil=world.getBlockState(pos.down()).getBlock();
        if(soil==Content.richSoil || soil==Content.compost) result.setReturnValue(true);
    }
}

