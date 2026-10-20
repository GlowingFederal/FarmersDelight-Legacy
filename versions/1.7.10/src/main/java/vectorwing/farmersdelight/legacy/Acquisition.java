package vectorwing.farmersdelight.legacy;

import com.google.gson.*;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import net.minecraft.item.ItemStack;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;
import cpw.mods.fml.common.registry.VillagerRegistry;
import net.minecraft.village.MerchantRecipe;

public final class Acquisition {
    private static JsonObject tables;
    /** A zero-weight category marker preserves all existing native loot weights and rolls. */
    public static final class Marker extends WeightedRandomChestContent {
        final String table;
        Marker(String table) { super(new ItemStack(Items.stick),0,0,0); this.table=table; }
    }
    public static void register() {
        try(InputStreamReader reader=new InputStreamReader(Acquisition.class.getResourceAsStream("/farmersdelight/source_loot.json"),StandardCharsets.UTF_8)) {
            tables=new Gson().fromJson(reader,JsonObject.class);
        } catch(java.io.IOException e) { throw new IllegalStateException("Cannot load source chest loot",e); }
        if(Settings.chestLoot) {
            ChestGenHooks.getInfo(ChestGenHooks.MINESHAFT_CORRIDOR).addItem(new Marker("fd_abandoned_mineshaft"));
            ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST).addItem(new Marker("fd_simple_dungeon"));
        }
        if(Settings.farmerTrades) VillagerRegistry.instance().registerVillageTradeHandler(0,(villager,offers,random) -> {
            String[] crops={"onion","tomato","cabbage","rice"}; int[] counts={26,26,16,20};
            for(int i=0;i<crops.length;i++) {
                MerchantRecipe offer=new MerchantRecipe(new ItemStack(Content.ITEMS.get(crops[i]),counts[i]),new ItemStack(Items.emerald));
                offer.func_82783_a(9); offers.add(offer);
            }
        });
    }
    public static void extra(Random random,WeightedRandomChestContent[] contents,IInventory destination) {
        if(!Settings.chestLoot || tables==null) return;
        String table=null;
        for(WeightedRandomChestContent content:contents) if(content instanceof Marker) { table=((Marker)content).table; break; }
        if(table==null) return;
        for(JsonElement poolElement:tables.getAsJsonObject(table).getAsJsonArray("pools")) {
            JsonObject pool=poolElement.getAsJsonObject(); JsonArray entries=pool.getAsJsonArray("entries");
            int rolls=integer(pool.get("rolls"),random),weight=0;
            for(JsonElement entry:entries) weight+=entry.getAsJsonObject().has("weight") ? entry.getAsJsonObject().get("weight").getAsInt() : 1;
            for(int roll=0;roll<rolls && weight>0;roll++) {
                int choice=random.nextInt(weight); JsonObject selected=null;
                for(JsonElement entry:entries) {
                    JsonObject candidate=entry.getAsJsonObject(); choice-=candidate.has("weight") ? candidate.get("weight").getAsInt() : 1;
                    if(choice<0) { selected=candidate; break; }
                }
                if(selected==null || selected.get("type").getAsString().equals("minecraft:empty")) continue;
                ItemStack stack=Stacks.resolve(selected.get("name").getAsString()); if(Stacks.empty(stack)) continue;
                if(selected.has("functions")) for(JsonElement function:selected.getAsJsonArray("functions")) {
                    JsonObject operation=function.getAsJsonObject(); String type=operation.get("function").getAsString();
                    if(type.equals("minecraft:set_count")) Stacks.count(stack,integer(operation.get("count"),random));
                    else if(type.equals("minecraft:set_damage")) {
                        JsonObject range=operation.getAsJsonObject("damage"); float min=range.get("min").getAsFloat(),max=range.get("max").getAsFloat();
                        stack.setItemDamage((int)((1-min-random.nextFloat()*(max-min))*stack.getMaxDamage()));
                    } else throw new IllegalArgumentException("Unsupported source loot function "+type);
                }
                List<Integer> empty=new ArrayList<Integer>();
                for(int slot=0;slot<destination.getSizeInventory();slot++) if(Stacks.empty(destination.getStackInSlot(slot))) empty.add(slot);
                while(!Stacks.empty(stack) && !empty.isEmpty()) {
                    int index=random.nextInt(empty.size()),slot=empty.remove(index);
                    ItemStack placed=Stacks.split(stack,Math.min(stack.getMaxStackSize(),Stacks.size(stack))); destination.setInventorySlotContents(slot,placed);
                }
                destination.markDirty();
            }
        }
    }
    private static int integer(JsonElement value,Random random) {
        if(value.isJsonPrimitive()) return value.getAsInt();
        JsonObject range=value.getAsJsonObject(); int min=(int)range.get("min").getAsDouble(),max=(int)range.get("max").getAsDouble();
        return min+random.nextInt(max-min+1);
    }
}

