package com.little_creatures.mod.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

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
        private ItemEntity currentTarget = null;

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
        public void tick() {
            BlockPos chestPos = golem.getTargetPos();
            if (chestPos == null) return;
            if (golem.isInventoryFull()) {
                golem.setWorkState(WorkState.DELIVERING);
                return;
            }
            if (currentTarget == null || !currentTarget.isAlive() || currentTarget.getItem().isEmpty()) {
                currentTarget = findNearestItem(chestPos);
                if (currentTarget == null) {
                    if (findNearestItem(chestPos) == null) {
                        golem.setWorkState(MiniGolem.WorkState.DELIVERING);
                    }
                    return;
                }
            }
            golem.getNavigation().moveTo(currentTarget, 1.0);
            if (golem.distanceTo(currentTarget) < 1.5) {
                ItemEntity item = currentTarget;
                ItemStack stack = item.getItem().copy();
                ItemStack remaining = golem.getInventory().addItem(stack);
                if (remaining.isEmpty()) {
                    item.discard();
                } else {
                    item.setItem(remaining);
                }
                currentTarget = null;
            }
        }


        private ItemEntity findNearestItem(BlockPos chestPos) {
            double radius = 4;
            List<ItemEntity> items = golem.level().getEntitiesOfClass(
                    ItemEntity.class,
                    new net.minecraft.world.phys.AABB(
                            chestPos.getX() - radius, chestPos.getY() - 2, chestPos.getZ() - radius,
                            chestPos.getX() + radius, chestPos.getY() + 2, chestPos.getZ() + radius
                    )
            );

            ItemEntity nearest = null;
            double nearestDist = Double.MAX_VALUE;

            for (ItemEntity item : items) {
                if (!item.isAlive() || item.getItem().isEmpty()) continue;
                double dist = golem.distanceToSqr(item);
                if (dist < nearestDist) {
                    nearest = item;
                    nearestDist = dist;
                }
            }

            return nearest;
        }
    }
}
