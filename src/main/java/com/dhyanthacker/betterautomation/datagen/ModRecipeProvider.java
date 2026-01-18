package com.dhyanthacker.betterautomation.datagen;

import com.dhyanthacker.betterautomation.block.ModBlocks;
import com.dhyanthacker.betterautomation.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
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
        // wire recipe: gold + rubber(to be added) -> wire
        // electric furnace recipe: furnace + circuit board(new recipe) + copper -> electric furnace
        // circuit board recipe: plastic + copper + gold -> circuit board
        // plastic recipe:
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, ModBlocks.PIPE, 4)
                .pattern("IGI")
                .pattern("IGI")
                .pattern("IGI")
                .input('I', Items.IRON_INGOT)
                .input('G', Items.GLASS)
                .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT))
                .criterion(hasItem(Items.GLASS), conditionsFromItem(Items.GLASS))
                .offerTo(recipeExporter);
    }
}
