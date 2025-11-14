package com.little_creatures.mod.entity;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class DiamondOreGolem extends MiniGolem {

    private final Map<ItemStack, Double> oreWeights = new HashMap<>();
    private double totalWeight;
    private final Random random = new Random();
    private static final int productionCooldown = 200;
    private boolean initialized = false;

    public DiamondOreGolem(EntityType<? extends MiniGolem> type, Level level) {
        super(type, level, 9);
    }

    /**
     * Initialize ore weights by discovering all ore items from tags.
     * This supports both vanilla and modded ores.
     */
    private void initializeOreWeights() {
        if (initialized) return;
        initialized = true;

        // Track which items we've already processed
        Map<Item, Double> processedItems = new HashMap<>();

        // Scan all registered items for ore-related tags
        for (Item item : BuiltInRegistries.ITEM) {
            ItemStack stack = new ItemStack(item);
            
            // Skip air and already processed items
            if (item == Items.AIR || processedItems.containsKey(item)) continue;
            
            // Check for raw ore items (common)
            if (item == Items.RAW_IRON) {
                processedItems.put(item, 30.0);
            } else if (item == Items.RAW_COPPER) {
                processedItems.put(item, 22.0);
            } else if (item == Items.RAW_GOLD) {
                processedItems.put(item, 10.0);
            }
            // Check for common resources
            else if (item == Items.COAL || item == Items.REDSTONE) {
                processedItems.put(item, 20.0);
            } else if (item == Items.LAPIS_LAZULI) {
                processedItems.put(item, 5.0);
            }
            // Check for rare gems
            else if (item == Items.DIAMOND) {
                processedItems.put(item, 2.0);
            } else if (item == Items.EMERALD) {
                processedItems.put(item, 1.0);
            }
            // Detect modded ores by checking common ore tags
            else if (stack.is(ItemTags.create(new net.minecraft.resources.ResourceLocation("forge", "raw_materials"))) ||
                     stack.is(ItemTags.create(new net.minecraft.resources.ResourceLocation("forge", "gems"))) ||
                     stack.is(ItemTags.create(new net.minecraft.resources.ResourceLocation("forge", "ores")))) {
                // Assign weights based on the item's perceived rarity
                // This is a heuristic - modded ores get medium weight by default
                processedItems.put(item, 10.0);
            }
        }

        // Convert to ItemStack-based map
        for (Map.Entry<Item, Double> entry : processedItems.entrySet()) {
            oreWeights.put(new ItemStack(entry.getKey()), entry.getValue());
        }

        // Calculate total weight
        totalWeight = oreWeights.values().stream().mapToDouble(d -> d).sum();
        
        // Ensure we have at least some ores (fallback to vanilla if no tags found)
        if (oreWeights.isEmpty()) {
            initializeFallbackOres();
        }
    }

    /**
     * Fallback to vanilla ores if no ores are discovered through tags
     */
    private void initializeFallbackOres() {
        // Raw Ores
        oreWeights.put(new ItemStack(Items.RAW_IRON), 30.0);
        oreWeights.put(new ItemStack(Items.RAW_COPPER), 22.0);
        oreWeights.put(new ItemStack(Items.RAW_GOLD), 10.0);

        // Resources
        oreWeights.put(new ItemStack(Items.COAL), 20.0);
        oreWeights.put(new ItemStack(Items.REDSTONE), 10.0);
        oreWeights.put(new ItemStack(Items.LAPIS_LAZULI), 5.0);

        // Rare
        oreWeights.put(new ItemStack(Items.DIAMOND), 2.0);
        oreWeights.put(new ItemStack(Items.EMERALD), 1.0);

        totalWeight = oreWeights.values().stream().mapToDouble(d -> d).sum();
    }

    @Override
    public void resetAllPos() {
        super.resetAllPos();
        oreWeights.clear();
        totalWeight = 0;
        initialized = false;
    }

    @Override
    public void tick() {
        super.tick();
        
        // Initialize ore weights on first tick
        if (!initialized && !this.level().isClientSide) {
            initializeOreWeights();
        }
        
        if (!this.level().isClientSide && this.getTargetPos() != null && this.tickCount % productionCooldown == 0) {
            setWorkState(WorkState.DELIVERING);
            ItemStack ore = getRandomOre();
            this.getInventory().addItem(ore);
        } else if (this.getTargetPos() == null) {
            setWorkState(WorkState.IDLE);
        }
    }

    private ItemStack getRandomOre() {
        if (oreWeights.isEmpty()) {
            return new ItemStack(Items.COAL);
        }
        
        double r = random.nextDouble() * totalWeight;
        double cumulative = 0.0;
        for (Map.Entry<ItemStack, Double> entry : oreWeights.entrySet()) {
            cumulative += entry.getValue();
            if (r <= cumulative) {
                return entry.getKey().copy();
            }
        }
        return new ItemStack(Items.COAL);
    }
}
