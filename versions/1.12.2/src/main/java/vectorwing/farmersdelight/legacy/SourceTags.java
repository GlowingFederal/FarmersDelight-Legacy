package vectorwing.farmersdelight.legacy;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

/** Resolves source tags against native items and explicit OreDictionary boundaries. */
public final class SourceTags {
    private static final JsonObject TAGS = new Gson().fromJson(new InputStreamReader(
            SourceTags.class.getResourceAsStream("/farmersdelight/source_tags.json"), StandardCharsets.UTF_8), JsonObject.class);
    private static boolean frozen;
    /** Merge additive upstream item tags during addon init, before native recipes are compiled. */
    public static void addCatalog(Class<?> owner, String resource) {
        if (frozen) throw new IllegalStateException("Tag registration has closed");
        java.io.InputStream stream=owner.getResourceAsStream(resource);
        if(stream==null) throw new IllegalArgumentException("Missing tag catalog: "+resource);
        try (InputStreamReader reader=new InputStreamReader(stream,StandardCharsets.UTF_8)) {
            JsonObject root=new Gson().fromJson(reader,JsonObject.class);
            for(java.util.Map.Entry<String,JsonElement> entry:root.entrySet()) {
                JsonObject incoming=entry.getValue().getAsJsonObject();
                if(incoming.has("replace") && incoming.get("replace").getAsBoolean())
                    throw new IllegalArgumentException("Addon tags must be additive: "+entry.getKey());
                if(!TAGS.has(entry.getKey())) TAGS.add(entry.getKey(),incoming);
                else for(JsonElement value:incoming.getAsJsonArray("values"))
                    TAGS.getAsJsonObject(entry.getKey()).getAsJsonArray("values").add(value);
            }
        } catch(java.io.IOException e) { throw new IllegalStateException("Cannot load addon tags",e); }
    }
    public static void freeze() { frozen=true; }
    public static List<ItemStack> candidates(String tag) { return collect(tag, new HashSet<String>()); }
    private static List<ItemStack> collect(String tag, Set<String> path) {
        if (!path.add(tag)) throw new IllegalArgumentException("Tag cycle: " + tag);
        List<ItemStack> result = new ArrayList<ItemStack>();
        if (Ingredients.REGISTRY.contains(tag)) result.addAll(Ingredients.REGISTRY.candidates(tag));
        if (TAGS.has(tag)) {
            for (JsonElement value : TAGS.getAsJsonObject(tag).getAsJsonArray("values")) {
                String id = value.isJsonPrimitive() ? value.getAsString() : value.getAsJsonObject().get("id").getAsString();
                if (id.startsWith("#")) result.addAll(collect(id.substring(1), path));
                else { ItemStack stack = Stacks.resolve(id); if (!Stacks.empty(stack)) result.add(stack); }
            }
        }
        if (tag.equals("minecraft:planks")) {
            for (int i = 0; i < 6; i++) result.add(new ItemStack(net.minecraft.init.Blocks.PLANKS, 1, i));
        }
        if (tag.equals("minecraft:logs") || tag.equals("minecraft:logs_that_burn")) {
            for (String wood : new String[] {"oak", "spruce", "birch", "jungle", "acacia", "dark_oak"})
                result.add(Stacks.resolve("minecraft:" + wood + "_log"));
        }
        String ore = null;
        if (tag.startsWith("forge:crops/")) {
            String crop = tag.substring("forge:crops/".length()); ore = "crop" + Character.toUpperCase(crop.charAt(0)) + crop.substring(1);
        } else if(tag.startsWith("forge:dyes/")) {
            String color=tag.substring("forge:dyes/".length());
            ItemStack dye=Stacks.resolve("minecraft:"+color+"_dye");
            if(!Stacks.empty(dye)) result.add(dye);
            StringBuilder name=new StringBuilder("dye");
            for(String word:color.split("_")) if(!word.isEmpty()) name.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
            ore=name.toString();
        } else if (tag.equals("forge:ingots/iron")) ore = "ingotIron";
        else if (tag.equals("forge:ingots/gold")) ore = "ingotGold";
        else if (tag.equals("forge:nuggets/iron")) ore = "nuggetIron";
        else if (tag.equals("forge:eggs")) ore = "listAllegg";
        else if (tag.equals("forge:milk")) ore = "listAllmilk";
        else if (tag.equals("forge:tools/knives")) ore = "toolKnife";
        else if (tag.equals("forge:shears")) result.add(new ItemStack(net.minecraft.init.Items.SHEARS));
        if (ore != null) for (ItemStack stack : OreDictionary.getOres(ore)) {
            if (stack.getItemDamage() != OreDictionary.WILDCARD_VALUE) result.add(stack.copy());
        }
        path.remove(tag);
        return result;
    }
    private SourceTags() {}
}
