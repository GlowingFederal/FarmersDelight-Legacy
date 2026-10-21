package vectorwing.farmersdelight.legacy;

import java.util.List;
import java.util.Random;
import net.minecraft.world.World;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.gen.structure.*;
import net.minecraftforge.fml.common.registry.VillagerRegistry;

public final class VillageCompost extends StructureVillagePieces.Village {
    private String theme="plains";
    public VillageCompost() {}
    private VillageCompost(StructureVillagePieces.Start start,int type,StructureBoundingBox box,net.minecraft.util.EnumFacing facing) {
        super(start,type); boundingBox=box; coordBaseMode=facing;
    }
    public static void register() {
        VillageTemplates.load();
        MapGenStructureIO.registerStructureComponent(VillageCompost.class,"FDCompostHeap");
        if(!Settings.compostHeaps) return;
        VillagerRegistry.instance().registerVillageCreationHandler(new VillagerRegistry.IVillageCreationHandler() {
            public StructureVillagePieces.PieceWeight getVillagePieceWeight(Random random,int size) { return new StructureVillagePieces.PieceWeight(VillageCompost.class,5,1+random.nextInt(2)); }
            public Class<?> getComponentClass() { return VillageCompost.class; }
            public StructureVillagePieces.Village buildComponent(StructureVillagePieces.PieceWeight weight,StructureVillagePieces.Start start,List<StructureComponent> pieces,Random random,int x,int y,int z,net.minecraft.util.EnumFacing face,int type) {
                StructureBoundingBox box=StructureBoundingBox.getComponentToAddBoundingBox(x,y,z,0,0,0,11,7,9,face);
                return canVillageGoDeeper(box) && StructureComponent.findIntersecting(pieces,box)==null ? new VillageCompost(start,type,box,face) : null;
            }
        });
    }
    protected void writeStructureToNBT(NBTTagCompound nbt) { super.writeStructureToNBT(nbt); nbt.setString("FDTheme",theme); }
    protected void readStructureFromNBT(NBTTagCompound nbt) {
        super.readStructureFromNBT(nbt); theme=nbt.hasKey("FDTheme") ? nbt.getString("FDTheme") : "plains";
    }
    public boolean addComponentParts(World world,Random random,StructureBoundingBox bounds) {
        if(field_143015_k<0) {
            field_143015_k=getAverageGroundLevel(world,bounds);
            if(field_143015_k<0) return true;
            boundingBox.offset(0,field_143015_k-boundingBox.minY-1,0);
            net.minecraft.world.biome.BiomeGenBase biome=world.getBiomeGenForCoords(new net.minecraft.util.BlockPos(boundingBox.minX,64,boundingBox.minZ));
            theme=biome instanceof net.minecraft.world.biome.BiomeGenDesert ? "desert"
                    : biome instanceof net.minecraft.world.biome.BiomeGenSavanna ? "savanna"
                    : biome.temperature<0.15F ? "snowy"
                    : biome instanceof net.minecraft.world.biome.BiomeGenTaiga ? "taiga" : "plains";
        }
        VillageTemplates template=VillageTemplates.ALL.get(theme);
        if(template==null) throw new IllegalStateException("Missing persisted compost-pile theme "+theme);
        for(VillageTemplates.Cell cell:template.cells) {
            setBlockState(world,cell.state,cell.x,cell.y,cell.z,bounds);
        }
        return true;
    }
}

