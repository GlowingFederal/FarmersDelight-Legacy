package vectorwing.farmersdelight.legacy.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

/** The original recipe data, indexed once; native adapters decide which prerequisites exist. */
public final class SourceRecipes {
    private static final Map<String, JsonObject> RECIPES = load();
    private static boolean frozen;
    private static final Map<String, String> DEFERRED = new LinkedHashMap<String, String>();

    private static Map<String, JsonObject> load() {
        try (InputStreamReader reader = new InputStreamReader(SourceRecipes.class.getResourceAsStream(
                "/farmersdelight/source_recipes.json"), StandardCharsets.UTF_8)) {
            JsonObject root = new com.google.gson.JsonParser().parse(reader).getAsJsonObject();
            Map<String, JsonObject> result = new LinkedHashMap<String, JsonObject>();
            for (Map.Entry<String, JsonElement> entry : root.entrySet()) result.put(entry.getKey(), entry.getValue().getAsJsonObject());
            return result;
        } catch (IOException e) { throw new IllegalStateException("Cannot load original recipes", e); }
    }

    public static Map<String, JsonObject> all() { return Collections.unmodifiableMap(RECIPES); }

    /** Addon initialization hook. Call during init, before FD compiles recipes in postInit. */
    public static void addCatalog(String namespace, Class<?> owner, String resource) {
        if (frozen) throw new IllegalStateException("Recipe registration has closed");
        java.io.InputStream stream = owner.getResourceAsStream(resource);
        if (stream == null) throw new IllegalArgumentException("Missing recipe catalog: " + resource);
        try (InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            JsonObject root = new com.google.gson.JsonParser().parse(reader).getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : root.entrySet()) {
                if (!entry.getKey().startsWith(namespace + ":") || RECIPES.containsKey(entry.getKey()))
                    throw new IllegalArgumentException("Invalid or duplicate addon recipe: " + entry.getKey());
            }
            for (Map.Entry<String, JsonElement> entry : root.entrySet())
                {
                    JsonObject recipe=entry.getValue().getAsJsonObject();
                    String type=recipe.get("type").getAsString();
                    if(type.indexOf(':')<0) recipe.addProperty("type","minecraft:"+type);
                    RECIPES.put(entry.getKey(),recipe);
                }
        } catch (IOException e) { throw new IllegalStateException("Cannot load addon recipes: " + resource, e); }
    }

    public static void freeze() { frozen = true; }

    /** Defer a recipe whose registered ingredient has no survival acquisition on this target. */
    public static void defer(String id, String reason) {
        if (frozen) throw new IllegalStateException("Recipe registration has closed");
        if (reason == null || reason.isEmpty() || !RECIPES.containsKey(id))
            throw new IllegalArgumentException("Invalid recipe deferral: " + id);
        RECIPES.remove(id);
        DEFERRED.put(id, reason);
    }

    public static Map<String, String> deferred() { return Collections.unmodifiableMap(DEFERRED); }

    /** Fortune rolls each output unit independently, including secondary products. */
    public static int roll(int count, double chance, int fortune, double bonus, Random random) {
        int result = 0;
        double probability = Math.min(1, chance + Math.max(0, fortune) * bonus);
        for (int i = 0; i < count; i++) if (random.nextFloat() <= probability) result++;
        return result;
    }

    private SourceRecipes() {}
}
