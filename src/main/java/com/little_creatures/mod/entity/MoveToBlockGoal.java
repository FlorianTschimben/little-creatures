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
        return golem.getTargetPos() != null;
    }

    @Override
    public void tick() {
        BlockPos target = golem.getTargetPos();
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

