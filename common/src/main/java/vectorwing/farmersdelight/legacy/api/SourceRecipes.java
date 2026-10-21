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

    private static Map<String, JsonObject> load() {
        try (InputStreamReader reader = new InputStreamReader(SourceRecipes.class.getResourceAsStream(
                "/farmersdelight/source_recipes.json"), StandardCharsets.UTF_8)) {
            JsonObject root = new com.google.gson.JsonParser().parse(reader).getAsJsonObject();
            Map<String, JsonObject> result = new LinkedHashMap<String, JsonObject>();
            for (Map.Entry<String, JsonElement> entry : root.entrySet()) result.put(entry.getKey(), entry.getValue().getAsJsonObject());
            return Collections.unmodifiableMap(result);
        } catch (IOException e) { throw new IllegalStateException("Cannot load original recipes", e); }
    }

    public static Map<String, JsonObject> all() { return RECIPES; }

    /** Fortune rolls each output unit independently, including secondary products. */
    public static int roll(int count, double chance, int fortune, double bonus, Random random) {
        int result = 0;
        double probability = Math.min(1, chance + Math.max(0, fortune) * bonus);
        for (int i = 0; i < count; i++) if (random.nextFloat() <= probability) result++;
        return result;
    }

    private SourceRecipes() {}
}
