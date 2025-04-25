package com.dhyanthacker.betterautomation.compat;

import com.dhyanthacker.betterautomation.block.ModBlocks;
import com.dhyanthacker.betterautomation.compat.electricfurnace.ElectricFurnaceCategory;
import com.dhyanthacker.betterautomation.compat.electricfurnace.ElectricFurnaceDisplay;
import com.dhyanthacker.betterautomation.recipe.ElectricFurnaceRecipe;
import com.dhyanthacker.betterautomation.recipe.ModRecipes;
import com.dhyanthacker.betterautomation.screen.custom.screen.ElectricFurnaceScreen;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;

public class BetterAutomationREIClient implements REIClientPlugin {
    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new ElectricFurnaceCategory());

        registry.addWorkstations(ElectricFurnaceCategory.ELECTRIC_FURNACE, EntryStacks.of(ModBlocks.ELECTRIC_FURNACE));
    }

    @Override
    public void registerScreens(ScreenRegistry registry) {
        registry.registerClickArea(screen -> new Rectangle(((screen.width - 176) / 2) + 78,
                ((screen.height - 166) / 2) + 30, 20, 25), ElectricFurnaceScreen.class,
                ElectricFurnaceCategory.ELECTRIC_FURNACE);
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        registry.registerRecipeFiller(ElectricFurnaceRecipe.class, ModRecipes.ELECTRIC_FURNACE_TYPE,
                ElectricFurnaceDisplay::new);
    }
}
