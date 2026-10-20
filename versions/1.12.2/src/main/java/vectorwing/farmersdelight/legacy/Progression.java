package vectorwing.farmersdelight.legacy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.world.BlockEvent;

public final class Progression {
    public static void register() { net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(new Progression()); }
    public static void grant(EntityPlayer player,String id,String criterion) {
        if(!(player instanceof EntityPlayerMP)) return;
        EntityPlayerMP serverPlayer=(EntityPlayerMP)player;
        net.minecraft.advancements.Advancement advancement=serverPlayer.getServerWorld().getAdvancementManager().getAdvancement(new ResourceLocation("farmersdelight","main/"+id));
        if(advancement!=null) serverPlayer.getAdvancements().grantCriterion(advancement,criterion);
    }
    @SubscribeEvent public void place(BlockEvent.PlaceEvent event) {
        if(event.getPlayer() instanceof EntityPlayerMP && !event.getWorld().isRemote)
            net.minecraft.advancements.CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP)event.getPlayer(),event.getPos(),event.getPlayer().getHeldItemMainhand());
    }
    public static void consumed(EntityPlayer player,net.minecraft.item.ItemStack stack) {
        if(player instanceof EntityPlayerMP) net.minecraft.advancements.CriteriaTriggers.CONSUME_ITEM.trigger((EntityPlayerMP)player,stack);
    }
}

