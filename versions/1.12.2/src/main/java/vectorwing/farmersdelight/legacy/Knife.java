package vectorwing.farmersdelight.legacy;
import com.google.common.collect.Multimap;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.inventory.EntityEquipmentSlot; import net.minecraft.block.state.IBlockState; import net.minecraft.block.material.Material; import net.minecraft.util.ActionResult; import net.minecraft.util.EnumActionResult; import net.minecraft.util.EnumHand;
public final class Knife extends ItemSword {
    private final ToolMaterial material;
    public Knife(ToolMaterial material) { super(material); this.material = material; }
    @Override public int getItemEnchantability() { return material == ToolMaterial.STONE ? 5 : super.getItemEnchantability(); }
    @Override public boolean getIsRepairable(ItemStack knife, ItemStack repair) {
        return material == ToolMaterial.STONE ? repair.getItem() == net.minecraft.init.Items.FLINT : super.getIsRepairable(knife, repair);
    }
    @Override public java.util.Set<String> getToolClasses(ItemStack stack) { return java.util.Collections.singleton("farmersdelight_knife"); }
    @Override public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) { stack.damageItem(1, attacker); return true; }
    @Override public float getDestroySpeed(ItemStack stack, IBlockState state) {
        Material type = state.getMaterial();
        return type == Material.CLOTH || type == Material.CARPET || type == Material.CAKE || type == Material.WEB
                ? material.getEfficiency() : 1.0F;
    }
    @Override public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        return new ActionResult<ItemStack>(EnumActionResult.PASS, player.getHeldItem(hand));
    }
    @Override public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        if (enchantment == net.minecraft.init.Enchantments.FORTUNE) return false;
        if (enchantment == net.minecraft.init.Enchantments.LOOTING || enchantment == net.minecraft.init.Enchantments.SHARPNESS
                || enchantment == net.minecraft.init.Enchantments.SMITE || enchantment == net.minecraft.init.Enchantments.BANE_OF_ARTHROPODS
                || enchantment == net.minecraft.init.Enchantments.KNOCKBACK || enchantment == net.minecraft.init.Enchantments.FIRE_ASPECT) return true;
        return super.canApplyAtEnchantingTable(stack, enchantment);
    }
    @Override public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot slot) {
        Multimap<String, AttributeModifier> modifiers = super.getItemAttributeModifiers(slot);
        if (slot != EntityEquipmentSlot.MAINHAND) return modifiers;
        String damage = SharedMonsterAttributes.ATTACK_DAMAGE.getName();
        modifiers.removeAll(damage);
        modifiers.put(damage, new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Knife damage", 0.5F + material.getAttackDamage(), 0));
        modifiers.removeAll(SharedMonsterAttributes.ATTACK_SPEED.getName()); modifiers.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Knife speed", -2.0, 0));
        return modifiers;
    }
}
