package com.dhyanthacker.betterautomation.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public record ElectricFurnaceRecipe(Ingredient inputItem, int smeltingTime, ItemStack output) implements Recipe<ElectricFurnaceRecipeInput> {
    public int getSmeltingTime() { return smeltingTime; }

    @Override
    public DefaultedList<Ingredient> getIngredients() {
        DefaultedList<Ingredient> list = DefaultedList.of();
        list.add(inputItem);
        return list;
    }

    @Override
    public boolean matches(ElectricFurnaceRecipeInput input, World world) {
        if (world.isClient()) return false;

        return inputItem.test(input.getStackInSlot(0));
    }

    @Override
    public ItemStack craft(ElectricFurnaceRecipeInput input, RegistryWrapper.WrapperLookup lookup) {
        return output.copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
        return output;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.ELECTRIC_FURNACE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.ELECTRIC_FURNACE_TYPE;
    }

    public static class Serializer implements RecipeSerializer<ElectricFurnaceRecipe> {
        public static final MapCodec<ElectricFurnaceRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("ingredient").forGetter(ElectricFurnaceRecipe::inputItem),
                Codec.INT.fieldOf("smeltingTime").forGetter(ElectricFurnaceRecipe::smeltingTime),
                ItemStack.CODEC.fieldOf("result").forGetter(ElectricFurnaceRecipe::output)
        ).apply(inst, ElectricFurnaceRecipe::new));

        public static final PacketCodec<RegistryByteBuf, ElectricFurnaceRecipe> STREAM_CODEC =
                PacketCodec.tuple(
                        Ingredient.PACKET_CODEC, ElectricFurnaceRecipe::inputItem,
                        PacketCodecs.INTEGER, ElectricFurnaceRecipe::smeltingTime,
                        ItemStack.PACKET_CODEC, ElectricFurnaceRecipe::output,
                        ElectricFurnaceRecipe::new);

        @Override
        public MapCodec<ElectricFurnaceRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, ElectricFurnaceRecipe> packetCodec() {
            return STREAM_CODEC;
        }
    }
}
