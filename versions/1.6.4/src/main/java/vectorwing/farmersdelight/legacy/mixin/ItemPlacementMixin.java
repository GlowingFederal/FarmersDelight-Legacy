package vectorwing.farmersdelight.legacy.mixin;
import vectorwing.farmersdelight.legacy.NativeRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(net.minecraft.item.ItemBlock.class)
public abstract class ItemPlacementMixin {
    @Inject(method="placeBlockAt",at=@At("RETURN"),remap=false,require=1)
    private void farmersdelight$placed(net.minecraft.item.ItemStack stack,net.minecraft.entity.player.EntityPlayer player,net.minecraft.world.World world,int x,int y,int z,int side,float hx,float hy,float hz,int meta,CallbackInfoReturnable<Boolean> result) {
        if(result.getReturnValue() && !world.isRemote) vectorwing.farmersdelight.legacy.Progression.placed(player,vectorwing.farmersdelight.legacy.NativeRegistry.at(world,x,y,z));
    }
}
