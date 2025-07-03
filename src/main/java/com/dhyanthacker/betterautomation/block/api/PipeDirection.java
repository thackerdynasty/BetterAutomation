package com.dhyanthacker.betterautomation.block.api;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;

public enum PipeDirection {
    LEFT,
    RIGHT,
    UP,
    DOWN,
    FRONT,
    BACK;

    public Direction toDirection(BlockState state) {
        Direction facing = state.get(Properties.FACING);
        return switch (this) {
            case FRONT -> facing;
            case BACK -> facing.getOpposite();
            case LEFT -> facing.rotateYCounterclockwise();
            case RIGHT -> facing.rotateYClockwise();
            case UP -> Direction.UP;
            case DOWN -> Direction.DOWN;
        };
    }
}
