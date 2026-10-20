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
        super(start,type); boundingBox=box; setCoordBaseMode(facing);
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
    protected void readStructureFromNBT(NBTTagCompound nbt,net.minecraft.world.gen.structure.template.TemplateManager manager) {
        super.readStructureFromNBT(nbt,manager); theme=nbt.hasKey("FDTheme") ? nbt.getString("FDTheme") : "plains";
    }
    public boolean addComponentParts(World world,Random random,StructureBoundingBox bounds) {
        if(averageGroundLvl<0) {
            averageGroundLvl=getAverageGroundLevel(world,bounds);
            if(averageGroundLvl<0) return true;
            boundingBox.offset(0,averageGroundLvl-boundingBox.minY-1,0);
            net.minecraft.world.biome.Biome biome=world.getBiome(new net.minecraft.util.math.BlockPos(boundingBox.minX,64,boundingBox.minZ));
            theme=biome instanceof net.minecraft.world.biome.BiomeDesert ? "desert"
                    : biome instanceof net.minecraft.world.biome.BiomeSavanna ? "savanna"
                    : biome.getDefaultTemperature()<0.15F ? "snowy"
                    : biome instanceof net.minecraft.world.biome.BiomeTaiga ? "taiga" : "plains";
        }
        VillageTemplates template=VillageTemplates.ALL.get(theme);
        if(template==null) throw new IllegalStateException("Missing persisted compost-pile theme "+theme);
        for(VillageTemplates.Cell cell:template.cells) {
            setBlockState(world,cell.state,cell.x,cell.y,cell.z,bounds);
            if(cell.banner) {
                net.minecraft.util.math.BlockPos pos=new net.minecraft.util.math.BlockPos(getXWithOffset(cell.x,cell.z),getYWithOffset(cell.y),getZWithOffset(cell.x,cell.z));
                if(bounds.isVecInside(pos) && world.getTileEntity(pos) instanceof net.minecraft.tileentity.TileEntityBanner) {
                    net.minecraft.tileentity.TileEntity tile=world.getTileEntity(pos); NBTTagCompound data=tile.writeToNBT(new NBTTagCompound()); data.setInteger("Base",3); tile.readFromNBT(data); tile.markDirty();
                }
            }
        }
        return true;
    }
}

