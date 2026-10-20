package vectorwing.farmersdelight.legacy;

import java.util.List;
import java.util.Random;
import net.minecraft.world.World;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.gen.structure.*;
import cpw.mods.fml.common.registry.VillagerRegistry;

public final class VillageCompost extends StructureVillagePieces.Village {
    private String theme="plains";
    public VillageCompost() {}
    private VillageCompost(StructureVillagePieces.Start start,int type,StructureBoundingBox box,int facing) {
        super(start,type); boundingBox=box; coordBaseMode=facing;
    }
    public static void register() {
        VillageTemplates.load();
        MapGenStructureIO.func_143031_a(VillageCompost.class,"FDCompostHeap");
        if(!Settings.compostHeaps) return;
        VillagerRegistry.instance().registerVillageCreationHandler(new VillagerRegistry.IVillageCreationHandler() {
            public StructureVillagePieces.PieceWeight getVillagePieceWeight(Random random,int size) { return new StructureVillagePieces.PieceWeight(VillageCompost.class,5,1+random.nextInt(2)); }
            public Class<?> getComponentClass() { return VillageCompost.class; }
            public Object buildComponent(StructureVillagePieces.PieceWeight weight,StructureVillagePieces.Start start,List pieces,Random random,int x,int y,int z,int face,int type) {
                StructureBoundingBox box=StructureBoundingBox.getComponentToAddBoundingBox(x,y,z,0,0,0,11,7,9,face);
                return canVillageGoDeeper(box) && StructureComponent.findIntersecting(pieces,box)==null ? new VillageCompost(start,type,box,face) : null;
            }
        });
    }
    protected void func_143012_a(NBTTagCompound nbt) { super.func_143012_a(nbt); nbt.setString("FDTheme",theme); }
    protected void func_143011_b(NBTTagCompound nbt) {
        super.func_143011_b(nbt); theme=nbt.hasKey("FDTheme") ? nbt.getString("FDTheme") : "plains";
    }
    public boolean addComponentParts(World world,Random random,StructureBoundingBox bounds) {
        if(field_143015_k<0) {
            field_143015_k=getAverageGroundLevel(world,bounds);
            if(field_143015_k<0) return true;
            boundingBox.offset(0,field_143015_k-boundingBox.minY-1,0);
            net.minecraft.world.biome.BiomeGenBase biome=world.getBiomeGenForCoords(boundingBox.minX,boundingBox.minZ);
            theme=biome instanceof net.minecraft.world.biome.BiomeGenDesert ? "desert"
                    : biome instanceof net.minecraft.world.biome.BiomeGenSavanna ? "savanna"
                    : biome.temperature<0.15F ? "snowy"
                    : biome instanceof net.minecraft.world.biome.BiomeGenTaiga ? "taiga" : "plains";
        }
        VillageTemplates template=VillageTemplates.ALL.get(theme);
        if(template==null) throw new IllegalStateException("Missing persisted compost-pile theme "+theme);
        for(VillageTemplates.Cell cell:template.cells) {
            int meta=getMetadataWithOffset(cell.block,cell.meta);
            if(cell.block instanceof net.minecraft.block.BlockLog && (coordBaseMode==1 || coordBaseMode==3)) {
                int axis=cell.meta&12;
                meta=(cell.meta&3)|(axis==4 ? 8 : axis==8 ? 4 : axis);
            }
            placeBlockAtCurrentPosition(world,cell.block,meta,cell.x,cell.y,cell.z,bounds);
        }
        return true;
    }
}

