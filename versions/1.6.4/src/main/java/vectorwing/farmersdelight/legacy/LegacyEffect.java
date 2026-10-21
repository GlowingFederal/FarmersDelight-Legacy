package vectorwing.farmersdelight.legacy;
import net.minecraft.potion.Potion;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import vectorwing.farmersdelight.legacy.api.EffectRules;
public final class LegacyEffect extends Potion {
    private final boolean healing;
    public LegacyEffect(int id, String name) {
        super(free(id), false, 0);
        healing = name.equals("comfort");
        inventoryIcon=new net.minecraft.util.ResourceLocation("farmersdelight","textures/mob_effect/"+(healing ? "comfort" : "nourished")+"_inventory.png");
        setPotionName("potion.farmersdelight." + name);

    }
    private static int free(int id) {
        if (id <= 0 || id >= potionTypes.length || potionTypes[id] != null)
            throw new IllegalStateException("Farmer's Delight potion ID " + id + " is occupied; configure unused comfortId/nourishmentId on client and server");
        return id;
    }
    private final net.minecraft.util.ResourceLocation inventoryIcon;
    @cpw.mods.fml.relauncher.SideOnly(cpw.mods.fml.relauncher.Side.CLIENT)
    @Override public boolean hasStatusIcon() { return true; }
    @cpw.mods.fml.relauncher.SideOnly(cpw.mods.fml.relauncher.Side.CLIENT)
    @Override public int getStatusIconIndex() {
        net.minecraft.client.Minecraft.getMinecraft().getTextureManager().bindTexture(inventoryIcon); return 0;
    }
    @Override public boolean isReady(int duration, int amplifier) { return healing && duration % 80 == 0; }
    @Override public void performEffect(EntityLivingBase entity, int amplifier) {
        if (entity.worldObj.isRemote || !Settings.comfort) return;
        EntityPlayer player = entity instanceof EntityPlayer ? (EntityPlayer) entity : null;
        if (EffectRules.comfort(entity.isPotionActive(Potion.regeneration), player != null,
                player == null ? 0 : player.getFoodStats().getSaturationLevel(),
                player != null && player.worldObj.getGameRules().getGameRuleBooleanValue("naturalRegeneration"),
                player == null ? 0 : player.getFoodStats().getFoodLevel(), entity.getHealth() < entity.getMaxHealth())) entity.heal(1.0F);
    }
}
