package vectorwing.farmersdelight.legacy;
import java.util.LinkedHashMap;
import java.util.Map;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import com.google.gson.Gson;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemBlock;
import net.minecraft.potion.Potion;
import net.minecraftforge.fml.common.registry.GameRegistry;
import vectorwing.farmersdelight.legacy.api.FoodDefinition;
import net.minecraft.util.ResourceLocation;

public final class Content {
    public static final Map<String, Item> ITEMS = new LinkedHashMap<String, Item>();
    public static final CreativeTabs TAB = new CreativeTabs("farmersdelight") {
        @Override public ItemStack createIcon() { return new ItemStack(ITEMS.get("flint_knife")); }
    };
    public static Potion comfort, nourishment;
    public static Block strawBale;
    public static void register() {
        comfort = new LegacyEffect("comfort"); nourishment = new LegacyEffect("nourishment");
        GameRegistry.findRegistry(Potion.class).register(comfort); GameRegistry.findRegistry(Potion.class).register(nourishment);
        strawBale = new StrawBale();
        strawBale.setRegistryName("farmersdelight", "straw_bale"); GameRegistry.findRegistry(Block.class).register(strawBale); add("straw_bale", new ItemBlock(strawBale));
        for (String id : new String[] {"rice", "rice_panicle", "straw", "canvas", "tree_bark"}) add(id, new Item());
        add("flint_knife", new Knife(Item.ToolMaterial.STONE));
        add("iron_knife", new Knife(Item.ToolMaterial.IRON));
        add("golden_knife", new Knife(Item.ToolMaterial.GOLD));
        add("diamond_knife", new Knife(Item.ToolMaterial.DIAMOND));
        FoodDefinition[] foods;
        try (InputStreamReader reader = new InputStreamReader(Content.class.getResourceAsStream("/farmersdelight/foods.json"), StandardCharsets.UTF_8)) {
            foods = new Gson().fromJson(reader, FoodDefinition[].class);
        } catch (java.io.IOException e) { throw new IllegalStateException("Cannot load food contract", e); }
        for (FoodDefinition definition : foods) add(definition.id, new LegacyFood(definition));
        Ingredients.register();
    }
    private static void add(String id, Item item) {
        if (ITEMS.containsKey(id)) throw new IllegalArgumentException("Duplicate item: " + id);
        item.setTranslationKey("farmersdelight." + id).setCreativeTab(TAB);
        item.setRegistryName("farmersdelight", id); GameRegistry.findRegistry(Item.class).register(item);
        ITEMS.put(id, item);
    }
    public static Item item(String id) {
        Item item = Item.REGISTRY.getObject(new ResourceLocation(id));
        if (item == null || item == net.minecraft.init.Items.AIR) throw new IllegalArgumentException("Missing item: " + id);
        return item;
    }
}
