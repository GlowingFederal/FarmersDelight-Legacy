package vectorwing.farmersdelight.legacy;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemShears;
import vectorwing.farmersdelight.legacy.api.SourceRecipes;

public final class CuttingRecipes {
    public static final List<Recipe> ALL = new ArrayList<Recipe>();
    public static final Map<String, String> DEFERRED = new LinkedHashMap<String, String>();
    private static final Map<Item, List<Recipe>> BY_INPUT = new LinkedHashMap<Item, List<Recipe>>();
    public static void register() {
        for (Map.Entry<String, JsonObject> entry : SourceRecipes.all().entrySet()) {
            JsonObject json = entry.getValue();
            if (!"farmersdelight:cutting".equals(json.get("type").getAsString())) continue;
            List<ItemStack> inputs = Stacks.candidates(json.getAsJsonArray("ingredients").get(0));
            List<ItemStack> outputs = new ArrayList<ItemStack>();
            List<Double> chances = new ArrayList<Double>();
            String unavailable = inputs.isEmpty() ? "Unavailable input " + json.get("ingredients") : null;
            for (JsonElement value : json.getAsJsonArray("result")) {
                JsonObject output = value.getAsJsonObject();
                ItemStack stack = Stacks.result(output);
                if (Stacks.empty(stack)) unavailable = "Unavailable output " + output.get("item").getAsString();
                JsonObject input = json.getAsJsonArray("ingredients").get(0).getAsJsonObject();
                if (input.has("item") && !input.get("item").getAsString().equals(output.get("item").getAsString()))
                    for (ItemStack candidate : inputs) if (Stacks.same(candidate, stack))
                        unavailable = "Equivalent input/output would create a conversion loop: " + input.get("item") + " -> " + output.get("item");
                outputs.add(stack);
                double chance = output.has("chance") ? output.get("chance").getAsDouble() : 1;
                if (Double.isNaN(chance) || chance < 0 || chance > 1) throw new IllegalArgumentException("Invalid chance: " + entry.getKey());
                chances.add(chance);
            }
            if (unavailable != null) { DEFERRED.put(entry.getKey(), unavailable); continue; }
            if (outputs.isEmpty() || outputs.size() > 4) throw new IllegalArgumentException("Invalid cutting results: " + entry.getKey());
            JsonObject tool = json.getAsJsonObject("tool");
            String toolKind = tool.has("tool") ? tool.get("tool").getAsString()
                    : tool.get("tag").getAsString().equals("forge:shears") ? "shears" : "knife";
            Recipe recipe = new Recipe(entry.getKey(), inputs, outputs, chances, toolKind,
                    json.has("sound") ? json.get("sound").getAsString() : "");
            ALL.add(recipe);
            for (ItemStack input : inputs) {
                List<Recipe> list = BY_INPUT.get(input.getItem());
                if (list == null) { list = new ArrayList<Recipe>(); BY_INPUT.put(input.getItem(), list); }
                if (!list.contains(recipe)) list.add(recipe);
            }
        }
    }
    public static Recipe find(ItemStack input, ItemStack tool) {
        if (Stacks.empty(input) || Stacks.empty(tool)) return null;
        List<Recipe> recipes = BY_INPUT.get(input.getItem());
        if (recipes != null) for (Recipe recipe : recipes) if (recipe.matchesInput(input) && recipe.matchesTool(tool)) return recipe;
        return null;
    }
    public static boolean tool(ItemStack stack, String kind) {
        if (Stacks.empty(stack)) return false;
        if (kind.equals("knife")) return Ingredients.isKnife(stack);
        if (kind.equals("shears")) return stack.getItem() instanceof ItemShears || Stacks.matches(SourceTags.candidates("forge:shears"), stack);
        return stack.getItem().getToolClasses(stack).contains(kind);
    }
    public static final class Recipe {
        public final String id, tool, sound;
        public final List<ItemStack> inputs, outputs;
        public final List<Double> chances;
        Recipe(String id, List<ItemStack> inputs, List<ItemStack> outputs, List<Double> chances, String tool, String sound) {
            this.id = id; this.inputs = Collections.unmodifiableList(inputs); this.outputs = Collections.unmodifiableList(outputs);
            this.chances = Collections.unmodifiableList(chances); this.tool = tool; this.sound = sound;
        }
        public boolean matchesInput(ItemStack stack) { return Stacks.matches(inputs, stack); }
        public boolean matchesTool(ItemStack stack) { return CuttingRecipes.tool(stack, tool); }
        public List<ItemStack> tools() {
            List<ItemStack> result = new ArrayList<ItemStack>();
            if (tool.equals("knife")) {
                for (String id : new String[] {"flint_knife", "iron_knife", "golden_knife", "diamond_knife"})
                    result.add(new ItemStack(Content.ITEMS.get(id)));
                for (ItemStack stack : net.minecraftforge.oredict.OreDictionary.getOres("toolKnife")) result.add(stack.copy());
            } else if (tool.equals("shears")) result.addAll(SourceTags.candidates("forge:shears"));
            else for (String material : new String[] {"wooden", "stone", "iron", "golden", "diamond"}) {
                ItemStack stack = Stacks.resolve("minecraft:" + material + "_" + tool);
                if (!Stacks.empty(stack)) result.add(stack);
            }
            return result;
        }
    }
    private CuttingRecipes() {}
}
