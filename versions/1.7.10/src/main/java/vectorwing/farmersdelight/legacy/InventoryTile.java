package vectorwing.farmersdelight.legacy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;

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
        if (worldObj != null && !worldObj.isRemote) {
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord); worldObj.func_147453_f(xCoord, yCoord, zCoord, getBlockType());
        }
    }
    public boolean isUseableByPlayer(EntityPlayer player) {
        return worldObj.getTileEntity(xCoord, yCoord, zCoord) == this
                && player.getDistanceSq(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5) <= 64;
    }
    public String getInventoryName() { return "container.farmersdelight"; }
    public boolean hasCustomInventoryName() { return false; }
    public void openInventory() {}
    public void closeInventory() {}
    public ItemStack getStackInSlotOnClosing(int slot) { ItemStack result = inventory[slot]; inventory[slot] = Stacks.none(); return result; }
    public int[] getAccessibleSlotsFromSide(int side) { int[] slots = new int[inventory.length]; for (int i = 0; i < slots.length; i++) slots[i] = i; return slots; }
    public boolean canInsertItem(int slot, ItemStack stack, int side) { return isItemValidForSlot(slot, stack); }
    public boolean canExtractItem(int slot, ItemStack stack, int side) { return true; }
    @Override public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        java.util.Arrays.fill(inventory, Stacks.none());
        NBTTagList list = nbt.getTagList("Inventory", 10);
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound entry = list.getCompoundTagAt(i);
            int slot = entry.getByte("Slot") & 255;
            if (slot < inventory.length) inventory[slot] = ItemStack.loadItemStackFromNBT(entry);
        }
    }
    @Override public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        NBTTagList list = new NBTTagList();
        for (int i = 0; i < inventory.length; i++) if (!Stacks.empty(inventory[i])) {
            NBTTagCompound entry = new NBTTagCompound(); entry.setByte("Slot", (byte) i);
            inventory[i].writeToNBT(entry); list.appendTag(entry);
        }
        nbt.setTag("Inventory", list);

    }
    @Override public Packet getDescriptionPacket() { NBTTagCompound nbt = new NBTTagCompound(); writeToNBT(nbt); return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, nbt); }
    @Override public void onDataPacket(NetworkManager manager, S35PacketUpdateTileEntity packet) { readFromNBT(packet.func_148857_g()); }
}
