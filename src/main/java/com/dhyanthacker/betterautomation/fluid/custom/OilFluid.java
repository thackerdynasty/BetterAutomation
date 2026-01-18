package com.dhyanthacker.betterautomation.fluid.custom;

import com.dhyanthacker.betterautomation.block.ModBlocks;
import com.dhyanthacker.betterautomation.fluid.ModFluids;
import com.dhyanthacker.betterautomation.item.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.*;
import net.minecraft.item.Item;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.*;

import java.util.Optional;

public abstract class OilFluid extends FlowableFluid {
    @Override
	public Fluid getFlowing() {
		return ModFluids.FLOWING_OIL;
	}

	@Override
	public Fluid getStill() {
		return ModFluids.STILL_OIL;
	}

	@Override
	public Item getBucketItem() {
		return ModItems.OIL_BUCKET;
	}

//	@Override
//	public void randomDisplayTick(World world, BlockPos pos, FluidState state, Random random) {
//		if (!state.isStill() && !(Boolean)state.get(FALLING)) {
//			if (random.nextInt(64) == 0) {
//				world.playSound(
//					pos.getX() + 0.5,
//					pos.getY() + 0.5,
//					pos.getZ() + 0.5,
//					SoundEvents.BLOCK_WATER_AMBIENT,
//					SoundCategory.BLOCKS,
//					random.nextFloat() * 0.25F + 0.75F,
//					random.nextFloat() + 0.5F,
//					false
//				);
//			}
//		} else if (random.nextInt(10) == 0) {
//			world.addParticle(
//				ParticleTypes.UNDERWATER, pos.getX() + random.nextDouble(), pos.getY() + random.nextDouble(), pos.getZ() + random.nextDouble(), 0.0, 0.0, 0.0
//			);
//		}
//	}

//	@Nullable
//	@Override
//	public ParticleEffect getParticle() {
//		return ParticleTypes.DRIPPING_WATER;
//	}

	@Override
	protected boolean isInfinite(World world) {
		return false;
	}

	@Override
	protected void beforeBreakingBlock(WorldAccess world, BlockPos pos, BlockState state) {
		BlockEntity blockEntity = state.hasBlockEntity() ? world.getBlockEntity(pos) : null;
		Block.dropStacks(state, world, pos, blockEntity);
	}

	@Override
	public int getMaxFlowDistance(WorldView world) {
		return 4;
	}

	@Override
	public BlockState toBlockState(FluidState state) {
		return ModBlocks.OIL.getDefaultState().with(FluidBlock.LEVEL, getBlockStateLevel(state));
	}

	@Override
	public boolean matchesType(Fluid fluid) {
		return fluid == ModFluids.STILL_OIL || fluid == ModFluids.FLOWING_OIL;
	}

	@Override
	public int getLevelDecreasePerBlock(WorldView world) {
		return 1;
	}

	@Override
	public int getTickRate(WorldView world) {
		return 5;
	}

	@Override
	public boolean canBeReplacedWith(FluidState state, BlockView world, BlockPos pos, Fluid fluid, Direction direction) {
		// Water flowing downward pushes oil out
        if (fluid.isIn(FluidTags.WATER)) {
            return direction == Direction.DOWN;
        }

        // Lava always replaces oil
        return fluid.isIn(FluidTags.LAVA);
	}

	@Override
	protected float getBlastResistance() {
		return 100.0F;
	}

	@Override
	public Optional<SoundEvent> getBucketFillSound() {
		return Optional.of(SoundEvents.ITEM_BUCKET_FILL);
	}

	public static class Flowing extends OilFluid {
		@Override
		protected void appendProperties(StateManager.Builder<Fluid, FluidState> builder) {
			super.appendProperties(builder);
			builder.add(LEVEL);
		}

		@Override
		public int getLevel(FluidState state) {
			return state.get(LEVEL);
		}

		@Override
		public boolean isStill(FluidState state) {
			return false;
		}
	}

	public static class Still extends OilFluid {
		@Override
		public int getLevel(FluidState state) {
			return 8;
		}

		@Override
		public boolean isStill(FluidState state) {
			return true;
		}
	}
}
