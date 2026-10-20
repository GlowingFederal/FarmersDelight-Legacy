package vectorwing.farmersdelight.legacy;

import java.util.*;
import java.io.InputStream;
import net.minecraft.nbt.*;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import vectorwing.farmersdelight.legacy.api.LegacyNames;
import net.minecraft.block.state.IBlockState;

/** Original five compost-pile layouts, prepared once against the native block palette. */
public final class VillageTemplates {
    public static final Map<String,VillageTemplates> ALL=new LinkedHashMap<String,VillageTemplates>();
    public final List<Cell> cells=new ArrayList<Cell>();
    public final int width,height,depth;
    public static final class Cell {
        public final int x,y,z;
        public final IBlockState state; public final boolean banner;
        Cell(int x,int y,int z,IBlockState state,boolean banner) { this.x=x;this.y=y;this.z=z;this.state=state;this.banner=banner; }
    }
    public static void load() {
        for(String biome:new String[]{"plains","desert","savanna","snowy","taiga"}) {
            try(InputStream input=VillageTemplates.class.getResourceAsStream("/farmersdelight/structures/"+biome+"_compost_pile.nbt")) {
                if(input==null) throw new IllegalStateException("Missing compost-pile layout "+biome);
                ALL.put(biome,new VillageTemplates(CompressedStreamTools.readCompressed(input)));
            } catch(java.io.IOException e) { throw new IllegalStateException("Cannot read compost-pile layout "+biome,e); }
        }
    }
    private static int integer(NBTTagList list,int index) { return Integer.parseInt(list.getStringTagAt(index)); }
    private VillageTemplates(NBTTagCompound nbt) {
        NBTTagList size=nbt.getTagList("size",3); width=integer(size,0); height=integer(size,1); depth=integer(size,2);
        NBTTagList palette=nbt.getTagList("palette",10),blocks=nbt.getTagList("blocks",10);
        for(int i=0;i<blocks.tagCount();i++) {
            NBTTagCompound entry=blocks.getCompoundTagAt(i),definition=palette.getCompoundTagAt(entry.getInteger("state"));
            String name=definition.getString("Name");
            NBTTagCompound properties=definition.getCompoundTag("Properties");
            if(name.equals("minecraft:jigsaw")) {
                name=entry.getCompoundTag("nbt").getString("final_state");
                if(name.equals("minecraft:structure_void") || name.isEmpty()) continue;
                properties=new NBTTagCompound();
            }
            String mapped=equivalent(name); int marker=mapped.indexOf('@'),meta=marker<0 ? 0 : Integer.parseInt(mapped.substring(marker+1));
            if(marker>=0) mapped=mapped.substring(0,marker);
            Block block=Block.getBlockFromName(mapped);
            if(block==null || block==Blocks.AIR && !mapped.equals("minecraft:air")) throw new IllegalStateException("Unresolved village block "+name+" -> "+mapped);
            NBTTagList pos=entry.getTagList("pos",3); int x=integer(pos,0),y=integer(pos,1),z=integer(pos,2);
            IBlockState state=block.getStateFromMeta(meta);
            for(net.minecraft.block.properties.IProperty<?> property:state.getPropertyKeys()) {
                String key=property.getName(),value=properties.getString(key);
                if(key.equals("axis") && name.endsWith("_wood")) continue;
                if(key.equals("half") && properties.hasKey("type")) value=properties.getString("type");
                if(!value.isEmpty()) state=apply(state,property,value);
            }
            cells.add(new Cell(x,y,z,state,name.equals("minecraft:brown_wall_banner")));
        }
    }
    private static <T extends Comparable<T>> IBlockState apply(IBlockState state,net.minecraft.block.properties.IProperty<T> property,String value) {
        com.google.common.base.Optional<T> parsed=property.parseValue(value); return parsed.isPresent() ? state.withProperty(property,parsed.get()) : state;
    }
    private static String equivalent(String source) {
        String name=source.replace("minecraft:stripped_","minecraft:");
        if(name.equals("minecraft:smooth_sandstone") || name.equals("minecraft:cut_sandstone")) return "minecraft:sandstone@2";
        if(name.equals("minecraft:smooth_sandstone_slab")) return "minecraft:stone_slab@1";
        if(name.equals("minecraft:sandstone_wall")) return "minecraft:cobblestone_wall";
        if(name.equals("minecraft:lantern")) return "minecraft:glowstone";
        if(name.equals("minecraft:brown_wall_banner")) return "minecraft:wall_banner";
        if(name.equals("minecraft:acacia_wood")) return "minecraft:log2@12";
        if(name.equals("minecraft:oak_fence")) return "minecraft:fence";
        if(name.equals("minecraft:oak_fence_gate")) return "minecraft:fence_gate";

        if(name.equals("minecraft:coarse_dirt")) return "minecraft:dirt@1";
        if(name.equals("minecraft:podzol")) return "minecraft:dirt@2";
        return LegacyNames.ITEMS.containsKey(name) ? LegacyNames.ITEMS.get(name) : name;
    }
}

