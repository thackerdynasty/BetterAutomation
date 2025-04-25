package com.dhyanthacker.betterautomation.compat.electricfurnace;

import com.dhyanthacker.betterautomation.BetterAutomation;
import com.dhyanthacker.betterautomation.block.ModBlocks;
import com.dhyanthacker.betterautomation.item.ModItemGroups;
import com.dhyanthacker.betterautomation.item.ModItems;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.LinkedList;
import java.util.List;

public class ElectricFurnaceCategory implements DisplayCategory<BasicDisplay> {
    public static final Identifier TEXTURE = Identifier.of(BetterAutomation.MOD_ID,
            "textures/gui/electric_furnace/electric_furnace_gui.png");
    public static final CategoryIdentifier<ElectricFurnaceDisplay> ELECTRIC_FURNACE =
            CategoryIdentifier.of(BetterAutomation.MOD_ID, "electric_furnace");

    @Override
    public CategoryIdentifier<? extends BasicDisplay> getCategoryIdentifier() {
        return ELECTRIC_FURNACE;
    }

    @Override
    public Text getTitle() {
        return Text.translatable("block.betterautomation.electric_furnace");
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(ModBlocks.ELECTRIC_FURNACE.asItem().getDefaultStack());
    }

    @Override
    public List<Widget> setupDisplay(BasicDisplay display, Rectangle bounds) {
        Point startPoint = new Point(bounds.getCenterX() - 87, bounds.getCenterY() - 35);
        List<Widget> widgets = new LinkedList<>();

        widgets.add(Widgets.createTexturedWidget(TEXTURE, new Rectangle(startPoint.x, startPoint.y, 175, 82)));

        widgets.add(Widgets.createSlot(new Point(startPoint.x + 56, startPoint.y + 17))
                .entries(display.getInputEntries().get(0)).markInput());
        widgets.add(Widgets.createSlot(new Point(startPoint.x + 56, startPoint.y + 53))
                .entries(EntryIngredient.of(EntryStacks.of(ModItems.BATTERY))).markInput());
        widgets.add(Widgets.createSlot(new Point(startPoint.x + 116, startPoint.y + 35))
                .entries(display.getOutputEntries().get(0)).markOutput());

        return widgets;
    }

    @Override
    public int getDisplayHeight() {
        return 90;
    }
}
