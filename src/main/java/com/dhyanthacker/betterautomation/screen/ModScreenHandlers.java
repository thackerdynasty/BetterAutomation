package com.dhyanthacker.betterautomation.screen;

import com.dhyanthacker.betterautomation.BetterAutomation;
import com.dhyanthacker.betterautomation.screen.custom.handler.CoalGeneratorScreenHandler;
import com.dhyanthacker.betterautomation.screen.custom.handler.ElectricFurnaceScreenHandler;
import com.dhyanthacker.betterautomation.screen.custom.handler.SolarPanelScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class ModScreenHandlers {
    public static final ScreenHandlerType<SolarPanelScreenHandler> SOLAR_PANEL_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER,
                    Identifier.of(BetterAutomation.MOD_ID, "solar_panel_screen_handler"),
                    new ExtendedScreenHandlerType<>(SolarPanelScreenHandler::new, BlockPos.PACKET_CODEC));
    public static final ScreenHandlerType<ElectricFurnaceScreenHandler> ELECTRIC_FURNACE_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER,
                    Identifier.of(BetterAutomation.MOD_ID, "electric_furnace_screen_handler"),
                    new ExtendedScreenHandlerType<>(ElectricFurnaceScreenHandler::new, BlockPos.PACKET_CODEC));

    public static final ScreenHandlerType<CoalGeneratorScreenHandler> COAL_GENERATOR_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER,
                    Identifier.of(BetterAutomation.MOD_ID, "coal_generator_screen_handler"),
                    new ExtendedScreenHandlerType<>(CoalGeneratorScreenHandler::new, BlockPos.PACKET_CODEC));

    public static void registerScreenHandlers() {
        BetterAutomation.LOGGER.info("Registering screen handlers for " + BetterAutomation.MOD_ID);
    }
}
