package vectorwing.farmersdelight.legacy;
import net.minecraft.item.Item;
import net.minecraft.item.EnumToolMaterial;
import com.google.common.collect.Multimap;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.block.Block; import net.minecraft.block.material.Material;
public final class Knife extends ItemSword {
    private final EnumToolMaterial material;
    public Knife(EnumToolMaterial material) { super(LegacyIds.item(material==net.minecraft.item.EnumToolMaterial.STONE ? "flint_knife" : material==net.minecraft.item.EnumToolMaterial.IRON ? "iron_knife" : material==net.minecraft.item.EnumToolMaterial.GOLD ? "golden_knife" : "diamond_knife"),material); this.material = material; }
    @Override public int getItemEnchantability() { return material == EnumToolMaterial.STONE ? 5 : super.getItemEnchantability(); }
    @Override public boolean getIsRepairable(ItemStack knife, ItemStack repair) {
        return material == EnumToolMaterial.STONE ? repair.getItem() == Item.flint : super.getIsRepairable(knife, repair);
    }
    @Override public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) { stack.damageItem(1, attacker); return true; }
    @Override public float getStrVsBlock(ItemStack stack, Block block) {
        Material type = block.blockMaterial;
        return type == Material.cloth || type == Material.cloth || type == Material.cake || type == Material.web
                ? material.getEfficiencyOnProperMaterial() : 1.0F;
    }
    @Override public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        return stack;
    }
    @Override public Multimap<String, AttributeModifier> getItemAttributeModifiers() {
        Multimap<String, AttributeModifier> modifiers = super.getItemAttributeModifiers();

        String damage = SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName();
        modifiers.removeAll(damage);
        modifiers.put(damage, new AttributeModifier(field_111210_e, "Knife damage", 0.5F + material.getDamageVsEntity(), 0));

        return modifiers;
    }
}
