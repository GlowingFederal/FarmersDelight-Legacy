package vectorwing.farmersdelight.legacy.api;

import com.google.gson.JsonObject;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/** Read-only, on-demand inventories supplied by the authoritative native managers. */
public final class RecipeDiagnostics {
    public interface Catalog { Map<String, Entry> inspect(); }
    public static final class Entry {
        public final String category, status, reason;
        public Entry(String category, String status, String reason) {
            this.category = category; this.status = status; this.reason = reason;
        }
    }
    private static final Map<String, Catalog> CATALOGS = new LinkedHashMap<String, Catalog>();
    public static void register(String name, Catalog catalog) {
        if (catalog == null || CATALOGS.containsKey(name)) throw new IllegalArgumentException("Duplicate/null diagnostic catalog: " + name);
        CATALOGS.put(name, catalog);
    }
    public static Map<String, Entry> snapshot(Map<String, Entry> nativeEntries) {
        Map<String, Entry> result = new LinkedHashMap<String, Entry>(nativeEntries);
        for (Catalog catalog : CATALOGS.values()) for (Map.Entry<String, Entry> entry : catalog.inspect().entrySet()) {
            if (result.containsKey(entry.getKey())) throw new IllegalStateException("Duplicate native recipe claim: " + entry.getKey());
            result.put(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, String> entry : SourceRecipes.deferred().entrySet())
            result.put(entry.getKey(), new Entry(type(entry.getKey()), "unavailable", entry.getValue()));
        for (Map.Entry<String, JsonObject> entry : SourceRecipes.expected().entrySet()) if (!result.containsKey(entry.getKey())) {
            String type = type(entry.getKey());
            boolean nativeType = type.startsWith("minecraft:") || type.equals("farmersdelight:cooking") || type.equals("farmersdelight:cutting") || type.equals("brewinandchewin:fermenting");
            result.put(entry.getKey(), new Entry(type, nativeType ? "missing" : "unsupported",
                    nativeType ? "No native manager accounts for this reference recipe" : "No native adapter for external processor " + type));
        }
        return Collections.unmodifiableMap(result);
    }
    private static String type(String id) {
        JsonObject source = SourceRecipes.expected().get(id);
        return source == null ? "native" : source.get("type").getAsString();
    }
    private RecipeDiagnostics() {}
}
