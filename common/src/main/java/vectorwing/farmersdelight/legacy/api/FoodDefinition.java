package vectorwing.farmersdelight.legacy.api;

/** Canonical nutrition and consumption data; the platform supplies native food/effect objects. */
public final class FoodDefinition {
    public String id;
    public int nutrition;
    public float saturation;
    public boolean meat;
    public boolean fast;
    public boolean alwaysEat;
    public String effect;
    public int duration;
    public int amplifier;
    public float chance = 1.0F;
    public String container;
    public int stackSize = 64;
    public String action;
}
