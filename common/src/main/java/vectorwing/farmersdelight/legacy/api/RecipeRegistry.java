package vectorwing.farmersdelight.legacy.api;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/** Ordered registration transaction shared by native recipe families and viewers. */
public final class RecipeRegistry<R> {
    private final Map<String, R> recipes = new LinkedHashMap<String, R>();
    private boolean frozen;

    public void register(String id, R recipe) {
        if (frozen) throw new IllegalStateException("Recipe registry is frozen");
        if (id == null || !id.matches("[a-z0-9_.-]+:[a-z0-9_./-]+") || recipe == null)
            throw new IllegalArgumentException("Invalid recipe: " + id);
        if (recipes.containsKey(id)) throw new IllegalArgumentException("Duplicate recipe ID: " + id);
        recipes.put(id, recipe);
    }

    public void freeze() { frozen = true; }
    public boolean isFrozen() { return frozen; }
    public Map<String, R> entries() { return Collections.unmodifiableMap(recipes); }
}
