package vectorwing.farmersdelight.legacy.client;
import net.minecraft.item.Item;

import java.util.List;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import org.lwjgl.opengl.GL11;
import vectorwing.farmersdelight.legacy.*;
import vectorwing.farmersdelight.legacy.api.EffectRules;
import vectorwing.farmersdelight.legacy.api.FoodDefinition;

/** Native overlays and tooltips; no gameplay state is changed here. */
public final class ClientFeedback extends Gui {
    private static final ResourceLocation ICONS=new ResourceLocation("farmersdelight","textures/gui/fd_icons.png");
    private final Random random=new Random();
    private int foodOffset,healthOffset;
    @ForgeSubscribe public void before(RenderGameOverlayEvent.Pre event) {
        if(event.isCanceled()) return;
        if(event.type==RenderGameOverlayEvent.ElementType.FOOD) foodOffset=GuiIngameForge.right_height;
        if(event.type==RenderGameOverlayEvent.ElementType.HEALTH) healthOffset=GuiIngameForge.left_height;
    }
    @ForgeSubscribe public void after(RenderGameOverlayEvent.Post event) {
        Minecraft mc=Minecraft.getMinecraft();
        EntityPlayer player=mc.thePlayer;
        if(player==null) return;
        int width=event.resolution.getScaledWidth(),height=event.resolution.getScaledHeight();
        int ticks=mc.ingameGUI.getUpdateCounter();
        boolean food=Settings.nourishmentOverlay && Settings.nourishment && event.type==RenderGameOverlayEvent.ElementType.FOOD && player.isPotionActive(Content.nourishment.id);
        boolean health=Settings.comfortOverlay && Settings.comfort && event.type==RenderGameOverlayEvent.ElementType.HEALTH
                && player.isPotionActive(Content.comfort.id) && EffectRules.comfort(player.isPotionActive(Potion.regeneration.id),true,player.getFoodStats().getSaturationLevel(),
                player.worldObj.getGameRules().getGameRuleBooleanValue("naturalRegeneration"),player.getFoodStats().getFoodLevel(),player.getHealth()<player.getMaxHealth());
        if(!food && !health) return;
        random.setSeed(ticks*312871L);
        mc.getTextureManager().bindTexture(ICONS);
        GL11.glColor4f(1,1,1,1); GL11.glEnable(GL11.GL_BLEND); GL11.glBlendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE_MINUS_SRC_ALPHA);
        if(food) {
            int hunger=player.getFoodStats().getFoodLevel();
            boolean healing=player.worldObj.getGameRules().getGameRuleBooleanValue("naturalRegeneration") && player.getHealth()<player.getMaxHealth()
                    && player.getFoodStats().getSaturationLevel()>0 && hunger>=20;
            for(int i=0;i<10;i++) {
                int x=width/2+91-i*8-9,y=height-foodOffset;
                if(player.getFoodStats().getSaturationLevel()<=0 && ticks%(hunger*3+1)==0) y+=random.nextInt(3)-1;
                drawTexturedModalRect(x,y,0,0,9,9);
                if(hunger>i*2) drawTexturedModalRect(x,y,(hunger>i*2+1 ? 18 : 9)+(healing ? 18 : 0),0,9,9);
            }
        }
        if(health) {
            int total=(int)Math.ceil((player.getMaxHealth()+player.getAbsorptionAmount())/2D);
            int rows=(int)Math.ceil(total/10D),spacing=Math.max(10-(rows-2),3),sheen=ticks%50,frame=sheen%2;
            for(int i=total-1;i>=0;i--) {
                int column=i%10,x=width/2-91+column*8,y=height-healthOffset-(i/10)*spacing;
                if(player.getHealth()<=4) y+=random.nextInt(2);
                if(column==sheen/2) drawTexturedModalRect(x,y,0,9,frame==0 ? 5 : 9,9);
                if(column==sheen/2-1 && frame==0) drawTexturedModalRect(x+5,y,5,9,4,9);
            }
        }
        GL11.glDisable(GL11.GL_BLEND); mc.getTextureManager().bindTexture(new ResourceLocation("minecraft","textures/gui/icons.png"));
    }
    private static void effect(List<String> lines,Potion potion,int duration,int amplifier) {
        if(potion==null) return;
        String text=I18n.getStringParams(potion.getName());
        if(amplifier>0) text+=" "+I18n.getStringParams("potion.potency."+amplifier);
        if(duration>20) text+=" ("+(duration/1200)+":"+String.format(java.util.Locale.ROOT,"%02d",duration/20%60)+")";
        lines.add((potion.isBadEffect() ? "\u00a7c" : "\u00a79")+text);
    }
    @ForgeSubscribe public void tooltip(ItemTooltipEvent event) {
        if(!Settings.foodTooltips) return;
        ItemStack stack=event.itemStack;
        if(Stacks.empty(stack)) return;
        List<String> lines=event.toolTip;
        if(stack.getItem() instanceof LegacyFood) {
            FoodDefinition definition=((LegacyFood)stack.getItem()).definition;
            effect(lines,LegacyFood.effect(definition.effect),definition.duration,definition.amplifier);
            if(definition.id.equals("milk_bottle") || definition.id.equals("hot_cocoa") || definition.id.equals("melon_juice"))
                lines.add("\u00a79"+I18n.getStringParams("farmersdelight.tooltip."+definition.id));
        }
        if(Settings.comfortSoups && (stack.getItem()==Item.bowlSoup))
            effect(lines,Content.comfort,3600,0);

        if(stack.getItem()==Content.ITEMS.get("dog_food") || stack.getItem()==Content.ITEMS.get("horse_feed")) {
            boolean horse=stack.getItem()==Content.ITEMS.get("horse_feed");
            lines.add("\u00a77"+I18n.getStringParams("farmersdelight.tooltip."+(horse ? "horse_feed" : "dog_food")+".when_feeding"));
            effect(lines,Potion.moveSpeed,6000,horse ? 1 : 0);
            if(horse) effect(lines,Potion.jump,6000,0);
            else { effect(lines,Potion.damageBoost,6000,0); effect(lines,Potion.resistance,6000,0); }
        }
        if(stack.getItem()==Content.ITEMS.get("skillet")) {
            lines.add("\u00a77"+I18n.getStringParams("farmersdelight.tooltip.skillet.controls"));
            lines.add("\u00a77"+I18n.getStringParams("farmersdelight.tooltip.skillet.hand"));
        }
        if(stack.getItem()==Content.ITEMS.get("cooking_pot")) {
            NBTTagCompound data=stack.hasTagCompound() ? stack.getTagCompound().getCompoundTag("MealData") : new NBTTagCompound();
            ItemStack meal=ItemStack.loadItemStackFromNBT(data.getCompoundTag("Meal"));
            if(Stacks.empty(meal)) lines.add("\u00a77"+I18n.getStringParams("farmersdelight.tooltip.cooking_pot.empty"));
            else {
                lines.add("\u00a77"+I18n.getStringParams("farmersdelight.tooltip.cooking_pot."+ (Stacks.size(meal)==1 ? "single_serving" : "many_servings"),Stacks.size(meal)));
                lines.add("\u00a79"+meal.getDisplayName());
            }
        }
    }
}
