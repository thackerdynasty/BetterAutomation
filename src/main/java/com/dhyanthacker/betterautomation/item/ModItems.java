package com.dhyanthacker.betterautomation.item;

import com.dhyanthacker.betterautomation.BetterAutomation;
import com.dhyanthacker.betterautomation.fluid.ModFluids;
import com.dhyanthacker.betterautomation.item.custom.BatteryItem;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
    public static final Item BATTERY = registerItem("battery", new BatteryItem(new Item.Settings()));
    public static final Item RAW_LITHIUM = registerItem("raw_lithium", new Item(new Item.Settings()));
    public static final Item LITHIUM = registerItem("lithium", new Item(new Item.Settings()));

    public static final Item OIL_BUCKET = registerItem("oil_bucket",
            new BucketItem(ModFluids.STILL_OIL, new Item.Settings().recipeRemainder(Items.BUCKET).maxCount(1)));

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(BetterAutomation.MOD_ID, name), item);
    }

    public static void registerModItems() {
        BetterAutomation.LOGGER.info("Registering Mod Items for " + BetterAutomation.MOD_ID);
    }
}
