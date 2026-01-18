package com.dhyanthacker.betterautomation;

import com.dhyanthacker.betterautomation.block.entity.ModBlockEntities;
import com.dhyanthacker.betterautomation.block.entity.renderer.PipeBlockEntityRenderer;
import com.dhyanthacker.betterautomation.fluid.ModFluids;
import com.dhyanthacker.betterautomation.screen.ModScreenHandlers;
import com.dhyanthacker.betterautomation.screen.custom.screen.CoalGeneratorScreen;
import com.dhyanthacker.betterautomation.screen.custom.screen.ElectricFurnaceScreen;
import com.dhyanthacker.betterautomation.screen.custom.screen.SolarPanelScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.util.Identifier;

public class BetterAutomationClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HandledScreens.register(ModScreenHandlers.SOLAR_PANEL_SCREEN_HANDLER, SolarPanelScreen::new);
        HandledScreens.register(ModScreenHandlers.ELECTRIC_FURNACE_SCREEN_HANDLER, ElectricFurnaceScreen::new);
        HandledScreens.register(ModScreenHandlers.COAL_GENERATOR_SCREEN_HANDLER, CoalGeneratorScreen::new);

        BlockEntityRendererFactories.register(ModBlockEntities.PIPE_BE, PipeBlockEntityRenderer::new);

        FluidRenderHandlerRegistry.INSTANCE.register(ModFluids.STILL_OIL, ModFluids.FLOWING_OIL,
                new SimpleFluidRenderHandler(
                        Identifier.ofVanilla("block/water_still"),
                        Identifier.ofVanilla("block/water_flow"),
                        0xFF202020));
        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(), ModFluids.STILL_OIL, ModFluids.FLOWING_OIL);
    }
}
