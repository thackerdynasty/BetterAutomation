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
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.DEEPSLATE_LITHIUM_ORE);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.LITHIUM_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.RAW_LITHIUM_BLOCK);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(ModItems.BATTERY, Models.GENERATED);
        itemModelGenerator.register(ModItems.LITHIUM, Models.GENERATED);
        itemModelGenerator.register(ModItems.RAW_LITHIUM, Models.GENERATED);
        itemModelGenerator.register(ModItems.OIL_BUCKET, Models.GENERATED);
        itemModelGenerator.register(ModItems.PLASTIC, Models.GENERATED);
        itemModelGenerator.register(ModItems.CIRCUIT_BOARD, Models.GENERATED);
        itemModelGenerator.register(ModItems.RUBBER, Models.GENERATED);
    }
}
