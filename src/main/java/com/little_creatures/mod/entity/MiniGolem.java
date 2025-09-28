package com.little_creatures.mod.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class MiniGolem extends PathfinderMob {
    private BlockPos targetPos = null;
    private final SimpleContainer inventory;
    private WorkState workState = WorkState.IDLE;

    public MiniGolem(EntityType<? extends PathfinderMob> type, Level level, int invSlots) {
        super(type, level);
        inventory = new SimpleContainer(invSlots);
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
        this.goalSelector.addGoal(2, moveToBlockGoal);
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

    public void resetAllPos() {
        setTargetPos(null);
        setWorkState(WorkState.IDLE);
    }

    public SimpleContainer getInventory() {
        return inventory;
    }

    public boolean isInventoryFull() {
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            if (inventory.getItem(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public boolean addItem(ItemStack stack) {
        return inventory.addItem(stack).isEmpty();
    }

    public ItemStack removeItem(int slot, int amount) {
        return inventory.removeItem(slot, amount);
    }

    public WorkState getWorkState() {
        return workState;
    }

    public void setWorkState(WorkState state) {
        this.workState = state;
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.put("Inventory", inventory.createTag());
        if (targetPos != null) {
            tag.putInt("TargetPosX", targetPos.getX());
            tag.putInt("TargetPosY", targetPos.getY());
            tag.putInt("TargetPosZ", targetPos.getZ());
        }
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        inventory.fromTag(tag.getList("Inventory", 10));
        if (tag.contains("TargetPosX")) {
            targetPos = new BlockPos(
                    tag.getInt("TargetPosX"),
                    tag.getInt("TargetPosY"),
                    tag.getInt("TargetPosZ")
            );
        }
    }

    public static class MoveToBlockGoal extends Goal {
        private final MiniGolem golem;

        public MoveToBlockGoal(MiniGolem golem) {
            this.golem = golem;
        }

        @Override
        public boolean canUse() {
            return golem.getWorkState() != WorkState.WORKING && golem.getTargetPos() != null;
        }

        @Override
        public void tick() {
            BlockPos target = golem.getTargetPos();
            if (target != null) {
                golem.getNavigation().moveTo(target.getX() + 0.5, target.getY() + 1, target.getZ() + 0.5, 1.0);
                if (golem.distanceToSqr(target.getX() + 0.5, target.getY() + 1, target.getZ() + 0.5) < 2.0) {
                    if (golem.level().getBlockEntity(target) instanceof net.minecraft.world.Container chest) {
                        for (int i = 0; i < golem.getInventory().getContainerSize(); i++) {
                            ItemStack stack = golem.getInventory().getItem(i);
                            if (!stack.isEmpty()) {
                                ItemStack remaining = stack.copy();
                                for (int j = 0; j < chest.getContainerSize(); j++) {
                                    ItemStack chestStack = chest.getItem(j);
                                    if (chestStack.isEmpty()) {
                                        chest.setItem(j, remaining);
                                        remaining = ItemStack.EMPTY;
                                        break;
                                    }
                                    if (ItemStack.isSameItemSameTags(chestStack, remaining)) {
                                        int maxStackSize = Math.min(chest.getMaxStackSize(), chestStack.getMaxStackSize());
                                        int space = maxStackSize - chestStack.getCount();
                                        if (space > 0) {
                                            int moveAmount = Math.min(space, remaining.getCount());
                                            chestStack.grow(moveAmount);
                                            remaining.shrink(moveAmount);
                                            if (remaining.isEmpty()) break;
                                        }
                                    }
                                }
                                golem.getInventory().setItem(i, remaining);
                                chest.setChanged();
                                if (remaining.isEmpty()) golem.setWorkState(WorkState.WORKING);
                            }
                        }
                        if (golem.getInventory().isEmpty()) {
                            golem.setWorkState(WorkState.WORKING);
                        }
                    }
                }
            }
        }
    }

    public enum WorkState {
        WORKING,
        DELIVERING,
        IDLE
    }
}
