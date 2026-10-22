package vectorwing.farmersdelight.legacy;
import net.minecraft.potion.Potion;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import vectorwing.farmersdelight.legacy.api.EffectRules;
public final class LegacyEffect extends Potion {
    private final boolean healing;
    public LegacyEffect(String name) { this(Foundation.POTIONS.id(name), name); }
    /** Legacy constructor remains valid only for the canonical named allocation. */
    public LegacyEffect(int id, String name) {
        super(allocated(id, name), false, 0);
        healing = name.equals("comfort");
        setPotionName("potion.farmersdelight." + name);
        Foundation.POTIONS.track(name, this);

    }
    private static int allocated(int supplied, String name) {
        int id = Foundation.POTIONS.id(name);
        if (supplied != id || potionTypes[id] != null)
            throw new IllegalStateException("Farmer's Delight potion " + name + " must use its vacant canonical allocation " + id);
        return id;
    }
    @cpw.mods.fml.relauncher.SideOnly(cpw.mods.fml.relauncher.Side.CLIENT)
    @Override public void renderInventoryEffect(int x,int y,net.minecraft.potion.PotionEffect effect,net.minecraft.client.Minecraft mc) {
        mc.getTextureManager().bindTexture(new net.minecraft.util.ResourceLocation("farmersdelight","textures/mob_effect/"+(healing ? "comfort" : "nourished")+".png"));
        org.lwjgl.opengl.GL11.glColor4f(1,1,1,1);
        net.minecraft.client.gui.Gui.func_146110_a(x+6,y+7,0,0,18,18,18,18);
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
