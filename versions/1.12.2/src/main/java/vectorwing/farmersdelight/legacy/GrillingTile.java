package vectorwing.farmersdelight.legacy;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public abstract class GrillingTile extends InventoryTile implements net.minecraft.util.ITickable {
    public final int[] progress;
    private final GrillingRecipes.Recipe[] recipes;
    private final String[] savedRecipes;
    private boolean dirtyRecipes = true;
    protected final boolean stove;
    protected GrillingTile(boolean stove) {
        super(stove ? 6 : 1); this.stove = stove; progress = new int[inventory.length];
        recipes = new GrillingRecipes.Recipe[inventory.length]; savedRecipes = new String[inventory.length];
        java.util.Arrays.fill(savedRecipes, "");
    }
    protected int fireAspect() { return 0; }
    @Override public int getInventoryStackLimit() { return stove ? 1 : 64; }
    @Override public void markDirty() { dirtyRecipes = true; super.markDirty(); }
    @Override public boolean isItemValidForSlot(int slot, ItemStack stack) { return GrillingRecipes.find(stack) != null; }
    public boolean blocked() {
        if (!stove) return false;
        if (world.isAirBlock(pos.up())) return false;
        net.minecraft.util.math.AxisAlignedBB box = world.getBlockState(pos.up()).getBoundingBox(world, pos.up());
        return box != null && box.intersects(new net.minecraft.util.math.AxisAlignedBB(0.1875, 0, 0.1875, 0.8125, 0.0625, 0.8125));
    }
    public boolean heated() { return stove ? world.getBlockState(pos).getValue(GrillBlock.LIT) : Heat.heated(world, pos, false); }
    public boolean add(ItemStack held, boolean creative) {
        if (Stacks.empty(held) || GrillingRecipes.find(held) == null || blocked()) return false;
        for (int i = 0; i < inventory.length; i++) {
            int room = Math.min(getInventoryStackLimit(), held.getMaxStackSize()) - Stacks.size(inventory[i]);
            if (room > 0 && (Stacks.empty(inventory[i]) || Stacks.same(inventory[i], held))) {
                int amount = Math.min(room, stove ? 1 : Stacks.size(held));
                if (Stacks.empty(inventory[i])) inventory[i] = Stacks.copy(held, amount);
                else Stacks.count(inventory[i], Stacks.size(inventory[i]) + amount);
                if (!creative) Stacks.shrink(held, amount);
                progress[i] = 0; changed(); return true;
            }
        }
        return false;
    }
    @Override public void update() {
        if (world == null) return;
        if (world.isRemote) {
            if (heated() && (stove || !Stacks.empty(inventory[0])) && world.rand.nextInt(8) == 0)
                world.spawnParticle(stove ? net.minecraft.util.EnumParticleTypes.FLAME : net.minecraft.util.EnumParticleTypes.SMOKE_NORMAL, pos.getX() + 0.5, pos.getY() + (stove ? 0.3 : 0.25), pos.getZ() + 0.5, 0, 0.01, 0);
            return;
        }
        if (blocked()) {
            boolean any = false;
            for (int i = 0; i < inventory.length; i++) if (!Stacks.empty(inventory[i])) {
                ItemStack input = inventory[i]; inventory[i] = Stacks.none(); progress[i] = 0; drop(input); any = true;
            }
            if (any) changed();
            return;
        }
        if (dirtyRecipes) {
            for (int i = 0; i < inventory.length; i++) {
                recipes[i] = GrillingRecipes.find(inventory[i]);
                String id = recipes[i] == null ? "" : recipes[i].id;
                if (!id.equals(savedRecipes[i])) progress[i] = 0;
                savedRecipes[i] = id;
            }
            dirtyRecipes = false;
        }
        boolean hot = heated(), progressed = false, cooked = false;
        if (hot && world.rand.nextInt(100) == 0)
            world.playSound(null,pos,stove ? net.minecraft.init.SoundEvents.BLOCK_FIRE_AMBIENT : Content.sizzleSound,net.minecraft.util.SoundCategory.BLOCKS,0.4F,1F);
        for (int i = 0; i < inventory.length; i++) {
            GrillingRecipes.Recipe recipe = recipes[i];
            if (recipe == null || Stacks.empty(inventory[i])) { progress[i] = 0; continue; }
            if (!hot) { if (progress[i] > 0) { progress[i] = Math.max(0, progress[i] - 2); progressed = true; } continue; }
            progressed = true;
            if (++progress[i] >= (stove ? recipe.time : GrillingRecipes.skilletTime(recipe.time, fireAspect()))) {
                ItemStack result = recipe.output.copy(), remainder = CookingRecipes.remainder(Stacks.copy(inventory[i], 1));
                Stacks.shrink(inventory[i], 1); if (Stacks.empty(inventory[i])) inventory[i] = Stacks.none();
                progress[i] = 0; drop(result); drop(remainder); cooked = true;
            }
        }
        if (cooked) changed(); else if (progressed) super.markDirty();
    }
    public void drop(ItemStack stack) {
        if (Stacks.empty(stack) || world.isRemote) return;
        EntityItem entity = new EntityItem(world, pos.getX() + 0.5, pos.getY() + (stove ? 1.1 : 0.3), pos.getZ() + 0.5, stack);
        entity.motionX = 0; entity.motionY = 0.1; entity.motionZ = 0;
        world.spawnEntity(entity);
    }
    @Override public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt); int[] saved = nbt.getIntArray("CookingTimes");
        for (int i = 0; i < progress.length; i++) {
            progress[i] = i < saved.length ? Math.max(0, Math.min(32766, saved[i])) : 0;
            savedRecipes[i] = nbt.getString("Recipe" + i);
        }
        dirtyRecipes = true;
    }
    @Override public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt); nbt.setIntArray("CookingTimes", progress);
        for (int i = 0; i < progress.length; i++) nbt.setString("Recipe" + i, savedRecipes[i]);
        return nbt;
    }
}
