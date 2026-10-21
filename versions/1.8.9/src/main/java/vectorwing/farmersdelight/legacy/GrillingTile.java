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
        if (worldObj.isAirBlock(pos.up())) return false;
        net.minecraft.util.AxisAlignedBB box = worldObj.getBlockState(pos.up()).getBlock().getCollisionBoundingBox(worldObj,pos.up(),worldObj.getBlockState(pos.up()));
        return box != null && box.intersectsWith(new net.minecraft.util.AxisAlignedBB(pos.getX()+0.1875,pos.getY()+1,pos.getZ()+0.1875,pos.getX()+0.8125,pos.getY()+1.0625,pos.getZ()+0.8125));
    }
    public boolean heated() { return stove ? worldObj.getBlockState(pos).getValue(GrillBlock.LIT) : Heat.heated(worldObj, pos, false); }
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
        if (worldObj == null) return;
        if (worldObj.isRemote) {
            if (heated() && (stove || !Stacks.empty(inventory[0])) && worldObj.rand.nextInt(8) == 0)
                worldObj.spawnParticle(stove ? net.minecraft.util.EnumParticleTypes.FLAME : net.minecraft.util.EnumParticleTypes.SMOKE_NORMAL, pos.getX() + 0.5, pos.getY() + (stove ? 0.3 : 0.25), pos.getZ() + 0.5, 0, 0.01, 0);
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
        if (hot && worldObj.rand.nextInt(100) == 0)
            worldObj.playSoundEffect(pos.getX()+0.5,pos.getY()+0.5,pos.getZ()+0.5,stove ? "fire.fire" : "farmersdelight:block.skillet.sizzle",0.4F,1F);
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
        if (Stacks.empty(stack) || worldObj.isRemote) return;
        EntityItem entity = new EntityItem(worldObj, pos.getX() + 0.5, pos.getY() + (stove ? 1.1 : 0.3), pos.getZ() + 0.5, stack);
        entity.motionX = 0; entity.motionY = 0.1; entity.motionZ = 0;
        worldObj.spawnEntityInWorld(entity);
    }
    @Override public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt); int[] saved = nbt.getIntArray("CookingTimes");
        for (int i = 0; i < progress.length; i++) {
            progress[i] = i < saved.length ? Math.max(0, Math.min(32766, saved[i])) : 0;
            savedRecipes[i] = nbt.getString("Recipe" + i);
        }
        dirtyRecipes = true;
    }
    @Override public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt); nbt.setIntArray("CookingTimes", progress);
        for (int i = 0; i < progress.length; i++) nbt.setString("Recipe" + i, savedRecipes[i]);
        
    }
}
