package vectorwing.farmersdelight.legacy;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import vectorwing.farmersdelight.legacy.api.LegacyNames;

/** Native stack operations and exact flattened-to-legacy vanilla identities. */
public final class Stacks {
    public static boolean empty(ItemStack stack) { return stack == null || stack.stackSize <= 0; }
    public static ItemStack none() { return null; }
    public static int size(ItemStack stack) { return empty(stack) ? 0 : stack.stackSize; }
    public static void count(ItemStack stack, int amount) { stack.stackSize = amount; }
    public static ItemStack copy(ItemStack stack, int amount) {
        if (empty(stack) || amount <= 0) return none();
        ItemStack copy = stack.copy(); count(copy, amount); return copy;
    }
    public static void shrink(ItemStack stack, int amount) { count(stack, Math.max(0, size(stack) - amount)); }
    public static ItemStack split(ItemStack stack, int amount) { return stack.splitStack(amount); }
    public static boolean same(ItemStack a, ItemStack b) {
        return !empty(a) && !empty(b) && a.getItem() == b.getItem() && a.getItemDamage() == b.getItemDamage()
                && ItemStack.areItemStackTagsEqual(a, b);
    }
    public static ItemStack resolve(String modernId) {
        if ((modernId.equals("farmersdelight:mutton_chops") || modernId.equals("farmersdelight:cooked_mutton_chops"))
                && net.minecraft.item.Item.itemRegistry.getObject(new net.minecraft.util.ResourceLocation("minecraft:mutton")) == null) return none();
        String id = LegacyNames.ITEMS.containsKey(modernId) ? LegacyNames.ITEMS.get(modernId) : modernId;
        int metadata = 0;
        int marker = id.indexOf('@');
        if (marker >= 0) { metadata = Integer.parseInt(id.substring(marker + 1)); id = id.substring(0, marker); }
        if(id.equals("minecraft:cooked_fish")) id="minecraft:cooked_fish";
        Item item = (Item) Item.itemRegistry.getObject(new net.minecraft.util.ResourceLocation(id));
        return item == null  ? none() : new ItemStack(item, 1, metadata);
    }
    public static ItemStack result(JsonObject json) {
        ItemStack stack = resolve(json.get("item").getAsString());
        if (empty(stack)) return none();
        int amount = json.has("count") ? json.get("count").getAsInt() : 1;
        if (amount <= 0 || amount > stack.getMaxStackSize()) throw new IllegalArgumentException("Invalid recipe output count: " + json);
        count(stack, amount);
        if (json.has("nbt")) throw new IllegalArgumentException("NBT recipe output requires a native decoder: " + json);
        return stack;
    }
    public static List<ItemStack> candidates(JsonElement ingredient) {
        List<ItemStack> result = new ArrayList<ItemStack>();
        if (ingredient.isJsonArray()) {
            for (JsonElement entry : ingredient.getAsJsonArray()) result.addAll(candidates(entry));
        } else {
            JsonObject json = ingredient.getAsJsonObject();
            if (json.has("item")) {
                ItemStack stack = resolve(json.get("item").getAsString());
                if (!empty(stack)) result.add(stack);
            } else if (json.has("tag")) result.addAll(SourceTags.candidates(json.get("tag").getAsString()));
            else throw new IllegalArgumentException("Unknown ingredient: " + ingredient);
        }
        return result;
    }
    public static boolean matches(List<ItemStack> candidates, ItemStack actual) {
        if (empty(actual)) return false;
        // Vanilla JSON item ingredients ignore damage on damageable items and preserve arbitrary input NBT.
        for (ItemStack expected : candidates)
            if (expected.getItem() == actual.getItem() && (actual.isItemStackDamageable()
                    || expected.getItemDamage() == actual.getItemDamage())) return true;
        return false;
    }
    private Stacks() {}
}
