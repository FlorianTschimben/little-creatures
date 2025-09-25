package com.little_creatures.mod.item;

import com.little_creatures.mod.entity.MiniGolem;
import com.little_creatures.mod.entity.WoodGolem;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GolemWand extends Item {
    private static final Map<UUID, MiniGolem> selectedGolems = new HashMap<>();
    public GolemWand(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity entity, InteractionHand hand) {
        if (entity instanceof WoodGolem golem) {
            selectedGolems.put(player.getUUID(), golem);
            player.displayClientMessage(Component.literal("Golem ausgewählt!"), true);
            return InteractionResult.SUCCESS;
        }
        return super.interactLivingEntity(stack, player, entity, hand);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        BlockPos pos = context.getClickedPos();

        if (!level.isClientSide) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof ChestBlockEntity chest) {
                MiniGolem golem = selectedGolems.get(player.getUUID());
                if (golem != null) {
                    golem.setTarget_pos(pos);
                    player.displayClientMessage(Component.literal("Golem läuft zur Chest!"), true);
                } else {
                    player.displayClientMessage(Component.literal("Kein Golem ausgewählt!"), true);
                }
                return InteractionResult.SUCCESS;
            }
        }
        return super.useOn(context);
    }
}
