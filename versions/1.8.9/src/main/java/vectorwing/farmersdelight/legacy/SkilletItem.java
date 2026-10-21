package vectorwing.farmersdelight.legacy;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.enchantment.EnchantmentHelper;


public final class SkilletItem extends ItemBlock {
    public com.google.common.collect.Multimap<String,net.minecraft.entity.ai.attributes.AttributeModifier> getItemAttributeModifiers() {
        com.google.common.collect.Multimap<String,net.minecraft.entity.ai.attributes.AttributeModifier> map=super.getItemAttributeModifiers();
        map.put(net.minecraft.entity.SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(),new net.minecraft.entity.ai.attributes.AttributeModifier(itemModifierUUID,"Skillet damage",6,0));
        return map;
    }
    public SkilletItem(Block block) { super(block); setMaxStackSize(1); setMaxDamage(250); }
    public boolean getIsRepairable(ItemStack stack, ItemStack repair) { return repair.getItem() == net.minecraft.init.Items.iron_ingot || super.getIsRepairable(stack,repair); }
    public int getItemEnchantability() { return 14; }
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) { stack.damageItem(1,attacker); return true; }
    public int getMaxItemUseDuration(ItemStack stack) {
        if (!stack.hasTagCompound()) return 60;
        int fire = EnchantmentHelper.getEnchantmentLevel(net.minecraft.enchantment.Enchantment.fireAspect.effectId,stack);
        return GrillingRecipes.skilletTime(Math.max(1,stack.getTagCompound().getInteger("CookTimeHandheld")),fire);
    }
    public net.minecraft.item.EnumAction getItemUseAction(ItemStack stack) { return net.minecraft.item.EnumAction.BOW; }
    private static boolean nearbyHeat(EntityPlayer player, World world) {
        if (player.isBurning()) return true;
        int x=(int)Math.floor(player.posX), y=(int)Math.floor(player.posY), z=(int)Math.floor(player.posZ);
        for(int dx=-1;dx<=1;dx++) for(int dy=-1;dy<=1;dy++) for(int dz=-1;dz<=1;dz++)
            if(Heat.heated(world,new net.minecraft.util.BlockPos(x+dx,y+dy+1,z+dz),true)) return true;
        return false;
    }
    @Override public ItemStack onItemRightClick(ItemStack stack,World world,EntityPlayer player) {

        if(!nearbyHeat(player,world)) return stack;
        NBTTagCompound tag=stack.hasTagCompound() ? stack.getTagCompound() : new NBTTagCompound();
        if(!tag.hasKey("Cooking",10)) {
            // Pre-offhand controls use the next hotbar slot, with no mandatory inventory mod.
            ItemStack input=player.inventory.getStackInSlot((player.inventory.currentItem+1)%9);
            GrillingRecipes.Recipe recipe=GrillingRecipes.find(input);
            if(recipe==null) return stack;
            NBTTagCompound reserved=new NBTTagCompound(); Stacks.copy(input,1).writeToNBT(reserved);
            tag.setTag("Cooking",reserved); tag.setInteger("CookTimeHandheld",recipe.time); tag.setBoolean("CookingPaid",!player.capabilities.isCreativeMode);
            if(!player.capabilities.isCreativeMode) Stacks.shrink(input,1);
            stack.setTagCompound(tag);
        }
        player.setItemInUse(stack,getMaxItemUseDuration(stack));
        return stack;
    }
    private static ItemStack reserved(ItemStack stack) {
        return !stack.hasTagCompound() ? Stacks.none() : ItemStack.loadItemStackFromNBT(stack.getTagCompound().getCompoundTag("Cooking"));
    }
    private static boolean clearReservation(ItemStack stack) {
        if(!stack.hasTagCompound()) return false;
        NBTTagCompound tag=stack.getTagCompound(); boolean paid=tag.getBoolean("CookingPaid");
        tag.removeTag("Cooking"); tag.removeTag("CookingPaid"); tag.removeTag("CookTimeHandheld"); return paid;
    }
    private static void give(EntityPlayer player,ItemStack result) {
        if(!Stacks.empty(result) && !player.inventory.addItemStackToInventory(result)) player.dropPlayerItemWithRandomChoice(result,false);
    }
    @Override public void onPlayerStoppedUsing(ItemStack stack,World world,EntityPlayer user,int remaining) {
        ItemStack input=reserved(stack); boolean paid=clearReservation(stack);
        if(!world.isRemote && paid && user instanceof EntityPlayer) give((EntityPlayer)user,input);
    }
    @Override public ItemStack onItemUseFinish(ItemStack stack,World world,EntityPlayer user) {
        ItemStack input=reserved(stack); boolean paid=clearReservation(stack);
        if(!world.isRemote && user instanceof EntityPlayer) {
            GrillingRecipes.Recipe recipe=GrillingRecipes.find(input);
            if(recipe!=null) {
                give((EntityPlayer)user,recipe.output.copy()); if(paid) give((EntityPlayer)user,CookingRecipes.remainder(input));
                Progression.grant((EntityPlayer)user,"use_skillet","skillet");
            }
            else if(paid) give((EntityPlayer)user,input);
        }
        return stack;
    }
    @Override public boolean onItemUse(ItemStack stack,EntityPlayer player,World world,net.minecraft.util.BlockPos pos,net.minecraft.util.EnumFacing side,float hx,float hy,float hz) {

        if(!player.isSneaking() || stack.hasTagCompound() && stack.getTagCompound().hasKey("Cooking",10)) return false;
        return super.onItemUse(stack,player,world,pos,side,hx,hy,hz);
    }
}
