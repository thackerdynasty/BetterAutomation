package com.dhyanthacker.betterautomation.screen.custom.screen;

import com.dhyanthacker.betterautomation.BetterAutomation;
import com.dhyanthacker.betterautomation.screen.custom.handler.ElectricFurnaceScreenHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ElectricFurnaceScreen extends HandledScreen<ElectricFurnaceScreenHandler> {
    public static final Identifier GUI_TEXTURE =
            Identifier.of(BetterAutomation.MOD_ID, "textures/gui/electric_furnace/electric_furnace_gui.png");
    public static final Identifier BATTERY_TEXTURE =
            Identifier.of(BetterAutomation.MOD_ID, "textures/gui/battery_progress.png");
    public static final Identifier ARROW_TEXTURE =
            Identifier.of(BetterAutomation.MOD_ID, "textures/gui/arrow_progress.png");

    public ElectricFurnaceScreen(ElectricFurnaceScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);

        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        context.drawTexture(GUI_TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);

        drawBattery(context, x, y);
        renderArrowProgress(context, x, y);
    }

    private void drawBattery(DrawContext context, int x, int y) {
        int batteryPixelSize = 16;
        int scaled = handler.getScaledBattery(); // Value from 0 (empty) to 16 (full)
        int yOffset = batteryPixelSize - scaled; // Invert for bottom-up rendering

        // Draw only the filled portion, starting from the bottom
        context.drawTexture(
            BATTERY_TEXTURE,
            x + 59, y + 35 + yOffset, // Y position moves up as battery fills
            0, yOffset,               // Texture V offset
            10, scaled,               // Width, Height to draw
            10, batteryPixelSize      // Texture total size
        );
    }

    private void renderArrowProgress(DrawContext context, int x, int y) {
        if (handler.isSmelting()) {
            context.drawTexture(ARROW_TEXTURE, x + 79, y + 35, 0, 0,
                    handler.getScaledArrowProgress(), 16, 24, 16);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }
}
