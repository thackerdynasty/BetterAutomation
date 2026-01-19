package com.dhyanthacker.betterautomation.item.custom.creative;

import com.dhyanthacker.betterautomation.component.ModDataComponentTypes;
import com.dhyanthacker.betterautomation.item.custom.BatteryItem;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import java.util.List;

public class InfiniteBatteryItem extends BatteryItem {
    public InfiniteBatteryItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("item.betterautomation.infinite_battery.tooltip"));
        tooltip.add(Text.translatable("item.betterautomation.infinite_battery.creative_mode_only"));
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);

        stack.set(ModDataComponentTypes.BATTERY_POWER, 21000); // 1000 over max for screen handlers to draw full
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }
}
