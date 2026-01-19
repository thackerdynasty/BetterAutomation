package com.dhyanthacker.betterautomation.datagen;

import com.dhyanthacker.betterautomation.block.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends FabricTagProvider.BlockTagProvider {
    public ModBlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE)
                .add(ModBlocks.LITHIUM_ORE)
                .add(ModBlocks.DEEPSLATE_LITHIUM_ORE)
                .add(ModBlocks.SILICON_ORE)
                .add(ModBlocks.DEEPSLATE_SILICON_ORE)
                .add(ModBlocks.LITHIUM_BLOCK)
                .add(ModBlocks.RAW_LITHIUM_BLOCK)
                .add(ModBlocks.ELECTRIC_FURNACE)
                .add(ModBlocks.SOLAR_PANEL)
                .add(ModBlocks.COAL_GENERATOR);

        getOrCreateTagBuilder(BlockTags.NEEDS_IRON_TOOL)
                .add(ModBlocks.LITHIUM_ORE)
                .add(ModBlocks.DEEPSLATE_LITHIUM_ORE)
                .add(ModBlocks.ELECTRIC_FURNACE)
                .add(ModBlocks.SOLAR_PANEL)
                .add(ModBlocks.COAL_GENERATOR);

        getOrCreateTagBuilder(BlockTags.NEEDS_DIAMOND_TOOL)
                .add(ModBlocks.SILICON_ORE)
                .add(ModBlocks.DEEPSLATE_SILICON_ORE);
    }
}
