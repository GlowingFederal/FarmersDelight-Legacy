package vectorwing.farmersdelight.legacy;

import net.minecraft.item.ItemStack;
public final class SoupRules {
    public static boolean selected(ItemStack stack) {
        if(!Settings.stackSoups || Stacks.empty(stack) || !(stack.getItem() instanceof net.minecraft.item.ItemSoup)) return false;
        String name=stack.getItem().getRegistryName().toString();
        return Settings.allSoups!=Settings.soupItems.contains(name);
    }
}

