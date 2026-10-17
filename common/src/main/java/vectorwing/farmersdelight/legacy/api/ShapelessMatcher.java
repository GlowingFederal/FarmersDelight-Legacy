package vectorwing.farmersdelight.legacy.api;

/** Exact occupied-slot matching; broad memberships cannot steal narrow matches. */
public final class ShapelessMatcher {
    public interface Match { boolean accepts(int ingredient, int slot); }
    private ShapelessMatcher() {}
    public static boolean matches(int ingredients, int occupiedSlots, Match predicate) {
        if (ingredients != occupiedSlots || ingredients > 9) return false;
        return search(0, ingredients, 0, predicate);
    }
    private static boolean search(int ingredient, int count, int used, Match predicate) {
        if (ingredient == count) return true;
        for (int slot = 0; slot < count; slot++)
            if ((used & (1 << slot)) == 0 && predicate.accepts(ingredient, slot)
                    && search(ingredient + 1, count, used | (1 << slot), predicate)) return true;
        return false;
    }
}
