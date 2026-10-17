package vectorwing.farmersdelight.legacy.api;

/** No hunger replacement, healing timer, FoodStats reflection or optional-mod classes. */
public final class EffectRules {
    private EffectRules() {}
    public static boolean comfort(boolean regeneration, boolean player, float saturation,
                                  boolean naturalRegen, int hunger, boolean hurt) {
        return hurt && !regeneration && (!player || (saturation <= 0.0F
                && !(naturalRegen && hunger >= 18)));
    }
    public static float activityExhaustion(float amount, float allowance) {
        return amount > 0.0F ? Math.max(0.0F, amount - allowance) : amount;
    }
}
