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
    private static int networkCacheVersion = 0;

    private int currentPower = 0;
    private int cachedNetworkVersion = -1;
    private int cachedNetworkPower = 0;
    @Nullable
    private List<BlockPos> cachedNetworkPositions = null;

    public WireBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.WIRE_BE, pos, state);
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        // Wire networks are traversed lazily and cached until the wire layout changes.
    }

    public static void invalidateNetworkCaches() {
        networkCacheVersion++;
        if (networkCacheVersion == Integer.MAX_VALUE) {
            networkCacheVersion = 1;
        }
    }

    public int getCurrentPower() {
        return currentPower;
    }

    public int getNetworkPower() {
        World world = getWorld();
        if (world == null) return currentPower;

        if (hasValidNetworkCache()) {
            return cachedNetworkPower;
        }

        List<WireBlockEntity> network = rebuildNetworkCache(world);
        return network.isEmpty() ? currentPower : cachedNetworkPower;
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

        List<WireBlockEntity> network = getConnectedWires(world);
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
        updateNetworkCache(network);
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

        List<WireBlockEntity> network = getConnectedWires(world);
        int remainingAmount = amount;

        for (WireBlockEntity wire : network) {
            if (wire.currentPower <= 0) continue;

            int extractedPower = Math.min(wire.currentPower, remainingAmount);
            wire.setCurrentPower(wire.currentPower - extractedPower);
            remainingAmount -= extractedPower;

            if (remainingAmount == 0) break;
        }

        balancePower(network);
        updateNetworkCache(network);
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

    private List<WireBlockEntity> getConnectedWires(World world) {
        if (hasValidNetworkCache()) {
            List<WireBlockEntity> cachedWires = resolveCachedNetwork(world);
            if (cachedWires != null) {
                return cachedWires;
            }
            clearNetworkCache();
        }

        return rebuildNetworkCache(world);
    }

    private boolean hasValidNetworkCache() {
        return cachedNetworkPositions != null && cachedNetworkVersion == networkCacheVersion;
    }

    @Nullable
    private List<WireBlockEntity> resolveCachedNetwork(World world) {
        List<BlockPos> positions = cachedNetworkPositions;
        if (positions == null) return null;

        List<WireBlockEntity> wires = new ArrayList<>(positions.size());
        for (BlockPos wirePos : positions) {
            if (!(world.getBlockEntity(wirePos) instanceof WireBlockEntity wire)) {
                return null;
            }
            wires.add(wire);
        }

        return wires;
    }

    private List<WireBlockEntity> rebuildNetworkCache(World world) {
        List<WireBlockEntity> network = collectConnectedWires(world, getPos());
        updateNetworkCache(network);
        return network;
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

    private static void updateNetworkCache(List<WireBlockEntity> wires) {
        if (wires.isEmpty()) return;

        List<BlockPos> positions = new ArrayList<>(wires.size());
        int totalPower = 0;

        for (WireBlockEntity wire : wires) {
            positions.add(wire.getPos().toImmutable());
            totalPower += wire.currentPower;
        }

        List<BlockPos> cachedPositions = List.copyOf(positions);
        for (WireBlockEntity wire : wires) {
            wire.cachedNetworkPositions = cachedPositions;
            wire.cachedNetworkPower = totalPower;
            wire.cachedNetworkVersion = networkCacheVersion;
        }
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

    private void setCurrentPower(int power) {
        int clampedPower = Math.max(0, Math.min(MAX_POWER, power));
        if (currentPower == clampedPower) return;

        currentPower = clampedPower;
        markDirty();
    }

    private void clearNetworkCache() {
        cachedNetworkPositions = null;
        cachedNetworkVersion = -1;
        cachedNetworkPower = 0;
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
