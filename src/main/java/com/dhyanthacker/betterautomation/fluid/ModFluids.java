package com.dhyanthacker.betterautomation.fluid;

import com.dhyanthacker.betterautomation.BetterAutomation;
import com.dhyanthacker.betterautomation.fluid.custom.OilFluid;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModFluids {
    public static final FlowableFluid STILL_OIL = Registry.register(Registries.FLUID,
            Identifier.of(BetterAutomation.MOD_ID, "oil"),
            new OilFluid.Still());
    public static final FlowableFluid FLOWING_OIL = Registry.register(Registries.FLUID,
            Identifier.of(BetterAutomation.MOD_ID, "flowing_oil"),
            new OilFluid.Flowing());

    public static void registerFluids() {
        BetterAutomation.LOGGER.info("Registering Mod Fluids for " + BetterAutomation.MOD_ID);
    }
}
