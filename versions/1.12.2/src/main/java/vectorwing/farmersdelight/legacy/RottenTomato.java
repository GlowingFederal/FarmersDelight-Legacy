package vectorwing.farmersdelight.legacy;

import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

public final class RottenTomato extends EntityThrowable {
    public RottenTomato(World world) { super(world); }
    public RottenTomato(World world,EntityLivingBase thrower) { super(world,thrower); }
    protected void onImpact(net.minecraft.util.math.RayTraceResult hit) {
        if(world.isRemote) return;
        if(hit.entityHit!=null) hit.entityHit.attackEntityFrom(net.minecraft.util.DamageSource.causeThrownDamage(this,getThrower()),0);
        if(hit.entityHit instanceof net.minecraft.entity.monster.AbstractIllager && getThrower() instanceof net.minecraft.entity.player.EntityPlayer)
            Progression.grant((net.minecraft.entity.player.EntityPlayer)getThrower(),"hit_raider_with_rotten_tomato","hit_raider_with_rotten_tomato");
        world.playSound(null,posX,posY,posZ,net.minecraft.init.SoundEvents.ENTITY_SLIME_ATTACK,net.minecraft.util.SoundCategory.NEUTRAL,1F,1F);
        world.setEntityState(this,(byte)3); setDead();
    }
    @net.minecraftforge.fml.relauncher.SideOnly(net.minecraftforge.fml.relauncher.Side.CLIENT)
    public void handleStatusUpdate(byte status) {
        if(status!=3) { super.handleStatusUpdate(status); return; }
        for(int i=0;i<12;i++) world.spawnParticle(net.minecraft.util.EnumParticleTypes.ITEM_CRACK,posX,posY,posZ,
                (rand.nextFloat()*2-1)*0.1,(rand.nextFloat()*2-1)*0.1+0.1,(rand.nextFloat()*2-1)*0.1,net.minecraft.item.Item.getIdFromItem(Content.ITEMS.get("rotten_tomato")),0);
    }
}

