import java.io.*;
import java.lang.reflect.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/** Uses Minecraft's real Item/ItemBlock display-name methods, without a GL client. */
public class NativeLookup112 {
 public static void main(String[] args) throws Exception {
  Class.forName("net.minecraft.init.Bootstrap").getMethod("register").invoke(null);
  Class<?> item=Class.forName("net.minecraft.item.Item"), block=Class.forName("net.minecraft.block.Block"), stack=Class.forName("net.minecraft.item.ItemStack");
  Class<?> material=Class.forName("net.minecraft.block.material.Material");
  Constructor<?> blockConstructor=block.getDeclaredConstructor(material);
  blockConstructor.setAccessible(true);
  Class<?> language=Class.forName("net.minecraft.util.text.translation.LanguageMap");
  Method instance=language.getDeclaredMethod("getInstance");instance.setAccessible(true);
  Object lm=instance.invoke(null);
  Field translations=null;
  for(Field f:language.getDeclaredFields()) if(Map.class.isAssignableFrom(f.getType())) {translations=f;break;}
  translations.setAccessible(true);
  @SuppressWarnings("unchecked") Map<String,String> names=(Map<String,String>)translations.get(lm);
  int tests=0;
  for(String path:args) {
   Map<String,String> loaded=new LinkedHashMap<String,String>();
   for(String line:Files.readAllLines(Paths.get(path),StandardCharsets.UTF_8)) {
    int marker=line.indexOf('=');if(marker>0 && !line.startsWith("#"))loaded.put(line.substring(0,marker),line.substring(marker+1));
   }
   names.putAll(loaded);
   for(Map.Entry<String,String> entry:loaded.entrySet()) {
    String key=entry.getKey();if(!key.endsWith(".name") || !(key.startsWith("item.")||key.startsWith("tile.")))continue;
    String id=key.substring(5,key.length()-5);
    Object target;
    if(key.startsWith("tile.")) {
     Object b=blockConstructor.newInstance(material.getField("WOOD").get(null));
     block.getMethod("setTranslationKey",String.class).invoke(b,id);
     target=Class.forName("net.minecraft.item.ItemBlock").getConstructor(block).newInstance(b);
     // Deliberately set a different item key: ItemBlock must use its block key.
     item.getMethod("setTranslationKey",String.class).invoke(target,"audit.wrong_item_key");
    } else {
     target=item.getConstructor().newInstance();item.getMethod("setTranslationKey",String.class).invoke(target,id);
    }
    Object s=stack.getConstructor(item).newInstance(target);
    String actual=(String)item.getMethod("getItemStackDisplayName",stack).invoke(target,s);
    if(!entry.getValue().equals(actual)) throw new AssertionError(key+": expected "+entry.getValue()+" got "+actual);
    tests++;
   }
   System.out.println("NATIVE_LOOKUP_PASS "+path+" "+loaded.size()+" entries");
  }
  System.out.println("NATIVE_DISPLAY_NAMES_PASS "+tests);
 }
}
