package com.dhyanthacker.betterautomation.world.gen;

import com.dhyanthacker.betterautomation.world.ModPlacedFeatures;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;

public class ModFluidGeneration {
    public static void generateFluids() {
        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.FLUID_SPRINGS,
                ModPlacedFeatures.OIL_SPRING_PLACED_KEY);
        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.LAKES,
                ModPlacedFeatures.OIL_LAKE_PLACED_KEY);
    }
}
