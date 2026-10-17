package vectorwing.farmersdelight.legacy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import vectorwing.farmersdelight.legacy.api.IngredientRegistry;
public final class Ingredients {
    public static final IngredientRegistry<ItemStack> REGISTRY = new IngredientRegistry<ItemStack>(new IngredientRegistry.Stacks<ItemStack>() {
        public ItemStack copy(ItemStack stack) { return stack.copy(); }
        public boolean same(ItemStack expected, ItemStack actual) {
            return actual != null && actual.stackSize > 0 && expected.getItem() == actual.getItem()
                    && expected.getItemDamage() == actual.getItemDamage() && ItemStack.areItemStackTagsEqual(expected, actual);
        }
        public List<ItemStack> ore(String name) {
            List<ItemStack> result = new ArrayList<ItemStack>();
            for (ItemStack stack : OreDictionary.getOres(name)) {
                // No wildcard species or damage/NBT conflation; tools use an explicit predicate.
                if (stack.getItemDamage() != OreDictionary.WILDCARD_VALUE) result.add(stack.copy());
            }
            Collections.sort(result, new Comparator<ItemStack>() {
                public int compare(ItemStack a, ItemStack b) {
                    String x = net.minecraft.item.Item.itemRegistry.getNameForObject(a.getItem()) + "@" + a.getItemDamage();
                    String y = net.minecraft.item.Item.itemRegistry.getNameForObject(b.getItem()) + "@" + b.getItemDamage();
                    return x.compareTo(y);
                }
            });
            return result;
        }
    });
    public static void register() {
        String[][] crops = {{"cabbage", "cropCabbage"}, {"tomato", "cropTomato"}, {"onion", "cropOnion"}, {"rice", "cropRice"}};
        for (String[] crop : crops) {
            ItemStack stack = new ItemStack(Content.ITEMS.get(crop[0]));
            OreDictionary.registerOre(crop[1], stack);
            REGISTRY.add("forge:crops/" + crop[0], stack);
            REGISTRY.ore("forge:crops/" + crop[0], crop[1]);
        }
        bridge("forge:dough", "wheat_dough", "foodDough");
        bridge("forge:pasta/raw_pasta", "raw_pasta", "foodPasta");
        REGISTRY.ore("forge:ingots/iron", "ingotIron");
        REGISTRY.add("forge:ingots/iron", new ItemStack(net.minecraft.init.Items.iron_ingot));
        REGISTRY.add("forge:eggs", new ItemStack(net.minecraft.init.Items.egg));
        REGISTRY.ore("forge:eggs", "listAllegg");
        REGISTRY.add("farmersdelight:milk/bucket", new ItemStack(net.minecraft.init.Items.milk_bucket));
        REGISTRY.add("farmersdelight:milk/bottle", new ItemStack(Content.ITEMS.get("milk_bottle")));
        // Bucket and bottle are separate portion memberships, never a universal listAllmilk match.
        REGISTRY.alias("forge:milk", "farmersdelight:milk/bucket");
        REGISTRY.alias("forge:milk", "farmersdelight:milk/bottle");
        for (String name : new String[] {"cod", "salmon", "pufferfish"}) {
            int metadata = name.equals("cod") ? 0 : name.equals("salmon") ? 1 : 3;
            REGISTRY.add("forge:raw_fishes/" + name, new ItemStack(net.minecraft.init.Items.fish, 1, metadata));
        }
    }
    private static void bridge(String name, String item, String ore) {
        ItemStack stack = new ItemStack(Content.ITEMS.get(item));
        OreDictionary.registerOre(ore, stack); REGISTRY.add(name, stack); REGISTRY.ore(name, ore);
    }
    public static List<ItemStack> candidates(String specification) {
        if (specification.startsWith("#")) return REGISTRY.candidates(specification.substring(1));
        return Collections.singletonList(new ItemStack(Content.item(specification)));
    }
    public static boolean matches(String specification, ItemStack actual) {
        if (actual == null || actual.stackSize <= 0) return false;
        if (specification.startsWith("#")) return REGISTRY.matches(specification.substring(1), actual);
        return actual.getItem() == Content.item(specification) && actual.getItemDamage() == 0 && !actual.hasTagCompound();
    }
    public static boolean isKnife(ItemStack stack) {
        if (stack == null || stack.stackSize <= 0) return false;
        if (stack.getItem() instanceof Knife) return true;
        for (ItemStack registered : OreDictionary.getOres("toolKnife"))
            if (registered.getItem() == stack.getItem() && (!stack.getItem().isDamageable()
                    ? registered.getItemDamage() == stack.getItemDamage() : true)) return true;
        return false;
    }
}
