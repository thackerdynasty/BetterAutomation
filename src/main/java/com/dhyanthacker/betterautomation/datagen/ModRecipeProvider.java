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
        List<ItemConvertible> LITHIUM_SMELTABLES = List.of(ModItems.RAW_LITHIUM, ModBlocks.LITHIUM_ORE);

        // moved to electric furnace
//        offerSmelting(recipeExporter, LITHIUM_SMELTABLES, RecipeCategory.MISC, ModItems.LITHIUM, .25f, 200, "lithium");
//        offerBlasting(recipeExporter, LITHIUM_SMELTABLES, RecipeCategory.MISC, ModItems.LITHIUM, .25f, 100, "lithium");

        offerReversibleCompactingRecipes(recipeExporter, RecipeCategory.MISC, ModItems.LITHIUM,
                RecipeCategory.BUILDING_BLOCKS, ModBlocks.LITHIUM_BLOCK);

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
    }
}
