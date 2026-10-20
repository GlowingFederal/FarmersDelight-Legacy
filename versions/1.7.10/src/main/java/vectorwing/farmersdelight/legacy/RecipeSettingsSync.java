package vectorwing.farmersdelight.legacy;

import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.*;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayerMP;

public final class RecipeSettingsSync {
    private static final SimpleNetworkWrapper CHANNEL=NetworkRegistry.INSTANCE.newSimpleChannel("fd_recipe_settings");
    public static void register() {
        CHANNEL.registerMessage(Handler.class,Snapshot.class,0,Side.CLIENT);
        cpw.mods.fml.common.FMLCommonHandler.instance().bus().register(new RecipeSettingsSync());
    }
    @SubscribeEvent public void login(PlayerEvent.PlayerLoggedInEvent event) {
        if(event.player instanceof EntityPlayerMP) CHANNEL.sendTo(new Snapshot(Settings.vanillaCrates,Settings.cuttingFortuneBonus),(EntityPlayerMP)event.player);
    }
    @SubscribeEvent public void disconnect(cpw.mods.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        Foundation.proxy.recipeSettings(Settings.localVanillaCrates,Settings.localFortuneBonus);
    }
    public static final class Snapshot implements IMessage {
        boolean crates; double fortune;
        public Snapshot() {}
        Snapshot(boolean crates,double fortune) { this.crates=crates;this.fortune=fortune; }
        public void fromBytes(ByteBuf buffer) { crates=buffer.readBoolean(); fortune=buffer.readDouble(); if(Double.isNaN(fortune) || fortune<0 || fortune>1) fortune=0.1; }
        public void toBytes(ByteBuf buffer) { buffer.writeBoolean(crates);buffer.writeDouble(fortune); }
    }
    public static final class Handler implements IMessageHandler<Snapshot,IMessage> {
        public IMessage onMessage(Snapshot message,MessageContext context) { Foundation.proxy.recipeSettings(message.crates,message.fortune); return null; }
    }
}

