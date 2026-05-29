package com.dhyanthacker.betterautomation.block.custom;

import com.dhyanthacker.betterautomation.block.api.PipeType;
import com.dhyanthacker.betterautomation.block.api.PipeDirection;
import com.dhyanthacker.betterautomation.block.api.PipeableBlockEntity;
import com.dhyanthacker.betterautomation.block.entity.custom.WireBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class WireBlock extends BlockWithEntity {
    public static final MapCodec<WireBlock> CODEC = createCodec(WireBlock::new);
    public static final DirectionProperty FACING = Properties.FACING;
    public static final EnumProperty<WireShape> SHAPE = EnumProperty.of("shape", WireShape.class);

    public WireBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(FACING, Direction.NORTH).with(SHAPE, WireShape.STRAIGHT));
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return getConnectedOutlineShape(state);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new WireBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return null;
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState state = getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().rotateYClockwise());
        return updateConnectionState(state, ctx.getWorld(), ctx.getBlockPos());
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState,
                                                   WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        WireBlockEntity.invalidateNetworkCaches();
        return updateConnectionState(state, world, pos);
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        super.onStateReplaced(state, world, pos, newState, moved);

        if (state.getBlock() != newState.getBlock()) {
            WireBlockEntity.invalidateNetworkCaches();
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(FACING, SHAPE);
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    private static BlockState updateConnectionState(BlockState state, BlockView world, BlockPos pos) {
        boolean north = connectsToEnergy(world, pos, pos.north());
        boolean south = connectsToEnergy(world, pos, pos.south());
        boolean east = connectsToEnergy(world, pos, pos.east());
        boolean west = connectsToEnergy(world, pos, pos.west());
        boolean up = connectsToEnergy(world, pos, pos.up());
        boolean down = connectsToEnergy(world, pos, pos.down());

        int horizontalConnections = count(north, south, east, west);
        int verticalConnections = count(up, down);

        if (verticalConnections == 2 && horizontalConnections >= 2) {
            return state.with(SHAPE, WireShape.CROSS_VERTICAL)
                    .with(FACING, getVerticalCrossFacing(north, south, east, west, state.get(FACING)));
        }

        if (verticalConnections == 2 && horizontalConnections == 1) {
            return state.with(SHAPE, WireShape.THREEWAY_VERTICAL)
                    .with(FACING, getFirstHorizontalDirection(north, south, east, west, state.get(FACING)));
        }

        if (verticalConnections == 1 && horizontalConnections > 0) {
            return state.with(SHAPE, up ? WireShape.CORNER_UP : WireShape.CORNER_DOWN)
                    .with(FACING, getFirstHorizontalDirection(north, south, east, west, state.get(FACING)));
        }

        if (verticalConnections > 0) {
            return state.with(SHAPE, WireShape.STRAIGHT).with(FACING, Direction.UP);
        }

        if (horizontalConnections == 4) {
            return state.with(SHAPE, WireShape.CROSS).with(FACING, Direction.NORTH);
        }

        if (((north && south) && (east || west)) || ((north || south) && (east && west))) {
            return state.with(SHAPE, WireShape.THREEWAY)
                    .with(FACING, getThreewayFacing(north, south, east, west, state.get(FACING)));
        }

        if ((north || south) && (east || west)) {
            return state.with(SHAPE, WireShape.CORNER)
                    .with(FACING, getCornerFacing(north, south, east, west, state.get(FACING)));
        }

        return state.with(SHAPE, WireShape.STRAIGHT)
                .with(FACING, getStraightFacing(north, south, east, west, state.get(FACING)));
    }

    private static boolean connectsToEnergy(BlockView world, BlockPos wirePos, BlockPos neighborPos) {
        BlockState state = world.getBlockState(neighborPos);
        if (state.getBlock() instanceof WireBlock) return true;

        BlockEntity entity = world.getBlockEntity(neighborPos);
        if (!(entity instanceof PipeableBlockEntity pipeable)) return false;

        return energySideTouchesWire(pipeable.getInputType(), pipeable.getInputDirection(), state, neighborPos, wirePos)
                || energySideTouchesWire(pipeable.getOutputType(), pipeable.getOutputDirection(), state, neighborPos, wirePos);
    }

    private static boolean energySideTouchesWire(PipeType type, @Nullable PipeDirection pipeDirection, BlockState state,
                                                 BlockPos pipeablePos, BlockPos wirePos) {
        if (type != PipeType.ENERGY || pipeDirection == null) return false;
        return pipeablePos.offset(pipeDirection.toDirection(state)).equals(wirePos);
    }

    private static int count(boolean... values) {
        int count = 0;
        for (boolean value : values) {
            if (value) count++;
        }
        return count;
    }

    private static Direction getStraightFacing(boolean north, boolean south, boolean east, boolean west,
                                               Direction fallback) {
        if ((north || south) && !(east || west)) return Direction.EAST;
        if ((east || west) && !(north || south)) return Direction.NORTH;
        return fallback.getAxis().isHorizontal() ? fallback : Direction.NORTH;
    }

    private static Direction getCornerFacing(boolean north, boolean south, boolean east, boolean west,
                                             Direction fallback) {
        if (north && east) return Direction.NORTH;
        if (east && south) return Direction.EAST;
        if (south && west) return Direction.SOUTH;
        if (west && north) return Direction.WEST;
        return fallback.getAxis().isHorizontal() ? fallback : Direction.NORTH;
    }

    private static Direction getThreewayFacing(boolean north, boolean south, boolean east, boolean west,
                                               Direction fallback) {
        if (north && (east && west)) return Direction.NORTH;
        if (north && (east && south)) return Direction.EAST;
        if (south && (east && west)) return Direction.SOUTH;
        if (north && (south && west)) return Direction.WEST;
        return fallback.getAxis().isHorizontal() ? fallback : Direction.NORTH;
    }

    private static Direction getFirstHorizontalDirection(boolean north, boolean south, boolean east, boolean west,
                                                        Direction fallback) {
        if (north) return Direction.NORTH;
        if (east) return Direction.EAST;
        if (south) return Direction.SOUTH;
        if (west) return Direction.WEST;
        return fallback.getAxis().isHorizontal() ? fallback : Direction.NORTH;
    }

    private static Direction getVerticalCrossFacing(boolean north, boolean south, boolean east, boolean west,
                                                   Direction fallback) {
        if (east || west) return Direction.NORTH;
        if (north || south) return Direction.EAST;
        return fallback.getAxis().isHorizontal() ? fallback : Direction.NORTH;
    }

    private static VoxelShape getConnectedOutlineShape(BlockState state) {
        Direction facing = state.get(FACING);
        return switch (state.get(SHAPE)) {
            case CORNER -> getShapeForDirections(getHorizontalCornerDirections(facing));
            case CORNER_UP -> getShapeForDirections(Direction.UP, getHorizontalFacingOrDefault(facing));
            case CORNER_DOWN -> getShapeForDirections(Direction.DOWN, getHorizontalFacingOrDefault(facing));
            case THREEWAY -> getShapeForDirections(getHorizontalThreewayDirections(facing));
            case THREEWAY_VERTICAL -> getShapeForDirections(Direction.UP, Direction.DOWN, getHorizontalFacingOrDefault(facing));
            case CROSS -> getShapeForDirections(Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST);
            case CROSS_VERTICAL -> getVerticalCrossOutlineShape(facing);
            default -> getStraightOutlineShape(facing);
        };
    }

    private static VoxelShape getStraightOutlineShape(Direction facing) {
        if (facing.getAxis() == Direction.Axis.Y) {
            return getShapeForDirections(Direction.UP, Direction.DOWN);
        }

        if (facing == Direction.NORTH || facing == Direction.SOUTH) {
            return getShapeForDirections(Direction.EAST, Direction.WEST);
        }

        return getShapeForDirections(Direction.NORTH, Direction.SOUTH);
    }

    private static VoxelShape getVerticalCrossOutlineShape(Direction facing) {
        if (facing == Direction.EAST || facing == Direction.WEST) {
            return getShapeForDirections(Direction.UP, Direction.DOWN, Direction.NORTH, Direction.SOUTH);
        }

        return getShapeForDirections(Direction.UP, Direction.DOWN, Direction.EAST, Direction.WEST);
    }

    private static Direction getHorizontalFacingOrDefault(Direction facing) {
        return facing.getAxis().isHorizontal() ? facing : Direction.NORTH;
    }

    private static Direction[] getHorizontalCornerDirections(Direction facing) {
        return switch (facing) {
            case NORTH -> new Direction[]{Direction.NORTH, Direction.EAST};
            case EAST -> new Direction[]{Direction.EAST, Direction.SOUTH};
            case SOUTH -> new Direction[]{Direction.SOUTH, Direction.WEST};
            default -> new Direction[]{Direction.WEST, Direction.NORTH};
        };
    }

    private static Direction[] getHorizontalThreewayDirections(Direction facing) {
        return switch (facing) {
            case EAST -> new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH};
            case SOUTH -> new Direction[]{Direction.EAST, Direction.SOUTH, Direction.WEST};
            case WEST -> new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST};
            default -> new Direction[]{Direction.NORTH, Direction.EAST, Direction.WEST};
        };
    }

    private static VoxelShape getShapeForDirections(Direction... directions) {
        VoxelShape shape = Block.createCuboidShape(6, 6, 6, 10, 10, 10);
        for (Direction direction : directions) {
            shape = VoxelShapes.union(shape, getArmShape(direction));
        }
        return shape;
    }

    private static VoxelShape getArmShape(Direction direction) {
        return switch (direction) {
            case NORTH -> Block.createCuboidShape(6, 6, 0, 10, 10, 10);
            case SOUTH -> Block.createCuboidShape(6, 6, 6, 10, 10, 16);
            case EAST -> Block.createCuboidShape(6, 6, 6, 16, 10, 10);
            case WEST -> Block.createCuboidShape(0, 6, 6, 10, 10, 10);
            case UP -> Block.createCuboidShape(6, 6, 6, 10, 16, 10);
            case DOWN -> Block.createCuboidShape(6, 0, 6, 10, 10, 10);
        };
    }

    public enum WireShape implements StringIdentifiable {
        STRAIGHT("straight"),
        CORNER("corner"),
        CORNER_UP("corner_up"),
        CORNER_DOWN("corner_down"),
        THREEWAY("threeway"),
        THREEWAY_VERTICAL("threeway_vertical"),
        CROSS("cross"),
        CROSS_VERTICAL("cross_vertical");

        private final String name;

        WireShape(String name) {
            this.name = name;
        }

        @Override
        public String asString() {
            return name;
        }
    }
}
