package vectorwing.farmersdelight.legacy;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.item.ItemStack;
import vectorwing.farmersdelight.legacy.api.ShapelessMatcher;
import vectorwing.farmersdelight.legacy.api.SourceRecipes;

public final class CookingRecipes {
    public static final List<Recipe> ALL = new ArrayList<Recipe>();
    public static final Map<String, Recipe> BY_ID = new LinkedHashMap<String, Recipe>();
    public static final Map<String, String> DEFERRED = new LinkedHashMap<String, String>();
    public static void register() {
        for (Map.Entry<String, JsonObject> entry : SourceRecipes.all().entrySet()) {
            JsonObject json = entry.getValue();
            if (!"farmersdelight:cooking".equals(json.get("type").getAsString())) continue;
            List<List<ItemStack>> inputs = new ArrayList<List<ItemStack>>();
            String unavailable = null;
            for (JsonElement ingredient : json.getAsJsonArray("ingredients")) {
                List<ItemStack> options = Stacks.candidates(ingredient);
                if (options.isEmpty()) unavailable = "Unavailable ingredient " + ingredient;
                inputs.add(options);
            }
            ItemStack output = Stacks.result(json.getAsJsonObject("result"));
            ItemStack container = json.has("container") ? Stacks.result(json.getAsJsonObject("container")) : remainder(output);
            if (Stacks.empty(output)) unavailable = "Unavailable meal " + json.get("result");
            if (json.has("container") && Stacks.empty(container)) unavailable = "Unavailable serving container " + json.get("container");
            if (unavailable != null) { DEFERRED.put(entry.getKey(), unavailable); continue; }
            int time = json.has("cookingtime") ? json.get("cookingtime").getAsInt() : 200;
            float xp = json.has("experience") ? json.get("experience").getAsFloat() : 0;
            if (time <= 0 || time > 32767 || inputs.isEmpty() || inputs.size() > 6 || xp < 0 || Float.isNaN(xp) || Float.isInfinite(xp))
                throw new IllegalArgumentException("Invalid cooking recipe: " + entry.getKey());
            Recipe recipe = new Recipe(entry.getKey(), inputs, output, container, time, xp);
            ALL.add(recipe); BY_ID.put(recipe.id, recipe);
        }
    }
    public static ItemStack remainder(ItemStack stack) {
        return !Stacks.empty(stack) && stack.getItem().hasContainerItem(stack) ? stack.getItem().getContainerItem(stack) : Stacks.none();
    }
    public static Recipe find(ItemStack[] inventory) { for (Recipe recipe : ALL) if (recipe.matches(inventory)) return recipe; return null; }
    public static final class Recipe {
        public final String id;
        public final List<List<ItemStack>> inputs;
        public final ItemStack output, container;
        public final int time;
        public final float experience;
        Recipe(String id, List<List<ItemStack>> inputs, ItemStack output, ItemStack container, int time, float experience) {
            this.id = id; this.inputs = inputs; this.output = output; this.container = container; this.time = time; this.experience = experience;
        }
        public boolean matches(final ItemStack[] inventory) {
            final int[] occupied = new int[6]; int count = 0;
            for (int i = 0; i < 6; i++) if (!Stacks.empty(inventory[i])) occupied[count++] = i;
            return ShapelessMatcher.matches(inputs.size(), count, new ShapelessMatcher.Match() {
                public boolean accepts(int ingredient, int slot) { return Stacks.matches(inputs.get(ingredient), inventory[occupied[slot]]); }
            });
        }
    }
    private CookingRecipes() {}
}
