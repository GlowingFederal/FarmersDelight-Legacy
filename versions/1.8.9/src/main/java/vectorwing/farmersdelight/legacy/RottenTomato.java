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
    @net.minecraftforge.fml.relauncher.SideOnly(net.minecraftforge.fml.relauncher.Side.CLIENT)
    @Override public void handleStatusUpdate(byte status) {
        if(status!=3) { super.handleStatusUpdate(status); return; }
        for(int i=0;i<12;i++) worldObj.spawnParticle(net.minecraft.util.EnumParticleTypes.ITEM_CRACK,posX,posY,posZ,
                (rand.nextFloat()*2-1)*0.1,(rand.nextFloat()*2-1)*0.1+0.1,(rand.nextFloat()*2-1)*0.1,net.minecraft.item.Item.getIdFromItem(Content.ITEMS.get("rotten_tomato")));
    }
}

