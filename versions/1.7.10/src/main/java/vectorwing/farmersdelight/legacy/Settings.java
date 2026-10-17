package vectorwing.farmersdelight.legacy;
import java.io.File;
import net.minecraftforge.common.config.Configuration;
public final class Settings {
    public static boolean comfort = true, nourishment = true;
    public static int comfortId = 30, nourishmentId = 31;
    public static void load(File file) {
        Configuration config = new Configuration(file);
        config.load();
        comfort = config.getBoolean("enableComfort", "effects", true, "Enable supplemental Comfort healing.");
        nourishment = config.getBoolean("enableNourishment", "effects", true, "Reduce activity exhaustion by at most 0.1 per tick; regeneration costs are preserved.");
        comfortId = config.getInt("comfortId", "effects", 30, 1, net.minecraft.potion.Potion.potionTypes.length - 1, "Unused potion slot; must match client and server.");
        nourishmentId = config.getInt("nourishmentId", "effects", 31, 1, net.minecraft.potion.Potion.potionTypes.length - 1, "Unused potion slot; must match client and server.");
        if (config.hasChanged()) config.save();
    }
}
