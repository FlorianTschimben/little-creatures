package com.little_creatures.mod.registry;

import com.little_creatures.mod.LittleCreatures;
import com.little_creatures.mod.item.GolemHeart;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItemTypes {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, LittleCreatures.MODID);

    public static final RegistryObject<Item> GOLEM_HEART = ITEMS.register("golem_heart",
            () -> new GolemHeart(new Item.Properties()));
}
