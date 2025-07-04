package com.dhyanthacker.betterautomation.block.entity.custom;

import com.dhyanthacker.betterautomation.block.api.PipeDirection;
import com.dhyanthacker.betterautomation.block.api.PipeType;
import com.dhyanthacker.betterautomation.block.api.PipeableBlockEntity;
import com.dhyanthacker.betterautomation.block.custom.ElectricFurnaceBlock;
import com.dhyanthacker.betterautomation.block.entity.ImplementedInventory;
import com.dhyanthacker.betterautomation.block.entity.ModBlockEntities;
import com.dhyanthacker.betterautomation.component.ModDataComponentTypes;
import com.dhyanthacker.betterautomation.recipe.ElectricFurnaceRecipe;
import com.dhyanthacker.betterautomation.recipe.ElectricFurnaceRecipeInput;
import com.dhyanthacker.betterautomation.recipe.ModRecipes;
import com.dhyanthacker.betterautomation.screen.custom.handler.ElectricFurnaceScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.recipe.*;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.stream.Stream;

public class ElectricFurnaceBlockEntity extends PipeableBlockEntity implements ImplementedInventory, ExtendedScreenHandlerFactory<BlockPos> {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(3, ItemStack.EMPTY);

    public static final int INPUT_SLOT = 0;
    public static final int BATTERY_SLOT = 1;
    public static final int OUTPUT_SLOT = 2;

    protected final PropertyDelegate propertyDelegate;
    private int progress = 0;
    private int maxProgress = 300;
    private boolean isWired = false;
    private boolean isPowered = false;

