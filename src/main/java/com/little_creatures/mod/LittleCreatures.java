package com.little_creatures.mod;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(LittleCreatures.MODID)
public class LittleCreatures
{
    public static final String MODID = "little_creatures";

    public LittleCreatures() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
    }
}
