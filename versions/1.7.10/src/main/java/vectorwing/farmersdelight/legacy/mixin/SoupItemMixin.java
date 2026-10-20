package vectorwing.farmersdelight.legacy.mixin;

import net.minecraft.item.ItemSoup;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.init.Items;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vectorwing.farmersdelight.legacy.SoupRules;

@Mixin(ItemSoup.class)
public abstract class SoupItemMixin extends ItemFood {
    protected SoupItemMixin(int nutrition,boolean wolf) { super(nutrition,wolf); }
    @Override public int getItemStackLimit(ItemStack stack) { return SoupRules.selected(stack) ? 16 : super.getItemStackLimit(stack); }
    @Inject(method="onEaten(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/player/EntityPlayer;)Lnet/minecraft/item/ItemStack;",at=@At("HEAD"),cancellable=true,require=1)
    private void farmersdelight$eat(ItemStack stack,World world,EntityPlayer player,CallbackInfoReturnable<ItemStack> result) {
        if(!SoupRules.selected(stack) && vectorwing.farmersdelight.legacy.Stacks.size(stack)<=1) return;

        if(player.capabilities.isCreativeMode) { super.onEaten(stack.copy(),world,player); result.setReturnValue(stack); return; }
        ItemStack remaining=super.onEaten(stack,world,player);
        ItemStack bowl=new ItemStack(Items.bowl);
        if(vectorwing.farmersdelight.legacy.Stacks.empty(remaining)) result.setReturnValue(bowl);
        else {
            if(!world.isRemote && !player.inventory.addItemStackToInventory(bowl)) player.dropPlayerItemWithRandomChoice(bowl,false);
            result.setReturnValue(remaining);
        }
    }
}

