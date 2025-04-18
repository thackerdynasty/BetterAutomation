package com.dhyanthacker.betterautomation.component;

import com.dhyanthacker.betterautomation.BetterAutomation;
import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.function.UnaryOperator;

public class ModDataComponentTypes {
    public static final ComponentType<Integer> BATTERY_POWER =
            register("battery_power", integerBuilder -> integerBuilder.codec(Codec.INT));

    private static <T> ComponentType<T> register(String name, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of(BetterAutomation.MOD_ID, name),
                builderOperator.apply(ComponentType.builder()).build());
    }

    public static void registerDataComponentTypes() {
        BetterAutomation.LOGGER.info("Registering Data Component Types for " + BetterAutomation.MOD_ID);
    }
}
