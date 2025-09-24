package com.little_creatures.mod.registry;

import com.little_creatures.mod.LittleCreatures;
import com.little_creatures.mod.entity.WoodGolem;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntityTypes {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, LittleCreatures.MODID);

    public static final RegistryObject<EntityType<WoodGolem>> WOODGOLEM =
            ENTITIES.register("wood_golem", () ->
                    EntityType.Builder.of(WoodGolem::new, MobCategory.CREATURE)
                            .sized(0.6f, 1.8f) // Breite, Höhe
                            .build(LittleCreatures.MODID + ":wood_golem"));
}