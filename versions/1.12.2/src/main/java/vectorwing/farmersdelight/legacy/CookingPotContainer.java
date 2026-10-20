package vectorwing.farmersdelight.legacy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.IContainerListener;

public final class CookingPotContainer extends Container {
    public final CookingPotTile pot;
    private int previousProgress = -1, previousTotal = -1, previousHeat = -1;
    public CookingPotContainer(InventoryPlayer player, final CookingPotTile pot) {
        this.pot = pot;
        for (int i = 0; i < 6; i++) addSlotToContainer(new Slot(pot, i, 30 + i % 3 * 18, 17 + i / 3 * 18));
        addSlotToContainer(new Slot(pot, 6, 124, 26) {
            public boolean isItemValid(ItemStack stack) { return false; }
            public boolean canTakeStack(EntityPlayer player) { return false; }
        });
        addSlotToContainer(new Slot(pot, 7, 92, 55));
        addSlotToContainer(new Slot(pot, 8, 124, 55) {
            public boolean isItemValid(ItemStack stack) { return false; }
            public ItemStack onTake(EntityPlayer player, ItemStack stack) {
                pot.payExperience();
                return super.onTake(player, stack);
            }
        });
        for (int row = 0; row < 3; row++) for (int column = 0; column < 9; column++)
            addSlotToContainer(new Slot(player, 9 + row * 9 + column, 8 + column * 18, 84 + row * 18));
        for (int column = 0; column < 9; column++) addSlotToContainer(new Slot(player, column, 8 + column * 18, 142));
    }
    @Override public boolean canInteractWith(EntityPlayer player) { return pot.isUsableByPlayer(player); }
    @Override public boolean canDragIntoSlot(Slot slot) { return slot.slotNumber != 6 && slot.slotNumber != 8 && super.canDragIntoSlot(slot); }
    @Override public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        if (index < 0 || index >= inventorySlots.size() || index == 6) return Stacks.none();
        Slot slot = (Slot) inventorySlots.get(index);
        if (!slot.getHasStack()) return Stacks.none();
        ItemStack stack = slot.getStack(), before = stack.copy();
        if (index < 9) {
            if (!mergeItemStack(stack, 9, 45, true)) return Stacks.none();
        } else {
            boolean container = pot.isContainer(stack) || stack.getItem() == net.minecraft.init.Items.BOWL;
            if (container) {
                if (!mergeItemStack(stack, 7, 8, false)) return Stacks.none();
            } else if (!mergeItemStack(stack, 0, 6, false)) return Stacks.none();
        }
        if (Stacks.size(stack) == Stacks.size(before)) return Stacks.none();
        if (Stacks.empty(stack)) slot.putStack(Stacks.none()); else slot.onSlotChanged();
        slot.onTake(player, stack);
        return before;
    }
    @Override public void detectAndSendChanges() {
        super.detectAndSendChanges();
        int heat = pot.heated ? 1 : 0;
        for (IContainerListener value : listeners) {
            IContainerListener listener = value;
            if (previousProgress != pot.progress) listener.sendWindowProperty(this, 0, pot.progress);
            if (previousTotal != pot.total) listener.sendWindowProperty(this, 1, pot.total);
            if (previousHeat != heat) listener.sendWindowProperty(this, 2, heat);
        }
        previousProgress = pot.progress; previousTotal = pot.total; previousHeat = heat;
    }
    @Override public void addListener(IContainerListener listener) {
        super.addListener(listener);
        listener.sendWindowProperty(this, 0, pot.progress);
        listener.sendWindowProperty(this, 1, pot.total);
        listener.sendWindowProperty(this, 2, pot.heated ? 1 : 0);
    }
    @Override public void updateProgressBar(int id, int value) {
        if (id == 0) pot.progress = value;
        if (id == 1) pot.total = value;
        if (id == 2) pot.heated = value != 0;
    }
}
