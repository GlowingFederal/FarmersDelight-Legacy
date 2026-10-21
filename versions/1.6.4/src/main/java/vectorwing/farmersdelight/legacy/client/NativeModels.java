package vectorwing.farmersdelight.legacy.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraftforge.event.ForgeSubscribe;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.event.TextureStitchEvent;
import org.lwjgl.opengl.GL11;
import vectorwing.farmersdelight.legacy.Content;

/** Prepared JSON cuboids rendered through the native 1.7.10 tessellator and block atlas. */
public final class NativeModels implements ISimpleBlockRenderingHandler {
    public final int id = RenderingRegistry.getNextAvailableRenderId();
    private final Map<Block, Quad[][]> models = new LinkedHashMap<Block, Quad[][]>();
    private final Map<Block, Quad[][][]> variants = new LinkedHashMap<Block, Quad[][][]>();
    private final Map<String, Icon> textures = new LinkedHashMap<String, Icon>();
    public NativeModels() {
        add(Content.cuttingBoard, "cutting_board"); add(Content.cookingPot, "cooking_pot"); add(Content.skillet, "skillet");
        add(Content.basket,"basket");
        Quad[][][] pot=new Quad[3][][];
        int support=0;
        for(String name:new String[]{"cooking_pot","cooking_pot_tray","cooking_pot_handle"}) { add(Content.cookingPot,name); pot[support++]=models.get(Content.cookingPot); }
        variants.put(Content.cookingPot,pot);
        for(Block block:Content.MEALS.values()) {
            vectorwing.farmersdelight.legacy.ServingBlock meal=(vectorwing.farmersdelight.legacy.ServingBlock)block;
            Quad[][][] stages=new Quad[5][][];
            for(int remaining=0;remaining<=4;remaining++) {
                String suffix=meal.pie ? remaining==4 ? "" : "_slice"+Math.min(3,4-remaining)
                        : remaining==0 && meal.leftovers ? "_leftover" : "_stage"+Math.min(3,4-remaining);
                add(block,meal.id+suffix); stages[remaining]=models.get(block);
            }
            variants.put(block,stages);
        }
        for(Block block:new Block[]{Content.fullMat,Content.halfMat,Content.canvasRug}) {
            Quad[][][] stages=new Quad[2][][];
            String name=block==Content.canvasRug ? "canvas_rug" : block==Content.halfMat ? "tatami_mat_half" : "tatami_mat_foot";
            add(block,name); stages[0]=models.get(block);
            add(block,block==Content.fullMat ? "tatami_mat_head" : name); stages[1]=models.get(block);
            variants.put(block,stages);
        }
        Quad[][][] tatami=new Quad[3][][];
        int stage=0;
        for(String name:new String[]{"tatami_half","tatami_even","tatami_odd"}) { add(Content.tatami,name); tatami[stage++]=models.get(Content.tatami); }
        variants.put(Content.tatami,tatami);
    }
    private void add(Block block, String name) {
        JsonObject json = new com.google.gson.JsonParser().parse(new InputStreamReader(NativeModels.class.getResourceAsStream(
                "/assets/farmersdelight/models/block/" + name + ".json"), StandardCharsets.UTF_8)).getAsJsonObject();
        Quad[][] rotated = new Quad[block==Content.basket || block==Content.tatami ? 6 : 4][];
        for (int facing = 0; facing < rotated.length; facing++) {
            List<Quad> quads = new ArrayList<Quad>();
            for (JsonElement element : json.getAsJsonArray("elements")) {
                JsonObject box = element.getAsJsonObject(); double[] from = vector(box.getAsJsonArray("from")), to = vector(box.getAsJsonArray("to"));
                for (Map.Entry<String, JsonElement> face : box.getAsJsonObject("faces").entrySet()) {
                    JsonObject data = face.getValue().getAsJsonObject();
                    String texture = data.get("texture").getAsString(); int depth = 0;
                    while (texture.startsWith("#")) {
                        if (++depth > 16) throw new IllegalArgumentException("Cyclic model texture " + name);
                        texture = json.getAsJsonObject("textures").get(texture.substring(1)).getAsString();
                    }
                    texture = texture.replace("farmersdelight:block/", "farmersdelight:").replace("farmersdelight:blocks/", "farmersdelight:").replace("minecraft:block/","minecraft:").replace("minecraft:blocks/","minecraft:"); textures.put(texture, null);
                    double[][] points = face(face.getKey(), from, to);
                    for (double[] point : points) {
                        if (box.has("rotation")) rotate(point, box.getAsJsonObject("rotation"));
                        if(block==Content.basket || block==Content.tatami) {
                            int turns=facing==0 ? 2 : facing==2 ? 1 : facing==3 ? 3 : facing==4 ? 3 : facing==5 ? 1 : 0;
                            for(int turn=0;turn<turns;turn++) {
                                if(facing<4) { double y=point[1]; point[1]=1-point[2]; point[2]=y; }
                                else { double x=point[0]; point[0]=1-point[1]; point[1]=x; }
                            }
                        } else for (int turn = 0; turn < facing; turn++) { double x = point[0]; point[0] = 1 - point[2]; point[2] = x; }
                    }
                    JsonArray sourceUV = data.getAsJsonArray("uv"); double[] uv = sourceUV == null ? new double[] {0, 0, 16, 16} : new double[] {
                            sourceUV.get(0).getAsDouble(), sourceUV.get(1).getAsDouble(), sourceUV.get(2).getAsDouble(), sourceUV.get(3).getAsDouble()};
                    int turns = data.has("rotation") ? data.get("rotation").getAsInt() / 90 : 0;
                    quads.add(new Quad(texture, points, uv, turns));
                }
            }
            rotated[facing] = quads.toArray(new Quad[0]);
        }
        models.put(block, rotated);
    }
    private static double[] vector(JsonArray array) { return new double[] {array.get(0).getAsDouble() / 16, array.get(1).getAsDouble() / 16, array.get(2).getAsDouble() / 16}; }
    private static void rotate(double[] point, JsonObject rotation) {
        double[] pivot = vector(rotation.getAsJsonArray("origin"));
        String axis = rotation.get("axis").getAsString(); int a = axis.equals("x") ? 1 : axis.equals("y") ? 2 : 0, b = (a + 1) % 3;
        double radians = Math.toRadians(rotation.get("angle").getAsDouble()), cos = Math.cos(radians), sin = Math.sin(radians);
        double x = point[a] - pivot[a], y = point[b] - pivot[b];
        double scale = rotation.has("rescale") && rotation.get("rescale").getAsBoolean() ? 1 / cos : 1;
        point[a] = pivot[a] + (x * cos - y * sin) * scale; point[b] = pivot[b] + (x * sin + y * cos) * scale;
    }
    private static double[][] face(String face, double[] f, double[] t) {
        double x = f[0], y = f[1], z = f[2], X = t[0], Y = t[1], Z = t[2];
        if (face.equals("down")) return new double[][] {{x,y,Z},{x,y,z},{X,y,z},{X,y,Z}};
        if (face.equals("up")) return new double[][] {{x,Y,z},{x,Y,Z},{X,Y,Z},{X,Y,z}};
        if (face.equals("north")) return new double[][] {{X,Y,z},{X,y,z},{x,y,z},{x,Y,z}};
        if (face.equals("south")) return new double[][] {{x,Y,Z},{x,y,Z},{X,y,Z},{X,Y,Z}};
        if (face.equals("west")) return new double[][] {{x,Y,z},{x,y,z},{x,y,Z},{x,Y,Z}};
        return new double[][] {{X,Y,Z},{X,y,Z},{X,y,z},{X,Y,z}};
    }
    @ForgeSubscribe public void stitch(TextureStitchEvent.Pre event) {
        if (event.map.getTextureType() == 0) for (String texture : textures.keySet()) textures.put(texture, event.map.registerIcon(texture));
    }
    public int getRenderId() { return id; }
    public boolean shouldRender3DInInventory() { return true; }
    public void renderInventoryBlock(Block block, int metadata, int model, RenderBlocks renderer) {
        GL11.glPushMatrix(); GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        int variant=block instanceof vectorwing.farmersdelight.legacy.ServingBlock ? 4 : 0;
        Tessellator.instance.startDrawingQuads(); render(block, block==Content.basket || block==Content.tatami ? 1 : 0, variant, 0, 0, 0, false); Tessellator.instance.draw(); GL11.glPopMatrix();
    }
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int model, RenderBlocks renderer) {
        Tessellator.instance.setBrightness(block.getMixedBrightnessForBlock(world, x, y, z));
        int meta=world.getBlockMetadata(x,y,z);
        int variant=block==Content.cookingPot ? Math.min(2,(meta>>2)&3) : block instanceof vectorwing.farmersdelight.legacy.MatBlock ? (meta>>2)&1
                : block==Content.tatami ? (meta&8)==0 ? 0 : (meta&1)==0 ? 1 : 2
                : vectorwing.farmersdelight.legacy.ServingBlock.portions(world.getBlockTileEntity(x,y,z));
        render(block, meta & (block==Content.basket || block==Content.tatami ? 7 : 3), variant, x, y, z, true); return true;
    }
    private void render(Block block, int facing, int remaining, int x, int y, int z, boolean world) {
        Quad[][] prepared = variants.containsKey(block) ? variants.get(block)[remaining] : models.get(block); if (prepared == null) return;
        if(facing>=prepared.length) facing=0;
        Tessellator tess = Tessellator.instance;
        for (Quad quad : prepared[facing]) {
            Icon icon = textures.get(quad.texture); if (icon == null) continue;
            if (world) tess.setColorOpaque_F(quad.shade, quad.shade, quad.shade);
            else tess.setNormal(quad.nx, quad.ny, quad.nz);
            for (int i = 0; i < 4; i++) {
                int corner = (i + quad.turns) & 3;
                double u = corner < 2 ? quad.uv[0] : quad.uv[2], v = corner == 0 || corner == 3 ? quad.uv[1] : quad.uv[3];
                tess.addVertexWithUV(x + quad.points[i][0], y + quad.points[i][1], z + quad.points[i][2], icon.getInterpolatedU(u), icon.getInterpolatedV(v));
            }
        }
    }
    private static final class Quad {
        final String texture; final double[][] points; final double[] uv; final int turns; final float nx, ny, nz, shade;
        Quad(String texture, double[][] points, double[] uv, int turns) {
            this.texture = texture; this.points = points; this.uv = uv; this.turns = turns;
            double ax = points[1][0] - points[0][0], ay = points[1][1] - points[0][1], az = points[1][2] - points[0][2];
            double bx = points[2][0] - points[0][0], by = points[2][1] - points[0][1], bz = points[2][2] - points[0][2];
            double X = ay * bz - az * by, Y = az * bx - ax * bz, Z = ax * by - ay * bx, length = Math.sqrt(X*X + Y*Y + Z*Z);
            nx = (float) (X / length); ny = (float) (Y / length); nz = (float) (Z / length);
            shade = 0.6F + 0.4F * Math.max(0, ny) - 0.1F * Math.max(0, -ny) + 0.2F * nz * nz;
        }
    }
}
