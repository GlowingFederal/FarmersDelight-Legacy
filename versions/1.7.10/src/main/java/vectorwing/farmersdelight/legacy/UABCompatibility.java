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
        if (!active) {
            org.apache.logging.log4j.LogManager.getLogger(Foundation.MODID).info("UAB compatibility inactive: master switch disabled or etfuturum absent");
            return;
        }
        try {
            // Disabled content remains registered for saves. Read the effective acquisition API,
            // including update master switches. Reflection is confined to this startup snapshot.
            Class<?> availability = Class.forName("ganymedes01.etfuturum.configuration.ContentAvailability");
            Method available = availability.getMethod("available", Object.class);
            Method mobEnabled = null;
            try { mobEnabled = availability.getMethod("mobEnabled", Class.class); }
            catch (NoSuchMethodException failure) { org.apache.logging.log4j.LogManager.getLogger(Foundation.MODID).warn("UAB mob API unavailable; recipe families remain active", failure); }
            Object config = null; Method bool = null;
            try {
                Class<?> configuration = Class.forName("ganymedes01.etfuturum.configuration.UabConfiguration");
                config = configuration.getMethod("get").invoke(null);
                bool = configuration.getMethod("bool", String.class, String.class, boolean.class);
            } catch (ReflectiveOperationException | LinkageError failure) {
                org.apache.logging.log4j.LogManager.getLogger(Foundation.MODID).warn("UAB variant/world-generation configuration API unavailable; ordinary ingredient families remain active", failure);
            }
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
                {"campfire", "campfire"}, {"soul_campfire", "soul_campfire"}, {"bamboo", "bamboo"},
                {"scaffolding", "scaffolding"}, {"cornflower", "cornflower"}, {"iron_nugget", "nugget_iron"}, {"netherite_ingot", "netherite_ingot"},
                {"turtle_egg", "turtle_egg"}, {"end_stone_bricks", "end_bricks"}, {"end_crystal", "end_crystal"}
            };
            if (!recipes) org.apache.logging.log4j.LogManager.getLogger(Foundation.MODID).info("UAB recipe families disabled by compatibility.uab.enableUABRecipes");
            if (recipes) {
                for (Object object : Item.itemRegistry) {
                    Item item = (Item) object;
                    String id = Item.itemRegistry.getNameForObject(item);
                    if (id.startsWith("etfuturum:") && stack(id.substring("etfuturum:".length()), 0, available) != null) {
                        String name = id.substring("etfuturum:".length());
                        if (!name.equals("nether_fungus") && !name.equals("wood_planks") && !name.equals("wood_slab")
                                && !name.equals("crimson_stem") && !name.equals("warped_stem"))
                            ITEMS.put("minecraft:" + name, new ItemStack(item));
                    }
                }
                for (String[] entry : items) add(entry[0], entry[1], 0, available);
                if (enabled(config, bool, "experiments", "enableCrimsonBlocks", true)) add("crimson_fungus", "nether_fungus", 0, available);
                if (enabled(config, bool, "experiments", "enableWarpedBlocks", true)) add("warped_fungus", "nether_fungus", 1, available);
                String[] woods = {"oak", "spruce", "birch", "jungle", "acacia", "dark_oak"};
                for (int i = 0; i < woods.length; i++) {
                    String suffix = i < 4 ? "" : "2";
                    int metadata = i < 4 ? i : i - 4;
                    if (i > 0) {
                        add(woods[i] + "_door", "door_" + woods[i], 0, available);
                        add(woods[i] + "_trapdoor", "trapdoor_" + woods[i], 0, available);
                        add(woods[i] + "_sign", "item_sign_" + woods[i], 0, available);
                    }
                    add("stripped_" + woods[i] + "_log", "log" + suffix + "_stripped", metadata, available);
                    add(woods[i] + "_wood", "bark" + suffix, metadata, available);
                    add("stripped_" + woods[i] + "_wood", "wood" + suffix + "_stripped", metadata, available);
                }
                boolean stripped = enabled(config, bool, "blocksitems", "enableStrippedLogs", true);
                boolean bark = enabled(config, bool, "blocksitems", "enableBarkLogs", true);
                for (String wood : new String[] {"crimson", "warped"}) {
                    String key = wood.equals("crimson") ? "enableCrimsonBlocks" : "enableWarpedBlocks";
                    if (!enabled(config, bool, "experiments", key, true)) continue;
                    add(wood + "_stem", wood + "_stem", 0, available);
                    if (bark) add(wood + "_hyphae", wood + "_stem", 1, available);
                    if (stripped) add("stripped_" + wood + "_stem", wood + "_stem", 2, available);
                    if (stripped && bark) add("stripped_" + wood + "_hyphae", wood + "_stem", 3, available);
                    add(wood + "_planks", "wood_planks", wood.equals("crimson") ? 0 : 1, available);
                    add(wood + "_slab", "wood_slab", wood.equals("crimson") ? 0 : 1, available);
                    for (String part : new String[] {"door", "trapdoor", "sign"}) add(wood + "_" + part, wood + "_" + part, 0, available);
                }
                String[] laterWoods = {"mangrove", "cherry", "bamboo"};
                for (int i = 0; i < laterWoods.length; i++) {
                    String source = i == 0 ? "experiments" : "blocksitems";
                    String option = i == 0 ? "enableMangroveBlocks" : i == 1 ? "enableCherryBlocks" : "enableBambooBlocks";
                    if (enabled(config, bool, source, option, true)) {
                        add(laterWoods[i] + "_planks", "wood_planks", i + 2, available);
                        add(laterWoods[i] + "_slab", "wood_slab", i + 2, available);
                    }
                }
            }
            for (String name : new String[] {"beetroot_soup", "rabbit_stew", "rabbit_hide", "shulker_shell"}) {
                ItemStack stack = stack(name, 0, available);
                if (stack != null) GAMEPLAY.put(name, stack);
            }
            if (mobDrops && mobEnabled != null) for (String name : new String[] {"Guardian", "Shulker", "Endermite", "GlowSquid", "Rabbit"}) {
                try {
                    Class<?> type = Class.forName("ganymedes01.etfuturum.entities.Entity" + name);
                    if ((Boolean) mobEnabled.invoke(null, type)) MOBS.put(name, type);
                } catch (ReflectiveOperationException | LinkageError | RuntimeException absent) {
                    org.apache.logging.log4j.LogManager.getLogger(Foundation.MODID).warn("UAB mob integration unavailable: " + name, absent);
                }
            }
            if (worldgen) {
                Block candidate = GameRegistry.findBlock("etfuturum", "mud");
                if (candidate != null && stack("mud", 0, available) != null) mud = candidate;
                outerEnd = enabled(config, bool, "experiments", "endDimensionProvider", true);
            }
            org.apache.logging.log4j.LogManager.getLogger(Foundation.MODID).info("UAB compatibility: {} ingredient aliases, {} enabled mob types", ITEMS.size(), MOBS.size());
        } catch (ReflectiveOperationException | LinkageError | RuntimeException failure) {
            ITEMS.clear(); GAMEPLAY.clear(); MOBS.clear(); mud = null; outerEnd = false; active = false;
            org.apache.logging.log4j.LogManager.getLogger(Foundation.MODID).warn("UAB compatibility unavailable: unsupported acquisition API", failure);
        }
    }

    private static boolean enabled(Object config, Method bool, String category, String key, boolean fallback) {
        try { return (Boolean) bool.invoke(config, category, key, fallback); }
        catch (ReflectiveOperationException | RuntimeException failure) {
            org.apache.logging.log4j.LogManager.getLogger(Foundation.MODID).warn("UAB feature check unavailable: " + category + "." + key + "; only this family is disabled", failure);
            return false;
        }
    }
    public static boolean available(ItemStack stack) {
        if (Stacks.empty(stack) || !Item.itemRegistry.getNameForObject(stack.getItem()).startsWith("etfuturum:")) return true;
        for (ItemStack enabled : ITEMS.values()) if (enabled.getItem() == stack.getItem()
                && enabled.getItemDamage() == stack.getItemDamage()) return true;
        return false;
    }
    private static void add(String modern, String registry, int metadata, Method available) throws ReflectiveOperationException {
        ItemStack stack = stack(registry, metadata, available);
        if (stack != null) ITEMS.put("minecraft:" + modern, stack);
        else org.apache.logging.log4j.LogManager.getLogger(Foundation.MODID).info("UAB recipe ingredient {} deferred: etfuturum:{}@{} is absent or disabled by its effective feature settings", modern, registry, metadata);
    }
    private static ItemStack stack(String registry, int metadata, Method available) throws ReflectiveOperationException {
        Item item = GameRegistry.findItem("etfuturum", registry);
        if (item == null) return null;
        ItemStack stack = new ItemStack(item, 1, metadata);
        try { return (Boolean) available.invoke(null, stack) ? stack : null; }
        catch (ReflectiveOperationException | RuntimeException failure) {
            org.apache.logging.log4j.LogManager.getLogger(Foundation.MODID).warn("UAB ingredient " + registry + " deferred: effective availability check failed", failure);
            return null;
        }
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
    /** Registered saved identities are independent of whether their original survival inputs exist. */
    public static boolean foodAvailable(String id) {
        if (id.equals("farmersdelight:sweet_berry_cheesecake") || id.equals("farmersdelight:sweet_berry_cheesecake_slice") || id.equals("farmersdelight:sweet_berry_cookie"))
            return !Stacks.empty(resolve("minecraft:sweet_berries"));
        if (id.equals("farmersdelight:honey_cookie")) return !Stacks.empty(resolve("minecraft:honey_bottle"));
        if (id.equals("farmersdelight:honey_glazed_ham") || id.equals("farmersdelight:honey_glazed_ham_block"))
            return !Stacks.empty(resolve("minecraft:honey_bottle")) && !Stacks.empty(resolve("minecraft:sweet_berries"));
        if (id.equals("farmersdelight:nether_salad")) return !Stacks.empty(resolve("minecraft:crimson_fungus")) && !Stacks.empty(resolve("minecraft:warped_fungus"));
        return true;
    }
    /** Register into UAB's authoritative smithing processor, retaining its template policy and base NBT. */
    public static void registerRecipes() {
        ItemStack material = resolve("minecraft:netherite_ingot"), table = resolve("minecraft:smithing_table");
        org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(Foundation.MODID);
        if (Stacks.empty(material) || Stacks.empty(table)) {
            log.info("UAB smithing family deferred: effectively enabled netherite and smithing table are both required");
            return;
        }
        try {
            Class<?> catalog = Class.forName("ganymedes01.etfuturum.recipes.SmithingTableRecipes");
            Class<?> recipe = Class.forName("ganymedes01.etfuturum.recipes.SmithingTableRecipes$SmithingTableRecipe");
            Object registry = catalog.getMethod("getInstance").invoke(null);
            ItemStack base = new ItemStack(Content.ITEMS.get("diamond_knife"), 1, net.minecraftforge.oredict.OreDictionary.WILDCARD_VALUE);
            ItemStack template = resolve("minecraft:netherite_upgrade_smithing_template");
            Object upgrade = recipe.getConstructor(Object.class, Object.class, ItemStack.class, boolean.class, Object.class)
                    .newInstance(base, material, new ItemStack(Content.ITEMS.get("netherite_knife")), true, Stacks.empty(template) ? null : template);
            catalog.getMethod("addRecipe", recipe).invoke(registry, upgrade);
            log.info("UAB smithing: restored original netherite knife upgrade with native NBT/template handling");
        } catch (ReflectiveOperationException | LinkageError failure) {
            log.warn("UAB smithing family deferred: native smithing API unavailable", failure);
        }
    }
    private UABCompatibility() {}
}
