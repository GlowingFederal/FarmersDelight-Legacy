package vectorwing.farmersdelight.legacy;

import java.util.*;
import java.util.function.*;
import net.minecraft.item.ItemStack;

/** Ordered edits of the actual processor catalogs. Optional engines never own a parallel registry. */
public final class ScriptRecipes {
    private static final Map<String, Processor<?>> PROCESSORS = new LinkedHashMap<String, Processor<?>>();
    private static final List<Edit> PENDING = new ArrayList<Edit>();
    private static int revision;
    public static int revision() { return revision; }

    /** Examples feed recipe viewers; the predicate retains script metadata/NBT/ore conditions. */
    public static final class Ingredient extends AbstractList<ItemStack> {
        private final List<ItemStack> examples;
        private final Predicate<ItemStack> predicate;
        public Ingredient(List<ItemStack> examples, Predicate<ItemStack> predicate) {
            this.examples = Collections.unmodifiableList(new ArrayList<ItemStack>(examples)); this.predicate = predicate;
        }
        public ItemStack get(int index) { return examples.get(index); }
        public int size() { return examples.size(); }
        public boolean matches(ItemStack actual) { return !Stacks.empty(actual) && predicate.test(actual); }
    }

    /** Native stacks are copied by the engine boundary, including all output/container NBT. */
    public static final class Spec {
        public String id, tool = "ingredient", sound = "";
        public List<List<ItemStack>> inputs = new ArrayList<List<ItemStack>>();
        public List<ItemStack> outputs = new ArrayList<ItemStack>(), tools, liquid = Collections.emptyList();
        public List<Double> chances = new ArrayList<Double>();
        public ItemStack container = Stacks.none();
        public int time = 200, temperature = 3;
        public float experience;
        public void validate(String kind) {
            if (id == null || !id.matches("[a-z0-9_.-]+:[a-z0-9_./-]+")) throw new IllegalArgumentException("Use a namespaced recipe ID: " + id);
            int slots = kind.equals("cooking") ? 6 : kind.equals("fermenting") ? 4 : 1;
            if (inputs.size() > slots || inputs.isEmpty() && !kind.equals("fermenting")) throw new IllegalArgumentException("Wrong number of inputs for " + kind);
            for (List<ItemStack> input : inputs) if (input == null || input.isEmpty()) throw new IllegalArgumentException("Empty ingredient: " + id);
            if (outputs.isEmpty() || outputs.size() > (kind.equals("cutting") ? 4 : 1)) throw new IllegalArgumentException("Wrong number of outputs: " + id);
            for (ItemStack output : outputs) if (Stacks.empty(output) || Stacks.size(output) > output.getMaxStackSize()) throw new IllegalArgumentException("Invalid output: " + id);
            if (time <= 0 || !kind.equals("fermenting") && time > 32767 || !Float.isFinite(experience) || experience < 0 || temperature < 1 || temperature > 5) throw new IllegalArgumentException("Invalid time/experience/temperature: " + id);
            if (!Stacks.empty(container) && Stacks.size(container) != 1) throw new IllegalArgumentException("Serving container must have count 1: " + id);
            if (kind.equals("cutting")) {
                if (chances.size() != outputs.size() || tools == null || tools.isEmpty()) throw new IllegalArgumentException("Cutting needs a tool and one chance per output: " + id);
                for (double chance : chances) if (!Double.isFinite(chance) || chance < 0 || chance > 1) throw new IllegalArgumentException("Invalid chance: " + id);
            }
        }
    }

