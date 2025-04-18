package com.dhyanthacker.betterautomation.item.custom;

import com.dhyanthacker.betterautomation.component.ModDataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;

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
}