    public ElectricFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ELECTRIC_FURNACE_BE, pos, state);
        propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> ElectricFurnaceBlockEntity.this.progress;
                    case 1 -> ElectricFurnaceBlockEntity.this.maxProgress;
                    case 2 -> ElectricFurnaceBlockEntity.this.isWired ? 1 : 0;
                    case 3 -> ElectricFurnaceBlockEntity.this.isPowered ? 1 : 0;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0: ElectricFurnaceBlockEntity.this.progress = value;
                    case 1: ElectricFurnaceBlockEntity.this.maxProgress = value;
                    case 2: ElectricFurnaceBlockEntity.this.isWired = value == 1;
                    case 3: ElectricFurnaceBlockEntity.this.isPowered = value == 1;
                }
            }

            @Override
            public int size() {
                return 4;
            }
        };
    }

    private int getCookTime(World world) {
        Optional<RecipeEntry<ElectricFurnaceRecipe>> recipe = getCurrentRecipe();
        if (recipe.isPresent()) return recipe.get().value().getSmeltingTime();
        return -1;
	}

    public void tick(World world, BlockPos pos, BlockState state) {
        if (hasPower()) {
            getWorld().setBlockState(getPos(), getWorld().getBlockState(getPos()).with(ElectricFurnaceBlock.LIT, true));
        } else {
            getWorld().setBlockState(getPos(), getWorld().getBlockState(getPos()).with(ElectricFurnaceBlock.LIT, false));
        }
        if (hasRecipe() && hasBattery()) {
            maxProgress = getCookTime(world);
            progress++;
            if (getStack(BATTERY_SLOT).isEmpty() ||
                    getStack(BATTERY_SLOT).get(ModDataComponentTypes.BATTERY_POWER) == null) return;
            Random random = Random.create();
            int powerToUse = UniformIntProvider.create(2, 8).get(random);
            getStack(BATTERY_SLOT).set(ModDataComponentTypes.BATTERY_POWER,
                    getStack(BATTERY_SLOT).get(ModDataComponentTypes.BATTERY_POWER) - powerToUse);
            if (getStack(BATTERY_SLOT).get(ModDataComponentTypes.BATTERY_POWER) <= 0)
                getStack(BATTERY_SLOT).set(ModDataComponentTypes.BATTERY_POWER, 0);
            markDirty(world, pos, state);
            if (progress >= maxProgress) {
                smeltItem();
                resetProgress();
            }
        } else if (hasInputPipe() && hasRecipe()) {
            maxProgress = getCookTime(world);
            progress++;
            Random random = Random.create();
            int powerToUse = UniformIntProvider.create(2, 8).get(random);
            int extractedPower = extractEnergy(powerToUse);
            if (extractedPower < powerToUse) {
                // Not enough energy to continue processing
                resetProgress();
            } else {
                markDirty(world, pos, state);
                if (progress >= maxProgress) {
                    smeltItem();
                    resetProgress();
                }
            }
        } else resetProgress();
        if (hasOutputPipe()) {
            if (!getStack(OUTPUT_SLOT).isEmpty()) {
                ItemStack outputStack = getStack(OUTPUT_SLOT);
                ItemStack stackToTransfer = new ItemStack(outputStack.getItem(), 1);
                if (outputStack.getCount() > 1) {
                    outputStack.setCount(outputStack.getCount() - 1);
                } else {
                    setStack(OUTPUT_SLOT, ItemStack.EMPTY);
                }
                if (!copyToPipe(stackToTransfer)) {
                    outputStack.setCount(outputStack.getCount() + 1);
                    setStack(OUTPUT_SLOT, outputStack); // If transfer fails, revert the output slot
                }
            }
        }
    }

    private boolean hasBattery() {
        ItemStack batteryStack = getStack(BATTERY_SLOT);
        if (batteryStack != null) {
            return batteryStack.get(ModDataComponentTypes.BATTERY_POWER) != null &&
                    batteryStack.get(ModDataComponentTypes.BATTERY_POWER) > 0;
        }
        return false;
    }

    private void smeltItem() {
        Optional<RecipeEntry<ElectricFurnaceRecipe>> recipe = getCurrentRecipe();
        if (recipe.isEmpty()) return;

        ItemStack output = recipe.get().value().output();

        removeStack(INPUT_SLOT, 1);
        setStack(OUTPUT_SLOT, new ItemStack(output.getItem(),
                getStack(OUTPUT_SLOT).getCount() + output.getCount()));
    }

    private boolean hasRecipe() {
        Optional<RecipeEntry<ElectricFurnaceRecipe>> recipe = getCurrentRecipe();
        if (recipe.isEmpty()) return false;

        ItemStack output = recipe.get().value().output();

        return canInsertAmountIntoOutputSlot(output.getCount()) && canInsertItemIntoOutputSlot(output);
    }

    private Optional<RecipeEntry<ElectricFurnaceRecipe>> getCurrentRecipe() {
        return getWorld().getRecipeManager()
                .getFirstMatch(ModRecipes.ELECTRIC_FURNACE_TYPE, new ElectricFurnaceRecipeInput(getStack(INPUT_SLOT)), world);
    }

    private boolean canInsertItemIntoOutputSlot(ItemStack output) {
        return getStack(OUTPUT_SLOT).isEmpty() || getStack(OUTPUT_SLOT).getItem() == output.getItem();
    }

    private boolean canInsertAmountIntoOutputSlot(int count) {
        int maxCount = getStack(OUTPUT_SLOT).isEmpty() ? 64 : getStack(OUTPUT_SLOT).getMaxCount();
        int currentCount = getStack(OUTPUT_SLOT).getCount();

        return maxCount >= currentCount + count;
    }

    private void resetProgress() {
        progress = 0;
        maxProgress = 300;
    }

    private boolean hasPower() {
        if (hasBattery()) {
            isPowered = true;
            isWired = false;
        } else if (hasInputPipe() && getInputType() == PipeType.ENERGY) {
            isPowered = true;
            isWired = true;
        } else {
            isPowered = false;
            isWired = false;
        }
        return hasBattery() || hasInputPipe() && getInputType() == PipeType.ENERGY;
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        Inventories.readNbt(nbt, inventory, registryLookup);
        progress = nbt.getInt("progress");
        maxProgress = nbt.getInt("maxProgress");
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        Inventories.writeNbt(nbt, inventory, registryLookup);
        nbt.putInt("progress", progress);
        nbt.putInt("maxProgress", maxProgress);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public BlockPos getScreenOpeningData(ServerPlayerEntity serverPlayerEntity) {
        return pos;
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("block.betterautomation.electric_furnace");
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new ElectricFurnaceScreenHandler(syncId, playerInventory, this, propertyDelegate);
    }

    @Override
    public @Nullable Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }

    // flip directions because block faces player
    @Override
    public PipeDirection getInputDirection() {
        return PipeDirection.RIGHT;
    }

    @Override
    public PipeDirection getOutputDirection() {
        return PipeDirection.LEFT;
    }

    @Override
    public PipeType getInputType() {
        return PipeType.ENERGY;
    }

    @Override
    public PipeType getOutputType() {
        return PipeType.ITEM;
    }

    @Override
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
}
