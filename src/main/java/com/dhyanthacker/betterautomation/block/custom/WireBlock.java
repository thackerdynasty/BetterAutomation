package com.dhyanthacker.betterautomation.block.custom;

import com.dhyanthacker.betterautomation.block.api.PipeType;
import com.dhyanthacker.betterautomation.block.api.PipeableBlockEntity;
import com.dhyanthacker.betterautomation.block.entity.ModBlockEntities;
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
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final EnumProperty<WireShape> SHAPE = EnumProperty.of("shape", WireShape.class);

    public WireBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(FACING, Direction.NORTH).with(SHAPE, WireShape.STRAIGHT));
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Direction facing = state.get(FACING);
        if (state.get(SHAPE) == WireShape.CROSS) {
            return getCrossOutlineShape();
        }

        if (state.get(SHAPE) == WireShape.CORNER) {
            return getCornerOutlineShape(facing);
        }

        if (state.get(SHAPE) == WireShape.THREEWAY) {
            return getThreewayOutlineShape(facing);
        }

        return switch (facing) {
            case NORTH, SOUTH -> Block.createCuboidShape(0, 6, 6, 16, 10, 10);
            default -> Block.createCuboidShape(6, 6, 0, 10, 10, 16);
        };
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
        return validateTicker(type, ModBlockEntities.WIRE_BE,
                (world1, pos, state1, blockEntity) -> blockEntity.tick(world1, pos, state1));
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
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(FACING, SHAPE);
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    private static BlockState updateConnectionState(BlockState state, BlockView world, BlockPos pos) {
        boolean north = connectsToEnergy(world, pos.north());
        boolean south = connectsToEnergy(world, pos.south());
        boolean east = connectsToEnergy(world, pos.east());
        boolean west = connectsToEnergy(world, pos.west());

        if (north && south && east && west) {
            return state.with(SHAPE, WireShape.CROSS)
                    .with(FACING, Direction.NORTH); // Facing doesn't matter for cross shape
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

    private static boolean connectsToEnergy(BlockView world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof WireBlock) return true;

        BlockEntity entity = world.getBlockEntity(pos);
        return entity instanceof PipeableBlockEntity pipeable
                && (pipeable.getInputType() == PipeType.ENERGY || pipeable.getOutputType() == PipeType.ENERGY);
    }

    private static Direction getStraightFacing(boolean north, boolean south, boolean east, boolean west,
                                               Direction fallback) {
        if ((north || south) && !(east || west)) return Direction.EAST;
        if ((east || west) && !(north || south)) return Direction.NORTH;
        return fallback;
    }

    private static Direction getCornerFacing(boolean north, boolean south, boolean east, boolean west,
                                             Direction fallback) {
        if (north && east) return Direction.NORTH;
        if (east && south) return Direction.EAST;
        if (south && west) return Direction.SOUTH;
        if (west && north) return Direction.WEST;
        return fallback;
    }

    private static Direction getThreewayFacing(boolean north, boolean south, boolean east, boolean west,
                                               Direction fallback) {
        if (north && (east && west)) return Direction.NORTH;
        if (north && (east && south)) return Direction.EAST;
        if (south && (east && west)) return Direction.SOUTH;
        if (north && (south && west)) return Direction.WEST;
        return fallback;
    }

    private static VoxelShape getCornerOutlineShape(Direction facing) {
        VoxelShape center = Block.createCuboidShape(6, 6, 6, 10, 10, 10);
        VoxelShape northArm = Block.createCuboidShape(6, 6, 0, 10, 10, 10);
        VoxelShape southArm = Block.createCuboidShape(6, 6, 6, 10, 10, 16);
        VoxelShape eastArm = Block.createCuboidShape(6, 6, 6, 16, 10, 10);
        VoxelShape westArm = Block.createCuboidShape(0, 6, 6, 10, 10, 10);

        return switch (facing) {
            case EAST -> VoxelShapes.union(center, eastArm, southArm);
            case SOUTH -> VoxelShapes.union(center, southArm, westArm);
            case WEST -> VoxelShapes.union(center, westArm, northArm);
            default -> VoxelShapes.union(center, northArm, eastArm);
        };
    }

    private static VoxelShape getThreewayOutlineShape(Direction facing) {
        VoxelShape center = Block.createCuboidShape(6, 6, 6, 10, 10, 10);
        VoxelShape northArm = Block.createCuboidShape(6, 6, 0, 10, 10, 10);
        VoxelShape southArm = Block.createCuboidShape(6, 6, 6, 10, 10, 16);
        VoxelShape eastArm = Block.createCuboidShape(6, 6, 6, 16, 10, 10);
        VoxelShape westArm = Block.createCuboidShape(0, 6, 6, 10, 10, 10);

        return switch (facing) {
            case EAST -> VoxelShapes.union(center, eastArm, northArm, southArm);
            case SOUTH -> VoxelShapes.union(center, southArm, eastArm, westArm);
            case WEST -> VoxelShapes.union(center, westArm, northArm, southArm);
            default -> VoxelShapes.union(center, northArm, eastArm, westArm);
        };
    }

    private static VoxelShape getCrossOutlineShape() {
        VoxelShape center = Block.createCuboidShape(6, 6, 6, 10, 10, 10);
        VoxelShape northArm = Block.createCuboidShape(6, 6, 0, 10, 10, 10);
        VoxelShape southArm = Block.createCuboidShape(6, 6, 6, 10, 10, 16);
        VoxelShape eastArm = Block.createCuboidShape(6, 6, 6, 16, 10, 10);
        VoxelShape westArm = Block.createCuboidShape(0, 6, 6, 10, 10, 10);

        return VoxelShapes.union(center, northArm, southArm, eastArm, westArm);
    }

    public enum WireShape implements StringIdentifiable {
        STRAIGHT("straight"),
        CORNER("corner"),
        THREEWAY("threeway"),
        CROSS("cross");

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
