package com.dhyanthacker.betterautomation;

import com.dhyanthacker.betterautomation.datagen.ModBlockTagProvider;
import com.dhyanthacker.betterautomation.datagen.ModLootTableProvider;
import com.dhyanthacker.betterautomation.datagen.ModModelProvider;
import com.dhyanthacker.betterautomation.datagen.ModRecipeProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class BetterAutomationDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

		pack.addProvider(ModModelProvider::new);
		pack.addProvider(ModBlockTagProvider::new);
		pack.addProvider(ModLootTableProvider::new);
		pack.addProvider(ModRecipeProvider::new);
	}
}
