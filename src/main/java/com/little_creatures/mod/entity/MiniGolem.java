package com.little_creatures.mod.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class MiniGolem extends PathfinderMob {
    private BlockPos targetPos = null;
    private final SimpleContainer inventory;

    public MiniGolem(EntityType<? extends PathfinderMob> type, Level level, int invSlots) {
        super(type, level);
        inventory =  new SimpleContainer(invSlots);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        MoveToBlockGoal moveToBlockGoal = new MoveToBlockGoal(this);
        this.goalSelector.addGoal(1, moveToBlockGoal);
    }

    public BlockPos getTargetPos() {
        return this.targetPos;
    }

    public void setTargetPos(BlockPos b) {
        this.targetPos = b;
        if (targetPos == null) {
            this.getNavigation().stop();
            this.setDeltaMovement(0, this.getDeltaMovement().y, 0);
            this.setNoAi(false);
        }
    }

    public SimpleContainer getInventory() {
        return inventory;
    }

    public boolean addItem(ItemStack stack) {
        return inventory.addItem(stack).isEmpty();
    }

    public ItemStack removeItem(int slot, int amount) {
        return inventory.removeItem(slot, amount);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.put("Inventory", inventory.createTag());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        inventory.fromTag(tag.getList("Inventory", 10));
    }
}
