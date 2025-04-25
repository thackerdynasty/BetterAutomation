package com.dhyanthacker.betterautomation.block.custom;
import com.dhyanthacker.betterautomation.BetterAutomation;
import com.dhyanthacker.betterautomation.block.entity.ModBlockEntities;
import com.dhyanthacker.betterautomation.block.entity.custom.ElectricFurnaceBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ElectricFurnaceBlock extends BlockWithEntity {
    public static final MapCodec<ElectricFurnaceBlock> CODEC = ElectricFurnaceBlock.createCodec(ElectricFurnaceBlock::new);

    public ElectricFurnaceBlock(Settings settings) {
		super(settings);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient()) {
            NamedScreenHandlerFactory factory = ((ElectricFurnaceBlockEntity) world.getBlockEntity(pos));
            if (factory != null) {
                BetterAutomation.LOGGER.info("Opening Handled Screen!");
                player.openHandledScreen(factory);
            }
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ElectricFurnaceBlockEntity(pos, state);
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, ModBlockEntities.ELECTRIC_FURNACE_BE,
                (world1, pos, state1, blockEntity) -> blockEntity.tick(world1, pos, state1));
    }
}
