package com.dhyanthacker.betterautomation.datagen;

import com.dhyanthacker.betterautomation.block.ModBlocks;
import com.dhyanthacker.betterautomation.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;

public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.SOLAR_PANEL);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.LITHIUM_ORE);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.ELECTRIC_FURNACE);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.LITHIUM_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.PIPE);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.WIRE);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(ModItems.BATTERY, Models.GENERATED);
        itemModelGenerator.register(ModItems.LITHIUM, Models.GENERATED);
        itemModelGenerator.register(ModItems.RAW_LITHIUM, Models.GENERATED);
    }
}
