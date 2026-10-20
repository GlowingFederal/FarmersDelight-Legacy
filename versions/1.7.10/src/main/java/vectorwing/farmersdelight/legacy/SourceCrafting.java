package vectorwing.farmersdelight.legacy;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import vectorwing.farmersdelight.legacy.api.ShapelessMatcher;
import vectorwing.farmersdelight.legacy.api.SourceRecipes;
import cpw.mods.fml.common.registry.GameRegistry;

/** Native recipes compiled from the reference data; unresolved prerequisites remain deferred. */
public final class SourceCrafting implements IRecipe {
    public static final Map<String, String> DEFERRED = new LinkedHashMap<String, String>();
    public static final List<SourceCrafting> ALL = new ArrayList<SourceCrafting>();
    public final String id;
    public final List<List<ItemStack>> inputs;
    public final ItemStack output;
    private boolean vanillaCrate;
    public boolean enabled() { return !vanillaCrate || Settings.vanillaCrates; }
    public static int activeCount() { int count=0; for(SourceCrafting recipe:ALL) if(recipe.enabled()) count++; return count; }
    private final int width, height;
    private SourceCrafting(String id, List<List<ItemStack>> inputs, ItemStack output, int width, int height) {
        this.id = id; this.inputs = inputs; this.output = output; this.width = width; this.height = height;
    }
    public static void register() {
        net.minecraftforge.oredict.RecipeSorter.register("farmersdelight:source", SourceCrafting.class,
                net.minecraftforge.oredict.RecipeSorter.Category.SHAPED, "after:minecraft:shaped before:minecraft:shapeless");
        for (Map.Entry<String, JsonObject> entry : SourceRecipes.all().entrySet()) {
            JsonObject json = entry.getValue(); String type = json.get("type").getAsString();
            if (!type.equals("minecraft:crafting_shaped") && !type.equals("minecraft:crafting_shapeless")) continue;
            if (Recipes.REGISTRY.entries().containsKey(entry.getKey()) || entry.getKey().equals("farmersdelight:cutting_board")) continue;
            if(entry.getKey().equals("farmersdelight:paper_from_tree_bark") || entry.getKey().equals("farmersdelight:organic_compost_from_tree_bark")) {
                boolean bark=false;
                for(CuttingRecipes.Recipe cutting:CuttingRecipes.ALL) for(ItemStack result:cutting.outputs)
                    if(result.getItem()==Content.ITEMS.get("tree_bark")) bark=true;
                if(!bark) { DEFERRED.put(entry.getKey(),"Tree bark acquisition requires unavailable stripped-log outputs"); continue; }
            }
            boolean vanillaCrate=false; String conditionFailure=null;
            if(json.has("conditions")) for(JsonElement element:json.getAsJsonArray("conditions")) {
                JsonObject condition=element.getAsJsonObject(); String conditionType=condition.get("type").getAsString();
                if(conditionType.equals("farmersdelight:vanilla_crates_enabled")) vanillaCrate=true;
                else if(conditionType.equals("forge:mod_loaded")) {
                    if(!cpw.mods.fml.common.Loader.isModLoaded(condition.get("modid").getAsString())) conditionFailure="Optional mod is absent: "+condition.get("modid").getAsString();
                } else conditionFailure="Unsupported source condition: "+conditionType;
            }
            if(conditionFailure!=null) { DEFERRED.put(entry.getKey(),conditionFailure); continue; }
            ItemStack output = Stacks.result(json.getAsJsonObject("result"));
            String missing = Stacks.empty(output) ? "Unavailable output " + json.get("result") : null;
            List<List<ItemStack>> inputs = new ArrayList<List<ItemStack>>();
            int width = 0, height = 0;
            if (json.has("pattern")) {
                height = json.getAsJsonArray("pattern").size();
                width = json.getAsJsonArray("pattern").get(0).getAsString().length();
                for (JsonElement row : json.getAsJsonArray("pattern")) {
                    if (row.getAsString().length() != width) throw new IllegalArgumentException("Nonrectangular recipe " + entry.getKey());
                    for (char key : row.getAsString().toCharArray()) {
                        if (key == ' ') inputs.add(null);
                        else {
                            JsonElement ingredient = json.getAsJsonObject("key").get(String.valueOf(key));
                            List<ItemStack> options = Stacks.candidates(ingredient);
                            if (options.isEmpty()) missing = "Unavailable ingredient " + ingredient;
                            inputs.add(options);
                        }
                    }
                }
            } else for (JsonElement ingredient : json.getAsJsonArray("ingredients")) {
                List<ItemStack> options = Stacks.candidates(ingredient);
                if (options.isEmpty()) missing = "Unavailable ingredient " + ingredient;
                inputs.add(options);
            }
            if (missing != null) { DEFERRED.put(entry.getKey(), missing); continue; }
            SourceCrafting recipe = new SourceCrafting(entry.getKey(), inputs, output, width, height);
            recipe.vanillaCrate=vanillaCrate;
            if(!recipe.enabled()) DEFERRED.put(recipe.id,"Disabled by recipes.enableVanillaCropCrates");
            ALL.add(recipe);
            GameRegistry.addRecipe(recipe);
        }
    }
    public boolean matches(final InventoryCrafting grid, World world) {
        if(!enabled()) return false;
        if (width == 0) {
            final int[] occupied = new int[9]; int count = 0;
            for (int i = 0; i < grid.getSizeInventory(); i++) if (!Stacks.empty(grid.getStackInSlot(i))) {
                if (count == occupied.length) return false;
                occupied[count++] = i;
            }
            return ShapelessMatcher.matches(inputs.size(), count, new ShapelessMatcher.Match() {
                public boolean accepts(int ingredient, int slot) { return Stacks.matches(inputs.get(ingredient), grid.getStackInSlot(occupied[slot])); }
            });
        }
        for (int x = 0; x <= 3 - width; x++) for (int y = 0; y <= 3 - height; y++)
            if (pattern(grid, x, y, false) || pattern(grid, x, y, true)) return true;
        return false;
    }
    private boolean pattern(InventoryCrafting grid, int offsetX, int offsetY, boolean mirror) {
        for (int y = 0; y < 3; y++) for (int x = 0; x < 3; x++) {
            int rx = x - offsetX, ry = y - offsetY;
            List<ItemStack> expected = rx < 0 || ry < 0 || rx >= width || ry >= height ? null
                    : inputs.get(ry * width + (mirror ? width - rx - 1 : rx));
            ItemStack actual = grid.getStackInRowAndColumn(x, y);
            if (expected == null ? !Stacks.empty(actual) : !Stacks.matches(expected, actual)) return false;
        }
        return true;
    }
    public ItemStack getCraftingResult(InventoryCrafting grid) { return output.copy(); }
    public ItemStack getRecipeOutput() { return output; }
    public List<List<ItemStack>> grid() {
        List<List<ItemStack>> grid = new ArrayList<List<ItemStack>>();
        for (int i = 0; i < 9; i++) {
            int x = i % 3, y = i / 3;
            grid.add(width == 0 ? i < inputs.size() ? inputs.get(i) : null : x < width && y < height ? inputs.get(y * width + x) : null);
        }
        return grid;
    }
    public int getRecipeSize() { return inputs.size(); }
}
