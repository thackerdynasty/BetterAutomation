package com.dhyanthacker.betterautomation.block.api;

import com.dhyanthacker.betterautomation.block.entity.ImplementedInventory;
import com.dhyanthacker.betterautomation.block.entity.custom.PipeBlockEntity;
import com.dhyanthacker.betterautomation.block.entity.custom.WireBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public abstract class PipeableBlockEntity extends BlockEntity {
    public PipeableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public abstract PipeDirection getInputDirection();
    public abstract PipeDirection getOutputDirection();
    public abstract PipeType getInputType();
    public abstract PipeType getOutputType();

    public boolean hasInputPipe() {
        PipeDirection dir = getInputDirection();
        BlockPos pos = this.getPos().offset(dir.toDirection(getWorld().getBlockState(getPos())));
        BlockEntity entity = this.getWorld().getBlockEntity(pos);
        if (getInputType() == PipeType.ITEM) {
            return entity instanceof PipeBlockEntity;
        } else if (getInputType() == PipeType.ENERGY) {
            return entity instanceof WireBlockEntity;
        } else {
            return false; // For now, only ITEM and ENERGY pipes are supported
        }
    }
    public boolean hasOutputPipe() {
        PipeDirection dir = getOutputDirection();
        BlockPos pos = this.getPos().offset(dir.toDirection(getWorld().getBlockState(getPos())));
        BlockEntity entity = this.getWorld().getBlockEntity(pos);
        if (getOutputType() == PipeType.ITEM) {
            return entity instanceof PipeBlockEntity;
        } else if (getOutputType() == PipeType.ENERGY) {
            return entity instanceof WireBlockEntity;
        } else {
            return false; // For now, only ITEM and ENERGY pipes are supported
        }
    }

    public boolean copyToPipe(ItemStack stack) {
        if (hasOutputPipe() && getOutputType() == PipeType.ITEM) {
            PipeBlockEntity outputPipe = (PipeBlockEntity) this.getWorld().getBlockEntity(
                this.getPos().offset(getOutputDirection().toDirection(getWorld().getBlockState(getPos()))));
            if (outputPipe == null) return false;
            outputPipe.setStack(0, stack);
            outputPipe.markDirty();
            return true;
        }
        return false;
    }
    public ItemStack extractFromPipe() {
        if (hasInputPipe() && getInputType() == PipeType.ITEM) {
            PipeBlockEntity inputPipe = (PipeBlockEntity) this.getWorld().getBlockEntity(
                this.getPos().offset(getInputDirection().toDirection(getWorld().getBlockState(getPos()))));
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
        if (hasInputPipe() && getInputType() == PipeType.ENERGY) {
            WireBlockEntity inputWire = (WireBlockEntity) this.getWorld().getBlockEntity(
                this.getPos().offset(getInputDirection().toDirection(getWorld().getBlockState(getPos()))));
            if (inputWire == null) return 0;
            int extracted = inputWire.extractPower(amount);
            inputWire.markDirty();
            return extracted;
        }
        return 0;
    }
    public int insertEnergy(int amount) {
        if (hasOutputPipe() && getOutputType() == PipeType.ENERGY) {
            WireBlockEntity outputWire = (WireBlockEntity) this.getWorld().getBlockEntity(
                this.getPos().offset(getOutputDirection().toDirection(getWorld().getBlockState(getPos()))));
            if (outputWire == null) return 0;
            int excess = outputWire.insertPower(amount);
            outputWire.markDirty();
            return excess; // Return excess power that couldn't be stored
        }
        return amount; // If no output pipe, return the full amount as excess
    }
}
