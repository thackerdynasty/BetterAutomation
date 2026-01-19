package com.dhyanthacker.betterautomation.datagen;

import com.dhyanthacker.betterautomation.block.ModBlocks;
import com.dhyanthacker.betterautomation.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends FabricRecipeProvider {
    public ModRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generate(RecipeExporter recipeExporter) {
        offerReversibleCompactingRecipes(recipeExporter, RecipeCategory.MISC, ModItems.LITHIUM,
                RecipeCategory.BUILDING_BLOCKS, ModBlocks.LITHIUM_BLOCK);
        offerReversibleCompactingRecipes(recipeExporter, RecipeCategory.MISC, ModItems.RAW_LITHIUM,
                RecipeCategory.BUILDING_BLOCKS, ModBlocks.RAW_LITHIUM_BLOCK);

        offerSmelting(recipeExporter, List.of(ModItems.OIL_BUCKET), RecipeCategory.MISC, ModItems.RUBBER, 0.1f, 200, "oil_bucket");
        offerBlasting(recipeExporter, List.of(ModItems.OIL_BUCKET), RecipeCategory.MISC, ModItems.RUBBER, 0.1f, 100, "oil_bucket");

        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, ModItems.BATTERY)
                .pattern(" I ")
                .pattern("ILI")
                .pattern("ILI")
                .input('I', Items.IRON_INGOT)
                .input('L', ModItems.LITHIUM)
                .criterion(hasItem(ModItems.LITHIUM), conditionsFromItem(ModItems.LITHIUM))
                .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT))
                .offerTo(recipeExporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, ModBlocks.SOLAR_PANEL)
                .pattern("GGG")
                .pattern("LBL")
                .pattern("III")
                .input('G', Items.GLASS)
                .input('L', ModItems.LITHIUM)
                .input('B', ModItems.BATTERY)
                .input('I', Items.IRON_INGOT)
                .criterion(hasItem(Items.GLASS), conditionsFromItem(Items.GLASS))
                .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT))
                .criterion(hasItem(ModItems.LITHIUM), conditionsFromItem(ModItems.LITHIUM))
                .criterion(hasItem(ModItems.BATTERY), conditionsFromItem(ModItems.BATTERY))
                .offerTo(recipeExporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, ModBlocks.COAL_GENERATOR)
                .pattern("FFF")
                .pattern("FWF")
                .pattern("FFF")
                .input('F', Blocks.FURNACE)
                .input('W', ModBlocks.WIRE)
                .criterion(hasItem(Blocks.FURNACE), conditionsFromItem(Blocks.FURNACE))
                .criterion(hasItem(ModBlocks.WIRE), conditionsFromItem(ModBlocks.WIRE))
                .offerTo(recipeExporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, ModBlocks.WIRE, 4)
                .pattern("RRR")
                .pattern("GGG")
                .pattern("RRR")
                .input('R', ModItems.RUBBER)
                .input('G', Items.GOLD_INGOT)
                .criterion(hasItem(Items.GOLD_INGOT), conditionsFromItem(Items.GOLD_INGOT))
                .criterion(hasItem(ModItems.RUBBER), conditionsFromItem(ModItems.RUBBER))
                .offerTo(recipeExporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, ModItems.CIRCUIT_BOARD)
                .pattern("WWW")
                .pattern("PGP")
                .pattern("WWW")
                .input('W', ModBlocks.WIRE)
                .input('P', ModItems.PLASTIC)
                .input('G', Items.GOLD_INGOT)
                .criterion(hasItem(ModBlocks.WIRE), conditionsFromItem(ModBlocks.WIRE))
                .criterion(hasItem(ModItems.PLASTIC), conditionsFromItem(ModItems.PLASTIC))
                .criterion(hasItem(Items.GOLD_INGOT), conditionsFromItem(Items.GOLD_INGOT))
                .offerTo(recipeExporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, ModBlocks.ELECTRIC_FURNACE)
                .pattern("IGI")
                .pattern("FCF")
                .pattern("IGI")
                .input('C', ModItems.CIRCUIT_BOARD)
                .input('F', Items.FURNACE)
                .input('I', Items.IRON_INGOT)
                .input('G', Items.COPPER_INGOT)
                .criterion(hasItem(Items.FURNACE), conditionsFromItem(Items.FURNACE))
                .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT))
                .criterion(hasItem(Items.COPPER_INGOT), conditionsFromItem(Items.COPPER_INGOT))
                .criterion(hasItem(ModItems.CIRCUIT_BOARD), conditionsFromItem(ModItems.CIRCUIT_BOARD))
                .offerTo(recipeExporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, ModBlocks.PIPE, 4)
                .pattern("IGI")
                .pattern("IGI")
                .pattern("IGI")
                .input('I', Items.IRON_INGOT)
                .input('G', Items.GLASS)
                .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT))
                .criterion(hasItem(Items.GLASS), conditionsFromItem(Items.GLASS))
                .offerTo(recipeExporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, ModItems.PLASTIC, 8)
                .pattern("CCC")
                .pattern("COC")
                .pattern("CCC")
                .input('C', Items.COAL)
                .input('O', ModItems.OIL_BUCKET)
                .criterion(hasItem(Items.COAL), conditionsFromItem(Items.COAL))
                .criterion(hasItem(ModItems.OIL_BUCKET), conditionsFromItem(ModItems.OIL_BUCKET))
                .offerTo(recipeExporter);
    }
}
