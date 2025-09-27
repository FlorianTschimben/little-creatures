package com.little_creatures.mod.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class ClayGolem extends MiniGolem{
    public ClayGolem(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level, 3);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new CollectItemsGoal(this));
    }

    public static class CollectItemsGoal extends Goal {
        private final MiniGolem golem;
        private List<ItemEntity> itemQueue = new ArrayList<>();
        private int currentIndex = 0;
        private int stuckTicks = 0;
        private double lastDist = Double.MAX_VALUE;

        public CollectItemsGoal(MiniGolem golem) {
            this.golem = golem;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return golem.getTargetPos() != null
                    && golem.getWorkState() != WorkState.DELIVERING;
        }

        @Override
        public void start() {
            refillQueue();
            currentIndex = 0;
            stuckTicks = 0;
            lastDist = Double.MAX_VALUE;
        }

        @Override
        public void tick() {
            BlockPos chestPos = golem.getTargetPos();
            if (chestPos == null) return;

            if (golem.isInventoryFull()) {
                golem.setWorkState(WorkState.DELIVERING);
                return;
            }

            if (itemQueue.isEmpty() || currentIndex >= itemQueue.size()) {
                golem.setWorkState(WorkState.DELIVERING);
                return;
            }

            ItemEntity currentTarget = itemQueue.get(currentIndex);

            if (currentTarget == null || !currentTarget.isAlive() || currentTarget.getItem().isEmpty()) {
                currentIndex++;
                stuckTicks = 0;
                lastDist = Double.MAX_VALUE;
                return;
            }

            golem.getNavigation().moveTo(currentTarget, 1.0);

            double dist = golem.distanceToSqr(currentTarget);

            if (dist < 3.0) {
                ItemStack stack = currentTarget.getItem().copy();
                ItemStack remaining = golem.getInventory().addItem(stack);

                if (remaining.isEmpty()) {
                    currentTarget.discard();
                } else {
                    currentTarget.setItem(remaining);
                }

                currentIndex++;
                stuckTicks = 0;
                lastDist = Double.MAX_VALUE;
                return;
            }
            if (dist >= lastDist - 0.1) stuckTicks++;
            else stuckTicks = 0;
            lastDist = dist;

            if (stuckTicks > 20) {
                currentIndex++;
                stuckTicks = 0;
                lastDist = Double.MAX_VALUE;
            }
        }

        private void refillQueue() {
            BlockPos chestPos = golem.getTargetPos();
            if (chestPos == null) return;

            double radius = 5;
            itemQueue = golem.level().getEntitiesOfClass(
                    ItemEntity.class,
                    new net.minecraft.world.phys.AABB(
                            chestPos.getX() - radius, chestPos.getY() - 2, chestPos.getZ() - radius,
                            chestPos.getX() + radius, chestPos.getY() + 2, chestPos.getZ() + radius
                    )
            );
        }
    }

}
