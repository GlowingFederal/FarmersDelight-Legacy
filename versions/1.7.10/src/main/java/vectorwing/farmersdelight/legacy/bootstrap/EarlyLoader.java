package vectorwing.farmersdelight.legacy.bootstrap;
import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.Set;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import com.gtnewhorizon.gtnhmixins.IEarlyMixinLoader;
@IFMLLoadingPlugin.MCVersion("1.7.10")
@IFMLLoadingPlugin.SortingIndex(1002)
public final class EarlyLoader implements IFMLLoadingPlugin, IEarlyMixinLoader {
    public String getMixinConfig() { return "mixins.farmersdelight.json"; }
    public List<String> getMixins(Set<String> loadedCoreMods) { return Collections.singletonList("PlayerExhaustionMixin"); }
    public String[] getASMTransformerClass() { return new String[0]; }
    public String getModContainerClass() { return null; }
    public String getSetupClass() { return null; }
    public void injectData(Map<String, Object> data) {}
    public String getAccessTransformerClass() { return null; }
}
