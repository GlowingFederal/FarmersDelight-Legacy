package vectorwing.farmersdelight.legacy;
import java.io.*;
import java.util.*;
import com.google.gson.*;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.common.Configuration;
/** Named numeric IDs are saved in configuration; defaults are a frozen release manifest. */
public final class LegacyIds {
    private static final Map<String,Integer> BLOCKS=new LinkedHashMap<String,Integer>(),ITEMS=new LinkedHashMap<String,Integer>();
    public static void load(Configuration config) {
        JsonObject manifest;
        try(Reader reader=new InputStreamReader(LegacyIds.class.getResourceAsStream("/farmersdelight/registry164.json"),"UTF-8")) {
            manifest=new com.google.gson.JsonParser().parse(reader).getAsJsonObject();
        } catch(IOException e) { throw new IllegalStateException("Cannot load numeric registry manifest",e); }
        for(Map.Entry<String,JsonElement> entry:manifest.getAsJsonObject("achievements").entrySet()) {
            int id=config.get("achievement",entry.getKey(),entry.getValue().getAsInt()).getInt();
            if(id<0 || id>Integer.MAX_VALUE-5242880 || occupiedAchievement(id) || ACHIEVEMENTS.containsValue(id)) throw new IllegalStateException("Occupied achievement ID "+id);
            ACHIEVEMENTS.put(entry.getKey(),id);
        }
        Set<Integer> used=new HashSet<Integer>();
        read(config,manifest.getAsJsonObject("blocks"),BLOCKS,"block",used,256,4095);
        read(config,manifest.getAsJsonObject("items"),ITEMS,"item",used,4096,31999);
    }
    private static void read(Configuration config,JsonObject defaults,Map<String,Integer> destination,String category,Set<Integer> used,int min,int max) {
        for(Map.Entry<String,JsonElement> entry:defaults.entrySet()) {
            int id=config.get(category,entry.getKey(),entry.getValue().getAsInt(),"Stable numeric ID. Changing IDs in an existing world requires a save migration; clients and server must agree.").getInt();
            if(id<min || id>max || !used.add(id) || Item.itemsList[id]!=null || category.equals("block") && Block.blocksList[id]!=null)
                throw new IllegalStateException("Occupied or invalid Farmer's Delight "+category+" ID "+id+" for "+entry.getKey()+"; configure a free slot before creating a world");
            destination.put(entry.getKey(),id);
        }
    }
    private static boolean occupiedAchievement(int id) {
        for(Object value:net.minecraft.stats.StatList.allStats) if(((net.minecraft.stats.StatBase)value).statId==5242880+id) return true;
        return false;
    }
    public static int achievement(String name) {
        return ACHIEVEMENTS.get(name);
    }
    private static final Map<String,Integer> ACHIEVEMENTS=new HashMap<String,Integer>();
    public static Block findBlock(String name) { Integer id=BLOCKS.get(name);return id==null ? null : Block.blocksList[id]; }
    public static int block(String name) { Integer id=BLOCKS.get(name); if(id==null) throw new IllegalArgumentException("Unreserved block "+name); return id; }
    public static int item(String name) { Integer id=ITEMS.get(name); if(id==null) throw new IllegalArgumentException("Unreserved item "+name); return id-256; }
    private LegacyIds() {}
}
