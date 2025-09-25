package com.little_creatures.mod.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

public class MiniGolem extends PathfinderMob {
    private BlockPos target_pos = null;
    private MoveToBlockGoal moveToBlockGoal = null;
    public MiniGolem(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        moveToBlockGoal = new MoveToBlockGoal(this);
        this.goalSelector.addGoal(1, moveToBlockGoal);
    }

    public BlockPos getTarget_pos() {
        return this.target_pos;
    }

    public void setTarget_pos(BlockPos b) {
        this.target_pos = b;
    }
}
