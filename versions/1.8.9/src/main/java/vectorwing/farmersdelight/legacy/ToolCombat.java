package vectorwing.farmersdelight.legacy;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.enchantment.EnchantmentHelper;

public final class ToolCombat {
    public static float knockback(float strength,Entity attacker) {
        if(!(attacker instanceof EntityLivingBase)) return strength;
        ItemStack held=((EntityLivingBase)attacker).getHeldItem();
        if(!Stacks.empty(held) && held.getItem() instanceof SkilletItem) return strength*2;
        return Ingredients.isKnife(held) ? Math.max(0,strength-0.1F) : strength;
    }
    @SubscribeEvent public void backstab(LivingHurtEvent event) {
        net.minecraft.util.DamageSource source=event.source;
        Entity attacker=source.getEntity();
        EntityLivingBase target=event.entityLiving;
        if(target.worldObj.isRemote || !(attacker instanceof EntityPlayer) || source.isProjectile() || source.isMagicDamage()) return;
        ItemStack held=((EntityPlayer)attacker).getHeldItem();
        if(!Ingredients.isKnife(held)) return;
        int level=EnchantmentHelper.getEnchantmentLevel(Content.backstabbing.effectId,held);
        if(level<=0) return;
        // Preserve the source's normalized spatial angle, with its vertical component removed for the dot product.
        double dx=attacker.posX-target.posX,dy=attacker.posY-target.posY,dz=attacker.posZ-target.posZ;
        double length=Math.sqrt(dx*dx+dy*dy+dz*dz); if(length<1.0E-8) return;
        net.minecraft.util.Vec3 look=target.getLook(1F);
        if((dx*look.xCoord+dz*look.zCoord)/length < -0.5) {
            event.ammount*=1+level*0.4F;
            target.worldObj.playSoundAtEntity(attacker,"random.successful_hit",1F,1F);
        }
    }
}

