package com.little_creatures.mod.client;

import com.little_creatures.mod.LittleCreatures;
import com.little_creatures.mod.client.model.MiniGolemModel;
import com.little_creatures.mod.entity.*;
import com.little_creatures.mod.registry.ModEntityTypes;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

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
                    public @NotNull ResourceLocation getTextureLocation(@NotNull WoodGolem entity) {
                        return new ResourceLocation(LittleCreatures.MODID, "textures/entity/wood_golem.png");
                    }
                });
        event.registerEntityRenderer(ModEntityTypes.CLAY_GOLEM.get(), ctx ->
                new MobRenderer<ClayGolem, MiniGolemModel<ClayGolem>>(
                        ctx,
                        new MiniGolemModel<>(ctx.bakeLayer(MiniGolemModel.LAYER_LOCATION)),
                        0.5f
                ) {
                    @Override
                    public @NotNull ResourceLocation getTextureLocation(@NotNull ClayGolem entity) {
                        return new ResourceLocation(LittleCreatures.MODID, "textures/entity/clay_golem.png");
                    }
                });
        event.registerEntityRenderer(ModEntityTypes.DIRT_GOLEM.get(), ctx ->
                new MobRenderer<DirtGolem, MiniGolemModel<DirtGolem>>(
                        ctx,
                        new MiniGolemModel<>(ctx.bakeLayer(MiniGolemModel.LAYER_LOCATION)),
                        0.5f
                ) {
                    @Override
                    public @NotNull ResourceLocation getTextureLocation(@NotNull DirtGolem entity) {
                        return new ResourceLocation(LittleCreatures.MODID, "textures/entity/dirt_golem.png");
                    }
                });
        event.registerEntityRenderer(ModEntityTypes.DIAMOND_ORE_GOLEM.get(), ctx ->
                new MobRenderer<DiamondOreGolem, MiniGolemModel<DiamondOreGolem>>(
                        ctx,
                        new MiniGolemModel<>(ctx.bakeLayer(MiniGolemModel.LAYER_LOCATION)),
                        0.5f
                ) {
                    @Override
                    public @NotNull ResourceLocation getTextureLocation(@NotNull DiamondOreGolem entity) {
                        return new ResourceLocation(LittleCreatures.MODID, "textures/entity/diamond_ore_golem.png");
                    }
                });
        event.registerEntityRenderer(ModEntityTypes.CRAFTING_TABLE_GOLEM.get(), ctx ->
                new MobRenderer<CraftingTableGolem, MiniGolemModel<CraftingTableGolem>>(
                        ctx,
                        new MiniGolemModel<>(ctx.bakeLayer(MiniGolemModel.LAYER_LOCATION)),
                        0.5f
                ) {
                    @Override
                    public @NotNull ResourceLocation getTextureLocation(@NotNull CraftingTableGolem entity) {
                        return new ResourceLocation(LittleCreatures.MODID, "textures/entity/crafting_table_golem.png");
                    }
                });
    }
}