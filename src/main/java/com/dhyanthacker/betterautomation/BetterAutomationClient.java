package com.dhyanthacker.betterautomation;

import com.dhyanthacker.betterautomation.block.entity.ModBlockEntities;
import com.dhyanthacker.betterautomation.block.entity.renderer.PipeBlockEntityRenderer;
import com.dhyanthacker.betterautomation.screen.ModScreenHandlers;
import com.dhyanthacker.betterautomation.screen.custom.screen.ElectricFurnaceScreen;
import com.dhyanthacker.betterautomation.screen.custom.screen.SolarPanelScreen;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

public class BetterAutomationClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HandledScreens.register(ModScreenHandlers.SOLAR_PANEL_SCREEN_HANDLER, SolarPanelScreen::new);
        HandledScreens.register(ModScreenHandlers.ELECTRIC_FURNACE_SCREEN_HANDLER, ElectricFurnaceScreen::new);

        BlockEntityRendererFactories.register(ModBlockEntities.PIPE_BE, PipeBlockEntityRenderer::new);
    }
}
