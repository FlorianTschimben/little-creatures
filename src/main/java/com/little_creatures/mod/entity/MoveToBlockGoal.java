package com.little_creatures.mod.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;

public class MoveToBlockGoal extends Goal {
    private final MiniGolem golem;

    public MoveToBlockGoal(MiniGolem golem) {
        this.golem = golem;
    }


    @Override
    public boolean canUse() {
        return golem.getTarget_pos() != null;
    }

    @Override
    public void tick() {
        BlockPos target = golem.getTarget_pos();
        if (target != null) {
            golem.getNavigation().moveTo(
                    target.getX() + 0.5,
                    target.getY() + 1, // kleine Höhe damit er nicht im Block feststeckt
                    target.getZ() + 0.5,
                    1.0
            );
        }
    }
}

