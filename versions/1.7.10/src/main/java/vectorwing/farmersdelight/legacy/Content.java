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
import cpw.mods.fml.common.registry.GameRegistry;
import vectorwing.farmersdelight.legacy.api.FoodDefinition;


public final class Content {
    public static final Map<String, Item> ITEMS = new LinkedHashMap<String, Item>();
    public static final CreativeTabs TAB = new CreativeTabs("farmersdelight") {
        @Override public Item getTabIconItem() { return ITEMS.get("flint_knife"); }
    };
    public static Potion comfort, nourishment;
    public static Block strawBale, cuttingBoard, cookingPot, stove, skillet;
    public static Block cabbages, tomatoes, onions, riceCrop, riceUpper, wildRice, wildRiceUpper;
    public static Block richSoil, richFarmland, compost, brownColony, redColony;
    public static final Map<String, Block> WILD = new LinkedHashMap<String, Block>();
    public static final Map<String, Block> MEALS = new LinkedHashMap<String, Block>();
    public static Block basket;
    public static Block rope, safetyNet;
    public static Block tatami,fullMat,halfMat,canvasRug;
    public static final Map<String, Block> PACKED = new LinkedHashMap<String, Block>();
    public static final Map<String,Block> SIGNS=new LinkedHashMap<String,Block>();
    public static net.minecraft.enchantment.Enchantment backstabbing;
    public static final Map<String, Block> PANTRIES = new LinkedHashMap<String, Block>();

