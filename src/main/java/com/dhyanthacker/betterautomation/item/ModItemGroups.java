package com.dhyanthacker.betterautomation.item;

import com.dhyanthacker.betterautomation.BetterAutomation;
import com.dhyanthacker.betterautomation.block.ModBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {
    public static final ItemGroup POWER_GROUP =
            Registry.register(Registries.ITEM_GROUP, Identifier.of(BetterAutomation.MOD_ID, "power"),
                    FabricItemGroup.builder().icon(() -> new ItemStack(ModItems.BATTERY))
                            .displayName(Text.translatable("itemgroup.betterautomation.power"))
                            .entries((displayContext, entries) -> {
                                entries.add(ModBlocks.SOLAR_PANEL);
                                entries.add(ModItems.BATTERY);
                                entries.add(ModBlocks.COAL_GENERATOR);
                            }).build());

    public static final ItemGroup ORES_GROUP =
            Registry.register(Registries.ITEM_GROUP, Identifier.of(BetterAutomation.MOD_ID, "ores"),
                    FabricItemGroup.builder().icon(() -> new ItemStack(ModBlocks.LITHIUM_ORE))
                            .displayName(Text.translatable("itemgroup.betterautomation.ores"))
                            .entries((displayContext, entries) -> {
                                entries.add(ModBlocks.LITHIUM_ORE);
                                entries.add(ModBlocks.DEEPSLATE_LITHIUM_ORE);
                                entries.add(ModItems.RAW_LITHIUM);
                                entries.add(ModItems.LITHIUM);
                                entries.add(ModBlocks.LITHIUM_BLOCK);
                                entries.add(ModBlocks.RAW_LITHIUM_BLOCK);
                            }).build());

    public static final ItemGroup MACHINES_GROUP =
            Registry.register(Registries.ITEM_GROUP, Identifier.of(BetterAutomation.MOD_ID, "machines"),
                    FabricItemGroup.builder().icon(() -> new ItemStack(ModBlocks.ELECTRIC_FURNACE))
                            .displayName(Text.translatable("itemgroup.betterautomation.machines"))
                            .entries((displayContext, entries) -> {
                                entries.add(ModBlocks.ELECTRIC_FURNACE);
                                entries.add(ModItems.OIL_BUCKET);
                            }).build());

    public static final ItemGroup MOVEMENT_GROUP =
            Registry.register(Registries.ITEM_GROUP, Identifier.of(BetterAutomation.MOD_ID, "movement"),
                    FabricItemGroup.builder().icon(() -> new ItemStack(ModBlocks.PIPE))
                            .displayName(Text.translatable("itemgroup.betterautomation.movement"))
                            .entries((displayContext, entries) -> {
                                entries.add(ModBlocks.PIPE);
                                entries.add(ModBlocks.WIRE);
                            }).build());

    public static void registerItemGroups() {
        BetterAutomation.LOGGER.info("Registering Mod Item Groups for " + BetterAutomation.MOD_ID);
    }
}
