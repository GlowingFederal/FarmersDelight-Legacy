package vectorwing.farmersdelight.legacy;

import java.util.*;
import java.io.InputStream;
import net.minecraft.nbt.*;
import net.minecraft.block.Block;

import vectorwing.farmersdelight.legacy.api.LegacyNames;


/** Original five compost-pile layouts, prepared once against the native block palette. */
public final class VillageTemplates {
    public static final Map<String,VillageTemplates> ALL=new LinkedHashMap<String,VillageTemplates>();
    public final List<Cell> cells=new ArrayList<Cell>();
    public final int width,height,depth;
    public static final class Cell {
        public final int x,y,z;
        public final Block block; public final int meta;
        Cell(int x,int y,int z,Block block,int meta) { this.x=x;this.y=y;this.z=z;this.block=block;this.meta=meta; }
    }
    public static void load() {
        for(String biome:new String[]{"plains","desert","savanna","snowy","taiga"}) {
            try(InputStream input=VillageTemplates.class.getResourceAsStream("/farmersdelight/structures/"+biome+"_compost_pile.nbt")) {
                if(input==null) throw new IllegalStateException("Missing compost-pile layout "+biome);
                ALL.put(biome,new VillageTemplates(CompressedStreamTools.readCompressed(input)));
            } catch(java.io.IOException e) { throw new IllegalStateException("Cannot read compost-pile layout "+biome,e); }
        }
    }
    private static int integer(NBTTagList list,int index) { return ((NBTTagInt)list.tagAt(index)).data; }
    private VillageTemplates(NBTTagCompound nbt) {
        NBTTagList size=nbt.getTagList("size"); width=integer(size,0); height=integer(size,1); depth=integer(size,2);
        NBTTagList palette=nbt.getTagList("palette"),blocks=nbt.getTagList("blocks");
        for(int i=0;i<blocks.tagCount();i++) {
            NBTTagCompound entry=(NBTTagCompound)blocks.tagAt(i),definition=(NBTTagCompound)palette.tagAt(entry.getInteger("state"));
            String name=definition.getString("Name");
            NBTTagCompound properties=definition.getCompoundTag("Properties");
            if(name.equals("minecraft:jigsaw")) {
                name=entry.getCompoundTag("nbt").getString("final_state");
                if(name.equals("minecraft:structure_void") || name.isEmpty()) continue;
                properties=new NBTTagCompound();
            }
            String mapped=equivalent(name);
            if(mapped.startsWith("minecraft:log2@")) mapped="minecraft:log@3";
            if(mapped.equals("minecraft:planks@4") || mapped.equals("minecraft:planks@5")) mapped="minecraft:planks@3";
            if(mapped.equals("minecraft:wooden_slab@4") || mapped.equals("minecraft:wooden_slab@5")) mapped="minecraft:wooden_slab@3";
            if(mapped.equals("minecraft:stained_hardened_clay")) mapped="minecraft:hardened_clay";
             int marker=mapped.indexOf('@'),meta=marker<0 ? 0 : Integer.parseInt(mapped.substring(marker+1));
            if(marker>=0) mapped=mapped.substring(0,marker);
            Block block=NativeRegistry.block(mapped);
            if(block==null && !mapped.equals("minecraft:air")) throw new IllegalStateException("Unresolved village block "+name+" -> "+mapped);
            NBTTagList pos=entry.getTagList("pos"); int x=integer(pos,0),y=integer(pos,1),z=integer(pos,2);
            if(block==Block.torchWood) meta=5;
            if(properties.hasKey("composting")) meta=Integer.parseInt(properties.getString("composting"));
            if(properties.hasKey("moisture")) meta=Integer.parseInt(properties.getString("moisture"));
            if(properties.hasKey("age")) meta=Integer.parseInt(properties.getString("age"));
            if(block instanceof net.minecraft.block.BlockLog && !name.endsWith("_wood")) {
                String axis=properties.getString("axis"); meta=(meta&3)|(axis.equals("x") ? 4 : axis.equals("z") ? 8 : 0);
            }
            if((block==Block.woodSingleSlab || block==Block.stoneSingleSlab) && properties.getString("type").equals("top")) meta|=8;
            if(block==Block.trapdoor) {
                String face=properties.getString("facing");
                meta=face.equals("south") ? 0 : face.equals("north") ? 1 : face.equals("east") ? 2 : 3;
                if(properties.getString("open").equals("true")) meta|=4;
                if(properties.getString("half").equals("top")) meta|=8;
            }
            if(block==Block.fenceGate) { meta=horizontal(properties.getString("facing")); if(properties.getString("open").equals("true")) meta|=4; }
            if(block instanceof net.minecraft.block.BlockStairs) {
                String face=properties.getString("facing"); meta=face.equals("east") ? 0 : face.equals("west") ? 1 : face.equals("south") ? 2 : 3;
                if(properties.getString("half").equals("top")) meta|=4;
            }
            if(block instanceof net.minecraft.block.BlockDoor) {
                String face=properties.getString("facing");
                meta=properties.getString("half").equals("upper") ? 8|(properties.getString("hinge").equals("right") ? 1 : 0)
                        : (horizontal(face)+1)&3;
                if(!properties.getString("half").equals("upper") && properties.getString("open").equals("true")) meta|=4;
            }
            cells.add(new Cell(x,y,z,block,meta));
        }
    }
    private static int horizontal(String face) { return face.equals("south") ? 0 : face.equals("west") ? 1 : face.equals("north") ? 2 : 3; }
    private static String equivalent(String source) {
        String name=source.replace("minecraft:stripped_","minecraft:");
        if(name.equals("minecraft:acacia_stairs")) return "minecraft:jungle_stairs";
        if(name.equals("minecraft:smooth_sandstone") || name.equals("minecraft:cut_sandstone")) return "minecraft:sandstone@2";
        if(name.equals("minecraft:smooth_sandstone_slab")) return "minecraft:stone_slab@1";
        if(name.equals("minecraft:sandstone_wall")) return "minecraft:cobblestone_wall";
        if(name.equals("minecraft:lantern")) return "minecraft:glowstone";
        if(name.equals("minecraft:brown_wall_banner")) return "minecraft:wool@12";
        if(name.equals("minecraft:acacia_wood")) return "minecraft:log2@12";
        if(name.equals("minecraft:oak_fence")) return "minecraft:fence";
        if(name.equals("minecraft:oak_fence_gate")) return "minecraft:fence_gate";
        if(name.equals("minecraft:acacia_fence") || name.equals("minecraft:spruce_fence")) return "minecraft:fence";
        if(name.equals("minecraft:acacia_fence_gate") || name.equals("minecraft:spruce_fence_gate")) return "minecraft:fence_gate";
        if(name.equals("minecraft:grass_path")) return "minecraft:gravel";
        if(name.equals("minecraft:coarse_dirt")) return "minecraft:dirt";
        if(name.equals("minecraft:podzol")) return "minecraft:dirt";
        return LegacyNames.ITEMS.containsKey(name) ? LegacyNames.ITEMS.get(name) : name;
    }
}

