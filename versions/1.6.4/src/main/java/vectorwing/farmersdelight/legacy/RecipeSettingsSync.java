package vectorwing.farmersdelight.legacy;
import java.io.*;
import cpw.mods.fml.common.network.*;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.NetLoginHandler;
import net.minecraft.network.packet.*;
import net.minecraft.server.MinecraftServer;
public final class RecipeSettingsSync implements IPacketHandler,IConnectionHandler {
    public static void register() { NetworkRegistry.instance().registerConnectionHandler(new RecipeSettingsSync()); }
    public void playerLoggedIn(Player player,NetHandler handler,INetworkManager manager) {
        if(!(player instanceof net.minecraft.entity.player.EntityPlayerMP)) return;
        try {
            ByteArrayOutputStream bytes=new ByteArrayOutputStream(9); DataOutputStream out=new DataOutputStream(bytes);
            out.writeBoolean(Settings.vanillaCrates);out.writeDouble(Settings.cuttingFortuneBonus);
            Packet250CustomPayload packet=new Packet250CustomPayload("fd_recipe",bytes.toByteArray());
            PacketDispatcher.sendPacketToPlayer(packet,player);
        } catch(IOException e) { throw new IllegalStateException("Cannot encode recipe settings",e); }
    }
    public void onPacketData(INetworkManager manager,Packet250CustomPayload packet,Player player) {
        if(player instanceof net.minecraft.entity.player.EntityPlayerMP || !"fd_recipe".equals(packet.channel) || packet.data==null || packet.data.length!=9) return;
        try {
            DataInputStream in=new DataInputStream(new ByteArrayInputStream(packet.data)); boolean crates=in.readBoolean();double fortune=in.readDouble();
            if(Double.isNaN(fortune) || Double.isInfinite(fortune) || fortune<0 || fortune>1) return;
            Foundation.proxy.recipeSettings(crates,fortune);
        } catch(IOException e) { throw new IllegalStateException("Invalid recipe settings",e); }
    }
    public String connectionReceived(NetLoginHandler handler,INetworkManager manager) { return null; }
    public void connectionOpened(NetHandler handler,String host,int port,INetworkManager manager) {}
    public void connectionOpened(NetHandler handler,MinecraftServer server,INetworkManager manager) {}
    public void connectionClosed(INetworkManager manager) { Foundation.proxy.recipeSettings(Settings.localVanillaCrates,Settings.localFortuneBonus); }
    public void clientLoggedIn(NetHandler handler,INetworkManager manager,Packet1Login packet) {}
}
