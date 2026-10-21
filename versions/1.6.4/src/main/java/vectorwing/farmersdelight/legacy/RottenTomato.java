package vectorwing.farmersdelight.legacy;

import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

public final class RottenTomato extends EntityThrowable {
    public RottenTomato(World world) { super(world); }
    public RottenTomato(World world,EntityLivingBase thrower) { super(world,thrower); }
    protected void onImpact(net.minecraft.util.MovingObjectPosition hit) {
        if(worldObj.isRemote) return;
        if(hit.entityHit!=null) hit.entityHit.attackEntityFrom(net.minecraft.util.DamageSource.causeThrownDamage(this,getThrower()),0);
        worldObj.playSoundAtEntity(this,"mob.slime.attack",1F,1F);
        worldObj.setEntityState(this,(byte)3); setDead();
    }
    @cpw.mods.fml.relauncher.SideOnly(cpw.mods.fml.relauncher.Side.CLIENT)
    @Override public void handleHealthUpdate(byte status) {
        if(status!=3) { super.handleHealthUpdate(status); return; }
        for(int i=0;i<12;i++) worldObj.spawnParticle("iconcrack_"+NativeRegistry.id(Content.ITEMS.get("rotten_tomato")),posX,posY,posZ,
                (rand.nextFloat()*2-1)*0.1,(rand.nextFloat()*2-1)*0.1+0.1,(rand.nextFloat()*2-1)*0.1);
    }
}

