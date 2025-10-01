package com.little_creatures.mod.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

public class ObsidianGolem extends MiniGolem {
    public ObsidianGolem(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level, 0);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return MiniGolem.createAttributes()
                .add(Attributes.MAX_HEALTH, 100.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.23D)
                .add(Attributes.ATTACK_DAMAGE, 15.0D)
                .add(Attributes.ATTACK_KNOCKBACK, 1.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        this.goalSelector.addGoal(1, new ConditionalMeleeAttackGoal(this, 1.0D, true));
        this.targetSelector.addGoal(1, new ConditionalHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new ConditionalNearestAttackableTargetGoal(this));
    }

    private static class ConditionalMeleeAttackGoal extends MeleeAttackGoal {
        private final ObsidianGolem golem;

        public ConditionalMeleeAttackGoal(ObsidianGolem golem, double speed, boolean longMemory) {
            super(golem, speed, longMemory);
            this.golem = golem;
        }

        @Override
        public boolean canUse() {
            return golem.getTargetPos() != null && super.canUse();
        }
    }

    private static class ConditionalHurtByTargetGoal extends HurtByTargetGoal {
        private final ObsidianGolem golem;

        public ConditionalHurtByTargetGoal(ObsidianGolem golem) {
            super(golem);
            this.golem = golem;
        }

        @Override
        public boolean canUse() {
            return golem.getTargetPos() != null && super.canUse();
        }
    }

    private static class ConditionalNearestAttackableTargetGoal extends NearestAttackableTargetGoal<Monster> {
        private final ObsidianGolem golem;

        public ConditionalNearestAttackableTargetGoal(ObsidianGolem golem) {
            super(golem, Monster.class, true);
            this.golem = golem;
        }

        @Override
        public boolean canUse() {
            return golem.getTargetPos() != null && super.canUse();
        }
    }
}
