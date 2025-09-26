package com.little_creatures.mod.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class WoodGolem extends MiniGolem {
    private BlockPos targetChest = null;

    public WoodGolem(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level, 3);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new MoveToTargetChestGoal(this));
    }

    public BlockPos getTargetChest() {
        return targetChest;
    }

    public void setTargetChest(BlockPos targetChest) {
        this.targetChest = targetChest;
        if (targetChest == null) {
            this.getNavigation().stop();
            this.setDeltaMovement(0, this.getDeltaMovement().y, 0);
            this.setNoAi(false);
        }
    }

    @Override
    public void resetAllPos() {
        super.resetAllPos();
        setTargetChest(null);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (targetChest != null) {
            tag.putInt("SecTargetX", targetChest.getX());
            tag.putInt("SecTargetY", targetChest.getY());
            tag.putInt("SecTargetZ", targetChest.getZ());
        }
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("SecTargetX")) {
            this.targetChest = new BlockPos(
                    tag.getInt("SecTargetX"),
                    tag.getInt("SecTargetY"),
                    tag.getInt("SecTargetZ")
            );
        }
    }
    public static class MoveToTargetChestGoal extends Goal {
        private final WoodGolem golem;

        public MoveToTargetChestGoal(WoodGolem golem) {
            this.golem = golem;
        }

        @Override
        public boolean canUse() {
            if (golem.getWorkState() == WorkState.DELIVERING) return false;
            if (golem.getTargetChest() == null || golem.isInventoryFull()) return false;
            BlockPos chestPos = golem.getTargetChest();
            if (golem.level().getBlockEntity(chestPos) instanceof net.minecraft.world.Container chest) {
                for (int i = 0; i < chest.getContainerSize(); i++) {
                    if (!chest.getItem(i).isEmpty()) {
                        return true;
                    }
                }
            }
            golem.setWorkState(WorkState.DELIVERING);
            return false;
        }

        @Override
        public void tick() {
            BlockPos target = golem.getTargetChest();
            if (target != null) {
                golem.setWorkState(WorkState.COLLECTING);
                golem.getNavigation().moveTo(
                        target.getX() + 0.5,
                        target.getY() + 1,
                        target.getZ() + 0.5,
                        1.0
                );
                // Wenn nahe genug -> Items ziehen
                if (golem.distanceToSqr(
                        target.getX() + 0.5,
                        target.getY() + 1,
                        target.getZ() + 0.5
                ) < 2.0) {

                    if (golem.level().getBlockEntity(target) instanceof net.minecraft.world.Container chest) {
                        for (int i = 0; i < chest.getContainerSize(); i++) {
                            ItemStack stack = chest.getItem(i);
                            if (!stack.isEmpty()) {
                                ItemStack copy = stack.copy();
                                if (golem.addItem(copy)) {
                                    chest.setItem(i, ItemStack.EMPTY);
                                    chest.setChanged();
                                    if (golem.isInventoryFull()) {
                                        golem.setWorkState(WorkState.DELIVERING);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}