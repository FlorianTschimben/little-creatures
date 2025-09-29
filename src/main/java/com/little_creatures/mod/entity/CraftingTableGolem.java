package com.little_creatures.mod.entity;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

public class CraftingTableGolem extends MiniGolem {
    private ItemStack savedRecipe = ItemStack.EMPTY;
    public CraftingTableGolem(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level, 9);
    }

    public void setSavedRecipe(ItemStack stack) {
        this.savedRecipe = stack;
    }

    public ItemStack getSavedRecipe() {
        return this.savedRecipe;
    }

    @Override
    public @NotNull InteractionResult mobInteract(@NotNull Player player, @NotNull InteractionHand hand) {
        if (!level().isClientSide && hand == InteractionHand.MAIN_HAND) {
            if (player.getMainHandItem().isEmpty()) {
                MenuProvider provider = new MenuProvider() {
                    @Override
                    public @NotNull Component getDisplayName() {
                        return Component.translatable("container.crafting");
                    }

                    @Override
                    public AbstractContainerMenu createMenu(int id, @NotNull Inventory playerInventory, @NotNull Player player) {
                        return new GolemCraftingMenu(id, playerInventory, ContainerLevelAccess.create(level(), blockPosition()), CraftingTableGolem.this) {
                            @Override
                            public boolean stillValid(@NotNull Player playerIn) {
                                return !CraftingTableGolem.this.isRemoved() && playerIn.distanceTo(CraftingTableGolem.this) < 8.0F;
                            }
                        };
                    }
                };

                NetworkHooks.openScreen((ServerPlayer) player, provider);
                return InteractionResult.CONSUME;
            }
        }
        return super.mobInteract(player, hand);
    }

    public static class GolemCraftingMenu extends CraftingMenu {
        private final CraftingTableGolem golem;

        public GolemCraftingMenu(int id, net.minecraft.world.entity.player.Inventory playerInventory, ContainerLevelAccess access, CraftingTableGolem golem) {
            super(id, playerInventory, access);
            this.golem = golem;
        }

        @Override
        public void slotsChanged(@NotNull Container container) {
            super.slotsChanged(container);
            ItemStack result = this.getSlot(this.getResultSlotIndex()).getItem();
            golem.setSavedRecipe(result.copy());
            if (!result.isEmpty() && !golem.level().isClientSide) {
                Player player = (Player) golem.level().getNearestPlayer(golem, 5);
                if (player != null) {
                    player.displayClientMessage(Component.literal("Golem saved recipe: " + result.getItem().getDescription().getString()), true);
                }
            }
        }
    }
}
