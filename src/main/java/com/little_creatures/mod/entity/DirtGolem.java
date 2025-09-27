package com.little_creatures.mod.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CropBlock;

import java.util.EnumSet;

public class DirtGolem extends MiniGolem {

    public DirtGolem(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level, 0);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(3, new HarvestCropsGoal(this, 4)); // Radius = 4
    }

    public static class HarvestCropsGoal extends Goal {
        private final DirtGolem golem;
        private final int radius;
        private BlockPos harvestTarget = null;
        private int stuckTicks = 0;

        public HarvestCropsGoal(DirtGolem golem, int radius) {
            this.golem = golem;
            this.radius = radius;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return golem.getTargetPos() != null;
        }

        @Override
        public boolean canContinueToUse() {
            return golem.getTargetPos() != null;
        }

        @Override
        public void tick() {
            Level level = golem.level();
            BlockPos center = golem.getTargetPos();

            if (harvestTarget == null) {
                harvestTarget = findNearestCrop(level, center);
                stuckTicks = 0;
            }

            if (harvestTarget != null) {
                // zum Feld bewegen
                golem.getNavigation().moveTo(
                        harvestTarget.getX() + 0.5,
                        harvestTarget.getY(),
                        harvestTarget.getZ() + 0.5,
                        1.0
                );

                // Check ob nah genug zum Abbauen
                if (golem.distanceToSqr(harvestTarget.getX() + 0.5,
                        harvestTarget.getY(),
                        harvestTarget.getZ() + 0.5) < 2.0) {

                    if (level.getBlockState(harvestTarget).getBlock() instanceof CropBlock crop) {
                        if (crop.isMaxAge(level.getBlockState(harvestTarget))) {
                            level.destroyBlock(harvestTarget, true, golem);
                            level.setBlock(harvestTarget, crop.defaultBlockState(), 3);
                        }
                    }

                    harvestTarget = findNearestCrop(level, center);
                    stuckTicks = 0;
                } else {
                    stuckTicks++;
                    if (stuckTicks > 20) { // 1 Sekunde
                        harvestTarget = null;
                        stuckTicks = 0;
                    }
                }
            }
        }

        /**
         * Suche das nächstgelegene reife Feld
         */
        private BlockPos findNearestCrop(Level level, BlockPos center) {
            BlockPos nearest = null;
            double nearestDist = Double.MAX_VALUE;
            for (BlockPos pos : BlockPos.betweenClosed(
                    center.offset(-radius, -1, -radius),
                    center.offset(radius, 1, radius))) {
                if (level.getBlockState(pos).getBlock() instanceof CropBlock crop) {
                    if (crop.isMaxAge(level.getBlockState(pos))) {
                        double dist = golem.distanceToSqr(
                                pos.getX() + 0.5,
                                pos.getY() + 0.5,
                                pos.getZ() + 0.5
                        );
                        if (dist < nearestDist) {
                            nearestDist = dist;
                            nearest = pos.immutable();
                        }
                    }
                }
            }
            return nearest;
        }
    }
}
