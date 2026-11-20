package vectorwing.farmersdelight.legacy;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import net.minecraft.command.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.*;
import net.minecraft.server.MinecraftServer;
import vectorwing.farmersdelight.legacy.api.*;
import vectorwing.farmersdelight.legacy.api.RecipeDiagnostics.Entry;

/** No world mutations: inspect live catalogs only when an operator requests a report. */
public final class RecipeAudit extends CommandBase {
    public static Map<String, Entry> entries() {
        Map<String, Entry> rows = new TreeMap<String, Entry>();
        for (SourceCrafting recipe : SourceCrafting.ALL) {
            boolean present = CraftingManager.getInstance().getRecipeList().contains(recipe);
            put(rows, recipe.id, "crafting", !present ? "missing" : recipe.enabled() ? "registered" : "disabled",
                    !present ? "Recipe removed or replaced in the native crafting manager" : recipe.enabled() ? "" : "recipes.enableVanillaCropCrates=false");
        }
        for (FoundationRecipe recipe : Recipes.REGISTRY.entries().values()) {
            ItemStack output = new ItemStack(Content.item(recipe.output), recipe.count);
            boolean present = false;
            for (IRecipe nativeRecipe : (List<IRecipe>) CraftingManager.getInstance().getRecipeList()) {
                if (Stacks.same(output, nativeRecipe.getRecipeOutput()) && Stacks.size(output) == Stacks.size(nativeRecipe.getRecipeOutput())) { present = true; break; }
            }
            put(rows, recipe.id, "crafting", present ? "registered" : "missing", present ? "" : "No native recipe with the foundation output");
        }
        put(rows, "farmersdelight:cutting_board", "crafting", "registered", "Native bootstrap crafting recipe");
        for (CookingRecipes.Recipe recipe : CookingRecipes.ALL) {
            ItemStack[] input = new ItemStack[6];
            Arrays.fill(input, Stacks.none());
            boolean valid = !Stacks.empty(recipe.output) && recipe.inputs.size() <= 6;
            for (int i = 0; i < recipe.inputs.size() && i < 6; i++) {
                if (recipe.inputs.get(i).isEmpty()) valid = false;
                else input[i] = recipe.inputs.get(i).get(0).copy();
            }
            if (valid && !scripted(recipe.inputs)) valid = recipe.matches(input);
            put(rows, recipe.id, "cooking", valid ? "registered" : "invalid", valid ? "" : "Invalid output, ingredient, or native input assignment");
        }
        for (CuttingRecipes.Recipe recipe : CuttingRecipes.ALL) {
            boolean valid = !recipe.inputs.isEmpty() && !recipe.outputs.isEmpty();
            for (ItemStack output : recipe.outputs) valid &= !Stacks.empty(output);
            List<ItemStack> tools = recipe.tools();
            if (valid && !(recipe.inputs instanceof ScriptRecipes.Ingredient) && !tools.isEmpty())
                valid = CuttingRecipes.find(recipe.inputs.get(0), tools.get(0)) != null;
            put(rows, recipe.id, "cutting", valid ? "registered" : "invalid", valid ? "" : "Invalid input, output, tool or lookup");
        }
        for (GrillingRecipes.Recipe recipe : GrillingRecipes.ALL)
            put(rows, recipe.id, "grilling", !recipe.inputs.isEmpty() && !Stacks.empty(recipe.output) ? "registered" : "invalid", "");
        unavailable(rows, "crafting", SourceCrafting.DEFERRED);
        unavailable(rows, "cooking", CookingRecipes.DEFERRED);
        unavailable(rows, "cutting", CuttingRecipes.DEFERRED);
        unavailable(rows, "heat", GrillingRecipes.DEFERRED);
        for (Map.Entry<String, JsonObject> source : SourceRecipes.all().entrySet()) {
            JsonObject json = source.getValue();
            String type = json.get("type").getAsString();
            if (!type.equals("minecraft:smelting") && !type.equals("minecraft:smoking") && !type.equals("minecraft:blasting")) continue;
            if (GrillingRecipes.DEFERRED.containsKey(source.getKey())) continue;
            ItemStack output = Stacks.resolve(json.get("result").getAsString());
            List<ItemStack> inputs = Stacks.candidates(json.get("ingredient"));
            boolean valid = !Stacks.empty(output) && !inputs.isEmpty();
            for (ItemStack input : inputs) {
                ItemStack actual = FurnaceRecipes.smelting().getSmeltingResult(input);
                valid &= Stacks.same(output, actual) && Stacks.size(output) == Stacks.size(actual);
            }
            put(rows, source.getKey(), "furnace", valid ? "registered" : "conflict",
                    valid ? "Legacy furnace adaptation" : "Native furnace output differs from the reference recipe");
        }
        put(rows, "farmersdelight:netherite_knife_smithing", "smithing",
                UABCompatibility.smithingRegistered ? "registered" : "unavailable",
                UABCompatibility.smithingRegistered ? "UAB native smithing; configured template policy applies" : "Requires enabled netherite, smithing table and compatible UAB smithing API");
        return RecipeDiagnostics.snapshot(rows);
    }
    private static boolean scripted(List<List<ItemStack>> inputs) {
        for (List<ItemStack> input : inputs) if (input instanceof ScriptRecipes.Ingredient) return true;
        return false;
    }
    private static void put(Map<String, Entry> rows, String id, String category, String status, String reason) {
        if (rows.put(id, new Entry(category, status, reason)) != null)
            throw new IllegalStateException("Duplicate native recipe ID: " + id);
    }
    private static void unavailable(Map<String, Entry> rows, String category, Map<String, String> reasons) {
        for (Map.Entry<String, String> reason : reasons.entrySet()) {
            // Disabled crafting entries remain in the native registry with a runtime predicate.
            if (!rows.containsKey(reason.getKey())) put(rows, reason.getKey(), category, "unavailable", reason.getValue());
        }
    }
    public static String json() {
        Map<String, Entry> entries = entries();
        Map<String, Integer> summary = new TreeMap<String, Integer>();
        int failures = 0;
        for (Map.Entry<String, Entry> row : entries.entrySet()) {
            Entry entry = row.getValue();
            String group = row.getKey().split(":")[0] + "/" + entry.category + "/" + entry.status;
            summary.put(group, summary.containsKey(group) ? summary.get(group) + 1 : 1);
            if (entry.status.equals("missing") || entry.status.equals("invalid") || entry.status.equals("conflict")) failures++;
        }
        Map<String, Object> report = new LinkedHashMap<String, Object>();
        report.put("minecraft", "1.7.10"); report.put("modVersion", BuildVersion.VERSION);
        report.put("scriptRevision", ScriptRecipes.revision());
        report.put("validationFailures", failures); report.put("summary", summary); report.put("recipes", entries);
        report.put("limits", "Registration and representative input checks; not complete survival or viewer/gameplay acceptance. Scripts may intentionally remove recipes. Repeated equivalent furnace routes share one native mapping.");
        return new GsonBuilder().setPrettyPrinting().create().toJson(report);
    }
    @Override public String getCommandName() { return "fdrecipes"; }
    @Override public String getCommandUsage(ICommandSender sender) { return "/fdrecipes"; }
    @Override public int getRequiredPermissionLevel() { return 2; }
    @Override public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        File output = MinecraftServer.getServer().getFile("fd-recipe-audit.json");
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(output), StandardCharsets.UTF_8)) { writer.write(json()); }
        catch (IOException failure) { throw new CommandException("Cannot write recipe audit: " + failure.getMessage()); }
        sender.addChatMessage(new net.minecraft.util.ChatComponentText("Recipe inventory written to " + output.getAbsolutePath()));
    }
}

