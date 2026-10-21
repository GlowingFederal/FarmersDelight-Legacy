package vectorwing.farmersdelight.legacy.bootstrap;
import java.util.Map;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
@IFMLLoadingPlugin.MCVersion("1.8.9")
@IFMLLoadingPlugin.TransformerExclusions({"vectorwing.farmersdelight.legacy.bootstrap"})
public final class EarlyMixins implements IFMLLoadingPlugin {
    public EarlyMixins() {
        // Mixin's service interfaces live outside LaunchClassLoader's transformation domain.
        // Expose this embedded runtime to the parent before Mixin installs its exclusions.
        try {
            java.net.URLClassLoader parent=(java.net.URLClassLoader)net.minecraft.launchwrapper.Launch.class.getClassLoader();
            java.lang.reflect.Method add=java.net.URLClassLoader.class.getDeclaredMethod("addURL",java.net.URL.class);
            add.setAccessible(true); add.invoke(parent,EarlyMixins.class.getProtectionDomain().getCodeSource().getLocation());
            net.minecraft.launchwrapper.Launch.classLoader.addClassLoaderExclusion("org.spongepowered.asm.");
        } catch(ReflectiveOperationException e) { throw new IllegalStateException("Cannot initialize the embedded legacy Mixin runtime",e); }
        org.spongepowered.asm.launch.MixinBootstrap.init();
        org.spongepowered.asm.mixin.Mixins.addConfiguration("mixins.farmersdelight.json");
        org.spongepowered.asm.mixin.MixinEnvironment.getDefaultEnvironment().setObfuscationContext("searge");
    }
    public String[] getASMTransformerClass() { return new String[0]; }
    public String getModContainerClass() { return null; }
    public String getSetupClass() { return null; }
    public void injectData(Map<String,Object> data) {}
    public String getAccessTransformerClass() { return null; }
}
