package com.dhyanthacker.betterautomation;

import com.dhyanthacker.betterautomation.block.ModBlocks;
import com.dhyanthacker.betterautomation.block.entity.ModBlockEntities;
import com.dhyanthacker.betterautomation.component.ModDataComponentTypes;
import com.dhyanthacker.betterautomation.item.ModItemGroups;
import com.dhyanthacker.betterautomation.item.ModItems;
import com.dhyanthacker.betterautomation.recipe.ModRecipes;
import com.dhyanthacker.betterautomation.screen.ModScreenHandlers;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BetterAutomation implements ModInitializer {
	public static final String MOD_ID = "betterautomation";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModItems.registerModItems();
		ModItemGroups.registerItemGroups();

		ModBlocks.registerModBlocks();
		ModBlockEntities.registerBlockEntities();

		ModScreenHandlers.registerScreenHandlers();

		ModDataComponentTypes.registerDataComponentTypes();
		ModRecipes.registerRecipes();
	}
}