package com.dhyanthacker.betterautomation.block.entity;

import com.dhyanthacker.betterautomation.BetterAutomation;
import com.dhyanthacker.betterautomation.block.ModBlocks;
import com.dhyanthacker.betterautomation.block.entity.custom.ElectricFurnaceBlockEntity;
import com.dhyanthacker.betterautomation.block.entity.custom.SolarPanelBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {
    public static final BlockEntityType<SolarPanelBlockEntity> SOLAR_PANEL_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(BetterAutomation.MOD_ID, "solar_panel_be"),
                    BlockEntityType.Builder.create(SolarPanelBlockEntity::new, ModBlocks.SOLAR_PANEL).build());

    public static final BlockEntityType<ElectricFurnaceBlockEntity> ELECTRIC_FURNACE_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(BetterAutomation.MOD_ID, "electric_furnace_be"),
                    BlockEntityType.Builder.create(ElectricFurnaceBlockEntity::new, ModBlocks.ELECTRIC_FURNACE).build());

    public static void registerBlockEntities() {
        BetterAutomation.LOGGER.info("Registering Block Entities for" + BetterAutomation.MOD_ID);
    }
}
