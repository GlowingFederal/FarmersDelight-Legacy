package vectorwing.farmersdelight.legacy;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import vectorwing.farmersdelight.legacy.api.SourceRecipes;

public final class GrillingRecipes {
    public static final List<Recipe> ALL = new ArrayList<Recipe>();
    public static final Map<String, String> DEFERRED = new LinkedHashMap<String, String>();
    public static void register() {
        // These are the vanilla campfire foods in the reference generation, expressed natively.
        String[][] vanilla = {{"beef","cooked_beef"},{"chicken","cooked_chicken"},{"porkchop","cooked_porkchop"},
                {"cod","cooked_cod"},{"salmon","cooked_salmon"},{"mutton","cooked_mutton"},{"rabbit","cooked_rabbit"},{"potato","baked_potato"}};
        for (String[] food : vanilla) {
            ItemStack input = Stacks.resolve("minecraft:" + food[0]), output = Stacks.resolve("minecraft:" + food[1]);
            if (!Stacks.empty(input) && !Stacks.empty(output)) ALL.add(new Recipe("minecraft:" + food[1] + "_from_campfire_cooking",
                    java.util.Collections.singletonList(input), output, 600));
        }
        for (Map.Entry<String, JsonObject> entry : SourceRecipes.all().entrySet()) {
            JsonObject json = entry.getValue(); String type = json.get("type").getAsString();
            if (!type.equals("minecraft:campfire_cooking") && !type.equals("minecraft:smelting")
                    && !type.equals("minecraft:smoking") && !type.equals("minecraft:blasting")) continue;
            List<ItemStack> inputs = Stacks.candidates(json.get("ingredient"));
            ItemStack output = Stacks.resolve(json.get("result").getAsString());
            if (inputs.isEmpty() || Stacks.empty(output)) { DEFERRED.put(entry.getKey(), "Unavailable cooking ingredient or output"); continue; }
            if (type.equals("minecraft:campfire_cooking")) {
                ALL.add(new Recipe(entry.getKey(), inputs, output, json.has("cookingtime") ? json.get("cookingtime").getAsInt() : 600));
            } else {
                // Legacy furnaces cover the reference's ordinary baking and smoker-only food routes.
                for (ItemStack input : inputs) {
                    input = input.copy();
                    if (input.isItemStackDamageable()) input.setItemDamage(net.minecraftforge.oredict.OreDictionary.WILDCARD_VALUE);
                    ItemStack existing = net.minecraft.item.crafting.FurnaceRecipes.instance().getSmeltingResult(input);
                    if (Stacks.empty(existing)) GameRegistry.addSmelting(input, output.copy(), json.has("experience") ? json.get("experience").getAsFloat() : 0);
                }
            }
        }
    }
    public static Recipe find(ItemStack input) {
        for (Recipe recipe : ALL) if (Stacks.matches(recipe.inputs, input)) return recipe;
        return null;
    }
    public static int skilletTime(int original, int fireAspect) {
        return Math.max(60, (int) ((original / 20) * (0.2F - Math.max(0, fireAspect) * 0.05F)) * 20);
    }
    public static final class Recipe {
        public final String id;
        public final List<ItemStack> inputs;
        public final ItemStack output;
        public final int time;
        Recipe(String id, List<ItemStack> inputs, ItemStack output, int time) {
            if (time <= 0 || time > 32767) throw new IllegalArgumentException("Invalid grilling time: " + id);
            this.id = id; this.inputs = inputs; this.output = output; this.time = time;
        }
    }
    private GrillingRecipes() {}
}
