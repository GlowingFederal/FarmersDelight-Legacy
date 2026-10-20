package vectorwing.farmersdelight.legacy;

import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

public final class CookingPotTile extends InventoryTile implements net.minecraft.util.ITickable {
    private static final int[] INPUTS = {0, 1, 2, 3, 4, 5}, SERVING = {7, 8};
    public int progress, total = 200;
    public boolean heated;
    private boolean recipeDirty = true;
    private CookingRecipes.Recipe recipe;
    private String savedRecipe = "", customName = "";
    private ItemStack servingContainer = Stacks.none();
    private final Map<String, Integer> usedRecipes = new LinkedHashMap<String, Integer>();
    public CookingPotTile() { super(9); }
    @Override public void markDirty() { recipeDirty = true; super.markDirty(); }
    public String getName() { return customName.isEmpty() ? "container.farmersdelight.cooking_pot" : customName; }
    public boolean hasCustomName() { return !customName.isEmpty(); }
    public void name(String value) { customName = value; changed(); }
    @Override public void update() {
        if (world == null) return;
        boolean nowHeated = Heat.heated(world, pos, false);
        if (world.isRemote) {
            if (nowHeated && world.rand.nextInt(8) == 0)
                world.spawnParticle(net.minecraft.util.EnumParticleTypes.SMOKE_NORMAL, pos.getX() + 0.5, pos.getY() + 0.8, pos.getZ() + 0.5, 0, 0.01, 0);
            return;
        }
        if (heated != nowHeated) { heated = nowHeated; changed(); }
        if (recipeDirty) {
            CookingRecipes.Recipe found = CookingRecipes.find(inventory);
            String next = found == null ? "" : found.id;
            if (!next.equals(savedRecipe)) progress = 0;
            recipe = found; savedRecipe = next; recipeDirty = false;
            if (recipe != null) total = recipe.time;
        }
        int before = progress;
        if (heated && recipe != null && canCook(recipe)) {
            if (++progress >= total) cook();
        } else if (!heated) progress = Math.max(0, progress - 2);
        else progress = 0;
        if (before != progress) super.markDirty();
        serveStored();
        if (heated && recipe != null && world.rand.nextInt(100) == 0)
            world.playSound(null, pos, Content.boilingSound, net.minecraft.util.SoundCategory.BLOCKS, 0.4F, 1F);
    }
    private static boolean containersEqual(ItemStack a, ItemStack b) { return Stacks.empty(a) && Stacks.empty(b) || Stacks.same(a, b); }
    private boolean canCook(CookingRecipes.Recipe recipe) {
        return Stacks.empty(inventory[6]) || Stacks.same(inventory[6], recipe.output)
                && containersEqual(servingContainer, recipe.container) && Stacks.size(inventory[6]) <= 64 - Stacks.size(recipe.output);
    }
    private void cook() {
        CookingRecipes.Recipe completed = recipe;
        if (!completed.matches(inventory) || !canCook(completed)) { progress = 0; recipeDirty = true; return; }
        for (int i = 0; i < 6; i++) if (!Stacks.empty(inventory[i])) {
            ItemStack consumed = Stacks.copy(inventory[i], 1);
            Stacks.shrink(inventory[i], 1);
            if (Stacks.empty(inventory[i])) inventory[i] = Stacks.none();
            drop(CookingRecipes.remainder(consumed));
        }
        if (Stacks.empty(inventory[6])) inventory[6] = completed.output.copy();
        else Stacks.count(inventory[6], Stacks.size(inventory[6]) + Stacks.size(completed.output));
        servingContainer = Stacks.copy(completed.container, 1);
        Integer times = usedRecipes.get(completed.id);
        usedRecipes.put(completed.id, times == null ? 1 : times == Integer.MAX_VALUE ? times : times + 1);
        progress = 0; changed();
    }
    public boolean isContainer(ItemStack stack) { return !Stacks.empty(servingContainer) && Stacks.same(servingContainer, stack); }
    private void serveStored() {
        if (Stacks.empty(inventory[6])) return;
        boolean needsContainer = !Stacks.empty(servingContainer);
        if (needsContainer && !isContainer(inventory[7])) return;
        if (!Stacks.empty(inventory[8]) && !Stacks.same(inventory[8], inventory[6])) return;
        int room = Math.min(64, inventory[6].getMaxStackSize()) - Stacks.size(inventory[8]);
        int amount = Math.min(room, Stacks.size(inventory[6]));
        if (needsContainer) amount = Math.min(amount, Stacks.size(inventory[7]));
        if (amount <= 0) return;
        if (Stacks.empty(inventory[8])) inventory[8] = Stacks.copy(inventory[6], amount);
        else Stacks.count(inventory[8], Stacks.size(inventory[8]) + amount);
        Stacks.shrink(inventory[6], amount);
        if (needsContainer) Stacks.shrink(inventory[7], amount);
        if (Stacks.empty(inventory[6])) inventory[6] = Stacks.none();
        changed();
    }
    public ItemStack serve(ItemStack held, boolean creative) {
        if (Stacks.empty(inventory[6]) || !isContainer(held)) return Stacks.none();
        ItemStack result = Stacks.copy(inventory[6], 1);
        Stacks.shrink(inventory[6], 1);
        if (Stacks.empty(inventory[6])) inventory[6] = Stacks.none();
        if (!creative) Stacks.shrink(held, 1);
        changed(); return result;
    }
    @Override public boolean isItemValidForSlot(int slot, ItemStack stack) { return slot < 6 || slot == 7; }
    @Override public int[] getSlotsForFace(EnumFacing side) { return side == EnumFacing.UP || side == null ? INPUTS : SERVING; }
    @Override public boolean canInsertItem(int slot, ItemStack stack, EnumFacing side) { return side == EnumFacing.UP || side == null ? slot < 6 : slot == 7; }
    @Override public boolean canExtractItem(int slot, ItemStack stack, EnumFacing side) { return side == EnumFacing.UP || side == null ? slot < 6 : slot == 8; }
    public void drop(ItemStack stack) {
        if (Stacks.empty(stack) || world.isRemote) return;
        EntityItem item = new EntityItem(world, pos.getX() + 0.5, pos.getY() + 0.8, pos.getZ() + 0.5, stack);
        item.motionX = 0.15; item.motionY = 0.2; item.motionZ = 0;
        world.spawnEntity(item);
    }
    public void payExperience() {
        if (world.isRemote) return;
        Map<String, Integer> payout = new LinkedHashMap<String, Integer>(usedRecipes);
        usedRecipes.clear(); super.markDirty();
        for (Map.Entry<String, Integer> entry : payout.entrySet()) {
            CookingRecipes.Recipe used = CookingRecipes.BY_ID.get(entry.getKey());
            if (used == null) continue;
            double value = entry.getValue() * (double) used.experience;
            int xp = (int) Math.min(Integer.MAX_VALUE, Math.floor(value));
            if (xp < Integer.MAX_VALUE && world.rand.nextDouble() < value - Math.floor(value)) xp++;
            if (xp > 0) world.spawnEntity(new EntityXPOrb(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, xp));
        }
    }
    public ItemStack portable() {
        ItemStack pot = new ItemStack(Content.cookingPot);
        NBTTagCompound data = new NBTTagCompound(), carried = new NBTTagCompound();
        if (!Stacks.empty(inventory[6])) inventory[6].writeToNBT(carried);
        data.setTag("Meal", carried);
        NBTTagCompound bowl = new NBTTagCompound(); if (!Stacks.empty(servingContainer)) servingContainer.writeToNBT(bowl);
        data.setTag("Container", bowl); data.setString("CustomName", customName);
        NBTTagCompound root = new NBTTagCompound(); root.setTag("MealData", data); pot.setTagCompound(root);
        return pot;
    }
    public void restoreMeal(ItemStack pot) {
        if (!pot.hasTagCompound() || !pot.getTagCompound().hasKey("MealData", 10)) return;
        NBTTagCompound data = pot.getTagCompound().getCompoundTag("MealData");
        inventory[6] = new ItemStack(data.getCompoundTag("Meal"));
        servingContainer = new ItemStack(data.getCompoundTag("Container"));
        customName = data.getString("CustomName"); changed();
    }
    @Override public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        total = Math.max(1, Math.min(32767, nbt.getInteger("CookTimeTotal")));
        progress = Math.max(0, Math.min(total - 1, nbt.getInteger("CookTime")));
        savedRecipe = nbt.getString("Recipe"); customName = nbt.getString("CustomName");
        servingContainer = new ItemStack(nbt.getCompoundTag("Container"));
        usedRecipes.clear(); NBTTagCompound used = nbt.getCompoundTag("RecipesUsed");
        for (String value : used.getKeySet()) {
            String id = (String) value; int count = used.getInteger(id); if (count > 0) usedRecipes.put(id, count);
        }
        recipeDirty = true;
    }
    @Override public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setInteger("CookTime", progress); nbt.setInteger("CookTimeTotal", total); nbt.setString("Recipe", savedRecipe);
        nbt.setString("CustomName", customName);
        NBTTagCompound container = new NBTTagCompound(); if (!Stacks.empty(servingContainer)) servingContainer.writeToNBT(container);
        nbt.setTag("Container", container);
        NBTTagCompound used = new NBTTagCompound();
        for (Map.Entry<String, Integer> entry : usedRecipes.entrySet()) used.setInteger(entry.getKey(), entry.getValue());
        nbt.setTag("RecipesUsed", used);
        return nbt;
    }
}
