package com.little_creatures.mod.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class DiamondOreGolem extends MiniGolem {

    private static final Map<ItemStack, Double> ORE_WEIGHTS = new HashMap<>();
    private static double TOTAL_WEIGHT;
    private static final Random RANDOM = new Random();
    private static final int productionCooldown = 200;

    static {
        // Raw Ores
        ORE_WEIGHTS.put(new ItemStack(Items.RAW_IRON), 30.0);
        ORE_WEIGHTS.put(new ItemStack(Items.RAW_COPPER), 22.0);
        ORE_WEIGHTS.put(new ItemStack(Items.RAW_GOLD), 10.0);

        // Resources
        ORE_WEIGHTS.put(new ItemStack(Items.COAL), 20.0);
        ORE_WEIGHTS.put(new ItemStack(Items.REDSTONE), 10.0);
        ORE_WEIGHTS.put(new ItemStack(Items.LAPIS_LAZULI), 5.0);

        // Rare
        ORE_WEIGHTS.put(new ItemStack(Items.DIAMOND), 2.0);
        ORE_WEIGHTS.put(new ItemStack(Items.EMERALD), 1.0);

        TOTAL_WEIGHT = ORE_WEIGHTS.values().stream().mapToDouble(d -> d).sum();
    }

    public DiamondOreGolem(EntityType<? extends MiniGolem> type, Level level) {
        super(type, level, 9);
    }

    @Override
    public void resetAllPos() {
        super.resetAllPos();
        ORE_WEIGHTS.clear();
        TOTAL_WEIGHT = 0;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide && this.getTargetPos() != null && this.tickCount % productionCooldown == 0) {
            setWorkState(WorkState.DELIVERING);
            ItemStack ore = getRandomOre();
            this.getInventory().addItem(ore);
        } else if (this.getTargetPos() == null) {
            setWorkState(WorkState.IDLE);
        }
    }

    private ItemStack getRandomOre() {
        double r = RANDOM.nextDouble() * TOTAL_WEIGHT;
        double cumulative = 0.0;
        for (Map.Entry<ItemStack, Double> entry : ORE_WEIGHTS.entrySet()) {
            cumulative += entry.getValue();
            if (r <= cumulative) {
                return entry.getKey().copy();
            }
        }
        return new ItemStack(Items.COAL);
    }
}
