package com.dhyanthacker.betterautomation.block.custom;

import com.dhyanthacker.betterautomation.block.api.PipeType;
import com.dhyanthacker.betterautomation.block.api.PipeableBlockEntity;
import com.dhyanthacker.betterautomation.block.entity.ModBlockEntities;
import com.dhyanthacker.betterautomation.block.entity.custom.PipeBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class PipeBlock extends BlockWithEntity {
    public static final MapCodec<PipeBlock> CODEC = PipeBlock.createCodec(PipeBlock::new);
    public static final DirectionProperty FACING = Properties.FACING;
    public static final EnumProperty<PipeShape> SHAPE = EnumProperty.of("shape", PipeShape.class);

    public PipeBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(FACING, Direction.NORTH).with(SHAPE, PipeShape.STRAIGHT));
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PipeBlockEntity(pos, state);
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, ModBlockEntities.PIPE_BE,
                (world1, pos, state1, blockEntity) -> blockEntity.tick(world1, pos, state1));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(FACING, SHAPE);
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState state = getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().rotateYClockwise());
        return updateConnectionState(state, ctx.getWorld(), ctx.getBlockPos());
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState,
                                                   WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        return updateConnectionState(state, world, pos);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return getConnectedOutlineShape(state);
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        super.onStateReplaced(state, world, pos, newState, moved);

        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof PipeBlockEntity pipe) {
                ItemScatterer.spawn(world, pos, pipe);
                world.updateComparators(pos, this);
            }
        }
    }

    private static BlockState updateConnectionState(BlockState state, BlockView world, BlockPos pos) {
        boolean north = connectsToItems(world, pos.north());
        boolean south = connectsToItems(world, pos.south());
        boolean east = connectsToItems(world, pos.east());
        boolean west = connectsToItems(world, pos.west());
        boolean up = connectsToItems(world, pos.up());
        boolean down = connectsToItems(world, pos.down());

        int horizontalConnections = count(north, south, east, west);
        int verticalConnections = count(up, down);

        if (verticalConnections > 0 && horizontalConnections > 0) {
            return state.with(SHAPE, up ? PipeShape.CORNER_UP : PipeShape.CORNER_DOWN)
                    .with(FACING, getFirstHorizontalDirection(north, south, east, west, state.get(FACING)));
        }

        if (verticalConnections > 0) {
            return state.with(SHAPE, PipeShape.STRAIGHT).with(FACING, Direction.UP);
        }

        if (horizontalConnections >= 2 && hasPerpendicularHorizontalConnection(north, south, east, west)) {
            return state.with(SHAPE, PipeShape.CORNER)
                    .with(FACING, getCornerFacing(north, south, east, west, state.get(FACING)));
        }

        return state.with(SHAPE, PipeShape.STRAIGHT)
                .with(FACING, getStraightFacing(north, south, east, west, state.get(FACING)));
    }

    private static boolean connectsToItems(BlockView world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof PipeBlock) return true;

        BlockEntity entity = world.getBlockEntity(pos);
        if (entity instanceof ChestBlockEntity) return true;
        return entity instanceof PipeableBlockEntity pipeable
                && (pipeable.getInputType() == PipeType.ITEM || pipeable.getOutputType() == PipeType.ITEM);
    }

    private static int count(boolean... values) {
        int count = 0;
        for (boolean value : values) {
            if (value) count++;
        }
        return count;
    }

    private static boolean hasPerpendicularHorizontalConnection(boolean north, boolean south, boolean east,
                                                               boolean west) {
        return (north || south) && (east || west);
    }

    private static Direction getStraightFacing(boolean north, boolean south, boolean east, boolean west,
                                               Direction fallback) {
        if ((north || south) && !(east || west)) return Direction.EAST;
        if ((east || west) && !(north || south)) return Direction.NORTH;
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

    private static Direction getCornerFacing(boolean north, boolean south, boolean east, boolean west,
                                             Direction fallback) {
        if (north && east) return Direction.NORTH;
        if (east && south) return Direction.EAST;
        if (south && west) return Direction.SOUTH;
        if (west && north) return Direction.WEST;
        return fallback.getAxis().isHorizontal() ? fallback : Direction.NORTH;
    }

    private static VoxelShape getConnectedOutlineShape(BlockState state) {
        Direction facing = state.get(FACING);
        return switch (state.get(SHAPE)) {
            case CORNER -> getShapeForDirections(getHorizontalCornerDirections(facing));
            case CORNER_UP -> getShapeForDirections(Direction.UP, getHorizontalFacingOrDefault(facing));
            case CORNER_DOWN -> getShapeForDirections(Direction.DOWN, getHorizontalFacingOrDefault(facing));
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

    private static VoxelShape getShapeForDirections(Direction... directions) {
        VoxelShape shape = Block.createCuboidShape(5, 5, 5, 11, 11, 11);
        for (Direction direction : directions) {
            shape = VoxelShapes.union(shape, getArmShape(direction));
        }
        return shape;
    }

    private static VoxelShape getArmShape(Direction direction) {
        return switch (direction) {
            case NORTH -> Block.createCuboidShape(5, 5, 0, 11, 11, 11);
            case SOUTH -> Block.createCuboidShape(5, 5, 5, 11, 11, 16);
            case EAST -> Block.createCuboidShape(5, 5, 5, 16, 11, 11);
            case WEST -> Block.createCuboidShape(0, 5, 5, 11, 11, 11);
            case UP -> Block.createCuboidShape(5, 5, 5, 11, 16, 11);
            case DOWN -> Block.createCuboidShape(5, 0, 5, 11, 11, 11);
        };
    }

    public enum PipeShape implements StringIdentifiable {
        STRAIGHT("straight"),
        CORNER("corner"),
        CORNER_UP("corner_up"),
        CORNER_DOWN("corner_down");

        private final String name;

        PipeShape(String name) {
            this.name = name;
        }

        @Override
        public String asString() {
            return name;
        }
    }
}
