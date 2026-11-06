package vectorwing.farmersdelight.legacy;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;

/** Native 1.12.2 UAB uses Content.available(String), not the 1.7.10 acquisition API. */
public final class UABCompatibility {
    private static boolean enabled = true, recipes = true, initialized;
    private static final Map<String, ItemStack> ITEMS = new HashMap<String, ItemStack>();

    public static void configure(Configuration config) {
        enabled = config.getBoolean("enableUABCompatibility", "compatibility.uab", true, "Integrate with optional Universal Adaptive Backporter. Restart required.");
        recipes = config.getBoolean("enableUABRecipes", "compatibility.uab", true, "Use effectively enabled UAB ingredients in original core and addon recipes. Restart required.");
    }

    public static void initialize() {
        if (initialized) return;
        initialized = true;
        org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(Foundation.MODID);
        if (!Loader.isModLoaded("etfuturum") || !enabled || !recipes) {
            log.info("UAB recipe families inactive: etfuturum absent or compatibility/recipe switch disabled");
            return;
        }
        try {
            Method available = Class.forName("ganymedes01.etfuturum.Content").getMethod("available", String.class);
            for (ResourceLocation id : Item.REGISTRY.getKeys()) {
                if (!id.getNamespace().equals("etfuturum")) continue;
                try {
                    if ((Boolean) available.invoke(null, id.getPath()))
                        ITEMS.put("minecraft:" + id.getPath(), new ItemStack(Item.REGISTRY.getObject(id)));
                    else log.debug("UAB ingredient {} disabled by effective feature/dependency/update settings", id);
                } catch (ReflectiveOperationException | RuntimeException failure) {
                    log.warn("UAB ingredient " + id + " deferred: acquisition check failed", failure);
                }
            }
            log.info("UAB recipe compatibility: {} enabled exact ingredient aliases", ITEMS.size());
        } catch (ReflectiveOperationException | LinkageError failure) {
            log.warn("UAB recipe families unavailable: this build lacks Content.available(String)", failure);
        }
    }

    public static ItemStack resolve(String modern) {
        ItemStack stack = ITEMS.get(modern);
        return stack == null ? ItemStack.EMPTY : stack.copy();
    }

    public static boolean available(ItemStack stack) {
        if (Stacks.empty(stack)) return true;
        ResourceLocation id = stack.getItem().getRegistryName();
        return id == null || !id.getNamespace().equals("etfuturum") || ITEMS.containsKey("minecraft:" + id.getPath());
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
