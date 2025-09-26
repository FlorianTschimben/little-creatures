package com.little_creatures.mod.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
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
        this.goalSelector.addGoal(2, new MoveToTargetChestGoal(this));
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
            return golem.getTargetChest() != null;
        }

        @Override
        public void tick() {
            BlockPos target = golem.getTargetChest();
            if (target != null) {
                golem.getNavigation().moveTo(
                        target.getX() + 0.5,
                        target.getY() + 1,
                        target.getZ() + 0.5,
                        1.0
                );
            } else {
                golem.getNavigation().stop();
                golem.setDeltaMovement(0, golem.getDeltaMovement().y, 0);
                golem.setNoAi(false);
            }
        }
    }
}