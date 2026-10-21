package vectorwing.farmersdelight.legacy.mixin;

import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vectorwing.farmersdelight.legacy.Content;

@Mixin(EntityChicken.class)
public abstract class ChickenFoodMixin {
    @Inject(method="<init>(Lnet/minecraft/world/World;)V",at=@At("RETURN"),require=1)
    private void farmersdelight$tempt(World world,CallbackInfo info) {
        EntityChicken animal=(EntityChicken)(Object)this;
        for(String id:new String[]{"cabbage_seeds","tomato_seeds","rice"}) {
            Item food=Content.ITEMS.get(id);
            if(food!=null) animal.tasks.addTask(3,new net.minecraft.entity.ai.EntityAITempt(animal,1.0,food.itemID,false));
        }
    }
    @Inject(method="isBreedingItem(Lnet/minecraft/item/ItemStack;)Z",at=@At("HEAD"),cancellable=true,require=1)
    private void farmersdelight$breed(ItemStack stack,CallbackInfoReturnable<Boolean> result) {
        if(vectorwing.farmersdelight.legacy.Stacks.empty(stack)) return;
        Item food=stack.getItem();
        if(food==Content.ITEMS.get("cabbage_seeds") || food==Content.ITEMS.get("tomato_seeds") || food==Content.ITEMS.get("rice")) result.setReturnValue(true);
    }
}

