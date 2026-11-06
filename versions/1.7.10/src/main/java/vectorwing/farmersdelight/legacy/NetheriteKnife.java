package vectorwing.farmersdelight.legacy;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
/** Original netherite tier; the optional provider owns the upgrading recipe and material acquisition. */
public final class NetheriteKnife extends Knife {
    private static final Item.ToolMaterial TIER = net.minecraftforge.common.util.EnumHelper.addToolMaterial("FARMERSDELIGHT_NETHERITE", 3, 2031, 9, 4, 15);
    public NetheriteKnife() { super(TIER); }
    @Override public boolean getIsRepairable(ItemStack tool, ItemStack material) {
        return Stacks.same(Stacks.copy(material, 1), UABCompatibility.resolve("minecraft:netherite_ingot"));
    }
}
