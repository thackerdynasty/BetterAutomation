package com.dhyanthacker.betterautomation;

import com.dhyanthacker.betterautomation.screen.ModScreenHandlers;
import com.dhyanthacker.betterautomation.screen.custom.screen.SolarPanelScreen;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class BetterAutomationClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HandledScreens.register(ModScreenHandlers.SOLAR_PANEL_SCREEN_HANDLER, SolarPanelScreen::new);
    }
}
