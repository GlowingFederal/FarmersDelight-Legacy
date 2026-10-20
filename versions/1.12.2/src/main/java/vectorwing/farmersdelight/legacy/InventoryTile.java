package vectorwing.farmersdelight.legacy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

/** Native persistent inventory; callers perform mutations only on the owning world thread. */
public abstract class InventoryTile extends TileEntity implements ISidedInventory {
    protected final ItemStack[] inventory;
    protected InventoryTile(int size) {
        inventory = new ItemStack[size];
        java.util.Arrays.fill(inventory, Stacks.none());
    }
    public int getSizeInventory() { return inventory.length; }
    public ItemStack getStackInSlot(int slot) { return inventory[slot]; }
    public ItemStack decrStackSize(int slot, int amount) {
        if (Stacks.empty(inventory[slot]) || amount <= 0) return Stacks.none();
        ItemStack result = Stacks.split(inventory[slot], Math.min(amount, Stacks.size(inventory[slot])));
        if (Stacks.empty(inventory[slot])) inventory[slot] = Stacks.none();
        changed(); return result;
    }
    public void setInventorySlotContents(int slot, ItemStack stack) { inventory[slot] = Stacks.empty(stack) ? Stacks.none() : stack; changed(); }
    public int getInventoryStackLimit() { return 64; }
    public boolean isItemValidForSlot(int slot, ItemStack stack) { return true; }
    public void changed() {
        markDirty();
        if (world != null && !world.isRemote) {
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3); world.updateComparatorOutputLevel(pos, getBlockType());
        }
    }
    public boolean isUsableByPlayer(EntityPlayer player) {
        return world.getTileEntity(pos) == this
                && player.getDistanceSq(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 64;
    }
    public String getName() { return "container.farmersdelight"; }
    public boolean hasCustomName() { return false; }
    public ITextComponent getDisplayName() { return new TextComponentTranslation(getName()); }
    public boolean isEmpty() { for (ItemStack stack : inventory) if (!Stacks.empty(stack)) return false; return true; }
    public ItemStack removeStackFromSlot(int slot) { ItemStack result = inventory[slot]; inventory[slot] = Stacks.none(); changed(); return result; }
    public void openInventory(EntityPlayer player) {}
    public void closeInventory(EntityPlayer player) {}
    public int getField(int id) { return 0; }
    public void setField(int id, int value) {}
    public int getFieldCount() { return 0; }
    public void clear() { java.util.Arrays.fill(inventory, Stacks.none()); changed(); }
    public int[] getSlotsForFace(EnumFacing side) { int[] slots = new int[inventory.length]; for (int i = 0; i < slots.length; i++) slots[i] = i; return slots; }
    public boolean canInsertItem(int slot, ItemStack stack, EnumFacing side) { return isItemValidForSlot(slot, stack); }
    public boolean canExtractItem(int slot, ItemStack stack, EnumFacing side) { return true; }
    @Override public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        java.util.Arrays.fill(inventory, Stacks.none());
        NBTTagList list = nbt.getTagList("Inventory", 10);
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound entry = list.getCompoundTagAt(i);
            int slot = entry.getByte("Slot") & 255;
            if (slot < inventory.length) inventory[slot] = new ItemStack(entry);
        }
    }
    @Override public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        NBTTagList list = new NBTTagList();
        for (int i = 0; i < inventory.length; i++) if (!Stacks.empty(inventory[i])) {
            NBTTagCompound entry = new NBTTagCompound(); entry.setByte("Slot", (byte) i);
            inventory[i].writeToNBT(entry); list.appendTag(entry);
        }
        nbt.setTag("Inventory", list);
        return nbt;
    }
    @Override public NBTTagCompound getUpdateTag() { return writeToNBT(new NBTTagCompound()); }
    @Override public SPacketUpdateTileEntity getUpdatePacket() { return new SPacketUpdateTileEntity(pos, 0, getUpdateTag()); }
    @Override public void onDataPacket(NetworkManager manager, SPacketUpdateTileEntity packet) { readFromNBT(packet.getNbtCompound()); }
    private final IItemHandler[] handlers = new IItemHandler[6];
    @Override public boolean hasCapability(Capability<?> capability, EnumFacing side) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, side);
    }
    @Override public <T> T getCapability(Capability<T> capability, EnumFacing side) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            EnumFacing face = side == null ? EnumFacing.UP : side;
            if (handlers[face.ordinal()] == null) handlers[face.ordinal()] = new SidedInvWrapper(this, face);
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(handlers[face.ordinal()]);
        }
        return super.getCapability(capability, side);
    }
}
