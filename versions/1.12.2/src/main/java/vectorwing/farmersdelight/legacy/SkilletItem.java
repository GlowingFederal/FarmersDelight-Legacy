package vectorwing.farmersdelight.legacy;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public final class SkilletItem extends ItemBlock {
    public com.google.common.collect.Multimap<String,net.minecraft.entity.ai.attributes.AttributeModifier> getItemAttributeModifiers(net.minecraft.inventory.EntityEquipmentSlot slot) {
        com.google.common.collect.Multimap<String,net.minecraft.entity.ai.attributes.AttributeModifier> map=super.getItemAttributeModifiers(slot);
        if(slot==net.minecraft.inventory.EntityEquipmentSlot.MAINHAND) {
            map.put(net.minecraft.entity.SharedMonsterAttributes.ATTACK_DAMAGE.getName(),new net.minecraft.entity.ai.attributes.AttributeModifier(ATTACK_DAMAGE_MODIFIER,"Skillet damage",6,0));
            map.put(net.minecraft.entity.SharedMonsterAttributes.ATTACK_SPEED.getName(),new net.minecraft.entity.ai.attributes.AttributeModifier(ATTACK_SPEED_MODIFIER,"Skillet speed",-3.1,0));
        }
        return map;
    }
    public boolean canApplyAtEnchantingTable(ItemStack stack,net.minecraft.enchantment.Enchantment enchantment) {
        return enchantment.type==net.minecraft.enchantment.EnumEnchantmentType.WEAPON && enchantment!=net.minecraft.init.Enchantments.SWEEPING || super.canApplyAtEnchantingTable(stack,enchantment);
    }
    public SkilletItem(Block block) { super(block); setMaxStackSize(1); setMaxDamage(250); }
    public boolean getIsRepairable(ItemStack stack, ItemStack repair) { return repair.getItem() == net.minecraft.init.Items.IRON_INGOT || super.getIsRepairable(stack,repair); }
    public int getItemEnchantability() { return 14; }
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) { stack.damageItem(1,attacker); return true; }
    public int getMaxItemUseDuration(ItemStack stack) {
        if (!stack.hasTagCompound()) return 60;
        int fire = EnchantmentHelper.getEnchantmentLevel(net.minecraft.init.Enchantments.FIRE_ASPECT,stack);
        return GrillingRecipes.skilletTime(Math.max(1,stack.getTagCompound().getInteger("CookTimeHandheld")),fire);
    }
    public net.minecraft.item.EnumAction getItemUseAction(ItemStack stack) { return net.minecraft.item.EnumAction.BOW; }
    private static boolean nearbyHeat(EntityPlayer player, World world) {
        if (player.isBurning()) return true;
        int x=(int)Math.floor(player.posX), y=(int)Math.floor(player.posY), z=(int)Math.floor(player.posZ);
        for(int dx=-1;dx<=1;dx++) for(int dy=-1;dy<=1;dy++) for(int dz=-1;dz<=1;dz++)
            if(Heat.heated(world,new BlockPos(x+dx,y+dy+1,z+dz),true)) return true;
        return false;
    }
    @Override public ActionResult<ItemStack> onItemRightClick(World world,EntityPlayer player,EnumHand hand) {
        ItemStack stack=player.getHeldItem(hand);
        if(!nearbyHeat(player,world)) return new ActionResult<ItemStack>(EnumActionResult.PASS,stack);
        NBTTagCompound tag=stack.hasTagCompound() ? stack.getTagCompound() : new NBTTagCompound();
        if(!tag.hasKey("Cooking",10)) {
            // Pre-offhand controls use the next hotbar slot, with no mandatory inventory mod.
            ItemStack input=player.getHeldItem(hand==EnumHand.MAIN_HAND ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
            GrillingRecipes.Recipe recipe=GrillingRecipes.find(input);
            if(recipe==null) return new ActionResult<ItemStack>(EnumActionResult.PASS,stack);
            NBTTagCompound reserved=new NBTTagCompound(); Stacks.copy(input,1).writeToNBT(reserved);
            tag.setTag("Cooking",reserved); tag.setInteger("CookTimeHandheld",recipe.time); tag.setBoolean("CookingPaid",!player.capabilities.isCreativeMode);
            if(!player.capabilities.isCreativeMode) Stacks.shrink(input,1);
            stack.setTagCompound(tag);
        }
        player.setActiveHand(hand);
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS,stack);
    }
    private static ItemStack reserved(ItemStack stack) {
        return !stack.hasTagCompound() ? Stacks.none() : new ItemStack(stack.getTagCompound().getCompoundTag("Cooking"));
    }
    private static boolean clearReservation(ItemStack stack) {
        if(!stack.hasTagCompound()) return false;
        NBTTagCompound tag=stack.getTagCompound(); boolean paid=tag.getBoolean("CookingPaid");
        tag.removeTag("Cooking"); tag.removeTag("CookingPaid"); tag.removeTag("CookTimeHandheld"); return paid;
    }
    private static void give(EntityPlayer player,ItemStack result) {
        if(!Stacks.empty(result) && !player.inventory.addItemStackToInventory(result)) player.dropItem(result,false);
    }
    @Override public void onPlayerStoppedUsing(ItemStack stack,World world,EntityLivingBase user,int remaining) {
        ItemStack input=reserved(stack); boolean paid=clearReservation(stack);
        if(!world.isRemote && paid && user instanceof EntityPlayer) give((EntityPlayer)user,input);
    }
    @Override public ItemStack onItemUseFinish(ItemStack stack,World world,EntityLivingBase user) {
        ItemStack input=reserved(stack); boolean paid=clearReservation(stack);
        if(!world.isRemote && user instanceof EntityPlayer) {
            GrillingRecipes.Recipe recipe=GrillingRecipes.find(input);
            if(recipe!=null) {
                give((EntityPlayer)user,recipe.output.copy()); if(paid) give((EntityPlayer)user,CookingRecipes.remainder(input));
                Progression.consumed((EntityPlayer)user,stack);
            }
            else if(paid) give((EntityPlayer)user,input);
        }
        return stack;
    }
    @Override public EnumActionResult onItemUse(EntityPlayer player,World world,BlockPos pos,EnumHand hand,EnumFacing side,float hx,float hy,float hz) {
        ItemStack stack=player.getHeldItem(hand);
        if(!player.isSneaking() || stack.hasTagCompound() && stack.getTagCompound().hasKey("Cooking",10)) return EnumActionResult.PASS;
        return super.onItemUse(player,world,pos,hand,side,hx,hy,hz);
    }
}
