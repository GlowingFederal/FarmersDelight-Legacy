package vectorwing.farmersdelight.legacy.mixin;

import net.minecraft.potion.Potion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

/** The native potion allocator owns the bounded capacity change. */
@Mixin(Potion.class)
public interface PotionArrayAccessor {
    @Mutable
    @Accessor("potionTypes")
    static void farmersdelight$setPotionTypes(Potion[] potions) { throw new AssertionError("Mixin accessor was not applied"); }
}
