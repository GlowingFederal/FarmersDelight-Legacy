package vectorwing.farmersdelight.legacy;
/** The 1.6 Configuration API with the established names, defaults and bounds. */
public final class LegacyConfiguration extends net.minecraftforge.common.Configuration {
    public LegacyConfiguration(java.io.File file) { super(file); }
    public boolean getBoolean(String key,String category,boolean value,String comment) { return get(category,key,value,comment).getBoolean(value); }
    public int getInt(String key,String category,int value,int min,int max,String comment) { return Math.max(min,Math.min(max,get(category,key,value,comment).getInt(value))); }
    public float getFloat(String key,String category,float value,float min,float max,String comment) { return Math.max(min,Math.min(max,(float)get(category,key,(double)value,comment).getDouble(value))); }
    public String[] getStringList(String key,String category,String[] value,String comment) { return get(category,key,value,comment).getStringList(); }
}
