package com.little_creatures.mod.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;

public class CraftingTableGolem extends MiniGolem {
    public CraftingTableGolem(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level, 9);
    }
}
