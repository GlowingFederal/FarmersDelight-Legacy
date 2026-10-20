package vectorwing.farmersdelight.legacy;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.*;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.init.Items;
import net.minecraft.village.MerchantRecipe;

public final class Acquisition {
    public static void register() {
        for(String name:new String[]{"fd_abandoned_mineshaft","fd_simple_dungeon"}) LootTableList.register(new ResourceLocation("farmersdelight","chests/"+name));
        if(Settings.farmerTrades) {
            VillagerRegistry.VillagerCareer farmer=VillagerRegistry.FARMER.getCareer(0);
            farmer.addTrade(1,buy("onion",26),buy("tomato",26));
            farmer.addTrade(2,buy("cabbage",16),buy("rice",20));
        }
    }
    private static net.minecraft.entity.passive.EntityVillager.ITradeList buy(final String id,final int amount) {
        return (merchant,offers,random) -> {
            MerchantRecipe offer=new MerchantRecipe(new ItemStack(Content.ITEMS.get(id),amount),new ItemStack(Items.EMERALD));
            offer.increaseMaxTradeUses(9); offers.add(offer);
        };
    }
    @SubscribeEvent public void chest(LootTableLoadEvent event) {
        if(!Settings.chestLoot) return;
        String name=event.getName().toString();
        String extra=name.equals("minecraft:chests/abandoned_mineshaft") ? "fd_abandoned_mineshaft"
                : name.equals("minecraft:chests/simple_dungeon") ? "fd_simple_dungeon" : null;
        if(extra==null) return;
        LootCondition[] conditions=new LootCondition[0];
        LootEntry entry=new LootEntryTable(new ResourceLocation("farmersdelight","chests/"+extra),1,0,conditions,"farmersdelight:extra");
        event.getTable().addPool(new LootPool(new LootEntry[]{entry},conditions,new RandomValueRange(1),new RandomValueRange(0),"farmersdelight:extra"));
    }
}

