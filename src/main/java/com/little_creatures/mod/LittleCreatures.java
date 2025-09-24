package com.little_creatures.mod;

import com.little_creatures.mod.client.ClientModEvents;
import com.little_creatures.mod.entity.WoodGolem;
import com.little_creatures.mod.registry.ModEntityTypes;
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
    }

    @Mod.EventBusSubscriber(modid = LittleCreatures.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public class ModEvents {

        @SubscribeEvent
        public static void registerAttributes(EntityAttributeCreationEvent event) {
            event.put(ModEntityTypes.WOODGOLEM.get(), WoodGolem.createAttributes().build());
        }
    }
}
