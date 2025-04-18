package com.dhyanthacker.betterautomation.slot;

import com.dhyanthacker.betterautomation.item.ModItems;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class BatterySlot extends Slot {
    public BatterySlot(Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Override
    public int getMaxItemCount() {
        return 1;
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return stack.isOf(ModItems.BATTERY);
    }
}
