package com.dhyanthacker.betterautomation.block.entity.custom;

import com.dhyanthacker.betterautomation.block.entity.ImplementedInventory;
import com.dhyanthacker.betterautomation.block.entity.ModBlockEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
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

import java.util.List;

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
        if (cooldown > 0) {
            cooldown--;
            return;
        } else {
            cooldown = 5;
        }
        List<Block> extractableBlocks = List.of(Blocks.CHEST);

        BlockPos negXPos = new BlockPos(pos.getX() - 1, pos.getY(), pos.getZ());
        BlockPos posXPos = new BlockPos(pos.getX() + 1, pos.getY(), pos.getZ());

        if (world.getBlockEntity(negXPos) instanceof ChestBlockEntity) {
            ChestBlockEntity chest = (ChestBlockEntity) world.getBlockEntity(negXPos);
            extractFromChest(chest);
        }
//        else if (world.getBlockEntity(posXPos) instanceof ChestBlockEntity
//            && world.getBlockEntity(negXPos) != null
//            && ((PipeBlockEntity) world.getBlockEntity(negXPos)).isEmpty()) { // check for null
//            ChestBlockEntity chest = (ChestBlockEntity) world.getBlockEntity(posXPos);
//            extractFromChest(chest);
//        }

        insertItem(posXPos, negXPos);
    }

    private void insertItem(BlockPos posXPos, BlockPos negXPos) {
        if (world.getBlockEntity(posXPos) instanceof ChestBlockEntity) {
            ChestBlockEntity chest = ((ChestBlockEntity) world.getBlockEntity(posXPos));
            if (getStack(0) != ItemStack.EMPTY) insertIntoChest(chest);
        } else if (world.getBlockEntity(posXPos) instanceof PipeBlockEntity
            && ((PipeBlockEntity) world.getBlockEntity(posXPos)).isEmpty()) {
            PipeBlockEntity pipe = (PipeBlockEntity) world.getBlockEntity(posXPos);
            if (getStack(0) != ItemStack.EMPTY) insertIntoPipe(pipe);
        }
//        else if (world.getBlockEntity(negXPos) instanceof PipeBlockEntity
//            && ((PipeBlockEntity) world.getBlockEntity(negXPos)).isEmpty()) {
//            PipeBlockEntity pipe = (PipeBlockEntity) world.getBlockEntity(negXPos);
//            if (getStack(0) != ItemStack.EMPTY) insertIntoPipe(pipe);
//        }
    }

    private void extractFromChest(ChestBlockEntity chest) {
        ItemStack stack = ItemStack.EMPTY;
        for (int i = 0; i < chest.size(); i++) {
            if (!chest.getStack(i).isEmpty()) {
                ItemStack chestStack = chest.getStack(i);
                stack = chestStack.copyWithCount(1);
                if (!canInsert(0, stack, null)) {
                    return;
                }
//                chest.setStack(i, new ItemStack(chestStack.getItem(), chestStack.getCount() - 1));
                chest.setStack(i, chestStack.copyWithCount(chestStack.getCount() - 1));
                chest.markDirty();
                break;
            }
        }
        setStack(0, stack.copy());
        markDirty();
    }

    private void insertIntoChest(ChestBlockEntity chest) {
        ItemStack stack = getStack(0);
        for (int i = 0; i < chest.size(); i++) {
            if (chest.getStack(i).isEmpty() || chest.getStack(i).getItem() == stack.getItem() &&
                    chest.getStack(i).getMaxCount() != chest.getStack(i).getCount()) {
                chest.setStack(i, stack.copyWithCount(stack.getCount() + chest.getStack(i).getCount()));
                chest.markDirty();
                setStack(0, ItemStack.EMPTY);
                markDirty();
                break;
            }
        }
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
