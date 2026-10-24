package vectorwing.farmersdelight.legacy;
import com.google.common.collect.Multimap;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.block.Block; import net.minecraft.block.material.Material;
public class Knife extends ItemSword {
    private final ToolMaterial material;
    public Knife(ToolMaterial material) { super(material); this.material = material; }
    @Override public int getItemEnchantability() { return material == ToolMaterial.STONE ? 5 : super.getItemEnchantability(); }
    @Override public boolean getIsRepairable(ItemStack knife, ItemStack repair) {
        return material == ToolMaterial.STONE ? repair.getItem() == net.minecraft.init.Items.flint : super.getIsRepairable(knife, repair);
    }
    @Override public java.util.Set<String> getToolClasses(ItemStack stack) { return java.util.Collections.singleton("farmersdelight_knife"); }
    @Override public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) { stack.damageItem(1, attacker); return true; }
    @Override public float func_150893_a(ItemStack stack, Block block) {
        Material type = block.getMaterial();
        return type == Material.cloth || type == Material.carpet || type == Material.cake || type == Material.web
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
