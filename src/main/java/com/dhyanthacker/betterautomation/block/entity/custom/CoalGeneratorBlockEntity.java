package com.dhyanthacker.betterautomation.block.entity.custom;

import com.dhyanthacker.betterautomation.BetterAutomation;
import com.dhyanthacker.betterautomation.block.api.PipeDirection;
import com.dhyanthacker.betterautomation.block.api.PipeType;
import com.dhyanthacker.betterautomation.block.api.PipeableBlockEntity;
import com.dhyanthacker.betterautomation.block.entity.ImplementedInventory;
import com.dhyanthacker.betterautomation.block.entity.ModBlockEntities;
import com.dhyanthacker.betterautomation.screen.custom.handler.CoalGeneratorScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class CoalGeneratorBlockEntity extends PipeableBlockEntity implements ImplementedInventory, ExtendedScreenHandlerFactory<BlockPos> {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);
    private int fuelTime = 0;
    private final int maxFuelTime = 200;
    private int energyStored = 0;
    private final int maxEnergy = 1000;

    protected final PropertyDelegate propertyDelegate;

    public CoalGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.COAL_GENERATOR_BE, pos, state);
        propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> fuelTime;
                    case 1 -> maxFuelTime;
                    case 2 -> energyStored;
                    case 3 -> maxEnergy;
                    default -> 0; // Default case for safety
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> fuelTime = value;
                    case 2 -> energyStored = value;
                }
            }

            @Override
            public int size() {
                return 4;
            }
        };
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        getFuelFromPipe();
        if (hasFuel()) {
            int energy = generateEnergy();
            if (energy > 0) {
                // Insert energy into the output pipe
                int excess = insertEnergy(energy);
                storeExcessEnergy(excess);
            }
        } else {
            // If no fuel, reset fuel time
            fuelTime = 0;
        }
    }

    private int generateEnergy() {
        if (hasFuel()) {
            consumeFuel();
            return 10;
        }

        return 0;
    }

    private boolean hasFuel() {
        return getStack(0).isOf(Items.COAL);
    }

    private void consumeFuel() {
        if (fuelTime < maxFuelTime) {
            fuelTime++;
        } else {
            ItemStack fuel = getStack(0);
            if (fuel.getCount() > 0) {
                fuel.decrement(1); // Consume one coal
                setStack(0, fuel);
                fuelTime = 0; // Reset fuel time
            }
        }
    }

    private void storeExcessEnergy(int excess) {
        energyStored += excess;
        if (energyStored > maxEnergy) {
            energyStored = maxEnergy; // Cap the energy storage
        }
    }

    private void getFuelFromPipe() {
        if (hasInputPipe() && getStack(0).getCount() < 64) {
            ItemStack stack = extractFromPipe();
            ItemStack currentFuel = getStack(0);
            if (currentFuel.getCount() == 0) {
                setStack(0, stack);
                markDirty();
            } else {
                int totalCount = currentFuel.getCount() + stack.getCount();
                int maxStackSize = Math.min(currentFuel.getMaxCount(), 64);
                int insertable = Math.min(maxStackSize, totalCount);
                currentFuel.setCount(insertable);
                setStack(0, currentFuel);
                markDirty();
            }
        }
    }

    @Override
    public PipeDirection getInputDirection() {
        return PipeDirection.LEFT;
    }

    @Override
    public PipeDirection getOutputDirection() {
        return PipeDirection.RIGHT;
    }

    @Override
    public PipeType getInputType() {
        return PipeType.ITEM;
    }

    @Override
    public PipeType getOutputType() {
        return PipeType.ENERGY;
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
        return Text.translatable("block.betterautomation.coal_generator");
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new CoalGeneratorScreenHandler(syncId, playerInventory, this, propertyDelegate);
    }

    @Override
    public ItemStack extractFromPipe() {
        if (hasInputPipe() && getInputType() == PipeType.ITEM) {
            PipeBlockEntity inputPipe = (PipeBlockEntity) this.getWorld().getBlockEntity(
                this.getPos().offset(getInputDirection().toDirection(getWorld().getBlockState(getPos()))));
            if (inputPipe == null || inputPipe.isEmpty()) return ItemStack.EMPTY;
            ItemStack stack = inputPipe.getStack(0);
            if (stack.isEmpty() || !stack.isOf(Items.COAL)) return ItemStack.EMPTY;
            inputPipe.removeStack(0, 1);
            inputPipe.markDirty();
            return stack;
        }
        return ItemStack.EMPTY;
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.putInt("FuelTime", fuelTime);
        nbt.putInt("EnergyStored", energyStored);

        Inventories.writeNbt(nbt, inventory, registryLookup);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        fuelTime = nbt.getInt("FuelTime");
        energyStored = nbt.getInt("EnergyStored");

        Inventories.readNbt(nbt, inventory, registryLookup);
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
