package com.dhyanthacker.betterautomation.block.entity.custom;

import com.dhyanthacker.betterautomation.block.entity.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WireBlockEntity extends BlockEntity {
    private static final int MAX_POWER = 1000;
    private int currentPower = 0;

    public WireBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.WIRE_BE, pos, state);
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (world.isClient()) return;

        List<WireBlockEntity> network = collectConnectedWires(world, pos);
        if (isNetworkOrigin(network, pos)) {
            balancePower(network);
        }
    }

    public int getCurrentPower() {
        return currentPower;
    }

    public int getNetworkPower() {
        World world = getWorld();
        if (world == null) return currentPower;

        int totalPower = 0;
        for (WireBlockEntity wire : collectConnectedWires(world, getPos())) {
            totalPower += wire.currentPower;
        }
        return totalPower;
    }

    public boolean hasPower(int requiredPower) {
        return requiredPower <= 0 || getNetworkPower() >= requiredPower;
    }

    /**
     * Inserts power into this wire's whole connected network.
     *
     * @return power that could not fit in the network
     */
    public int insertPower(int power) {
        if (power <= 0) return 0;

        World world = getWorld();
        if (world == null) return insertIntoThisWire(power);

        List<WireBlockEntity> network = collectConnectedWires(world, getPos());
        int remainingPower = power;

        for (WireBlockEntity wire : network) {
            int availableSpace = MAX_POWER - wire.currentPower;
            if (availableSpace <= 0) continue;

            int insertedPower = Math.min(availableSpace, remainingPower);
            wire.setCurrentPower(wire.currentPower + insertedPower);
            remainingPower -= insertedPower;

            if (remainingPower == 0) break;
        }

        balancePower(network);
        return remainingPower;
    }

    /**
     * Extracts power from this wire's whole connected network.
     *
     * @return power actually extracted, which may be less than requested
     */
    public int extractPower(int amount) {
        if (amount <= 0) return 0;

        World world = getWorld();
        if (world == null) return extractFromThisWire(amount);

        List<WireBlockEntity> network = collectConnectedWires(world, getPos());
        int remainingAmount = amount;

        for (WireBlockEntity wire : network) {
            if (wire.currentPower <= 0) continue;

            int extractedPower = Math.min(wire.currentPower, remainingAmount);
            wire.setCurrentPower(wire.currentPower - extractedPower);
            remainingAmount -= extractedPower;

            if (remainingAmount == 0) break;
        }

        balancePower(network);
        return amount - remainingAmount;
    }

    private int insertIntoThisWire(int power) {
        int availableSpace = MAX_POWER - currentPower;
        int insertedPower = Math.min(availableSpace, power);
        setCurrentPower(currentPower + insertedPower);
        return power - insertedPower;
    }

    private int extractFromThisWire(int amount) {
        int extractedPower = Math.min(currentPower, amount);
        setCurrentPower(currentPower - extractedPower);
        return extractedPower;
    }

    private static List<WireBlockEntity> collectConnectedWires(World world, BlockPos startPos) {
        List<WireBlockEntity> connectedWires = new ArrayList<>();
        Set<BlockPos> visitedPositions = new HashSet<>();
        ArrayDeque<BlockPos> positionsToVisit = new ArrayDeque<>();

        visitedPositions.add(startPos);
        positionsToVisit.add(startPos);

        while (!positionsToVisit.isEmpty()) {
            BlockPos currentPos = positionsToVisit.removeFirst();
            if (!(world.getBlockEntity(currentPos) instanceof WireBlockEntity wire)) continue;

            connectedWires.add(wire);
            for (Direction direction : Direction.values()) {
                BlockPos neighborPos = currentPos.offset(direction);
                if (!visitedPositions.add(neighborPos)) continue;

                if (world.getBlockEntity(neighborPos) instanceof WireBlockEntity) {
                    positionsToVisit.add(neighborPos);
                }
            }
        }

        return connectedWires;
    }

    private static void balancePower(List<WireBlockEntity> wires) {
        if (wires.isEmpty()) return;

        int totalPower = 0;
        for (WireBlockEntity wire : wires) {
            totalPower += wire.currentPower;
        }

        int powerPerWire = totalPower / wires.size();
        int remainder = totalPower % wires.size();

        for (WireBlockEntity wire : wires) {
            int newPower = powerPerWire;
            if (remainder > 0) {
                newPower++;
                remainder--;
            }
            wire.setCurrentPower(newPower);
        }
    }

    private static boolean isNetworkOrigin(List<WireBlockEntity> wires, BlockPos pos) {
        for (WireBlockEntity wire : wires) {
            if (compareBlockPositions(wire.getPos(), pos) < 0) return false;
        }
        return true;
    }

    private static int compareBlockPositions(BlockPos first, BlockPos second) {
        int xComparison = Integer.compare(first.getX(), second.getX());
        if (xComparison != 0) return xComparison;

        int yComparison = Integer.compare(first.getY(), second.getY());
        if (yComparison != 0) return yComparison;

        return Integer.compare(first.getZ(), second.getZ());
    }

    private void setCurrentPower(int power) {
        int clampedPower = Math.max(0, Math.min(MAX_POWER, power));
        if (currentPower == clampedPower) return;

        currentPower = clampedPower;
        markDirty();
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.putInt("power", currentPower);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        currentPower = Math.max(0, Math.min(MAX_POWER, nbt.getInt("power")));
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
