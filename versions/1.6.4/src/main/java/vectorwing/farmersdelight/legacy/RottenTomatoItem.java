package vectorwing.farmersdelight.legacy;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
public final class RottenTomatoItem extends Item {
    public RottenTomatoItem() { super(LegacyIds.item("rotten_tomato")); setMaxStackSize(16); }
    public ItemStack onItemRightClick(ItemStack stack,World world,EntityPlayer player) {
        world.playSoundAtEntity(player,"random.bow",0.5F,0.4F/(itemRand.nextFloat()*0.4F+0.8F));
        if(!world.isRemote) {
            RottenTomato tomato=new RottenTomato(world,player);
            world.spawnEntityInWorld(tomato);
            if(!player.capabilities.isCreativeMode) Stacks.shrink(stack,1);
        }
        player.addStat(net.minecraft.stats.StatList.objectUseStats[NativeRegistry.id(this)],1);
        return stack;
    }
}

