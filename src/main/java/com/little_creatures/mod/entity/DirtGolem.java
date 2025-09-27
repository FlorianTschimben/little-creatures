package com.little_creatures.mod.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CropBlock;

import java.util.EnumSet;
import java.util.ArrayList;
import java.util.List;

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
        private List<BlockPos> harvestArea;
        private int currentIndex = 0;
        private BlockPos currentTarget = null;
        private int stuckTicks = 0;
        private BlockPos lastCenter;
        private boolean forwardDirection = true; // True: vorwärts, False: rückwärts

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
        public void start() {
            // Reset beim Start des Goals
            harvestArea = null;
            currentIndex = 0;
            currentTarget = null;
            stuckTicks = 0;
            lastCenter = null;
            forwardDirection = true;
        }

        @Override
        public void tick() {
            Level level = golem.level();
            BlockPos center = golem.getTargetPos();

            if (center == null) return;

            if (lastCenter == null || !lastCenter.equals(center)) {
                harvestArea = null;
                lastCenter = center;
            }

            if (harvestArea == null) {
                harvestArea = createHarvestArea(center);
                currentIndex = 0;
                currentTarget = null;
                forwardDirection = true;
            }

            if (harvestArea.isEmpty()) {
                harvestArea = null;
                return;
            }

            if (currentTarget == null) {
                currentTarget = findNextCrop(level);
                stuckTicks = 0;

                if (currentTarget == null) {
                    harvestArea = null;
                    return;
                }
            }

            golem.getNavigation().moveTo(
                    currentTarget.getX() + 0.5,
                    currentTarget.getY(),
                    currentTarget.getZ() + 0.5,
                    1.0
            );

            if (golem.distanceToSqr(
                    currentTarget.getX() + 0.5,
                    currentTarget.getY(),
                    currentTarget.getZ() + 0.5) < 3.0) {

                harvestAndReplantCrop(level, currentTarget);
                currentTarget = null;
                stuckTicks = 0;

            } else {
                if (stuckTicks++ > 40) {
                    currentIndex++;
                    currentTarget = null;
                    stuckTicks = 0;
                }
            }
        }

        private List<BlockPos> createHarvestArea(BlockPos center) {
            List<BlockPos> area = new ArrayList<>();

            for (int y = -1; y <= 1; y++) {
                for (int z = -radius; z <= radius; z++) {
                    if (z % 2 == 0) {
                        for (int x = -radius; x <= radius; x++) {
                            BlockPos pos = center.offset(x, y, z).immutable();
                            area.add(pos);
                        }
                    } else {
                        for (int x = radius; x >= -radius; x--) {
                            BlockPos pos = center.offset(x, y, z).immutable();
                            area.add(pos);
                        }
                    }
                }
            }
            return area;
        }

        private BlockPos findNextCrop(Level level) {
            while (currentIndex < harvestArea.size()) {
                BlockPos pos = harvestArea.get(currentIndex);
                currentIndex++;

                if (level.getBlockState(pos).getBlock() instanceof CropBlock crop) {
                    if (crop.isMaxAge(level.getBlockState(pos))) {
                        return pos;
                    }
                }
            }
            return null;
        }

        private void harvestAndReplantCrop(Level level, BlockPos pos) {
            if (level.getBlockState(pos).getBlock() instanceof CropBlock crop) {
                if (crop.isMaxAge(level.getBlockState(pos))) {
                    level.destroyBlock(pos, true, golem);
                    level.setBlock(pos, crop.defaultBlockState(), 3);
                }
            }
        }
    }
}