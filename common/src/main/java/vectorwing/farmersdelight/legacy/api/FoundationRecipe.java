package vectorwing.farmersdelight.legacy.api;

/** Crafting recipes for this milestone; machine recipe contracts are intentionally separate. */
public final class FoundationRecipe {
    public String id;
    public String output;
    public int count = 1;
    public String[] inputs;
    public String[] pattern;

    public String[] grid() {
        String[] grid = new String[9];
        if (pattern == null) {
            if (inputs.length > grid.length) throw new IllegalArgumentException("Too many crafting inputs: " + id);
            System.arraycopy(inputs, 0, grid, 0, inputs.length);
        } else {
            for (int y = 0; y < pattern.length; y++) for (int x = 0; x < pattern[y].length(); x++) {
                char key = pattern[y].charAt(x);
                if (key != ' ') grid[y * 3 + x] = inputs[key == 'm' ? 0 : 1];
            }
        }
        return grid;
    }
}
