package vectorwing.farmersdelight.legacy.api;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/** Native registry names, metadata forms and equivalent legacy wood/flower materials. */
public final class LegacyNames {
    public static final Map<String, String> ITEMS;
    static {
        Map<String, String> names = new LinkedHashMap<String, String>();
        String[][] aliases = {
            {"cod", "fish@0"}, {"salmon", "fish@1"}, {"tropical_fish", "fish@2"}, {"pufferfish", "fish@3"},
            {"cooked_cod", "cooked_fish@0"}, {"cooked_salmon", "cooked_fish@1"},
            {"melon", "melon_block"}, {"melon_slice", "melon"}, {"bricks", "brick_block"},
            {"nether_bricks", "nether_brick"}, {"nether_brick", "netherbrick"},
            {"lily_pad", "waterlily"}, {"grass_block", "grass"}, {"short_grass", "tallgrass@1"},
            {"dandelion", "yellow_flower"}, {"rose", "red_flower@0"}, {"poppy", "red_flower@0"}, {"blue_orchid", "red_flower@1"},
            {"allium", "red_flower@2"}, {"azure_bluet", "red_flower@3"}, {"red_tulip", "red_flower@4"},
            {"orange_tulip", "red_flower@5"}, {"white_tulip", "red_flower@6"},
            {"pink_tulip", "red_flower@7"}, {"oxeye_daisy", "red_flower@8"},
            {"ink_sac", "dye@0"}, {"black_dye", "dye@0"}, {"red_dye", "dye@1"},
            {"green_dye", "dye@2"}, {"brown_dye", "dye@3"}, {"blue_dye", "dye@4"},
            {"purple_dye", "dye@5"}, {"cyan_dye", "dye@6"}, {"light_gray_dye", "dye@7"},
            {"gray_dye", "dye@8"}, {"pink_dye", "dye@9"}, {"lime_dye", "dye@10"},
            {"yellow_dye", "dye@11"}, {"light_blue_dye", "dye@12"}, {"magenta_dye", "dye@13"},
            {"orange_dye", "dye@14"}, {"white_dye", "dye@15"}, {"bone_meal", "dye@15"},
            {"cocoa_beans", "dye@3"}, {"oak_door", "wooden_door"}, {"oak_trapdoor", "trapdoor"},
            {"oak_sign", "sign"}, {"hay_block", "hay_block"}, {"white_wool", "wool@0"},
            {"white_carpet", "carpet@0"}
        };
        for (String[] alias : aliases) names.put("minecraft:" + alias[0], "minecraft:" + alias[1]);
        String[] woods = {"oak", "spruce", "birch", "jungle", "acacia", "dark_oak"};
        for (int i = 0; i < woods.length; i++) {
            names.put("minecraft:" + woods[i] + "_planks", "minecraft:planks@" + i);
            names.put("minecraft:" + woods[i] + "_slab", "minecraft:wooden_slab@" + i);
            names.put("minecraft:" + woods[i] + "_trapdoor", "minecraft:trapdoor");
            names.put("minecraft:" + woods[i] + "_log", "minecraft:" + (i < 4 ? "log@" + i : "log2@" + (i - 4)));
        }
        ITEMS = Collections.unmodifiableMap(names);
    }
    private LegacyNames() {}
}
