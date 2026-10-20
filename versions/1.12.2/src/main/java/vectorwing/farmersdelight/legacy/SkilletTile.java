package vectorwing.farmersdelight.legacy;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.enchantment.EnchantmentHelper;

public final class SkilletTile extends GrillingTile {
    private ItemStack skillet = Stacks.none();
    public SkilletTile() { super(false); }
    public void identity(ItemStack stack) { skillet = Stacks.copy(stack, 1); changed(); }
    public ItemStack identity() { return Stacks.empty(skillet) ? new ItemStack(Content.skillet) : skillet.copy(); }
    protected int fireAspect() { return Stacks.empty(skillet) ? 0 : EnchantmentHelper.getEnchantmentLevel(net.minecraft.init.Enchantments.FIRE_ASPECT, skillet); }
    @Override public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt); skillet = new ItemStack(nbt.getCompoundTag("Skillet"));
    }
    @Override public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt); NBTTagCompound saved = new NBTTagCompound();
        if (!Stacks.empty(skillet)) skillet.writeToNBT(saved); nbt.setTag("Skillet", saved); return nbt;
    }
}
