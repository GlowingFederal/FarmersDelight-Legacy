package vectorwing.farmersdelight.legacy.api;

/** Burn ticks of the reference's explicit fuel items; other FD items have no fuel value. */
public final class FuelRules {
    public static int ticks(String name) {
        if(name==null || !name.startsWith("farmersdelight:")) return -1;
        String path=name.substring("farmersdelight:".length());
        if(path.equals("canvas") || path.equals("tatami")) return 400;
        if(path.equals("basket") || path.endsWith("_pantry") && !path.equals("crimson_pantry") && !path.equals("warped_pantry")) return 300;
        if(path.equals("cutting_board") || path.equals("tree_bark") || path.equals("rope") || path.equals("safety_net") || path.equals("full_tatami_mat") || path.equals("canvas_rug")) return 200;
        if(path.equals("straw") || path.equals("half_tatami_mat")) return 100;
        return 0;
    }
    private FuelRules() {}
}

