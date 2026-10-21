package vectorwing.farmersdelight.legacy;

import com.google.gson.*;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.Achievement;

/** Native achievements retain the reference's individual criteria in persistent player data. */
public final class Progression {
    private static JsonObject source;
    private static final Map<String,Milestone> ALL=new LinkedHashMap<String,Milestone>();
    private static final Map<Item,List<Rule>> INVENTORY=new HashMap<Item,List<Rule>>(),FOOD=new HashMap<Item,List<Rule>>();
    private static final Map<Block,List<Rule>> PLACED=new HashMap<Block,List<Rule>>();
    private static final List<Rule> EFFECTS=new ArrayList<Rule>();
    private static final class Rule {
        final Milestone milestone; final String criterion;
        Rule(Milestone milestone,String criterion) { this.milestone=milestone;this.criterion=criterion; }
    }
    private static final class Milestone {
        final String id; final JsonObject json;
        final Map<String,JsonObject> criteria=new LinkedHashMap<String,JsonObject>();
        final List<List<String>> requirements=new ArrayList<List<String>>();
        Achievement achievement;
        Milestone(String id,JsonObject json) { this.id=id;this.json=json; }
    }
    public static void register() {
        try(InputStreamReader reader=new InputStreamReader(Progression.class.getResourceAsStream("/farmersdelight/source_advancements.json"),StandardCharsets.UTF_8)) {
            source=new Gson().fromJson(reader,JsonObject.class);
        } catch(java.io.IOException e) { throw new IllegalStateException("Cannot load advancement reference",e); }
        Set<Item> produced=new HashSet<Item>();
        for(SourceCrafting recipe:SourceCrafting.ALL) if(recipe.enabled()) produced.add(recipe.output.getItem());
        for(CookingRecipes.Recipe recipe:CookingRecipes.ALL) produced.add(recipe.output.getItem());
        for(Map.Entry<String,JsonElement> entry:source.entrySet()) {
            String id=entry.getKey(); if(id.equals("hit_raider_with_rotten_tomato")) continue;
            Milestone milestone=new Milestone(id,entry.getValue().getAsJsonObject());
            for(Map.Entry<String,JsonElement> criterion:milestone.json.getAsJsonObject("criteria").entrySet()) {
                JsonObject rule=criterion.getValue().getAsJsonObject(); String trigger=rule.get("trigger").getAsString();
                JsonObject conditions=rule.has("conditions") ? rule.getAsJsonObject("conditions") : new JsonObject();
                boolean available=true;
                if(trigger.equals("minecraft:inventory_changed") && conditions.has("items"))
                    available=!Stacks.empty(Stacks.resolve(conditions.getAsJsonArray("items").get(0).getAsJsonObject().get("item").getAsString()));
                if(trigger.equals("minecraft:consume_item")) {
                    String food=conditions.getAsJsonObject("item").get("item").getAsString(); ItemStack stack=Stacks.resolve(food);
                    available=!Stacks.empty(stack);
                    if(id.equals("master_chef") && available) {
                        ItemStack feast=Stacks.resolve(food+"_block");
                        available=produced.contains(stack.getItem()) || !Stacks.empty(feast) && produced.contains(feast.getItem());
                    }
                }
                if(trigger.equals("minecraft:placed_block")) available=block(conditions.get("block").getAsString())!=null;
                if(available) milestone.criteria.put(criterion.getKey(),rule);
            }
            if(milestone.criteria.isEmpty()) continue;
            for(JsonElement group:milestone.json.getAsJsonArray("requirements")) {
                List<String> names=new ArrayList<String>();
                for(JsonElement name:group.getAsJsonArray()) if(milestone.criteria.containsKey(name.getAsString())) names.add(name.getAsString());
                if(!names.isEmpty()) milestone.requirements.add(names);
            }
            ALL.put(id,milestone);
        }
        for(Milestone milestone:ALL.values()) create(milestone);
        net.minecraftforge.common.AchievementPage.registerAchievementPage(new net.minecraftforge.common.AchievementPage("Farmer's Delight",achievements().toArray(new Achievement[0])));
        for(Milestone milestone:ALL.values()) for(Map.Entry<String,JsonObject> criterion:milestone.criteria.entrySet()) {
            JsonObject rule=criterion.getValue(),conditions=rule.getAsJsonObject("conditions");
            String trigger=rule.get("trigger").getAsString(); Rule reference=new Rule(milestone,criterion.getKey());
            if(trigger.equals("minecraft:inventory_changed") && conditions.has("items")) add(INVENTORY,Stacks.resolve(conditions.getAsJsonArray("items").get(0).getAsJsonObject().get("item").getAsString()).getItem(),reference);
            else if(trigger.equals("minecraft:consume_item")) add(FOOD,Stacks.resolve(conditions.getAsJsonObject("item").get("item").getAsString()).getItem(),reference);
            else if(trigger.equals("minecraft:placed_block")) add(PLACED,block(conditions.get("block").getAsString()),reference);
            else if(trigger.equals("minecraft:effects_changed")) EFFECTS.add(reference);
        }
        Progression events=new Progression();
        net.minecraftforge.fml.common.FMLCommonHandler.instance().bus().register(events);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(events);
    }
    private static <T> void add(Map<T,List<Rule>> map,T key,Rule value) {
        List<Rule> list=map.get(key); if(list==null) { list=new ArrayList<Rule>();map.put(key,list); } list.add(value);
    }
    private static List<Achievement> achievements() { List<Achievement> list=new ArrayList<Achievement>();for(Milestone milestone:ALL.values()) list.add(milestone.achievement);return list; }
    private static Achievement create(Milestone milestone) {
        if(milestone.achievement!=null) return milestone.achievement;
        Achievement parent=null;
        String parentId=milestone.json.has("parent") ? milestone.json.get("parent").getAsString() : null;
        while(parentId!=null) {
            String id=parentId.substring(parentId.lastIndexOf('/')+1); Milestone ancestor=ALL.get(id);
            if(ancestor!=null) { parent=create(ancestor);break; }
            JsonObject original=source.getAsJsonObject(id); parentId=original!=null && original.has("parent") ? original.get("parent").getAsString() : null;
        }
        ItemStack icon=Stacks.resolve(milestone.json.getAsJsonObject("display").getAsJsonObject("icon").get("item").getAsString());
        if(Stacks.empty(icon)) icon=new ItemStack(Content.ITEMS.get("roast_chicken"));
        int index=new ArrayList<String>(ALL.keySet()).indexOf(milestone.id);
        milestone.achievement=new Achievement("achievement.farmersdelight."+milestone.id,"farmersdelight."+milestone.id,(index%5)*2-4,(index/5)*2,icon,parent).registerStat();
        if(milestone.json.getAsJsonObject("display").get("frame").getAsString().equals("challenge")) milestone.achievement.setSpecial();
        return milestone.achievement;
    }
    private static Block block(String id) {
        if(id.equals("minecraft:sugar_cane")) id="minecraft:reeds";
        Block block=Block.getBlockFromName(id);
        return block==null || block==net.minecraft.init.Blocks.air && !id.equals("minecraft:air") ? null : block;
    }
    private static NBTTagCompound progress(EntityPlayer player) {
        NBTTagCompound data=player.getEntityData(),persisted=data.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
        if(!data.hasKey(EntityPlayer.PERSISTED_NBT_TAG)) data.setTag(EntityPlayer.PERSISTED_NBT_TAG,persisted);
        NBTTagCompound progress=persisted.getCompoundTag("FarmersDelightProgress");
        if(!persisted.hasKey("FarmersDelightProgress")) persisted.setTag("FarmersDelightProgress",progress);
        return progress;
    }
    private static void mark(EntityPlayer player,List<Rule> rules) {
        if(!(player instanceof EntityPlayerMP) || rules==null) return;
        NBTTagCompound progress=progress(player);
        for(Rule rule:rules) progress.setBoolean(rule.milestone.id+":"+rule.criterion,true);
    }
    private static void award(EntityPlayer player) {
        if(!(player instanceof EntityPlayerMP)) return;
        EntityPlayerMP server=(EntityPlayerMP)player; NBTTagCompound progress=progress(player);
        boolean again;
        do {
            again=false;
            for(Milestone milestone:ALL.values()) {
                if(server.getStatFile().hasAchievementUnlocked(milestone.achievement) || !server.getStatFile().canUnlockAchievement(milestone.achievement)) continue;
                boolean complete=true;
                for(List<String> group:milestone.requirements) {
                    boolean any=false;for(String name:group) any |= progress.getBoolean(milestone.id+":"+name);
                    complete &= any;
                }
                if(!complete) continue;
                server.triggerAchievement(milestone.achievement);
                if(milestone.json.has("rewards") && milestone.json.getAsJsonObject("rewards").has("experience")) server.addExperience(milestone.json.getAsJsonObject("rewards").get("experience").getAsInt());
                again=true;
            }
        } while(again);
    }
    public static void grant(EntityPlayer player,String id,String criterion) {
        if(!(player instanceof EntityPlayerMP) || !ALL.containsKey(id)) return;
        progress(player).setBoolean(id+":"+criterion,true); award(player);
    }
    public static void consumed(EntityPlayer player,ItemStack stack) {
        if(!(player instanceof EntityPlayerMP) || stack==null) return;
        mark(player,FOOD.get(stack.getItem()));
        for(Rule rule:EFFECTS) {
            boolean nourishment=rule.milestone.id.equals("eat_nourishing_food");
            if(player.isPotionActive(nourishment ? Content.nourishment : Content.comfort)) mark(player,Collections.singletonList(rule));
        }
        award(player);
    }
    private static void inventory(EntityPlayer player) {
        if(!(player instanceof EntityPlayerMP)) return;
        progress(player).setBoolean("root:seeds",true);
        for(int slot=0;slot<player.inventory.getSizeInventory();slot++) {
            ItemStack stack=player.inventory.getStackInSlot(slot); if(!Stacks.empty(stack)) mark(player,INVENTORY.get(stack.getItem()));
        }
        award(player);
    }
    @SubscribeEvent public void picked(PlayerEvent.ItemPickupEvent event) { inventory(event.player); }
    @SubscribeEvent public void crafted(PlayerEvent.ItemCraftedEvent event) { inventory(event.player); mark(event.player,INVENTORY.get(event.crafting.getItem())); award(event.player); }
    @SubscribeEvent public void smelted(PlayerEvent.ItemSmeltedEvent event) { inventory(event.player); mark(event.player,INVENTORY.get(event.smelting.getItem())); award(event.player); }
    @SubscribeEvent public void login(PlayerEvent.PlayerLoggedInEvent event) { inventory(event.player); }
    @SubscribeEvent public void place(BlockEvent.PlaceEvent event) { if(!event.world.isRemote) { mark(event.player,PLACED.get(event.state.getBlock()));award(event.player); } }
}

