package com.little_creatures.mod.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;

public class ObsidianGolem extends MiniGolem{
    public ObsidianGolem(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level, 0);
    }
}
