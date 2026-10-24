package vectorwing.farmersdelight.legacy;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;

/** Startup snapshot of UAB acquisition availability; no linked UAB classes or dependency. */
public final class UABCompatibility {
    private static boolean initialized, active, recipes, mobDrops, worldgen, outerEnd;
    private static final Map<String, ItemStack> ITEMS = new HashMap<String, ItemStack>();
    private static final Map<String, ItemStack> GAMEPLAY = new HashMap<String, ItemStack>();
    private static final Map<String, Class<?>> MOBS = new HashMap<String, Class<?>>();
    private static Block mud;

    public static void configure(Configuration config) {
        String category = "compatibility.uab";
        active = config.getBoolean("enableUABCompatibility", category, true, "Automatically integrate with Universal Adaptive Backporter (etfuturum), when installed. Restart required.");
        recipes = config.getBoolean("enableUABRecipes", category, true, "Use enabled UAB ingredients in existing core and addon recipes. Restart required.");
        mobDrops = config.getBoolean("enableUABMobDrops", category, true, "Allow existing addon drops from enabled UAB mobs. Addon drop settings also apply. Restart required.");
        worldgen = config.getBoolean("enableUABWorldgenCompatibility", category, true, "Support UAB mud for rice and outer End islands for End's Delight succulents. Existing generation settings also apply. Restart required.");
    }

    public static void initialize() {
        if (initialized) return;
        initialized = true;
        active &= Loader.isModLoaded("etfuturum");
        if (!active) return;
        try {
            // Disabled content remains registered for saves. Read the effective acquisition API,
            // including update master switches. Reflection is confined to this startup snapshot.
            Class<?> availability = Class.forName("ganymedes01.etfuturum.configuration.ContentAvailability");
            Method available = availability.getMethod("available", Object.class);
            Method mobEnabled = availability.getMethod("mobEnabled", Class.class);
            Class<?> configuration = Class.forName("ganymedes01.etfuturum.configuration.UabConfiguration");
            Object config = configuration.getMethod("get").invoke(null);
            Method bool = configuration.getMethod("bool", String.class, String.class, boolean.class);
            String[][] items = {
                {"mutton", "mutton_raw"}, {"cooked_mutton", "mutton_cooked"},
                {"rabbit", "rabbit_raw"}, {"cooked_rabbit", "rabbit_cooked"},
                {"rabbit_hide", "rabbit_hide"}, {"rabbit_foot", "rabbit_foot"}, {"rabbit_stew", "rabbit_stew"},
                {"beetroot", "beetroot"}, {"beetroot_seeds", "beetroot_seeds"}, {"beetroot_soup", "beetroot_soup"},
                {"sweet_berries", "sweet_berries"}, {"glow_berries", "glow_berries_item"},
                {"honey_bottle", "honey_bottle"}, {"honeycomb", "honeycomb"}, {"glow_ink_sac", "glow_ink_sac"},
                {"chorus_fruit", "chorus_fruit"}, {"popped_chorus_fruit", "chorus_fruit_popped"},
                {"chorus_flower", "chorus_flower"}, {"chorus_plant", "chorus_plant"},
                {"purpur_block", "purpur_block"}, {"purpur_pillar", "purpur_pillar"},
                {"shulker_shell", "shulker_shell"}, {"dragon_breath", "dragon_breath"},
                {"suspicious_stew", "suspicious_stew"}, {"lily_of_the_valley", "lily_of_the_valley"},
                {"wither_rose", "wither_rose"}, {"shroomlight", "shroomlight"}, {"seagrass", "seagrass"},
                {"kelp", "kelp"}, {"dried_kelp", "dried_kelp"}, {"sea_pickle", "sea_pickle"},
                {"iron_nugget", "nugget_iron"}, {"netherite_ingot", "netherite_ingot"},
                {"turtle_egg", "turtle_egg"}, {"end_stone_bricks", "end_bricks"}, {"end_crystal", "end_crystal"}
            };
            if (recipes) {
                for (String[] entry : items) add(entry[0], entry[1], 0, available);
                // The original brewing catalog uses the modern name for native reeds.
                ITEMS.put("minecraft:sugar_cane", new ItemStack(net.minecraft.init.Items.reeds));
                if ((Boolean) bool.invoke(config, "experiments", "enableCrimsonBlocks", true)) add("crimson_fungus", "nether_fungus", 0, available);
                if ((Boolean) bool.invoke(config, "experiments", "enableWarpedBlocks", true)) add("warped_fungus", "nether_fungus", 1, available);
                String[] woods = {"oak", "spruce", "birch", "jungle", "acacia", "dark_oak"};
                for (int i = 0; i < woods.length; i++) {
                    String suffix = i < 4 ? "" : "2";
                    int metadata = i < 4 ? i : i - 4;
                    add("stripped_" + woods[i] + "_log", "log" + suffix + "_stripped", metadata, available);
                    add(woods[i] + "_wood", "bark" + suffix, metadata, available);
                    add("stripped_" + woods[i] + "_wood", "wood" + suffix + "_stripped", metadata, available);
                }
                boolean stripped = (Boolean) bool.invoke(config, "blocksitems", "enableStrippedLogs", true);
                boolean bark = (Boolean) bool.invoke(config, "blocksitems", "enableBarkLogs", true);
                for (String wood : new String[] {"crimson", "warped"}) {
                    String key = wood.equals("crimson") ? "enableCrimsonBlocks" : "enableWarpedBlocks";
                    if (!(Boolean) bool.invoke(config, "experiments", key, true)) continue;
                    add(wood + "_stem", wood + "_stem", 0, available);
                    if (bark) add(wood + "_hyphae", wood + "_stem", 1, available);
                    if (stripped) add("stripped_" + wood + "_stem", wood + "_stem", 2, available);
                    if (stripped && bark) add("stripped_" + wood + "_hyphae", wood + "_stem", 3, available);
                    add(wood + "_planks", "wood_planks", wood.equals("crimson") ? 0 : 1, available);
                    for (String part : new String[] {"door", "trapdoor", "sign"}) add(wood + "_" + part, wood + "_" + part, 0, available);
                }
            }
            for (String name : new String[] {"beetroot_soup", "rabbit_stew", "rabbit_hide", "shulker_shell"}) {
                ItemStack stack = stack(name, 0, available);
                if (stack != null) GAMEPLAY.put(name, stack);
            }
            if (mobDrops) for (String name : new String[] {"Guardian", "Shulker", "Endermite", "GlowSquid", "Rabbit"}) {
                try {
                    Class<?> type = Class.forName("ganymedes01.etfuturum.entities.Entity" + name);
                    if ((Boolean) mobEnabled.invoke(null, type)) MOBS.put(name, type);
                } catch (ClassNotFoundException absent) { /* A missing species has no integration. */ }
            }
            if (worldgen) {
                Block candidate = GameRegistry.findBlock("etfuturum", "mud");
                if (candidate != null && (Boolean) available.invoke(null, candidate)) mud = candidate;
                outerEnd = (Boolean) bool.invoke(config, "experiments", "endDimensionProvider", true);
            }
            org.apache.logging.log4j.LogManager.getLogger(Foundation.MODID).info("UAB compatibility: {} ingredient aliases, {} enabled mob types", ITEMS.size(), MOBS.size());
        } catch (ReflectiveOperationException | LinkageError | RuntimeException failure) {
            ITEMS.clear(); GAMEPLAY.clear(); MOBS.clear(); mud = null; outerEnd = false; active = false;
            org.apache.logging.log4j.LogManager.getLogger(Foundation.MODID).warn("UAB compatibility unavailable: unsupported acquisition API", failure);
        }
    }

