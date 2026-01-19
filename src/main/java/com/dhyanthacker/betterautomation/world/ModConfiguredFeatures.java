package com.dhyanthacker.betterautomation.world;

import com.dhyanthacker.betterautomation.BetterAutomation;
import com.dhyanthacker.betterautomation.block.ModBlocks;
import com.dhyanthacker.betterautomation.fluid.ModFluids;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.structure.rule.TagMatchRuleTest;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.stateprovider.WeightedBlockStateProvider;

import java.util.List;

public class ModConfiguredFeatures {
    public static final RegistryKey<ConfiguredFeature<?, ?>> LITHIUM_ORE_KEY = registerKey("lithium_ore");
    public static final RegistryKey<ConfiguredFeature<?, ?>> OIL_SPRING_KEY = registerKey("oil_spring");
    public static final RegistryKey<ConfiguredFeature<?, ?>> OIL_LAKE_KEY = registerKey("oil_lake");
    public static final RegistryKey<ConfiguredFeature<?, ?>> SILICON_ORE_KEY = registerKey("silicon_ore");

    public static void bootstrap(Registerable<ConfiguredFeature<?, ?>> context) {
        RuleTest stoneReplaceables = new TagMatchRuleTest(BlockTags.STONE_ORE_REPLACEABLES);
        RuleTest deepslateReplaceables = new TagMatchRuleTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);

        List<OreFeatureConfig.Target> lithiumOres =
                List.of(OreFeatureConfig.createTarget(stoneReplaceables, ModBlocks.LITHIUM_ORE.getDefaultState()),
                        OreFeatureConfig.createTarget(deepslateReplaceables, ModBlocks.DEEPSLATE_LITHIUM_ORE.getDefaultState()));
        List<OreFeatureConfig.Target> siliconOres =
                List.of(OreFeatureConfig.createTarget(stoneReplaceables, ModBlocks.SILICON_ORE.getDefaultState()),
                        OreFeatureConfig.createTarget(deepslateReplaceables, ModBlocks.DEEPSLATE_SILICON_ORE.getDefaultState()));

        register(context, LITHIUM_ORE_KEY, Feature.ORE, new OreFeatureConfig(lithiumOres, 8));
        register(context, SILICON_ORE_KEY, Feature.ORE, new OreFeatureConfig(siliconOres, 4));

        RegistryEntryList<Block> springReplaceables = RegistryEntryList.of(
                RegistryEntry.of(Blocks.STONE),
                RegistryEntry.of(Blocks.DEEPSLATE)
        );

        register(context, OIL_SPRING_KEY, Feature.SPRING_FEATURE,
                new SpringFeatureConfig(
                        ModFluids.STILL_OIL.getDefaultState(),
                        true,
                        4,
                        1,
                        springReplaceables));
        register(context, OIL_LAKE_KEY, Feature.LAKE,
                new LakeFeature.Config(
                        BlockStateProvider.of(ModBlocks.OIL),
                        BlockStateProvider.of(Blocks.STONE)
                ));
    }

    public static RegistryKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, Identifier.of(BetterAutomation.MOD_ID, name));
    }

    private static <FC extends FeatureConfig, F extends Feature<FC>> void register(Registerable<ConfiguredFeature<?, ?>> context,
                                                                                   RegistryKey<ConfiguredFeature<?, ?>> key,
                                                                                   F feature, FC config) {
        context.register(key, new ConfiguredFeature<>(feature, config));
    }
}
