package vectorwing.farmersdelight.legacy;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
public final class Backstabbing extends Enchantment {
    public Backstabbing() {
        super(Settings.backstabbingId,5,net.minecraft.enchantment.EnumEnchantmentType.weapon);
        setName("farmersdelight.backstabbing");

    }
    public int getMaxLevel() { return 3; }
    public int getMinEnchantability(int level) { return 15+(level-1)*9; }
    public int getMaxEnchantability(int level) { return super.getMinEnchantability(level)+50; }
    public boolean canApply(ItemStack stack) { return !Stacks.empty(stack) && Ingredients.isKnife(stack); }
    public boolean canApplyAtEnchantingTable(ItemStack stack) { return canApply(stack); }
}

