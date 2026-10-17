package vectorwing.farmersdelight.legacy.integration.nei;
import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import vectorwing.farmersdelight.legacy.BuildVersion;
public final class NEIFarmersDelightConfig implements IConfigureNEI {
    public void loadConfig() {
        API.registerRecipeHandler(new FoundationHandler());
        API.registerUsageHandler(new FoundationHandler());
    }
    public String getName() { return "Farmer's Delight Legacy"; }
    public String getVersion() { return BuildVersion.VERSION; }
}
