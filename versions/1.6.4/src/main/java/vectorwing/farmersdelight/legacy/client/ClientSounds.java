package vectorwing.farmersdelight.legacy.client;
/** Forge 1.6 predates sounds.json; sounds are registered on each resource reload. */
public final class ClientSounds {
    @net.minecraftforge.event.ForgeSubscribe public void load(net.minecraftforge.client.event.sound.SoundLoadEvent event) {
        for(String name:new String[]{"block/cutting_board/knife1","block/cutting_board/knife2","block/cooking_pot/boil1","block/cooking_pot/boil2","block/skillet/sizzle1","block/skillet/sizzle2","block/skillet/sizzle3"})
            event.manager.addSound("farmersdelight:"+name+".ogg");
    }
}
