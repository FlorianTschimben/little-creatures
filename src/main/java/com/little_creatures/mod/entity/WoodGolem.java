package com.little_creatures.mod.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;

public class WoodGolem extends MiniGolem {
    public WoodGolem(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
    }
}