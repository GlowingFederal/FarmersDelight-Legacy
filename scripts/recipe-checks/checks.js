// Run only in a disposable production server via run.ps1. Invoked at a server tick boundary.
// Tile callbacks use native worlds and can emit container-return items; no test mod is installed.
var is12=Java.type('net.minecraftforge.common.ForgeVersion').getVersion().startsWith('14');
var S=Java.type('vectorwing.farmersdelight.legacy.Stacks'),W=Java.type('net.minecraftforge.common.DimensionManager').getWorld(0);
var checks=0,failures=[],passed=[];
function check(ok,message){checks++;if(!ok)throw new Error(message);}
function each(list,fn){var it=list.iterator();while(it.hasNext())fn(it.next());}
function test(id,fn){try{fn();passed.push(String(id));}catch(e){failures.push({id:String(id),reason:String(e),stack:String(e.stack)});}}
function prop(obj,name,value){var f=obj.getClass().getDeclaredField(name);f.setAccessible(true);f.set(obj,value);}
var Craft=Java.type('vectorwing.farmersdelight.legacy.SourceCrafting'),Pot=Java.type('vectorwing.farmersdelight.legacy.CookingRecipes'),Cut=Java.type('vectorwing.farmersdelight.legacy.CuttingRecipes');
var Basic=Java.type('net.minecraft.inventory.InventoryBasic'),Chest=Java.type('net.minecraft.inventory.ContainerChest'),Grid=Java.type('net.minecraft.inventory.InventoryCrafting');
var container=is12?new Chest(new Basic('player',true,36),new Basic('chest',true,9),null):new Chest(new Basic('player',true,36),new Basic('chest',true,9));
var grid=new Grid(container,3,3);
function fill(cells){for(var i=0;i<9;i++)grid.func_70299_a(i,cells.get(i)==null?S.none():cells.get(i).get(0).func_77946_l());}
function actualCraft(){if(is12)return Java.type('net.minecraft.item.crafting.CraftingManager').func_82787_a(grid,W);return Java.type('net.minecraft.item.crafting.CraftingManager').func_77594_a().func_82787_a(grid,W);}
each(Craft.ALL,function(r){if(r.enabled())test(r.id,function(){fill(r.grid());check(r.func_77569_a(grid,W),'native recipe does not match');var out=actualCraft();check(S.same(out,r.output)&&S.size(out)==S.size(r.output),'crafting manager selects a different result');});});
each(Java.type('vectorwing.farmersdelight.legacy.Recipes').REGISTRY.entries().values(),function(r){if(r.pattern==null)test(String(r.id)+'/named-inputs',function(){
 for(var i=0;i<9;i++)grid.func_70299_a(i,S.none());
 for(var i=0;i<r.inputs.length;i++){var s=Java.type('vectorwing.farmersdelight.legacy.Ingredients').candidates(r.inputs[i]).get(0).func_77946_l();s.func_151001_c('Named ingredient');grid.func_70299_a(i,s);}
 var out=actualCraft();check(S.same(out,S.resolve(r.output))&&S.size(out)==r.count,'renamed ingredients rejected');
});});
each(Pot.ALL,function(r){test(String(r.id)+'/assignment',function(){var input=Java.to([S.none(),S.none(),S.none(),S.none(),S.none(),S.none()],'net.minecraft.item.ItemStack[]');for(var i=0;i<r.inputs.size();i++)input[5-i]=r.inputs.get(i).get(0).func_77946_l();check(r.matches(input),'pot input permutation rejected');check(Pot.find(input)!=null,'pot index');});});
each(Cut.ALL,function(r){test(String(r.id)+'/lookup',function(){check(!r.tools().isEmpty(),'no usable tool');check(Cut.find(r.inputs.get(0),r.tools().get(0))!=null,'cutting lookup');});});
var loader=Java.type(is12?'net.minecraftforge.fml.common.Loader':'cpw.mods.fml.common.Loader');
if(loader.isModLoaded('brewinandchewin')){
 var F=Java.type('com.brewinandchewin.legacy.Fermentation'),Keg=Java.type('com.brewinandchewin.legacy.KegTile');
 each(F.ALL,function(r){test(r.id,function(){var k=new Keg();k.func_145834_a(W);if(is12)k.func_174878_a(new (Java.type('net.minecraft.util.math.BlockPos'))(0,100,0));else{k.field_145851_c=0;k.field_145848_d=100;k.field_145849_e=0;}
  for(var i=0;i<r.ingredients.size();i++)k.func_70299_a(3-i,r.ingredients.get(i).get(0).func_77946_l());if(!r.liquid.isEmpty())k.func_70299_a(4,r.liquid.get(0).func_77946_l());
  check(r.matches(k),'fermentation input permutation');check(String(F.find(k).id)==String(r.id),'fermentation shadowed by another recipe');
  // Select a required temperature and finish one real native tick without waiting 12,000 ticks.
  k.temperature=[0,-10,-6,0,6,10][r.temperature];prop(k,'temperatureTicks',java.lang.Integer.valueOf(20));
  if(is12)k.func_73660_a();else k.func_145845_h();k.progress=r.time-1;
  if(!S.empty(r.container))k.func_70299_a(6,S.copy(r.container,S.size(r.result)));
  if(is12)k.func_73660_a();else k.func_145845_h();
  var out=k.func_70301_a(7);check(S.same(out,r.result)&&S.size(out)==S.size(r.result),'keg did not produce/serve expected output');
  for(var i=0;i<5;i++)check(S.empty(k.func_70301_a(i)),'input not consumed '+i);check(S.empty(k.func_70301_a(6)),'serving container not consumed');
  var restored=new Keg();restored.restore(k.portable());check(S.empty(restored.func_70301_a(5)),'empty keg portable roundtrip');
 });});
}
var inventory=JSON.parse(String(Java.type('vectorwing.farmersdelight.legacy.RecipeAudit').json()));
JSON.stringify({target:is12?'1.12.2':'1.7.10',checks:checks,failures:failures,passed:passed,inventory:inventory});
