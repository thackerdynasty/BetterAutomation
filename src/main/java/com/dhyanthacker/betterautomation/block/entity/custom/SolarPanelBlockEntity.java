package com.dhyanthacker.betterautomation.block.entity.custom;

import com.dhyanthacker.betterautomation.BetterAutomation;
import com.dhyanthacker.betterautomation.block.api.PipeDirection;
import com.dhyanthacker.betterautomation.block.api.PipeType;
import com.dhyanthacker.betterautomation.block.api.PipeableBlockEntity;
import com.dhyanthacker.betterautomation.block.entity.ImplementedInventory;
import com.dhyanthacker.betterautomation.block.entity.ModBlockEntities;
import com.dhyanthacker.betterautomation.component.ModDataComponentTypes;
import com.dhyanthacker.betterautomation.item.ModItems;
import com.dhyanthacker.betterautomation.screen.custom.handler.SolarPanelScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SolarPanelBlockEntity extends PipeableBlockEntity implements ImplementedInventory, ExtendedScreenHandlerFactory<BlockPos> {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);

    public SolarPanelBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SOLAR_PANEL_BE, pos, state);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        // only work in day
        if (world.isNight())
            return;

        if (inventory.get(0).isOf(ModItems.BATTERY)) {
            ItemStack battery = inventory.get(0);
            if (battery.get(ModDataComponentTypes.BATTERY_POWER) == null)
                battery.set(ModDataComponentTypes.BATTERY_POWER, 0);
            if (battery.get(ModDataComponentTypes.BATTERY_POWER) < 20000) {
                int newPower = battery.get(ModDataComponentTypes.BATTERY_POWER) + 17;
                if (newPower > 20000) {
                    newPower = 20000;
                }
                battery.set(ModDataComponentTypes.BATTERY_POWER, newPower);
            }
        } else if (hasOutputPipe()) {
            insertEnergy(17);
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        Inventories.writeNbt(nbt, inventory, registryLookup);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
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

    @Override
    public BlockPos getScreenOpeningData(ServerPlayerEntity serverPlayerEntity) {
        return pos;
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("block.betterautomation.solar_panel");
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new SolarPanelScreenHandler(syncId, playerInventory, pos);
    }

    @Override
    public PipeDirection getInputDirection() {
        return null;
    }

    @Override
    public PipeDirection getOutputDirection() {
        return PipeDirection.RIGHT;
    }

    @Override
    public PipeType getInputType() {
        return null;
    }

    @Override
    public PipeType getOutputType() {
        return PipeType.ENERGY;
    }
}