    private static final class Processor<R> {
        final List<R> recipes;
        final Function<Spec, R> create;
        final Function<R, String> id;
        final Function<R, List<ItemStack>> outputs;
        final Runnable changed;
        Processor(List<R> recipes, Function<Spec, R> create, Function<R, String> id, Function<R, List<ItemStack>> outputs, Runnable changed) {
            this.recipes = recipes; this.create = create; this.id = id; this.outputs = outputs; this.changed = changed;
        }
        void apply(Edit edit) {
            if (edit.spec != null) {
                for (R recipe : recipes) if (id.apply(recipe).equals(edit.spec.id)) throw new IllegalArgumentException("Duplicate recipe ID: " + edit.spec.id + "; remove it first");
                R recipe = create.apply(edit.spec);
                recipes.add(recipe); edit.added = recipe;
            } else {
                // Evaluate first, so a failing script predicate cannot partly remove a catalog.
                for (int i = 0; i < recipes.size(); i++) {
                    R recipe = recipes.get(i);
                    if (edit.removeId != null ? id.apply(recipe).equals(edit.removeId) : outputs.apply(recipe).stream().anyMatch(edit.removeOutput))
                        edit.removed.put(i, recipe);
                }
                recipes.removeAll(edit.removed.values());
            }
            changed.run(); revision++;
        }
        @SuppressWarnings("unchecked") void undo(Edit edit) {
            if (edit.added != null) recipes.remove(edit.added);
            else for (Map.Entry<Integer, Object> entry : edit.removed.entrySet()) {
                R previous = (R) entry.getValue();
                // Other mods may add/replace recipes after scripts load. Preserve those edits.
                boolean replaced = false;
                for (R current : recipes) if (id.apply(current).equals(id.apply(previous))) { replaced = true; break; }
                if (!replaced) recipes.add(Math.min(entry.getKey(), recipes.size()), previous);
            }
            changed.run(); revision++;
        }
    }

    public static <R> void register(String kind, List<R> recipes, Function<Spec, R> create, Function<R, String> id, Function<R, List<ItemStack>> outputs, Runnable changed) {
        if (PROCESSORS.containsKey(kind)) throw new IllegalStateException("Duplicate processor: " + kind);
        Processor<R> processor = new Processor<R>(recipes, create, id, outputs, changed);
        PROCESSORS.put(kind, processor);
        for (Iterator<Edit> it = PENDING.iterator(); it.hasNext();) {
            Edit edit = it.next();
            if (edit.kind.equals(kind)) {
                it.remove();
                try { processor.apply(edit); }
                catch (IllegalArgumentException failure) {
                    edit.added = null; edit.removed.clear();
                    org.apache.logging.log4j.LogManager.getLogger(Foundation.MODID).error("Rejected script edit: " + edit.describe(), failure);
                }
            }
        }
    }

    public static final class Edit {
        final String kind, removeId;
        final Spec spec;
        final Predicate<ItemStack> removeOutput;
        Object added;
        final Map<Integer, Object> removed = new TreeMap<Integer, Object>();
        private boolean submitted;
        private Edit(String kind, Spec spec, String removeId, Predicate<ItemStack> removeOutput) {
            this.kind = kind; this.spec = spec; this.removeId = removeId; this.removeOutput = removeOutput;
        }
        public void apply() {
            if (submitted) throw new IllegalStateException("Edit already applied");
            Processor<?> processor = PROCESSORS.get(kind);
            if (processor == null) PENDING.add(this); else processor.apply(this);
            submitted = true;
        }
        public void undo() {
            if (!submitted) return;
            if (!PENDING.remove(this)) PROCESSORS.get(kind).undo(this);
            submitted = false; added = null; removed.clear();
        }
        public String describe() { return "Farmer's Delight " + kind + ": " + (spec != null ? "add " + spec.id : removeId != null ? "remove " + removeId : "remove matching outputs"); }
    }
    public static Edit add(String kind, Spec spec) { spec.validate(kind); return new Edit(kind, spec, null, null); }
    public static Edit remove(String kind, String id) { return new Edit(kind, null, Objects.requireNonNull(id), null); }
    public static Edit removeOutput(String kind, Predicate<ItemStack> output) { return new Edit(kind, null, null, Objects.requireNonNull(output)); }

    public static void initialize() {
        register("cooking", CookingRecipes.ALL,
                s -> new CookingRecipes.Recipe(s.id, s.inputs, s.outputs.get(0), s.container, s.time, s.experience),
                r -> r.id, r -> Collections.singletonList(r.output), () -> {
                    CookingRecipes.BY_ID.clear(); for (CookingRecipes.Recipe r : CookingRecipes.ALL) CookingRecipes.BY_ID.put(r.id, r);
                });
        register("cutting", CuttingRecipes.ALL,
                s -> new CuttingRecipes.Recipe(s.id, s.inputs.get(0), s.outputs, s.chances, s.tool, s.sound, s.tools),
                r -> r.id, r -> r.outputs, CuttingRecipes::reindex);
        register("grilling", GrillingRecipes.ALL,
                s -> new GrillingRecipes.Recipe(s.id, s.inputs.get(0), s.outputs.get(0), s.time),
                r -> r.id, r -> Collections.singletonList(r.output), () -> {});
    }
    private ScriptRecipes() {}
}
