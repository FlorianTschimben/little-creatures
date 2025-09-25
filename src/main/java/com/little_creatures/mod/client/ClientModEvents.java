package com.little_creatures.mod.client;

import com.little_creatures.mod.LittleCreatures;
import com.little_creatures.mod.client.model.MiniGolemModel;
import com.little_creatures.mod.entity.WoodGolem;
import com.little_creatures.mod.registry.ModEntityTypes;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = LittleCreatures.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(MiniGolemModel.LAYER_LOCATION, MiniGolemModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntityTypes.WOOD_GOLEM.get(), ctx ->
                new MobRenderer<WoodGolem, MiniGolemModel<WoodGolem>>(
                        ctx,
                        new MiniGolemModel<>(ctx.bakeLayer(MiniGolemModel.LAYER_LOCATION)),
                        0.5f
                ) {
                    @Override
                    public ResourceLocation getTextureLocation(WoodGolem entity) {
                        return new ResourceLocation(LittleCreatures.MODID, "textures/entity/wood_golem.png");
                    }
                });
    }
}