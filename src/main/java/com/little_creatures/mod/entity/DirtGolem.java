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

            // Überprüfe ob sich die Center-Position geändert hat
            if (lastCenter == null || !lastCenter.equals(center)) {
                harvestArea = null;
                lastCenter = center;
            }

            // Initialisiere Erntebereich falls nötig
            if (harvestArea == null) {
                harvestArea = createHarvestArea(level, center);
                currentIndex = 0;
                currentTarget = null;
                forwardDirection = true;
            }

            // Wenn Bereich leer, warte kurz und reset
            if (harvestArea.isEmpty()) {
                harvestArea = null;
                return;
            }

            // Finde nächsten validen Target-Block
            if (currentTarget == null) {
                currentTarget = findNextCrop(level);
                stuckTicks = 0;

                if (currentTarget == null) {
                    // Keine weiteren reifen Crops gefunden, reset für nächsten Durchlauf
                    harvestArea = null;
                    return;
                }
            }

            // Bewege dich zum aktuellen Target
            golem.getNavigation().moveTo(
                    currentTarget.getX() + 0.5,
                    currentTarget.getY(),
                    currentTarget.getZ() + 0.5,
                    1.0
            );

            // Überprüfe ob nah genug zum Ernten
            if (golem.distanceToSqr(
                    currentTarget.getX() + 0.5,
                    currentTarget.getY(),
                    currentTarget.getZ() + 0.5) < 2.0) {

                harvestCrop(level, currentTarget);
                currentTarget = null; // Nächsten Crop suchen
                stuckTicks = 0;

            } else {
                // Stuck-Erkennung
                if (stuckTicks++ > 40) { // 2 Sekunden
                    // Überspringe diesen Block und gehe zum nächsten
                    currentIndex = getNextIndex();
                    currentTarget = null;
                    stuckTicks = 0;
                }
            }
        }

        /**
         * Erstellt den Erntebereich in Schlangenlinien-Form (hin und her)
         */
        private List<BlockPos> createHarvestArea(Level level, BlockPos center) {
            List<BlockPos> area = new ArrayList<>();

            // Erstelle eine geordnete Liste von Positionen im Schlangenmuster
            for (int y = -1; y <= 1; y++) {
                for (int z = -radius; z <= radius; z++) {
                    // Entscheide die Richtung basierend auf der z-Position
                    // Gerade z-Reihen: von links nach rechts, ungerade: von rechts nach links
                    if (z % 2 == 0) {
                        // Vorwärts: von -radius bis radius auf x-Achse
                        for (int x = -radius; x <= radius; x++) {
                            BlockPos pos = center.offset(x, y, z).immutable();
                            area.add(pos);
                        }
                    } else {
                        // Rückwärts: von radius bis -radius auf x-Achse
                        for (int x = radius; x >= -radius; x--) {
                            BlockPos pos = center.offset(x, y, z).immutable();
                            area.add(pos);
                        }
                    }
                }
            }
            return area;
        }

        /**
         * Sucht den nächsten reifen Crop in der vordefinierten Reihenfolge
         */
        private BlockPos findNextCrop(Level level) {
            while (currentIndex < harvestArea.size()) {
                BlockPos pos = harvestArea.get(currentIndex);
                currentIndex = getNextIndex();

                if (level.getBlockState(pos).getBlock() instanceof CropBlock crop) {
                    if (crop.isMaxAge(level.getBlockState(pos))) {
                        return pos;
                    }
                }
            }
            return null;
        }

        /**
         * Gibt den nächsten Index in der Reihenfolge zurück
         */
        private int getNextIndex() {
            return currentIndex + 1;
        }

        /**
         * Erntet und replanted einen Crop
         */
        private void harvestCrop(Level level, BlockPos pos) {
            if (level.getBlockState(pos).getBlock() instanceof CropBlock crop) {
                if (crop.isMaxAge(level.getBlockState(pos))) {
                    level.destroyBlock(pos, true, golem);
                    level.setBlock(pos, crop.defaultBlockState(), 3);
                }
            }
        }
    }
}