package vectorwing.farmersdelight.legacy;
import com.google.gson.*;
import java.util.*;
import net.minecraft.item.ItemStack;
import vectorwing.farmersdelight.legacy.api.SourceRecipes;
/** Startup-only survival reachability for addon-owned ingredients; original recipe data stays unchanged. */
public final class AddonRecipes {
 private static final Map<String,Contract> CONTRACTS=new LinkedHashMap<String,Contract>();
 private static final Set<String> NATIVE=new HashSet<String>(Arrays.asList("minecraft:crafting_shaped","minecraft:crafting_shapeless","minecraft:smelting","minecraft:smoking","minecraft:campfire_cooking","minecraft:blasting","farmersdelight:cooking","farmersdelight:cutting"));
 public static void register(String namespace,String[] acquisitionRoots,String... extraTypes) {
  if(CONTRACTS.containsKey(namespace)) throw new IllegalArgumentException("Duplicate addon contract: "+namespace);
  CONTRACTS.put(namespace,new Contract(namespace,acquisitionRoots,extraTypes));
 }
 /** Declare a real native conversion which is not represented by a recipe JSON, such as cheese aging. */
 public static void processing(String namespace,String input,String output) {
  Contract contract=CONTRACTS.get(namespace);
  if(contract==null || !input.startsWith(namespace+":") || !output.startsWith(namespace+":")) throw new IllegalArgumentException("Invalid native acquisition relation");
  contract.processing.put(output,input);
 }
 public static void resolve() {
  for(Contract contract:CONTRACTS.values()) contract.resolve();
 }
 private static final class Contract {
  final String namespace;
  final Map<String,String> processing=new LinkedHashMap<String,String>();
  final Set<String> reachable=new HashSet<String>(),types=new HashSet<String>(NATIVE);
  Contract(String namespace,String[] roots,String[] extras) { this.namespace=namespace;reachable.addAll(Arrays.asList(roots));types.addAll(Arrays.asList(extras)); }
  boolean available(JsonElement ingredient) {
   if(ingredient==null || ingredient.isJsonNull()) return true;
   if(ingredient.isJsonObject() && ingredient.getAsJsonObject().entrySet().isEmpty()) return true;
   for(ItemStack stack:Stacks.candidates(ingredient)) {
    String id=stack.getItem().getRegistryName().toString();
    if(!id.startsWith(namespace+":") || reachable.contains(id)) return true;
   }
   return false;
  }
  boolean inputs(JsonObject recipe) {
   if(recipe.has("ingredients")) for(JsonElement ingredient:recipe.getAsJsonArray("ingredients")) if(!available(ingredient)) return false;
   if(recipe.has("ingredient") && !available(recipe.get("ingredient"))) return false;
   if(recipe.has("key")) for(Map.Entry<String,JsonElement> key:recipe.getAsJsonObject("key").entrySet()) if(!available(key.getValue())) return false;
   if(recipe.has("liquid") && !available(recipe.get("liquid"))) return false;
   if(recipe.has("container") && !available(recipe.get("container"))) return false;
   return true;
  }
  String item(JsonElement output) { return output.isJsonPrimitive()?output.getAsString():output.getAsJsonObject().get("item").getAsString(); }
  boolean addOutput(JsonElement output) {
   String id=item(output);return id.startsWith(namespace+":") && !Stacks.empty(Stacks.resolve(id)) && reachable.add(id);
  }
  void resolve() {
   Map<String,JsonObject> recipes=new LinkedHashMap<String,JsonObject>();
   for(Map.Entry<String,JsonObject> entry:SourceRecipes.all().entrySet()) if(entry.getKey().startsWith(namespace+":")) recipes.put(entry.getKey(),entry.getValue());
   boolean changed;
   do {
    changed=false;
    for(JsonObject recipe:recipes.values()) if(types.contains(recipe.get("type").getAsString()) && inputs(recipe) && recipe.has("result")) {
     JsonElement result=recipe.get("result");
     if(result.isJsonArray()) { for(JsonElement output:result.getAsJsonArray()) if(!output.getAsJsonObject().has("chance") || output.getAsJsonObject().get("chance").getAsDouble()>0) changed=addOutput(output)||changed; }
     else changed=addOutput(result)||changed;
    }
    for(Map.Entry<String,String> process:processing.entrySet()) if(reachable.contains(process.getValue()) && !Stacks.empty(Stacks.resolve(process.getKey()))) changed=reachable.add(process.getKey())||changed;
   } while(changed);
   for(Map.Entry<String,JsonObject> entry:recipes.entrySet()) {
    String type=entry.getValue().get("type").getAsString();
    if(!types.contains(type)) SourceRecipes.defer(entry.getKey(),"No native dependency/processor for upstream recipe type "+type);
    else if(!inputs(entry.getValue())) SourceRecipes.defer(entry.getKey(),"An original ingredient or serving container has no acquisition on this target; deferred for its real provider (future UAB where applicable)");
   }
  }
 }
 private AddonRecipes() {}
}
