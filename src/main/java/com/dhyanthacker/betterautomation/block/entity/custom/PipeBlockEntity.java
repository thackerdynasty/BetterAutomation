package com.dhyanthacker.betterautomation.block.entity.custom;

import com.dhyanthacker.betterautomation.block.api.PipeType;
import com.dhyanthacker.betterautomation.block.api.PipeableBlockEntity;
import com.dhyanthacker.betterautomation.block.entity.ImplementedInventory;
import com.dhyanthacker.betterautomation.block.entity.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.inventory.Inventories;
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

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;

public class PipeBlockEntity extends BlockEntity implements ImplementedInventory {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);

    private int cooldown = 5;

    public PipeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PIPE_BE, pos, state);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (world.isClient()) return;

        if (cooldown > 0) {
            cooldown--;
            return;
        } else {
            cooldown = 5;
        }

        moveStoredItem();
    }

    private void moveStoredItem() {
        ItemStack stack = getStack(0);
        if (stack.isEmpty()) return;

        if (tryInsertIntoAdjacentChest(stack)) return;
        if (isNextToAcceptingItemInput(getPos())) return;

        PipeBlockEntity nextPipe = findNextPipeTowardDestination(stack);
        if (nextPipe != null) {
            insertIntoPipe(nextPipe);
            nextPipe.cooldown = cooldown;
        }
    }

    private boolean tryInsertIntoAdjacentChest(ItemStack stack) {
        World world = getWorld();
        if (world == null) return false;

        for (Direction direction : Direction.values()) {
            BlockEntity entity = world.getBlockEntity(getPos().offset(direction));
            if (entity instanceof ChestBlockEntity chest && canInsertIntoChest(chest, stack)) {
                return insertIntoChest(chest);
            }
        }

        return false;
    }

    private record PipeRouteNode(PipeBlockEntity pipe, PipeBlockEntity firstStep) {}

    @Nullable
    private PipeBlockEntity findNextPipeTowardDestination(ItemStack stack) {
        World world = getWorld();
        if (world == null) return null;

        Set<BlockPos> visited = new HashSet<>();
        ArrayDeque<PipeRouteNode> queue = new ArrayDeque<>();

        visited.add(getPos());
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = getPos().offset(direction);
            if (!visited.add(neighborPos)) continue;

            BlockEntity entity = world.getBlockEntity(neighborPos);
            if (entity instanceof PipeBlockEntity pipe && pipe.isEmpty()) {
                if (isDestinationPipe(pipe.getPos(), stack)) return pipe;
                queue.add(new PipeRouteNode(pipe, pipe));
            }
        }

        while (!queue.isEmpty()) {
            PipeRouteNode node = queue.poll();
            for (Direction direction : Direction.values()) {
                BlockPos nextPos = node.pipe().getPos().offset(direction);
                if (!visited.add(nextPos)) continue;

                BlockEntity entity = world.getBlockEntity(nextPos);
                if (entity instanceof PipeBlockEntity nextPipe && nextPipe.isEmpty()) {
                    if (isDestinationPipe(nextPipe.getPos(), stack)) return node.firstStep();
                    queue.add(new PipeRouteNode(nextPipe, node.firstStep()));
                }
            }
        }

        return null;
    }

    private boolean isDestinationPipe(BlockPos pipePos, ItemStack stack) {
        World world = getWorld();
        if (world == null) return false;

        if (isNextToAcceptingItemInput(pipePos)) return true;

        for (Direction direction : Direction.values()) {
            BlockEntity entity = world.getBlockEntity(pipePos.offset(direction));
            if (entity instanceof ChestBlockEntity chest && canInsertIntoChest(chest, stack)) {
                return true;
            }
        }

        return false;
    }

    private boolean isNextToAcceptingItemInput(BlockPos pipePos) {
        World world = getWorld();
        if (world == null) return false;

        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = pipePos.offset(direction);
            BlockEntity entity = world.getBlockEntity(neighborPos);
            if (!(entity instanceof PipeableBlockEntity pipeable)) continue;
            if (pipeable.getInputType() != PipeType.ITEM || pipeable.getInputDirection() == null) continue;

            Direction inputDirection = pipeable.getInputDirection().toDirection(world.getBlockState(neighborPos));
            if (neighborPos.offset(inputDirection).equals(pipePos)) return true;
        }

        return false;
    }

    private boolean canInsertIntoChest(ChestBlockEntity chest, ItemStack stack) {
        for (int i = 0; i < chest.size(); i++) {
            ItemStack chestStack = chest.getStack(i);
            if (chestStack.isEmpty()) return true;
            if (chestStack.getItem() == stack.getItem() && chestStack.getCount() < chestStack.getMaxCount()) {
                return true;
            }
        }

        return false;
    }

    private boolean insertIntoChest(ChestBlockEntity chest) {
        ItemStack stack = getStack(0);
        for (int i = 0; i < chest.size(); i++) {
            ItemStack chestStack = chest.getStack(i);
            if (chestStack.isEmpty()) {
                chest.setStack(i, stack.copy());
                chest.markDirty();
                setStack(0, ItemStack.EMPTY);
                markDirty();
                return true;
            }

            if (chestStack.getItem() == stack.getItem() && chestStack.getCount() < chestStack.getMaxCount()) {
                int insertedCount = Math.min(stack.getCount(), chestStack.getMaxCount() - chestStack.getCount());
                chestStack.increment(insertedCount);
                stack.decrement(insertedCount);
                chest.markDirty();
                if (stack.isEmpty()) {
                    setStack(0, ItemStack.EMPTY);
                }
                markDirty();
                return true;
            }
        }

        return false;
    }

    private void insertIntoPipe(PipeBlockEntity pipe) {
        ItemStack stack = getStack(0);
        for (int i = 0; i < pipe.size(); i++) {
            if (pipe.getStack(i).isEmpty() || pipe.getStack(i).getItem() == stack.getItem() &&
                    pipe.getStack(i).getMaxCount() != pipe.getStack(i).getCount()) {
                pipe.setStack(i, stack.copyWithCount(stack.getCount() + pipe.getStack(i).getCount()));
                pipe.markDirty();
                setStack(0, ItemStack.EMPTY);
                markDirty();
                break;
            }
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        Inventories.writeNbt(nbt, inventory, registryLookup);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        Inventories.readNbt(nbt, inventory, registryLookup);
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction side) {
        ItemStack previousStack = getStack(slot);
        return previousStack.isEmpty() && stack.getCount() == 1;
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
