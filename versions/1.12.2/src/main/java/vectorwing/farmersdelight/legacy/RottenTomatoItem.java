package vectorwing.farmersdelight.legacy;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
public final class RottenTomatoItem extends Item {
    public RottenTomatoItem() { setMaxStackSize(16); }
    public net.minecraft.util.ActionResult<ItemStack> onItemRightClick(World world,EntityPlayer player,net.minecraft.util.EnumHand hand) {
        ItemStack stack=player.getHeldItem(hand);
        world.playSound(null,player.posX,player.posY,player.posZ,net.minecraft.init.SoundEvents.ENTITY_SNOWBALL_THROW,net.minecraft.util.SoundCategory.NEUTRAL,0.5F,0.4F/(itemRand.nextFloat()*0.4F+0.8F));
        if(!world.isRemote) {
            RottenTomato tomato=new RottenTomato(world,player);
            tomato.shoot(player,player.rotationPitch,player.rotationYaw,0,1.5F,1F); world.spawnEntity(tomato);
            if(!player.capabilities.isCreativeMode) Stacks.shrink(stack,1);
        }
        player.addStat(net.minecraft.stats.StatList.getObjectUseStats(this));
        return new net.minecraft.util.ActionResult<ItemStack>(net.minecraft.util.EnumActionResult.SUCCESS,stack);
    }
}

