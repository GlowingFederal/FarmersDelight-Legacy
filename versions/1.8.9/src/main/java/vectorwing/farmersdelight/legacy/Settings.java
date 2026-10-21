package vectorwing.farmersdelight.legacy;
import java.io.File;
import net.minecraftforge.common.config.Configuration;
public final class Settings {
    public static boolean comfort = true, nourishment = true;
    public static boolean foodTooltips=true,comfortOverlay=true,nourishmentOverlay=true;
    public static double cuttingFortuneBonus = 0.1;
    public static boolean localVanillaCrates=true;
    public static double localFortuneBonus=0.1;
    public static double richSoilBoost = 0.2;
    public static boolean chestLoot=true,farmerTrades=true,compostHeaps=true;
    public static boolean stackSoups=true,allSoups=false,comfortSoups=true,rabbitJump=true,vanillaCrates=true,dispenserTools=true;
    public static final java.util.Set<String> soupItems=new java.util.HashSet<String>();
    public static final java.util.Set<String> darkSignBackgrounds=new java.util.HashSet<String>();
    public static final java.util.Map<String,Integer> wildRarity = new java.util.LinkedHashMap<String,Integer>();
    public static int comfortId = 30, nourishmentId = 31;
    public static int backstabbingId=80;
    public static void load(File file) {
        Configuration config = new Configuration(file);
        config.load();
        foodTooltips=config.getBoolean("foodEffectTooltip","client",true,"Show food effects, pet feeding benefits and portable pot contents.");
        comfortOverlay=config.getBoolean("comfortHealthOverlay","client",true,"Show a heart sheen while Comfort can supply healing.");
        nourishmentOverlay=config.getBoolean("nourishmentHungerOverlay","client",true,"Show gilded hunger icons while Nourishment is active.");
        chestLoot=config.getBoolean("generateChestLoot","world",true,"Add the original loot pools to native mineshaft and dungeon chests.");
        farmerTrades=config.getBoolean("farmersBuyCrops","world",true,"Allow native farmers to buy Farmer's Delight crops.");
        compostHeaps=config.getBoolean("generateVillageCompostHeaps","world",true,"Add adapted compost-pile components to newly generated native villages.");
        stackSoups=config.getBoolean("enableStackableSoups","overrides",true,"Enable safe stacking of selected native SoupItems to 16.");
        allSoups=config.getBoolean("overrideAllSoupItems","overrides",false,"Use soupItems as a deny-list instead of an allow-list.");
        java.util.Collections.addAll(soupItems,config.getStringList("soupItems","overrides",new String[]{"minecraft:mushroom_stew","minecraft:beetroot_soup","minecraft:rabbit_stew"},"Registry IDs of native SoupItems; absent items are ignored."));
        comfortSoups=config.getBoolean("comfortFoodEffects","overrides",true,"Grant the reference Comfort effects when native soups are consumed.");
        rabbitJump=config.getBoolean("rabbitStewJumpBoost","overrides",true,"Grant the reference jump boost when rabbit stew is consumed.");
        vanillaCrates=config.getBoolean("enableVanillaCropCrates","recipes",true,"Enable compression recipes for native vanilla crops.");
        dispenserTools=config.getBoolean("dispenserTools","cuttingBoard",true,"Allow dispenser tools to process a Cutting Board.");
        java.util.Collections.addAll(darkSignBackgrounds,config.getStringList("darkBackgrounds","canvasSigns",new String[]{"gray","purple","blue","brown","green","red","black"},"Canvas sign backgrounds that default to white text."));
        backstabbingId=config.getInt("backstabbingId","enchantments",80,0,255,"Unused enchantment slot; must match client and server.");
        if(net.minecraft.enchantment.Enchantment.getEnchantmentById(backstabbingId)!=null) throw new IllegalStateException("Backstabbing enchantment ID "+backstabbingId+" is already occupied; change enchantments.backstabbingId.");
        richSoilBoost = config.getFloat("growthBoost", "farming", 0.2F, 0, 1, "Chance for rich soil to boost the plant above it on a random tick.");
        for(String crop:new String[]{"cabbages","tomatoes","onions","carrots","potatoes","rice"}) {
            boolean enabled=config.getBoolean(crop,"wildCrops",true,"Generate wild "+crop+" in new surface chunks.");
            int rarity=config.getInt(crop+"Rarity","wildCrops",crop.equals("cabbages") || crop.equals("beetroots") || crop.equals("rice") ? 10 : 8,1,Integer.MAX_VALUE,"Average eligible chunks per wild crop patch.");
            wildRarity.put(crop,enabled ? rarity : 0);
        }
        cuttingFortuneBonus = config.getFloat("fortuneBonus", "cuttingBoard", 0.1F, 0, 1, "Additional chance per Fortune level, rolled independently for every output item.");
        comfort = config.getBoolean("enableComfort", "effects", true, "Enable supplemental Comfort healing.");
        nourishment = config.getBoolean("enableNourishment", "effects", true, "Reduce activity exhaustion by at most 0.1 per tick; regeneration costs are preserved.");
        comfortId = config.getInt("comfortId", "effects", 30, 1, net.minecraft.potion.Potion.potionTypes.length - 1, "Unused potion slot; must match client and server.");
        nourishmentId = config.getInt("nourishmentId", "effects", 31, 1, net.minecraft.potion.Potion.potionTypes.length - 1, "Unused potion slot; must match client and server.");
        localVanillaCrates=vanillaCrates; localFortuneBonus=cuttingFortuneBonus;
        if (config.hasChanged()) config.save();
    }
}
