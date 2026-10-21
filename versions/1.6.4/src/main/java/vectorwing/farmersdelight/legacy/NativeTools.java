package vectorwing.farmersdelight.legacy;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import java.util.Map;
import java.util.List;
/** Forge 1.6 exposes registration but no public tool-class lookup. Resolve its exact map once. */
public final class NativeTools {
    private static final Map<Item,List> CLASSES=classes();
    @SuppressWarnings("unchecked") private static Map<Item,List> classes() {
        try { java.lang.reflect.Field field=net.minecraftforge.common.ForgeHooks.class.getDeclaredField("toolClasses");field.setAccessible(true);return (Map<Item,List>)field.get(null); }
        catch(ReflectiveOperationException e) { throw new IllegalStateException("Unsupported Forge 1.6 tool registry",e); }
    }
    public static boolean matches(ItemStack stack,String kind) {
        if(Stacks.empty(stack)) return false;
        List definition=CLASSES.get(stack.getItem());return definition!=null && !definition.isEmpty() && kind.equals(definition.get(0));
    }
    private NativeTools() {}
}
