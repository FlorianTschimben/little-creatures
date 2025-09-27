package com.little_creatures.mod;

import com.little_creatures.mod.entity.ClayGolem;
import com.little_creatures.mod.entity.DirtGolem;
import com.little_creatures.mod.entity.WoodGolem;
import com.little_creatures.mod.registry.ModEntityTypes;
import com.little_creatures.mod.registry.ModItemTypes;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(LittleCreatures.MODID)
public class LittleCreatures
{
    public static final String MODID = "little_creatures";

    public LittleCreatures() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModEntityTypes.ENTITIES.register(modEventBus);
        ModItemTypes.ITEMS.register(modEventBus);
    }

    @Mod.EventBusSubscriber(modid = LittleCreatures.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEvents {

        @SubscribeEvent
        public static void registerAttributes(EntityAttributeCreationEvent event) {
            event.put(ModEntityTypes.WOOD_GOLEM.get(), WoodGolem.createAttributes().build());
            event.put(ModEntityTypes.CLAY_GOLEM.get(), ClayGolem.createAttributes().build());
            event.put(ModEntityTypes.DIRT_GOLEM.get(), DirtGolem.createAttributes().build());
        }
    }
}
