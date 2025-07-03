package com.dhyanthacker.betterautomation.item.custom;

import com.dhyanthacker.betterautomation.component.ModDataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import java.util.List;

public class BatteryItem extends Item {
    public BatteryItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        if (stack.get(ModDataComponentTypes.BATTERY_POWER) == null) {
            stack.set(ModDataComponentTypes.BATTERY_POWER, 0);
        }

        int power = stack.get(ModDataComponentTypes.BATTERY_POWER);

        tooltip.add(Text.translatable("item.betterautomation.battery.tooltip", power));

        super.appendTooltip(stack, context, tooltip, type);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);

        if (stack.get(ModDataComponentTypes.BATTERY_POWER) == null) {
            stack.set(ModDataComponentTypes.BATTERY_POWER, 0);
        }
        if (stack.get(ModDataComponentTypes.BATTERY_POWER) > 20000) {
            stack.set(ModDataComponentTypes.BATTERY_POWER, 20000);
        }
        if (stack.get(ModDataComponentTypes.BATTERY_POWER) < 0) {
            stack.set(ModDataComponentTypes.BATTERY_POWER, 0);
        }
    }
}
