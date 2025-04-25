package com.dhyanthacker.betterautomation.block;

import com.dhyanthacker.betterautomation.BetterAutomation;
import com.dhyanthacker.betterautomation.block.custom.ElectricFurnaceBlock;
import com.dhyanthacker.betterautomation.block.custom.SolarPanelBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.ExperienceDroppingBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.UniformIntProvider;

public class ModBlocks {
    public static final Block SOLAR_PANEL = registerBlock("solar_panel",
            new SolarPanelBlock(AbstractBlock.Settings.create()));

    public static final Block LITHIUM_ORE = registerBlock("lithium_ore",
            new ExperienceDroppingBlock(UniformIntProvider.create(2, 7), AbstractBlock.Settings.create().requiresTool()
                    .strength(3f, 3f)));

    public static final Block LITHIUM_BLOCK = registerBlock("lithium_block",
            new Block(AbstractBlock.Settings.create().strength(5f).requiresTool()));

    public static final Block ELECTRIC_FURNACE = registerBlock("electric_furnace",
            new ElectricFurnaceBlock(AbstractBlock.Settings.create().requiresTool().strength(4f)));

    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, Identifier.of(BetterAutomation.MOD_ID, name), block);
    }
    private static Block registerBlockWithoutBlockItem(String name, Block block) {
        return Registry.register(Registries.BLOCK, Identifier.of(BetterAutomation.MOD_ID, name), block);
    }

    private static void registerBlockItem(String name, Block block) {
        Registry.register(Registries.ITEM, Identifier.of(BetterAutomation.MOD_ID, name),
                new BlockItem(block, new Item.Settings()));
    }

    public static void registerModBlocks() {
        BetterAutomation.LOGGER.info("Registering Mod Blocks for " + BetterAutomation.MOD_ID);
    }
}