    public static void register() {
        comfort = new LegacyEffect(Settings.comfortId, "comfort"); nourishment = new LegacyEffect(Settings.nourishmentId, "nourishment");

        strawBale = new StrawBale();
        GameRegistry.registerBlock(strawBale, "straw_bale"); ITEMS.put("straw_bale", Item.getItemFromBlock(strawBale));
        add("rice", new CropSeeds("rice"));
        for (String id : new String[] {"rice_panicle", "straw", "canvas", "tree_bark"}) add(id, new Item());
        add("flint_knife", new Knife(Item.ToolMaterial.STONE));
        add("iron_knife", new Knife(Item.ToolMaterial.IRON));
        add("golden_knife", new Knife(Item.ToolMaterial.GOLD));
        add("diamond_knife", new Knife(Item.ToolMaterial.EMERALD ));
        FoodDefinition[] foods;
        try (InputStreamReader reader = new InputStreamReader(Content.class.getResourceAsStream("/farmersdelight/foods.json"), StandardCharsets.UTF_8)) {
            foods = new Gson().fromJson(reader, FoodDefinition[].class);
        } catch (java.io.IOException e) { throw new IllegalStateException("Cannot load food contract", e); }
        for (FoodDefinition definition : foods) add(definition.id, new LegacyFood(definition));
        cuttingBoard = new CuttingBoard();
        GameRegistry.registerBlock(cuttingBoard, "cutting_board"); ITEMS.put("cutting_board", Item.getItemFromBlock(cuttingBoard));
        GameRegistry.registerTileEntity(CuttingBoardTile.class, "farmersdelight:cutting_board");
        cookingPot = new CookingPot();
        GameRegistry.registerBlock(cookingPot, "cooking_pot"); ITEMS.put("cooking_pot", Item.getItemFromBlock(cookingPot));
        GameRegistry.registerTileEntity(CookingPotTile.class, "farmersdelight:cooking_pot");
        stove = new GrillBlock(true); skillet = new GrillBlock(false);
        GameRegistry.registerBlock(stove, "stove"); ITEMS.put("stove", Item.getItemFromBlock(stove));
        GameRegistry.registerBlock(skillet, SkilletItem.class, "skillet"); ITEMS.put("skillet", Item.getItemFromBlock(skillet));
        ITEMS.get("cooking_pot").setMaxStackSize(1);
        GameRegistry.registerTileEntity(StoveTile.class, "farmersdelight:stove");
        GameRegistry.registerTileEntity(SkilletTile.class, "farmersdelight:skillet");
        cabbages=block("cabbages",new NativeCrop("cabbages"),false);
        tomatoes=block("tomatoes",new NativeCrop("tomatoes"),false);
        onions=block("onions",new NativeCrop("onions"),false);
        riceCrop=block("rice_crop",new RicePlant(false),false);
        riceUpper=block("rice_upper_crop",new RiceUpper(false),false);
        wildRice=block("wild_rice",new RicePlant(true),false);
        wildRiceUpper=block("wild_rice_upper",new RiceUpper(true),false);
        richSoil=block("rich_soil",new SoilBlock(false),true);
        richFarmland=block("rich_soil_farmland",new RichFarmland(),true);
        compost=block("organic_compost",new SoilBlock(true),true);
        brownColony=block("brown_mushroom_colony",new MushroomColony(false),true);
        redColony=block("red_mushroom_colony",new MushroomColony(true),true);
        add("cabbage_seeds",new CropSeeds("cabbage"));
        add("tomato_seeds",new CropSeeds("tomato"));
        add("wild_rice",new CropSeeds("wild_rice"));
        for(String kind:new String[]{"cabbages","tomatoes","onions","carrots","potatoes"})
            WILD.put(kind,block("wild_"+kind,new WildPlant(kind),true));
        GameRegistry.registerTileEntity(RiceTile.class,"farmersdelight:rice");
        for(String id:new String[]{"apple_pie","chocolate_pie","sweet_berry_cheesecake"})
            if(ITEMS.containsKey(id+"_slice")) MEALS.put(id,block(id,new ServingBlock(id,true,id+"_slice"),true));
        for(String id:new String[]{"roast_chicken","stuffed_pumpkin","honey_glazed_ham","shepherds_pie"}) {
            if(!ITEMS.containsKey(id)) continue;
            String blockId=id+"_block";
            MEALS.put(blockId,block(blockId,new ServingBlock(blockId,false,id),true));
            ITEMS.get(blockId).setMaxStackSize(1);
        }
        GameRegistry.registerTileEntity(ServingTile.class,"farmersdelight:serving");
        basket=block("basket",new StorageBlock("basket",true),true);
        for(String wood:new String[]{"oak","spruce","birch","jungle","acacia","dark_oak"})
            PANTRIES.put(wood,block(wood+"_pantry",new StorageBlock(wood+"_pantry",false),true));
        GameRegistry.registerTileEntity(StorageTile.class,"farmersdelight:pantry");
        GameRegistry.registerTileEntity(BasketTile.class,"farmersdelight:basket");
        add("rotten_tomato",new RottenTomatoItem());
        add("horse_feed",new Item().setMaxStackSize(16));
        backstabbing=new Backstabbing();
        net.minecraft.enchantment.Enchantment.addToBookList(backstabbing);
        for(String id:new String[]{"carrot_crate","potato_crate","cabbage_crate","tomato_crate","onion_crate","rice_bag","rice_bale"})
            PACKED.put(id,block(id,new PackedBlock(id),true));
        safetyNet=block("safety_net",new SafetyNet(),true);
        rope=block("rope",new RopeBlock(),false); add("rope",new RopeItem(rope));
        for(String color:new String[]{"","white","orange","magenta","light_blue","yellow","lime","pink","gray","light_gray","cyan","purple","blue","brown","green","red","black"}) {
            CanvasSignBlock standing=new CanvasSignBlock(color,false),wall=new CanvasSignBlock(color,true);
            block(standing.id,standing,false);
            block(standing.id.replace("canvas_sign","canvas_wall_sign"),wall,false);
            SIGNS.put(standing.id,standing); add(standing.id,new CanvasSignItem(standing,wall));
        }
        GameRegistry.registerTileEntity(CanvasSignTile.class,"farmersdelight:canvas_sign");
        tatami=block("tatami",new TatamiSolid(),true);
        halfMat=block("half_tatami_mat",new MatBlock("half_tatami_mat"),true);
        canvasRug=block("canvas_rug",new MatBlock("canvas_rug"),true);
        fullMat=block("full_tatami_mat",new MatBlock("full_tatami_mat"),false); add("full_tatami_mat",new FullMatItem(fullMat));
        Ingredients.register();
    }
    private static Block block(String id,Block block,boolean item) {
        GameRegistry.registerBlock(block,item ? ItemBlock.class : null,id);
        if(item) ITEMS.put(id,Item.getItemFromBlock(block));
        return block;
    }
    private static void add(String id, Item item) {
        if (ITEMS.containsKey(id)) throw new IllegalArgumentException("Duplicate item: " + id);
        item.setUnlocalizedName("farmersdelight." + id).setCreativeTab(TAB);
        item.setTextureName("farmersdelight:" + id); GameRegistry.registerItem(item, id);
        ITEMS.put(id, item);
    }
    public static Item item(String id) {
        Item item = (Item) Item.itemRegistry.getObject(id);
        if (item == null) throw new IllegalArgumentException("Missing item: " + id);
        return item;
    }
}
