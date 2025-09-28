package com.little_creatures.mod.registry;

import com.little_creatures.mod.LittleCreatures;
import com.little_creatures.mod.entity.ClayGolem;
import com.little_creatures.mod.entity.DiamondOreGolem;
import com.little_creatures.mod.entity.DirtGolem;
import com.little_creatures.mod.entity.WoodGolem;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntityTypes {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, LittleCreatures.MODID);

    public static final RegistryObject<EntityType<WoodGolem>> WOOD_GOLEM =
            ENTITIES.register("wood_golem", () ->
                    EntityType.Builder.of(WoodGolem::new, MobCategory.CREATURE)
                            .sized(0.5f, 0.6f)
                            .build(LittleCreatures.MODID + ":wood_golem"));

    public static final RegistryObject<EntityType<ClayGolem>> CLAY_GOLEM =
            ENTITIES.register("clay_golem", () ->
                    EntityType.Builder.of(ClayGolem::new, MobCategory.CREATURE)
                            .sized(0.5f, 0.6f)
                            .build(LittleCreatures.MODID + ":clay_golem"));

    public static final RegistryObject<EntityType<DirtGolem>> DIRT_GOLEM =
            ENTITIES.register("dirt_golem", () ->
                    EntityType.Builder.of(DirtGolem::new, MobCategory.CREATURE)
                            .sized(0.5f, 0.6f)
                            .build(LittleCreatures.MODID + ":dirt_golem"));

    public static final RegistryObject<EntityType<DiamondOreGolem>> DIAMOND_ORE_GOLEM =
            ENTITIES.register("diamond_ore_golem", () ->
                    EntityType.Builder.of(DiamondOreGolem::new, MobCategory.CREATURE)
                            .sized(0.5f, 0.6f)
                            .build(LittleCreatures.MODID + ":diamond_ore_golem"));
}