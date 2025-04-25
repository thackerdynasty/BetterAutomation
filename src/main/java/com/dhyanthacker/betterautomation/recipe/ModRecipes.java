package com.dhyanthacker.betterautomation.recipe;

import com.dhyanthacker.betterautomation.BetterAutomation;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModRecipes {
    public static final RecipeSerializer<ElectricFurnaceRecipe> ELECTRIC_FURNACE_SERIALIZER = Registry.register(
            Registries.RECIPE_SERIALIZER, Identifier.of(BetterAutomation.MOD_ID, "electric_furnace"),
            new ElectricFurnaceRecipe.Serializer());
    public static final RecipeType<ElectricFurnaceRecipe> ELECTRIC_FURNACE_TYPE = Registry.register(
            Registries.RECIPE_TYPE, Identifier.of(BetterAutomation.MOD_ID, "electric_furnace"),
            new RecipeType<ElectricFurnaceRecipe>() {
                @Override
                public String toString() {
                    return "electric_furnace";
                }
            }
    );

    public static void registerRecipes() {
        BetterAutomation.LOGGER.info("Registering Recipes for " + BetterAutomation.MOD_ID);
    }
}
