package com.dhyanthacker.betterautomation.datagen;

import com.dhyanthacker.betterautomation.block.ModBlocks;
import com.dhyanthacker.betterautomation.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class ModLootTableProvider extends FabricBlockLootTableProvider {
    public ModLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        addDrop(ModBlocks.SOLAR_PANEL);
        addDrop(ModBlocks.ELECTRIC_FURNACE);
        addDrop(ModBlocks.LITHIUM_BLOCK);
        addDrop(ModBlocks.LITHIUM_ORE, oreDrops(ModBlocks.LITHIUM_ORE, ModItems.RAW_LITHIUM));
        addDrop(ModBlocks.DEEPSLATE_LITHIUM_ORE, oreDrops(ModBlocks.DEEPSLATE_LITHIUM_ORE, ModItems.RAW_LITHIUM));
        addDrop(ModBlocks.WIRE);
        addDrop(ModBlocks.PIPE);
        addDrop(ModBlocks.COAL_GENERATOR);
    }
}
