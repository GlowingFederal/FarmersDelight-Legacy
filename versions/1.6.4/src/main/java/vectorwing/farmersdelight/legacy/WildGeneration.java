package vectorwing.farmersdelight.legacy;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import cpw.mods.fml.common.IWorldGenerator;
import net.minecraft.world.chunk.IChunkProvider;


/** Use the native shifted population window, whose neighboring chunks are already present. */
public final class WildGeneration implements IWorldGenerator {
    public void generate(Random random,int chunkX,int chunkZ,World world,IChunkProvider generator,IChunkProvider provider) {
        if(!world.provider.isSurfaceWorld() || world.provider.dimensionId!=0) return;
        int baseX=chunkX*16+8,baseZ=chunkZ*16+8;
        int cx=baseX+8,cz=baseZ+8;
        net.minecraft.world.biome.BiomeGenBase biome=world.getBiomeGenForCoords(cx,cz);
        float temperature=biome.temperature;
        java.util.Set<net.minecraftforge.common.BiomeDictionary.Type> types=new java.util.HashSet<net.minecraftforge.common.BiomeDictionary.Type>();
        java.util.Collections.addAll(types,net.minecraftforge.common.BiomeDictionary.getTypesForBiome(biome));
        boolean beach=types.contains(net.minecraftforge.common.BiomeDictionary.Type.BEACH);
        boolean rice=types.contains(net.minecraftforge.common.BiomeDictionary.Type.SWAMP) || types.contains(net.minecraftforge.common.BiomeDictionary.Type.JUNGLE);
        for(java.util.Map.Entry<String,Integer> entry:Settings.wildRarity.entrySet()) {
            String kind=entry.getKey();
            boolean suitable=kind.equals("rice") ? rice : kind.equals("cabbages") || kind.equals("beetroots") ? beach
                    : kind.equals("tomatoes") ? temperature>=1 : kind.equals("potatoes") ? temperature>0 && temperature<=0.3F : temperature>0.3F && temperature<1;
            if(!suitable || entry.getValue()==0 || random.nextInt(entry.getValue())!=0) continue;
            Block crop=kind.equals("rice") ? Content.wildRice : Content.WILD.get(kind);
            if(crop==null) continue;
            int centreX=baseX+random.nextInt(16),centreZ=baseZ+random.nextInt(16),spread=kind.equals("rice") || kind.equals("cabbages") ? 2 : 4;
            for(int i=0;i<64;i++) {
                int x=centreX+random.nextInt(spread+1)-random.nextInt(spread+1),z=centreZ+random.nextInt(spread+1)-random.nextInt(spread+1);
                if(x<baseX || x>=baseX+16 || z<baseZ || z>=baseZ+16) continue;
                int y=world.getHeightValue(x,z);

                if(crop instanceof RicePlant) {
                    y--; if(((RicePlant)crop).canPlant(world,x,y,z)) ((RicePlant)crop).place(world,x,y,z);
                } else if(world.isAirBlock(x,y,z) && crop.canPlaceBlockAt(world,x,y,z))
                    NativeRegistry.set(world,x,y,z,crop,0,2);
            }
        }
    }
}

