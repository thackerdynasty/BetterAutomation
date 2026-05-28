package com.dhyanthacker.betterautomation.block.api;

import com.dhyanthacker.betterautomation.BetterAutomation;
import com.dhyanthacker.betterautomation.block.entity.custom.PipeBlockEntity;
import com.dhyanthacker.betterautomation.block.entity.custom.WireBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public abstract class PipeableBlockEntity extends BlockEntity {
    public PipeableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public abstract PipeDirection getInputDirection();
    public abstract PipeDirection getOutputDirection();
    public abstract PipeType getInputType();
    public abstract PipeType getOutputType();

    public boolean hasInputPipe() {
        PipeType inputType = getInputType();
        if (inputType == PipeType.ITEM) {
            return getDirectionalBlockEntity(getInputDirection()) instanceof PipeBlockEntity;
        } else if (inputType == PipeType.ENERGY) {
            return getDirectionalWire(getInputDirection()) != null;
        } else {
            BetterAutomation.LOGGER.warn("Unsupported pipe type: " + inputType);
            return false;
        }
    }

    public boolean hasOutputPipe() {
        PipeType outputType = getOutputType();
        if (outputType == PipeType.ITEM) {
            return getDirectionalBlockEntity(getOutputDirection()) instanceof PipeBlockEntity;
        } else if (outputType == PipeType.ENERGY) {
            return getDirectionalWire(getOutputDirection()) != null;
        } else {
            BetterAutomation.LOGGER.warn("Unsupported pipe type: " + outputType);
            return false;
        }
    }

    public boolean copyToPipe(ItemStack stack) {
        if (hasOutputPipe() && getOutputType() == PipeType.ITEM) {
            PipeBlockEntity outputPipe = (PipeBlockEntity) getDirectionalBlockEntity(getOutputDirection());
            if (outputPipe == null ||
                    outputPipe.getStack(0).getCount() >= outputPipe.getStack(0).getMaxCount()) return false;
            outputPipe.setStack(0, stack);
            outputPipe.markDirty();
            return true;
        }
        BetterAutomation.LOGGER.warn("Tried to copy to pipe, but no output pipe found or wrong type!");
        return false;
    }
    public ItemStack extractFromPipe() {
        if (hasInputPipe() && getInputType() == PipeType.ITEM) {
            PipeBlockEntity inputPipe = (PipeBlockEntity) getDirectionalBlockEntity(getInputDirection());
            if (inputPipe == null || inputPipe.isEmpty()) return ItemStack.EMPTY;
            ItemStack stack = inputPipe.getStack(0);
            if (stack.isEmpty()) return ItemStack.EMPTY;
            inputPipe.removeStack(0, 1);
            inputPipe.markDirty();
            return stack;
        }
        return ItemStack.EMPTY;
    }

    public int extractEnergy(int amount) {
        if (amount <= 0 || getInputType() != PipeType.ENERGY) return 0;

        WireBlockEntity inputWire = getDirectionalWire(getInputDirection());
        if (inputWire == null) return 0;

        return inputWire.extractPower(amount);
    }

    public int insertEnergy(int amount) {
        if (amount <= 0) return 0;
        if (getOutputType() != PipeType.ENERGY) return amount;

        WireBlockEntity outputWire = getDirectionalWire(getOutputDirection());
        if (outputWire == null) return amount;

        return outputWire.insertPower(amount);
    }

    public boolean inputWireHasPower() {
        return inputWireHasPower(1);
    }

    public boolean inputWireHasPower(int requiredPower) {
        if (getInputType() != PipeType.ENERGY) return false;

        WireBlockEntity inputWire = getDirectionalWire(getInputDirection());
        return inputWire != null && inputWire.hasPower(requiredPower);
    }

    private BlockEntity getDirectionalBlockEntity(PipeDirection direction) {
        World world = getWorld();
        if (world == null || direction == null) return null;

        Direction blockDirection = direction.toDirection(world.getBlockState(getPos()));
        return world.getBlockEntity(getPos().offset(blockDirection));
    }

    private WireBlockEntity getDirectionalWire(PipeDirection direction) {
        BlockEntity entity = getDirectionalBlockEntity(direction);
        if (entity instanceof WireBlockEntity wire) return wire;
        return null;
    }
}
