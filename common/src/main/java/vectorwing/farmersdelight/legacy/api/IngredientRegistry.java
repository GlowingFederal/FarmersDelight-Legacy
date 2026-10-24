package vectorwing.farmersdelight.legacy.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Semantic membership only. Native adapters own stacks, copying, OreDictionary and NBT. */
public final class IngredientRegistry<S> {
    public interface Stacks<S> {
        boolean same(S expected, S actual);
        S copy(S stack);
        List<S> ore(String name);
    }
    private static final class Entry<S> {
        final List<S> exact = new ArrayList<S>();
        final List<String> ores = new ArrayList<String>();
        final List<String> aliases = new ArrayList<String>();
    }
    private final Stacks<S> stacks;
    private final Map<String, Entry<S>> entries = new LinkedHashMap<String, Entry<S>>();
    private boolean frozen;
    public IngredientRegistry(Stacks<S> stacks) { this.stacks = stacks; }
    private Entry<S> edit(String name) {
        if (frozen) throw new IllegalStateException("Ingredient registry is frozen");
        if (!entries.containsKey(name)) entries.put(name, new Entry<S>());
        return entries.get(name);
    }
    public void define(String name) { edit(name); }
    public boolean contains(String name) { return entries.containsKey(name); }
    public void add(String name, S stack) { edit(name).exact.add(stacks.copy(stack)); }
    public void ore(String name, String ore) { edit(name).ores.add(ore); }
    public void alias(String name, String member) { edit(name).aliases.add(member); }
    public void freeze() {
        for (String name : entries.keySet()) collect(name, new HashSet<String>(), new ArrayList<S>());
        frozen = true;
    }
    private void collect(String name, Set<String> path, List<S> result) {
        Entry<S> entry = entries.get(name);
        if (entry == null) throw new IllegalArgumentException("Unknown ingredient alias: " + name);
        if (!path.add(name)) throw new IllegalArgumentException("Cyclic ingredient alias: " + name);
        for (S stack : entry.exact) append(result, stack);
        // Resolve current entries, including late OreRegisterEvent additions; no stale cache.
        for (String ore : entry.ores) for (S stack : stacks.ore(ore)) append(result, stack);
        for (String alias : entry.aliases) collect(alias, path, result);
        path.remove(name);
    }
    private void append(List<S> result, S stack) {
        for (S existing : result) if (stacks.same(existing, stack)) return;
        result.add(stacks.copy(stack));
    }
    public List<S> candidates(String name) {
        List<S> result = new ArrayList<S>();
        collect(name, new HashSet<String>(), result);
        return Collections.unmodifiableList(result);
    }
    public boolean matches(String name, S actual) {
        for (S candidate : candidates(name)) if (stacks.same(candidate, actual)) return true;
        return false;
    }
}
