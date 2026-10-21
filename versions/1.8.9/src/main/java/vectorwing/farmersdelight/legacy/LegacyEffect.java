package vectorwing.farmersdelight.legacy;
import net.minecraft.potion.Potion;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import vectorwing.farmersdelight.legacy.api.EffectRules;
public final class LegacyEffect extends Potion {
    private final boolean healing;
    public LegacyEffect(int id, String name) {
        super(free(id), new net.minecraft.util.ResourceLocation("farmersdelight",name), false, 0);
        healing = name.equals("comfort");
        setPotionName("potion.farmersdelight." + name);

    }
    private static int free(int id) {
        if (id <= 0 || id >= potionTypes.length || potionTypes[id] != null)
            throw new IllegalStateException("Farmer's Delight potion ID " + id + " is occupied; configure unused comfortId/nourishmentId on client and server");
        return id;
    }
    @net.minecraftforge.fml.relauncher.SideOnly(net.minecraftforge.fml.relauncher.Side.CLIENT)
    @Override public void renderInventoryEffect(int x,int y,net.minecraft.potion.PotionEffect effect,net.minecraft.client.Minecraft mc) {
        mc.getTextureManager().bindTexture(new net.minecraft.util.ResourceLocation("farmersdelight","textures/mob_effect/"+(healing ? "comfort" : "nourished")+".png"));
        org.lwjgl.opengl.GL11.glColor4f(1,1,1,1);
        net.minecraft.client.gui.Gui.drawModalRectWithCustomSizedTexture(x+6,y+7,0,0,18,18,18,18);
    }

    @Override public boolean isReady(int duration, int amplifier) { return healing && duration % 80 == 0; }
    @Override public void performEffect(EntityLivingBase entity, int amplifier) {
        if (entity.worldObj.isRemote || !Settings.comfort) return;
        EntityPlayer player = entity instanceof EntityPlayer ? (EntityPlayer) entity : null;
        if (EffectRules.comfort(entity.isPotionActive(Potion.regeneration), player != null,
                player == null ? 0 : player.getFoodStats().getSaturationLevel(),
                player != null && player.worldObj.getGameRules().getBoolean("naturalRegeneration"),
                player == null ? 0 : player.getFoodStats().getFoodLevel(), entity.getHealth() < entity.getMaxHealth())) entity.heal(1.0F);
    }
}
