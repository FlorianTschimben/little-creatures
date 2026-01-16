package com.little_creatures.mod.entity;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
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

    // Default weights for vanilla ores
    private static final Map<Item, Double> DEFAULT_ORE_WEIGHTS = new HashMap<>();
    
    static {
        // Default weights for vanilla ores
        DEFAULT_ORE_WEIGHTS.put(Items.RAW_IRON, 30.0);
        DEFAULT_ORE_WEIGHTS.put(Items.RAW_COPPER, 22.0);
        DEFAULT_ORE_WEIGHTS.put(Items.RAW_GOLD, 10.0);
        DEFAULT_ORE_WEIGHTS.put(Items.COAL, 20.0);
        DEFAULT_ORE_WEIGHTS.put(Items.REDSTONE, 10.0);
        DEFAULT_ORE_WEIGHTS.put(Items.LAPIS_LAZULI, 5.0);
        DEFAULT_ORE_WEIGHTS.put(Items.DIAMOND, 2.0);
        DEFAULT_ORE_WEIGHTS.put(Items.EMERALD, 1.0);
    }

    public DiamondOreGolem(EntityType<? extends MiniGolem> type, Level level) {
        super(type, level, 9);
        initializeOreWeights();
    }
    
    /**
     * Initializes the ore weights by scanning for items with ore-related tags.
     * This allows modded ores to be automatically included.
     */
    private void initializeOreWeights() {
        if (!ORE_WEIGHTS.isEmpty()) {
            return; // Already initialized
        }
        
        // Create tags for raw materials and ores
        TagKey<Item> forgeRawMaterialsTag = ItemTags.create(new ResourceLocation("forge", "raw_materials"));
        TagKey<Item> commonRawMaterialsTag = ItemTags.create(new ResourceLocation("c", "raw_materials"));
        
        // Add vanilla ores with their default weights
        for (Map.Entry<Item, Double> entry : DEFAULT_ORE_WEIGHTS.entrySet()) {
            ORE_WEIGHTS.put(new ItemStack(entry.getKey()), entry.getValue());
        }
        
        // Scan for modded raw materials from both Forge and Common tags
        addModdedOresFromTag(forgeRawMaterialsTag);
        addModdedOresFromTag(commonRawMaterialsTag);
        
        TOTAL_WEIGHT = ORE_WEIGHTS.values().stream().mapToDouble(d -> d).sum();
    }
    
    /**
     * Adds modded ores from a specific tag to the ore weights map.
     */
    private void addModdedOresFromTag(TagKey<Item> tag) {
        for (var holder : BuiltInRegistries.ITEM.getTagOrEmpty(tag)) {
            Item item = holder.value();
            // Skip if already added (vanilla items)
            if (DEFAULT_ORE_WEIGHTS.containsKey(item)) {
                continue;
            }
            // Skip if already added from another tag
            boolean alreadyAdded = false;
            for (ItemStack existing : ORE_WEIGHTS.keySet()) {
                if (existing.is(item)) {
                    alreadyAdded = true;
                    break;
                }
            }
            if (alreadyAdded) {
                continue;
            }
            // Add modded raw materials with a medium weight
            ORE_WEIGHTS.put(new ItemStack(item), 15.0);
        }
    }

    @Override
    public void resetAllPos() {
        super.resetAllPos();
        // Don't clear ORE_WEIGHTS as it's shared across all instances
        // and should remain initialized once set up
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
