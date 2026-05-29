package com.dhyanthacker.betterautomation.block.entity.custom;

import com.dhyanthacker.betterautomation.block.entity.ImplementedInventory;
import com.dhyanthacker.betterautomation.block.entity.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class WireBlockEntity extends BlockEntity {
    private static final int MAX_POWER = 1000; // Example max power value
    private int currentPower = 0; // Current power level

    public WireBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.WIRE_BE, pos, state);
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        // implement logic for power transfer including determining direction and amount of power to transfer
        // for now, only transfer in positive X direction as an example
        if (world.getBlockEntity(pos.offset(Direction.Axis.X, 1)) instanceof WireBlockEntity neighbor) {
            int excessPower = neighbor.insertPower(currentPower);
            currentPower -= (currentPower - excessPower); // Reduce current power by the amount transferred
        }
    }

    public int getCurrentPower() {
        return currentPower;
    }

    /// Returns the excess power if the current power exceeds MAX_POWER, otherwise returns 0.
    public int insertPower(int power) {
        if (currentPower + power > MAX_POWER) {
            int excess = (currentPower + power) - MAX_POWER;
            currentPower = MAX_POWER;
            return excess; // Return excess power that couldn't be stored
        } else {
            currentPower += power;
            return 0; // All power was successfully stored
        }
    }

    /// Returns the amount of power extracted,
    /// which may be less than the requested amount if not enough power is available.
    public int extractPower(int amount) {
        if (currentPower >= amount) {
            currentPower -= amount;
            return amount; // Successfully extracted the requested amount of power
        } else {
            int extracted = currentPower; // Extract all available power
            currentPower = 0;
            return extracted; // Return the amount actually extracted
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.putInt("power", currentPower);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        currentPower = nbt.getInt("power");
    }

    @Override
    public @Nullable Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }
}