    private static void add(String modern, String registry, int metadata, Method available) throws ReflectiveOperationException {
        ItemStack stack = stack(registry, metadata, available);
        if (stack != null) ITEMS.put("minecraft:" + modern, stack);
    }
    private static ItemStack stack(String registry, int metadata, Method available) throws ReflectiveOperationException {
        Item item = GameRegistry.findItem("etfuturum", registry);
        if (item == null) return null;
        ItemStack stack = new ItemStack(item, 1, metadata);
        return (Boolean) available.invoke(null, stack) ? stack : null;
    }
    /** Exact metadata, no global OreDictionary substitutions; callers receive owned copies. */
    public static ItemStack resolve(String modern) {
        ItemStack stack = ITEMS.get(modern);
        return stack == null ? null : stack.copy();
    }
    public static boolean mob(String name) { return MOBS.containsKey(name); }
    public static boolean isMob(Entity entity, String name) {
        Class<?> type = MOBS.get(name);
        return type != null && type.isInstance(entity);
    }
    public static boolean riceGround(Block block) { return mud != null && block == mud; }
    public static boolean outerEnd() { return outerEnd; }
    public static boolean soup(ItemStack actual, String name) {
        ItemStack expected = GAMEPLAY.get(name);
        // These soups have no NBT food variants; retain normal behavior for named stacks.
        return expected != null && !Stacks.empty(actual) && expected.getItem() == actual.getItem()
                && expected.getItemDamage() == actual.getItemDamage();
    }
    public static String soupName(ItemStack stack, String fallback) {
        for (String name : new String[] {"beetroot_soup", "rabbit_stew"}) if (soup(stack, name)) return "minecraft:" + name;
        return fallback;
    }
    public static ItemStack scavenge(Entity entity) {
        String name = isMob(entity, "Rabbit") ? "rabbit_hide" : isMob(entity, "Shulker") ? "shulker_shell" : null;
        ItemStack stack = GAMEPLAY.get(name);
        return stack == null ? null : stack.copy();
    }
    private UABCompatibility() {}
}
