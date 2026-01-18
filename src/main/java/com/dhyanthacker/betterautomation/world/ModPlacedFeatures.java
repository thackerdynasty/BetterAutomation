package com.dhyanthacker.betterautomation.world;

import com.dhyanthacker.betterautomation.BetterAutomation;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.placementmodifier.*;

import java.util.List;

public class ModPlacedFeatures {
    public static final RegistryKey<PlacedFeature> LITHIUM_ORE_PLACED_KEY = registerKey("lithium_ore_placed");
    public static final RegistryKey<PlacedFeature> OIL_SPRING_PLACED_KEY = registerKey("oil_spring_placed");
    public static final RegistryKey<PlacedFeature> OIL_LAKE_PLACED_KEY = registerKey("oil_lake_placed");

    public static void bootstrap(Registerable<PlacedFeature> context) {
        var configuredFeatures = context.getRegistryLookup(RegistryKeys.CONFIGURED_FEATURE);

        register(context, LITHIUM_ORE_PLACED_KEY,
                configuredFeatures.getOrThrow(ModConfiguredFeatures.LITHIUM_ORE_KEY),
                ModOrePlacement.modifiersWithCount(10,
                        HeightRangePlacementModifier.trapezoid(YOffset.fixed(-60), YOffset.fixed(30))));

        register(context, OIL_SPRING_PLACED_KEY,
                configuredFeatures.getOrThrow(ModConfiguredFeatures.OIL_SPRING_KEY),
                List.of(CountPlacementModifier.of(5),
                        HeightRangePlacementModifier.trapezoid(YOffset.fixed(-32), YOffset.fixed(32))));

        register(context, OIL_LAKE_PLACED_KEY,
                configuredFeatures.getOrThrow(ModConfiguredFeatures.OIL_LAKE_KEY),
                List.of(CountPlacementModifier.of(1),
                        HeightRangePlacementModifier.trapezoid(YOffset.fixed(-42), YOffset.fixed(42))));
    }

    public static RegistryKey<PlacedFeature> registerKey(String name) {
        return RegistryKey.of(RegistryKeys.PLACED_FEATURE, Identifier.of(BetterAutomation.MOD_ID, name));
    }

    private static void register(Registerable<PlacedFeature> context, RegistryKey<PlacedFeature> key,
                                 RegistryEntry<ConfiguredFeature<?, ?>> config, List<PlacementModifier> modifiers) {
        context.register(key, new PlacedFeature(config, List.copyOf(modifiers)));
    }

    private static <FC extends FeatureConfig, F extends Feature<FC>> void register(Registerable<PlacedFeature> context,
                                                                                   RegistryKey<PlacedFeature> key,
                                                                                   RegistryEntry<ConfiguredFeature<?, ?>> config,
                                                                                   PlacementModifier... modifiers) {
        register(context, key, config, List.of(modifiers));
    }
}
